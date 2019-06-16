package ambos;

import java.io.Serializable;

public class SegmentoTCP implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1328266880429121045L;
	public char porta_origem;
	public char porta_destino;
	public int numero_de_sequencia = 0;
	public int numero_ack = 0;
	public byte tamanho_do_cabecalho = 5;
	public int sem_uso = 0;
	public byte urg = 0;
	public byte ack = 0;
	public byte psh = 0;
	public byte rst = 0;
	public byte syn = 0;
	public byte fin = 0;
	public char janela_receptor;
	public char checksum;
	public int urgentPointer;
	public byte[] options;
	public int padding;
	public byte[] dados;
	public SegmentoTCP(char porta_origem, char porta_destino, int numero_de_sequencia, int numero_ack, byte tamanho_do_cabecalho, int sem_uso, byte urg, byte ack, byte psh, byte rst, byte syn, byte fin, char janela_receptor, char checksum, int urgentPointer, byte[] options, int padding, byte[] dados) {
		this.porta_origem = porta_origem;
		this.porta_destino = porta_destino;
		this.numero_de_sequencia = numero_de_sequencia;
		this.numero_ack = numero_ack;
		this.tamanho_do_cabecalho = tamanho_do_cabecalho;
		this.sem_uso = sem_uso;
		this.urg = urg;
		this.ack = ack;
		this.psh = psh;
		this.rst = rst;
		this.syn = syn;
		this.fin = fin;
		this.janela_receptor = janela_receptor;
		this.checksum = checksum;
		this.urgentPointer = urgentPointer;
		this.options = options;
		this.padding = padding;
		this.dados = dados;
	}
}
