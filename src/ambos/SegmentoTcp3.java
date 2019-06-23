package ambos;


/**
 * Classe do segmentoTcp.
 * 
 * @author ribamar
 * @version 3.0.0.0
 */
public class SegmentoTcp3 {
  
  private char portaOrigem;
  private char portaDestino;
  private int  numeroDeSequencia;
  private int  numeroAck;
  /*
   * Data Offset: 4 bits No-option = 0101 = 5
   * 
   * Reserved: 6 bits 000000
   * 
   * Control bits: 6 bits ACK = 010000 = 16 SYN = 000010 = 2 SYN + ACK = 010010 =
   * 18 FIN = 000001 = 1
   * 
   * dataOffsetReservedControlBits: No-option + Reserved = 0101000000000000 =
   * 20480 Option MSS + Reserved = 0110000000000000 = 24576
   */
  private char   dataOffsetAndReservedAndControlBits;
  private char   tamanhoJanelaReceptor;
  private char   checksum          = 0;
  private char   urgentPointer     = 0;
  private byte[] optionsAndPadding = new byte[0];
  private byte[] dados             = new byte[0];
  
  /**
   * Construtor.
   * 
   * @param portaOrigem                         = portaOrigem
   * @param portaDestino                        = portaDestino
   * @param numeroDeSequencia                   = numeroDeSequencia
   * @param numeroAck                           = numeroAck
   * @param dataOffsetAndReservedAndControlBits = dataOffsetReservedControlBits
   * @param tamanhoJanelaReceptor               = tamanhoJanelaReceptor
   * @param checksum                            = checksum
   * @param urgentPointer                       = urgentPointer
   * @param optionsAndPadding                   = optionsAndPadding
   */
  public SegmentoTcp3(char portaOrigem, char portaDestino, int numeroDeSequencia, int numeroAck,
      char dataOffsetAndReservedAndControlBits, char tamanhoJanelaReceptor, char checksum,
      char urgentPointer, byte[] optionsAndPadding, byte[] dados) {
    
    this.portaOrigem = portaOrigem;
    this.portaDestino = portaDestino;
    this.numeroDeSequencia = numeroDeSequencia;
    this.numeroAck = numeroAck;
    this.dataOffsetAndReservedAndControlBits = dataOffsetAndReservedAndControlBits;
    this.tamanhoJanelaReceptor = tamanhoJanelaReceptor;
    this.checksum = checksum;
    this.urgentPointer = urgentPointer;
    this.optionsAndPadding = optionsAndPadding;
    this.dados = dados;
    
  }
  
  public char getPortaOrigem() {
    return this.portaOrigem;
  }
  
  public char getPortaDestino() {
    return this.portaDestino;
  }
  
  public int getNumeroSequencia() {
    return this.numeroDeSequencia;
  }
  
  public int getNumeroAck() {
    return this.numeroAck;
  }
  
  public char getTamanhoJanelaReceptor() {
    return this.tamanhoJanelaReceptor;
  }
  
  public char getCheckSum() {
    return this.checksum;
  }
  
  public char getUrgentPointer() {
    return this.urgentPointer;
  }
  
  public byte[] getOptions() {
    return this.optionsAndPadding;
  }
  
  public boolean haveNotOptions() {
    return (this.dataOffsetAndReservedAndControlBits / 64) == 20480; 
  }
  
  public boolean optionsSize() {
    return (this.dataOffsetAndReservedAndControlBits / 64) == 20480; 
  }
  
  public boolean isAck() {
    return (this.dataOffsetAndReservedAndControlBits & 16) > 0;
  }
  
  public boolean isSyn() {
    return (this.dataOffsetAndReservedAndControlBits & 2) > 0;
  }
  
  public boolean isFin() {
    return (this.dataOffsetAndReservedAndControlBits & 1) > 0;
  }
  
  @Deprecated
  public boolean isMss() {
    return true;//(this.dataOffsetAndReservedAndControlBits & 8192) > 0;
  }
  
  /**
   * Retorna a quantidade de bytes do dados.
   * 
   * @return tamanho do dados
   */
  public int size() {
    return dados.length;
  }
}
