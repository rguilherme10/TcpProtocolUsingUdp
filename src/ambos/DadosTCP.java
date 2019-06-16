package ambos;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

//import org.apache.commons.lang3.SerializationUtils;

public class DadosTCP {
	public SegmentoTCP2 segmentoTCP = null;
	
	
	public DadosTCP(SegmentoTCP2 segmentoTCP) {
		// TODO Auto-generated constructor stub
		this.segmentoTCP = segmentoTCP;
	}

	public DadosTCP(int porta_origem, int porta_destino, int numero_de_sequencia, 
			int numero_ack, int ack, int syn, int fin, int janela_receptor, byte[] dados) {
		// TODO Auto-generated constructor stub
		this.segmentoTCP = new SegmentoTCP2((char) porta_origem, (char) porta_destino, numero_de_sequencia, numero_ack, 
				(byte) ack, (byte) syn, (byte) fin, (char) janela_receptor, dados);
	}
	public DadosTCP(byte[] segmentoTCP) {
		// TODO Auto-generated constructor stub
		
		//this.segmentoTCP = (SegmentoTCP2) SerializationUtils.deserialize(segmentoTCP);
		
		/*ByteArrayInputStream bis = new ByteArrayInputStream(segmentoTCP);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  Object retorno = in.readObject();
		  this.segmentoTCP = (SegmentoTCP2) retorno;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (Exception ex) {
		    // ignore close exception
		  }
		}*/
		
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
		
		byte[] dados = new byte[segmentoTCP.length-offset];
		System.arraycopy(segmentoTCP, offset, dados, 0, dados.length);
		
		this.segmentoTCP = new SegmentoTCP2(bf_porta_origem.getChar(), bf_porta_destino.getChar(), bf_numero_de_sequencia.getInt(), bf_numero_ack.getInt(), 
				bf_ack.get(), bf_syn.get(), bf_fin.get(), bf_janela_receptor.getChar(), dados);
	}
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		//return SerializationUtils.serialize(this.segmentoTCP);

		ByteBuffer bf_retorno = ByteBuffer.allocate(17 + this.size());
		bf_retorno.putChar(this.segmentoTCP.getPortaOrigem());
		bf_retorno.putChar(this.segmentoTCP.getPortaDestino());
		bf_retorno.putInt(this.segmentoTCP.getNumeroSequencia());
		bf_retorno.putInt(this.segmentoTCP.getNumeroACK());
		bf_retorno.put(this.segmentoTCP.getACK());
		bf_retorno.put(this.segmentoTCP.getSYN());
		bf_retorno.put(this.segmentoTCP.getFIN());
		bf_retorno.putChar(this.segmentoTCP.getJanelaReceptor());
		if(this.size()>0)
			bf_retorno.put(this.segmentoTCP.dados);
		
		return bf_retorno.array();
		/*ByteBuffer bf_porta_origem = ByteBuffer.allocate(2);
		bf_porta_origem.putChar(this.segmentoTCP.getPortaOrigem());
		byte[] porta_origem = bf_porta_origem.array();
		
		ByteBuffer bf_porta_destino = ByteBuffer.allocate(2);
		bf_porta_destino.putChar(this.segmentoTCP.getPortaDestino());
		byte[] porta_destino = bf_porta_destino.array();

		ByteBuffer bf_numero_de_sequencia = ByteBuffer.allocate(4);
		bf_numero_de_sequencia.putInt(this.segmentoTCP.getNumeroSequencia());
		byte[] numero_de_sequencia = bf_numero_de_sequencia.array();
		
		ByteBuffer bf_numero_ack = ByteBuffer.allocate(4);
		bf_numero_ack.putInt(this.segmentoTCP.getNumeroACK());
		byte[] numero_ack = bf_numero_ack.array();
		
		ByteBuffer bf_ack = ByteBuffer.allocate(1);
		bf_ack.put(this.segmentoTCP.getACK());
		byte[] ack = bf_ack.array();
		
		ByteBuffer bf_syn = ByteBuffer.allocate(1);
		bf_syn.put(this.segmentoTCP.getSYN());
		byte[] syn = bf_syn.array();
		
		ByteBuffer bf_fin = ByteBuffer.allocate(1);
		bf_fin.put(this.segmentoTCP.getFIN());
		byte[] fin = bf_fin.array();
		
		ByteBuffer bf_janela_receptor = ByteBuffer.allocate(2);
		bf_janela_receptor.putChar(this.segmentoTCP.getJanelaReceptor());
		byte[] janela_receptor = bf_janela_receptor.array();

		int offset=0;
		byte[] retorno = new byte[20 + this.size()];
		System.arraycopy(porta_origem, 0, retorno, offset, porta_origem.length);
		offset+=porta_origem.length;
		System.arraycopy(porta_destino, 0, retorno, offset, porta_destino.length);
		offset+=porta_destino.length;
		System.arraycopy(numero_de_sequencia, 0, retorno, offset, numero_de_sequencia.length);
		offset+=numero_de_sequencia.length;
		System.arraycopy(numero_ack, 0, retorno, offset, numero_ack.length);
		offset+=numero_ack.length;
		System.arraycopy(ack, 0, retorno, offset, ack.length);
		offset+=ack.length;
		System.arraycopy(syn, 0, retorno, offset, syn.length);
		offset+=syn.length;
		System.arraycopy(fin, 0, retorno, offset, fin.length);
		offset+=fin.length;
		System.arraycopy(janela_receptor, 0, retorno, offset, janela_receptor.length);
		offset+=janela_receptor.length;
		if(this.size()>0)
			System.arraycopy(this.segmentoTCP.dados, 0, retorno, offset, this.size());
		return retorno;
		*/
	}
	public int size() {
		// TODO Auto-generated method stub
		if(segmentoTCP.dados==null)
			return 0;
		else
			return segmentoTCP.dados.length;
	}
	
	public byte[] toBytes2(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] retorno = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(segmentoTCP);
		  out.flush();
		  retorno = bos.toByteArray();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  try {
		    bos.close();
		  } catch (Exception ex) {
		    // ignore close exception
		  }
		}
		return retorno;
	}
}
