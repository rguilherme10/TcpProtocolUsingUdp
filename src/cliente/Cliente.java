package cliente;

import ambos.Comunicacao;
import ambos.Config;
import ambos.Host;

import java.io.File;

/**
 * Classe responsavel por receber a configuracao e iniciar a comunicacao transmissora.
 * 
 * @author ribamar
 *
 */
public class Cliente implements Runnable {
  private Config  config;
  private int     tcpMaximumSegmentSize;
  private String  enderecoIpDoServidor;
  private boolean retransmissaoAckDuplicado;
  private int     timeout;
  
  /**
   * Recebe informações para cliente.
   * 
   * @param config                    = parametros recebidos
   * @param tcpMaximumSegmentSize     = Tcp Maximum Segment Size
   * @param enderecoIpDoServidor      = endereco ip do servidor
   * @param retransmissaoAckDuplicado = deve ser um para usar retransmissão via
   *                                  ACKs duplicados e zero caso contrário
   * @param timeout                   = valor inicial de timeout para
   *                                  retransmiss~ao de um segmento em
   *                                  milisegundos
   */
  public Cliente(Config config, int tcpMaximumSegmentSize, String enderecoIpDoServidor,
      boolean retransmissaoAckDuplicado, int timeout) {
    
    this.config = config;
    this.tcpMaximumSegmentSize = tcpMaximumSegmentSize;
    this.enderecoIpDoServidor = enderecoIpDoServidor;
    this.retransmissaoAckDuplicado = retransmissaoAckDuplicado;
    this.timeout = timeout;
  }
  
  @Override
  public void run() {
    /*
     * Inicia comunicação cliente
     */
    try {
      
      File arquivo = new File(config.nomeArquivo);
      if (arquivo.exists()) {
        
        Host hostServidor = new Host(enderecoIpDoServidor, config.portaUdp);
        
        Comunicacao comunicacao = new Comunicacao(hostServidor, config.tamanhoJanela,
            tcpMaximumSegmentSize, retransmissaoAckDuplicado, timeout);
        
        ComunicacaoCliente comunicacaoCliente = new ComunicacaoCliente(comunicacao, arquivo);
        
        Thread cliente = new Thread(comunicacaoCliente);
        cliente.start();
        
      } else {
        throw new Exception("Erro: Arquivo não existe!");
      }
      
    } catch (Exception e) {
      // TODO Mostra erro ao iniciar comunicaçao
      e.printStackTrace();
    }
  }
  
}
