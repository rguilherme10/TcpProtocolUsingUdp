package ambos;

import java.nio.ByteBuffer;

/**
 * Classe armazena e gerencia segmento.
 * 
 * @author ribamar
 * @version 2.0.0.0
 */
public class Pacote2 {
  public SegmentoTcp3 segmento = null;
  
  public Pacote2(SegmentoTcp3 segmento) {
    // TODO Auto-generated constructor stub
    this.segmento = segmento;
  }
  
  /**
   * Cria segmento com os parametros recebidos.
   * 
   * @param portaOrigem           = portaOrigem
   * @param portaDestino          = portaDestino
   * @param numeroDeSequencia     = numeroDeSequencia
   * @param numeroAck             = numeroAck
   * @param tamanhoJanelaReceptor = tamanhoJanelaReceptor
   * @param dados                 = dados
   */
  public Pacote2(char portaOrigem, char portaDestino, int numeroDeSequencia, int numeroAck,
      char dataOffsetAndReservedAndControlBits, char tamanhoJanelaReceptor, char checksum,
      char urgentPointer, byte[] optionsAndPadding, byte[] dados) {
    // TODO Auto-generated constructor stub
    this.segmento = new SegmentoTcp3((char) portaOrigem, (char) portaDestino, numeroDeSequencia,
        numeroAck, (char) dataOffsetAndReservedAndControlBits, (char) tamanhoJanelaReceptor,
        (char) checksum, (char) urgentPointer, optionsAndPadding, dados);
  }
  
  /**
   * Em desenvolvimento.
   */
  public Pacote2(int portaOrigem, int portaDestino, int numeroDeSequencia, int numeroAck,
      int dataOffsetAndReservedAndControlBits, int tamanhoJanelaReceptor, int checksum,
      int urgentPointer, byte[] optionsAndPadding, byte[] dados) {
    // TODO Auto-generated constructor stub
    this.segmento = new SegmentoTcp3((char) portaOrigem, (char) portaDestino, numeroDeSequencia,
        numeroAck, (char) dataOffsetAndReservedAndControlBits, (char) tamanhoJanelaReceptor,
        (char) checksum, (char) urgentPointer, optionsAndPadding, dados);
  }
  
  /**
   * Em desenvolvimento.
   */
  public Pacote2(char portaOrigem, char portaDestino, int numeroDeSequencia, int numeroAck, int mss,
      char tamanhoJanelaReceptor, char checksum, char urgentPointer, byte[] optionsAndPadding,
      byte[] dados) {
    ByteBuffer bufferRetorno = ByteBuffer.allocate(4);
    bufferRetorno.putInt(mss);
    this.segmento = new SegmentoTcp3(portaOrigem, portaDestino, numeroDeSequencia, numeroAck,
        (char) (24576 + 2), tamanhoJanelaReceptor, (char) 0, (char) 0, bufferRetorno.array(),
        new byte[0]);
  }
  
  /**
   * cria segmento com os bytes recebidos.
   * 
   * @param segmento = segmento em bytes
   */
  @SuppressWarnings("unused")
  public Pacote2(byte[] segmento) {
    int offset = 0;
    
    byte[] portaOrigem = new byte[2];
    System.arraycopy(segmento, offset, portaOrigem, 0, portaOrigem.length);
    offset += portaOrigem.length;
    
    byte[] portaDestino = new byte[2];
    System.arraycopy(segmento, offset, portaDestino, 0, portaDestino.length);
    offset += portaDestino.length;
    
    byte[] numeroDeSequencia = new byte[4];
    System.arraycopy(segmento, offset, numeroDeSequencia, 0, numeroDeSequencia.length);
    offset += numeroDeSequencia.length;
    
    byte[] numeroAck = new byte[4];
    System.arraycopy(segmento, offset, numeroAck, 0, numeroAck.length);
    offset += numeroAck.length;
    
    byte[] ack = new byte[1];
    System.arraycopy(segmento, offset, ack, 0, ack.length);
    offset += ack.length;
    
    byte[] syn = new byte[1];
    System.arraycopy(segmento, offset, syn, 0, syn.length);
    offset += syn.length;
    
    byte[] fin = new byte[1];
    System.arraycopy(segmento, offset, fin, 0, fin.length);
    offset += fin.length;
    
    byte[] tamanhoJanelaReceptor = new byte[2];
    System.arraycopy(segmento, offset, tamanhoJanelaReceptor, 0, tamanhoJanelaReceptor.length);
    offset += tamanhoJanelaReceptor.length;
    
    ByteBuffer bufferPortaOrigem = ByteBuffer.wrap(portaOrigem);
    ByteBuffer bufferPortaDestino = ByteBuffer.wrap(portaDestino);
    ByteBuffer bufferNumeroDeSequencia = ByteBuffer.wrap(numeroDeSequencia);
    ByteBuffer bufferNumeroAck = ByteBuffer.wrap(numeroAck);
    ByteBuffer bufferAck = ByteBuffer.wrap(ack);
    ByteBuffer bufferSyn = ByteBuffer.wrap(syn);
    ByteBuffer bufferFin = ByteBuffer.wrap(fin);
    ByteBuffer bufferTamanhoJanelaReceptor = ByteBuffer.wrap(tamanhoJanelaReceptor);
    
    byte[] tamanhoDados = new byte[4];
    System.arraycopy(segmento, offset, tamanhoDados, 0, tamanhoDados.length);
    offset += tamanhoDados.length;
    ByteBuffer bufferTamanhoDados = ByteBuffer.wrap(tamanhoDados);
  }
  
  public static int headerSize() {
    return 20;
  }
  
  /**
   * Transforma segmento em bytes.
   * 
   * @return segmento em bytes
   */
  public byte[] toBytes() {
    
    ByteBuffer bufferRetorno = ByteBuffer.allocate(Pacote2.headerSize() + this.size());
    bufferRetorno.putChar(segmento.getPortaOrigem());
    bufferRetorno.putChar(segmento.getPortaDestino());
    bufferRetorno.putInt(segmento.getNumeroSequencia());
    bufferRetorno.putInt(segmento.getNumeroAck());
    bufferRetorno.putChar(segmento.getTamanhoJanelaReceptor());
    
    return bufferRetorno.array();
  }
  
  public int size() {
    return segmento.size();
  }
}
