package ambos;

import java.io.Serializable;

/**
 * Classe do segmentoTcp.
 * 
 * @author ribamar
 * @version 1.0.0.0
 */
public class SegmentoTcp implements Serializable {
  
  private static final long serialVersionUID   = 1328266880429121045L;
  public char               portaOrigem;
  public char               portaDestino;
  public int                numeroDeSequencia  = 0;
  public int                numeroAck          = 0;
  public byte               tamanhoDoCabecalho = 5;
  public int                semUso             = 0;
  public byte               urg                = 0;
  public byte               ack                = 0;
  public byte               psh                = 0;
  public byte               rst                = 0;
  public byte               syn                = 0;
  public byte               fin                = 0;
  public char               janelaReceptor;
  public char               checksum;
  public int                urgentPointer;
  public byte[]             options;
  public int                padding;
  public byte[]             dados;
  
  /**
   * Construtor.
   * 
   * @param portaOrigem        = portaOrigem
   * @param portaDestino       = portaDestino
   * @param numeroDeSequencia  = numeroDeSequencia
   * @param numeroAck = numeroAck
   * @param tamanhoDoCabecalho = tamanhoDoCabecalho
   * @param semUso = semUso
   * @param urg = urg
   * @param ack = ack
   * @param psh = psh
   * @param rst = rst
   * @param syn = syn
   * @param fin = fin
   * @param janelaReceptor = janelaReceptor
   * @param checksum = checksum
   * @param urgentPointer = urgentPointer
   * @param options = options
   * @param padding = padding
   * @param dados = dados
   */
  public SegmentoTcp(char portaOrigem, char portaDestino, int numeroDeSequencia, int numeroAck,
      byte tamanhoDoCabecalho, int semUso, byte urg, byte ack, byte psh, byte rst, byte syn,
      byte fin, char janelaReceptor, char checksum, int urgentPointer, byte[] options, int padding,
      byte[] dados) {
    this.portaOrigem = portaOrigem;
    this.portaDestino = portaDestino;
    this.numeroDeSequencia = numeroDeSequencia;
    this.numeroAck = numeroAck;
    this.tamanhoDoCabecalho = tamanhoDoCabecalho;
    this.semUso = semUso;
    this.urg = urg;
    this.ack = ack;
    this.psh = psh;
    this.rst = rst;
    this.syn = syn;
    this.fin = fin;
    this.janelaReceptor = janelaReceptor;
    this.checksum = checksum;
    this.urgentPointer = urgentPointer;
    this.options = options;
    this.padding = padding;
    this.dados = dados;
  }
}
