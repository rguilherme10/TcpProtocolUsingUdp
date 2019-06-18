package ambos;

import java.util.TreeMap;

import utils.RandomInt;

/**
 * Classe responsavel por receber e transmitir os pacotes.
 * 
 * @author ribamar
 *
 */
public class Comunicacao {
  
  protected static final long TEMPO_CPU = 10;
  
  public boolean threeWayHandShake = false;
  
  private int    numeroAck      = 0;
  private Janela janelaReceptora;
  private int    portaReceptora = RandomInt.getInt(6790, 20000);
  
  private int   numeroDeSequencia = RandomInt.getInt(0, 65535);
  public Janela janelaTransmissora;
  
  private Host    host;
  public int      tcpMaximumSegmentSize;
  private boolean retransmissaoAckDuplicado;
  public int      timeout;
  private double  probabilidadeDeDescarte;
  
  public boolean receptorAtivo           = true;
  public boolean analisaReceptorAtivo    = true;
  public boolean transmissorAtivo        = true;
  public boolean analisaTransmissorAtivo = true;
  
  private boolean enviouFinAck = false;
  
  private Thread transmissor;
  
  public final TreeMap<Integer, byte[]> dadosRecebidos = new TreeMap<>();
  
  /**
   * Comunicacao passiva.
   * 
   * @param synRecebidoDoCliente = pacote SYN
   * @param tamanhoJanela        = tamanho inicial das Janelas transmissora e
   *                             receptora
   */
  public Comunicacao(Mensagem synRecebidoDoCliente, int tamanhoJanela) {
    
    numeroAck = synRecebidoDoCliente.getCalculaAck();
    
    this.host = new Host(synRecebidoDoCliente.host.endereco,
        (int) synRecebidoDoCliente.getPortaOrigem());
    this.tcpMaximumSegmentSize = tamanhoJanela;
    this.retransmissaoAckDuplicado = false;
    this.timeout = 0;
    
    iniciaComunicacao(tamanhoJanela);
    
  }
  
  /**
   * Comunicacao ativa.
   * 
   * @param hostServidor              = host do destinatario escutando
   * @param tamanhoJanela             = tamanho inicial das Janelas transmissora e
   *                                  receptora
   * @param tcpMaximumSegmentSize     = tcpMaximumSegmentSize
   * @param retransmissaoAckDuplicado = false se retransmissao por timeout
   * @param timeout                   = timeout em milisegundos para retransmissao
   *                                  caso nao receba ACK
   */
  public Comunicacao(Host hostServidor, int tamanhoJanela, int tcpMaximumSegmentSize,
      boolean retransmissaoAckDuplicado, int timeout) {
    
    this.host = hostServidor;
    this.tcpMaximumSegmentSize = tcpMaximumSegmentSize;
    this.retransmissaoAckDuplicado = retransmissaoAckDuplicado;
    this.timeout = timeout;
    
    iniciaComunicacao(tamanhoJanela);
    
  }
  
  /**
   * espera ThreeWayHandShake terminar.
   */
  public void esperaThreeWayHandShake() {
    while (threeWayHandShake == false) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * espera ThreeWayHandShake terminar ou timeout do tempoEspera.
   */
  public void esperaThreeWayHandShake(long tempoEspera) {
    try {
      Thread.sleep(tempoEspera);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Cria pacote de controle, cria mensagem e insere na transmissora se tiver
   * espaco.
   * 
   * @param ack = 1 se ack
   * @param syn = 1 se syn
   * @param fin = 1 se fin
   */
  public void enviaControle(int ack, int syn, int fin) {
    Pacote pacote = new Pacote(portaReceptora, host.porta, numeroDeSequencia, numeroAck, ack, syn,
        fin, (janelaReceptora.getEspacoLivre()), 0, null);
    janelaTransmissora.esperaCaberAdicionaMensagem(new Mensagem(host, pacote));
    incrementaNumeroSequencia(pacote.size() + 1);
  }
  
  public void enviaSyn() {
    System.out.println(portaReceptora + " enviou Syn " + numeroDeSequencia + " " + numeroAck);
    enviaControle(0, 1, 0);
  }
  
  public void enviaSynAck() {
    System.out.println(portaReceptora + " enviou SynAck " + numeroDeSequencia + " " + numeroAck);
    enviaControle(1, 1, 0);
  }
  
  private void enviaAck() {
    System.out.println(portaReceptora + " enviou Ack " + numeroDeSequencia + " " + numeroAck);
    enviaControle(1, 0, 0);
  }
  
  /**
   * Envia Fin.
   */
  public void enviaFin() {
    System.out.println(portaReceptora + " enviou Fin " + numeroDeSequencia + " " + numeroAck);
    janelaTransmissora.esperaFicarVazia();
    enviaControle(0, 0, 1);
  }
  
  /**
   * Envia Fin + Ack.
   */
  public void enviaFinAck() {
    System.out.println(portaReceptora + " enviou FinAck " + numeroDeSequencia + " " + numeroAck);
    janelaTransmissora.esperaFicarVazia();
    enviaControle(1, 0, 1);
  }
  
  /**
   * Cria pacote, cria mensagem e insere na transmissora se tiver espaço.
   * 
   * @param tamanhoDados = quantidade de bytes do dados
   * @param dados        = dados
   * @param timeout      = timeout
   */
  public void enviaDados(int tamanhoDados, byte[] dados, int timeout) {
    System.out.println(portaReceptora + " enviou dados " + numeroDeSequencia + " " + numeroAck);
    Pacote pacote = new Pacote(portaReceptora, host.porta, numeroDeSequencia, numeroAck, 0, 0, 0,
        (janelaReceptora.getEspacoLivre()), tamanhoDados, dados);
    Mensagem mensagemParaEnviar = new Mensagem(host, pacote);
    janelaTransmissora.esperaCaberAdicionaMensagem(mensagemParaEnviar);
    incrementaNumeroSequencia(pacote.size() + 1);
  }
  
  private void incrementaNumeroSequencia(int incremento) {
    numeroDeSequencia = numeroDeSequencia + incremento;
  }
  
  private void iniciaComunicacao(int tamanhoJanela) {
    
    this.janelaReceptora = new Janela(tamanhoJanela);
    /*
     * while () { try { Thread.sleep(10); } catch (InterruptedException e) {} }
     */
    inicializaReceptora();
    
    this.janelaTransmissora = new Janela(tamanhoJanela);
    inicializaTransmissora();
    
  }
  
  private boolean inicializaReceptora() {
    try {
      final Thread receptor = new Thread("Comunicacao.inicializaReceptora " + portaReceptora) {
        @Override
        public void run() {
          while (receptorAtivo) {
            Mensagem mensagemRecebida = Mensagem.receberMensagem(portaReceptora, 1480);
            if (mensagemRecebida != null) {
              janelaTransmissora.setTamanhoJanela(mensagemRecebida.getTamanhoJanelaReceptor());
              if (janelaReceptora.add(mensagemRecebida)) {
                System.out
                    .println(portaReceptora + " recebeu " + mensagemRecebida.getNumeroSequencia());
              }
              if (mensagemRecebida.isFin() || mensagemRecebida.isFinAck()) {
                receptorAtivo = false;
              }
            }
          }
          System.out.println(portaReceptora + " encerrou recepcao");
        }
      };
      receptor.start();
      
      Thread analisaRecepcoes = new Thread("Comunicacao.analisaRecepcoes " + portaReceptora) {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
          while (analisaReceptorAtivo) {
            try {
              for (Mensagem mensagemJanelaReceptora : janelaReceptora.queue.values()) {
                if (janelaTransmissora.contem(mensagemJanelaReceptora.getNumeroAck())) {
                  Mensagem mensagemJanelaTransmissora = janelaTransmissora
                      .getMensagem(mensagemJanelaReceptora.getNumeroAck());
                  mensagemJanelaTransmissora.retornouAck = true;
                }
                
                numeroAck = mensagemJanelaReceptora.getCalculaAck();
                
                System.out.println(portaReceptora + " analisaRecepcao "
                    + mensagemJanelaReceptora.getNumeroSequencia() + " "
                    + mensagemJanelaReceptora.getNumeroAck());
                
                if (mensagemJanelaReceptora.isControle()) {
                  if (mensagemJanelaReceptora.isAck()) {
                    
                    threeWayHandShake = true;
                    
                  } else if (mensagemJanelaReceptora.isSynAck()) {
                    
                    host = new Host(mensagemJanelaReceptora.host.endereco,
                        (int) mensagemJanelaReceptora.getPortaOrigem());
                    enviaAck();
                    
                    threeWayHandShake = true;
                    
                  } else if (mensagemJanelaReceptora.isFin()) {
                    
                    enviaAck();
                    enviaFinAck();
                    analisaReceptorAtivo = false;
                    
                  } else if (mensagemJanelaReceptora.isFinAck()) {
                    
                    enviaAck();
                    analisaReceptorAtivo = false;
                    
                  }
                } else {
                  
                  if (mensagemJanelaReceptora.size() > 0) {
                    dadosRecebidos.put(mensagemJanelaReceptora.getNumeroSequencia(),
                        mensagemJanelaReceptora.getDados());
                  }
                  
                  enviaAck();
                }
                janelaReceptora.remove(mensagemJanelaReceptora);
              }
            } catch (Exception e) {
              // TODO ignorar erro de concorrencia
              // e.printStackTrace();
            }
            try {
              Thread.sleep(TEMPO_CPU);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          receptor.stop();
        }
      };
      analisaRecepcoes.start();
      return true;
    } catch (Exception e) {
      // Porta já está em uso
      // portaReceptora = RandomInt.getInt(6791, 20000); // nova porta
      e.printStackTrace();
    }
    return false;
  }
  
  private void inicializaTransmissora() {
    transmissor = new Thread("Comunicacao.inicializaTransmissora " + portaReceptora) {
      @Override
      public void run() {
        // TODO Auto-generated method stub
        while (transmissorAtivo) {
          try {
            for (Mensagem mensagemJanelaTransmissora : janelaTransmissora.queue.values()) {
              if (mensagemJanelaTransmissora.enviada == false) {
                mensagemJanelaTransmissora.enviarMensagem(probabilidadeDeDescarte, timeout);
                if (mensagemJanelaTransmissora.isFin() || mensagemJanelaTransmissora.isFinAck()) {
                  enviouFinAck = true;
                }
              }
            }
          } catch (Exception e) {
            // TODO ignorar erro de concorrencia
            // e.printStackTrace();
          }
          
          if (enviouFinAck && janelaTransmissora.temMensagens() == false) {
            transmissorAtivo = false;
          }
          try {
            Thread.sleep(TEMPO_CPU);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        // System.out.println(portaReceptora + " encerrou transmissao");
      }
    };
    transmissor.start();
    
    Thread analisaTransmissoes = new Thread("Comunicacao.analisaTransmissoes " + portaReceptora) {
      @Override
      public void run() {
        while (analisaTransmissorAtivo) {
          try {
            for (Mensagem mensagemJanelaTransmissora : janelaTransmissora.queue.values()) {
              if (mensagemJanelaTransmissora.enviada) {
                if (mensagemJanelaTransmissora.retornouAck || mensagemJanelaTransmissora.isAck()
                    || mensagemJanelaTransmissora.isSyn() || mensagemJanelaTransmissora.isSynAck()
                    || mensagemJanelaTransmissora.isFinAck()) {
                  System.out.println(portaReceptora + " removeu da transmissora "
                      + mensagemJanelaTransmissora.getNumeroSequencia() + " "
                      + mensagemJanelaTransmissora.getNumeroAck());
                  janelaTransmissora.remove(mensagemJanelaTransmissora);
                } else if (retransmissaoAckDuplicado == false
                    && mensagemJanelaTransmissora.deuTimeout) {
                  mensagemJanelaTransmissora.enviarMensagem(probabilidadeDeDescarte, timeout);
                }
              }
            }
          } catch (Exception e) {
            // TODO ignorar erro de concorrencia
            // e.printStackTrace();
          }
          if (transmissorAtivo == false) {
            analisaTransmissorAtivo = false;
          }
          try {
            Thread.sleep(TEMPO_CPU);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    analisaTransmissoes.start();
  }
}
