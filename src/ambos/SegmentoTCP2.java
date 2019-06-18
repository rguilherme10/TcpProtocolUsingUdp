package ambos;

/**
 * Classe do segmentoTcp.
 * 
 * @author ribamar
 * @version 2.0.0.0
 */
public class SegmentoTcp2 {
  
  public char   portaOrigem;
  public char   portaDestino;
  public int    numeroDeSequencia;
  public int    numeroAck;
  public byte   ack;
  public byte   syn;
  public byte   fin;
  public char   tamanhoJanelaReceptor;
  public int    tamanhoDados;
  public byte[] dados = null;
  
  /**
   * Construtor.
   * 
   * @param portaOrigem           = portaOrigem
   * @param portaDestino          = portaDestino
   * @param numeroDeSequencia     = numeroDeSequencia
   * @param numeroAck             = numeroAck
   * @param ack                   = ack
   * @param syn                   = syn
   * @param fin                   = fin
   * @param tamanhoJanelaReceptor = tamanhoJanelaReceptor
   * @param tamanhoDados          = tamanhoDados
   */
  public SegmentoTcp2(char portaOrigem, char portaDestino, int numeroDeSequencia, int numeroAck,
      byte ack, byte syn, byte fin, char tamanhoJanelaReceptor, int tamanhoDados) {
    // TODO Auto-generated constructor stub
    super();
    this.portaOrigem = portaOrigem;
    this.portaDestino = portaDestino;
    this.numeroDeSequencia = numeroDeSequencia;
    this.numeroAck = numeroAck;
    this.ack = ack;
    this.syn = syn;
    this.fin = fin;
    this.tamanhoJanelaReceptor = tamanhoJanelaReceptor;
    this.tamanhoDados = tamanhoDados;
    this.dados = new byte[tamanhoDados];
  }
  
  public char getPortaOrigem() {
    return (char) this.portaOrigem;
  }
  
  public char getPortaDestino() {
    return (char) this.portaDestino;
  }
  
  public int getNumeroSequencia() {
    return this.numeroDeSequencia;
  }
  
  public int getNumeroAck() {
    return this.numeroAck;
  }
  
  public byte getAck() {
    return (byte) this.ack;
  }
  
  public byte getSyn() {
    return (byte) this.syn;
  }
  
  public byte getFin() {
    return (byte) this.fin;
  }
  
  public char getTamanhoJanelaReceptor() {
    return (char) this.tamanhoJanelaReceptor;
  }
  
  public int getTamanhoDados() {
    return (int) this.tamanhoDados;
  }
}
