package ambos;

import java.nio.ByteBuffer;

//import org.apache.commons.lang3.SerializationUtils;

public class Pacote {
	public SegmentoTCP2 segmentoTCP = null;
	
	
	public Pacote(SegmentoTCP2 segmentoTCP) {
		// TODO Auto-generated constructor stub
		this.segmentoTCP = segmentoTCP;
	}

	public Pacote(int porta_origem, int porta_destino, int numero_de_sequencia, 
			int numero_ack, int ack, int syn, int fin, int tamanho_janela_receptor, int tamanho_dados, byte[] dados) {
		// TODO Auto-generated constructor stub
		this.segmentoTCP = new SegmentoTCP2((char) porta_origem, (char) porta_destino, numero_de_sequencia, numero_ack, 
				(byte) ack, (byte) syn, (byte) fin, (char) tamanho_janela_receptor, tamanho_dados, dados);
	}
	public Pacote(byte[] segmentoTCP) {
		int offset=0;
		
		byte[] porta_origem = new byte[2];
		System.arraycopy(segmentoTCP, offset, porta_origem, 0, porta_origem.length);
		offset+=porta_origem.length;
		ByteBuffer bf_porta_origem = ByteBuffer.wrap(porta_origem);
		
		byte[] porta_destino = new byte[2];
		System.arraycopy(segmentoTCP, offset, porta_destino, 0, porta_destino.length);
		offset+=porta_destino.length;
		ByteBuffer bf_porta_destino = ByteBuffer.wrap(porta_destino);
		
		byte[] numero_de_sequencia = new byte[4];
		System.arraycopy(segmentoTCP, offset, numero_de_sequencia, 0, numero_de_sequencia.length);
		offset+=numero_de_sequencia.length;
		ByteBuffer bf_numero_de_sequencia = ByteBuffer.wrap(numero_de_sequencia);
		
		byte[] numero_ack = new byte[4];
		System.arraycopy(segmentoTCP, offset, numero_ack, 0, numero_ack.length);
		offset+=numero_ack.length;
		ByteBuffer bf_numero_ack = ByteBuffer.wrap(numero_ack);
		
		byte[] ack = new byte[1];
		System.arraycopy(segmentoTCP, offset, ack, 0, ack.length);
		offset+=ack.length;
		ByteBuffer bf_ack = ByteBuffer.wrap(ack);
		
		byte[] syn = new byte[1];
		System.arraycopy(segmentoTCP, offset, syn, 0, syn.length);
		offset+=syn.length;
		ByteBuffer bf_syn = ByteBuffer.wrap(syn);
		
		byte[] fin = new byte[1];
		System.arraycopy(segmentoTCP, offset, fin, 0, fin.length);
		offset+=fin.length;
		ByteBuffer bf_fin = ByteBuffer.wrap(fin);
		
		byte[] janela_receptor = new byte[2];
		System.arraycopy(segmentoTCP, offset, janela_receptor, 0, janela_receptor.length);
		offset+=janela_receptor.length;
		ByteBuffer bf_janela_receptor = ByteBuffer.wrap(janela_receptor);

		byte[] tamanho_dados = new byte[4];
		System.arraycopy(segmentoTCP, offset, tamanho_dados, 0, tamanho_dados.length);
		offset+=tamanho_dados.length;
		ByteBuffer bf_tamanho_dados = ByteBuffer.wrap(tamanho_dados);

		
		byte[] dados = null;
		int tamanhoDados = bf_tamanho_dados.getInt();
		if(tamanhoDados>0) {
			dados = new byte[segmentoTCP.length-offset];
			System.arraycopy(segmentoTCP, offset, dados, 0, dados.length);
		}
		this.segmentoTCP = new SegmentoTCP2(bf_porta_origem.getChar(), bf_porta_destino.getChar(), bf_numero_de_sequencia.getInt(), bf_numero_ack.getInt(), 
				bf_ack.get(), bf_syn.get(), bf_fin.get(), bf_janela_receptor.getChar(), tamanhoDados, dados);
	}
	public int headerSize() {
		return 21;
	}
	public byte[] toBytes() {
		ByteBuffer bf_retorno = ByteBuffer.allocate(this.headerSize() + this.size());
		bf_retorno.putChar(this.segmentoTCP.getPortaOrigem());
		bf_retorno.putChar(this.segmentoTCP.getPortaDestino());
		bf_retorno.putInt(this.segmentoTCP.getNumeroSequencia());
		bf_retorno.putInt(this.segmentoTCP.getNumeroACK());
		bf_retorno.put(this.segmentoTCP.getACK());
		bf_retorno.put(this.segmentoTCP.getSYN());
		bf_retorno.put(this.segmentoTCP.getFIN());
		bf_retorno.putChar(this.segmentoTCP.getTamanhoJanelaReceptor());
		bf_retorno.putInt(this.segmentoTCP.getTamanhoDados());
		if(this.size()>0)
			bf_retorno.put(this.segmentoTCP.dados);
		
		return bf_retorno.array();
	}
	public int size() {
		return this.segmentoTCP.getTamanhoDados();
	}
}
