package ambos;


public class SegmentoTCP2{

	/*
	private static final long serialVersionUID = -6525645738473543250L;
	private final int porta_origem;
	private final int porta_destino;
	private final int numero_de_sequencia;
	private final int numero_ack;
	private final int ack;
	private final int syn;
	private final int fin;
	private final int janela_receptor;*/
	/*
	 * 
	 */
	public char porta_origem;
	public char porta_destino;
	public int numero_de_sequencia;
	public int numero_ack;
	public byte ack;
	public byte syn;
	public byte fin;
	public char janela_receptor;
	public byte[] dados = null;
	public SegmentoTCP2(char porta_origem, char porta_destino, int numero_de_sequencia, int numero_ack,
			byte ack, byte syn, byte fin, char janela_receptor, byte[] dados) {
		// TODO Auto-generated constructor stub
		super();
		this.porta_origem = porta_origem;
		this.porta_destino = porta_destino;
		this.numero_de_sequencia = numero_de_sequencia;
		this.numero_ack = numero_ack;
		this.ack = ack;
		this.syn = syn;
		this.fin = fin;
		this.janela_receptor = janela_receptor;
		this.dados = dados;	
	}
	public char getPortaOrigem() {
		return (char) this.porta_origem;
	}
	public char getPortaDestino() {
		return (char) this.porta_destino;
	}
	public int getNumeroSequencia() {
		return this.numero_de_sequencia;
	}
	public int getNumeroACK() {
		return this.numero_ack;
	}
	public byte getACK() {
		return (byte) this.ack;
	}
	public byte getSYN() {
		return (byte) this.syn;
	}
	public byte getFIN() {
		return (byte) this.fin;
	}
	public char getJanelaReceptor() {
		return (char) this.janela_receptor;
	}
}
