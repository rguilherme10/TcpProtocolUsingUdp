package ambos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe que controla e encapsula o Pacote.
 * 
 * @author ribamar
 *
 */
public class Mensagem implements Comparable<Mensagem> {
  public Host    host;
  private Pacote pacote;
  public boolean enviada     = false;
  public boolean deuTimeout  = false;
  public boolean retornouAck = false;
  
  /**
   * Recebe o host do destinario e o pacote com o conteudo da mensagem.
   * 
   * @param host   = Host do destinatario
   * @param pacote = Pacote da mensagem
   */
  public Mensagem(Host host, Pacote pacote) {
    this.host = host;
    this.pacote = pacote;
  }
  
  public int getPortaOrigem() {
    return pacote.segmento.getPortaOrigem();
  }
  
  public int getNumeroSequencia() {
    return pacote.segmento.getNumeroSequencia();
  }
  
  public int getNumeroAck() {
    return pacote.segmento.getNumeroAck();
  }
  
  public int getTamanhoJanelaReceptor() {
    return pacote.segmento.getTamanhoJanelaReceptor();
  }
  
  public byte[] getDados() {
    return pacote.segmento.dados;
  }
  
  /**
   * Verifica controle do pacote.
   * 
   * @return true se é um pacote SYN
   */
  public boolean isSyn() {
    try {
      return (pacote.segmento.syn == 1 && pacote.segmento.ack == 0 && pacote.segmento.fin == 0);
    } catch (Exception e) {
      // TODO ignorar, pois pacote pode ser null
    }
    return false;
  }
  
  public boolean isSynAck() {
    return (pacote.segmento.syn == 1 && pacote.segmento.ack == 1 && pacote.segmento.fin == 0);
  }
  
  public boolean isAck() {
    return (pacote.segmento.syn == 0 && pacote.segmento.ack == 1 && pacote.segmento.fin == 0);
  }
  
  public boolean isFin() {
    return (pacote.segmento.syn == 0 && pacote.segmento.ack == 0 && pacote.segmento.fin == 1);
  }
  
  public boolean isFinAck() {
    return (pacote.segmento.syn == 0 && pacote.segmento.ack == 1 && pacote.segmento.fin == 1);
  }
  
  /**
   * Verifica controle do pacote.
   * 
   * @return true se é um pacote SYN, SYN+ACK, FIN, FIN+ACK ou ACK
   */
  public boolean isControle() {
    try {
      return (pacote.segmento.syn == 1 || pacote.segmento.ack == 1 || pacote.segmento.fin == 1);
    } catch (Exception e) {
      // TODO ignorar, pois pacote pode ser null
    }
    return false;
  }
  
  /**
   * Retorna tamanho completo do pacote.
   * 
   * @return tamanho do pacote considerando HEAD e dados
   */
  public int size() {
    if (this.isControle()) {
      return 21;
    } else {
      return 21 + pacote.size();
    }
  }
  
  public int getCalculaAck() {
    return this.getNumeroSequencia() + size();
  }
  
  /**
   * Retorna mensagem quando chegar na porta informada.
   * 
   * @param porta                 = porta a ser escutada
   * @param tcpMaximumSegmentSize = tamanho maximo dos dados
   * @return Mensagem
   */
  public static Mensagem receberMensagem(int porta, int tcpMaximumSegmentSize) {
    Mensagem retorno = null;
    DatagramSocket serverSocket = null;
    try {
      serverSocket = new DatagramSocket(porta);
      byte[] receiveData = new byte[21 + tcpMaximumSegmentSize];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, 21 + tcpMaximumSegmentSize);
      serverSocket.receive(receivePacket);
      byte[] pacote = new byte[21];
      System.arraycopy(receivePacket.getData(), 0, pacote, 0, pacote.length);
      Pacote pacoteTcp = new Pacote(pacote);
      if (pacoteTcp.size() > 0) {
        pacoteTcp.segmento.dados = new byte[pacoteTcp.size()];
        System.arraycopy(receivePacket.getData(), Pacote.headerSize(), pacoteTcp.segmento.dados, 0,
            pacoteTcp.segmento.dados.length);
      }
      Host host = new Host(receivePacket.getAddress(), receivePacket.getPort());
      retorno = new Mensagem(host, pacoteTcp);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (serverSocket != null) {
        serverSocket.close();
      }
    }
    return retorno;
  }
  
  /**
   * Envia mensagem.
   * 
   * @param probabilidadeDescarte = 1 para 100% dos pacotes serem descartados
   * @param timeout               = tempo em milisegundos para considerar que
   *                              mensagem deu timeout
   */
  public void enviarMensagem(double probabilidadeDescarte, int timeout) {
    
    deuTimeout = false;
    
    if (Math.random() > probabilidadeDescarte) {
      DatagramSocket datagramSocket = null;
      try {
        datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(pacote.toBytes(),
            Pacote.headerSize() + pacote.size(), host.endereco, host.porta);
        datagramSocket.send(datagramPacket);
      } catch (Exception e) {
        // TODO Mostra erro ao enviar mensagem
        e.printStackTrace();
      } finally {
        if (datagramSocket != null) {
          datagramSocket.close();
        }
      }
      
    }
    
    this.enviada = true;
    
    if (timeout > 0) {
      
      final Timer timerTimeout = new Timer("timerTimeout" + getNumeroSequencia());
      timerTimeout.schedule(new TimerTask() {
        @Override
        public void run() {
          
          deuTimeout = true;
          
          try {
            timerTimeout.cancel();
          } catch (Exception e) {
            // TODO ignora erro ao finalizar o timer do timeout
          }
          
        }
      }, timeout);
      
    }
  }
  
  @Override
  public int compareTo(Mensagem o) {
    if (this.getCalculaAck() == o.getCalculaAck()) {
      return 1;
    } else {
      return 0;
    }
  }
}
