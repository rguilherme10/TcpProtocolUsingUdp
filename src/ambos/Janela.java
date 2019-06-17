package ambos;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Janela {
	private int tamanhoJanela=1500000;
	//private Queue<Mensagem> queue = new PriorityQueue<Mensagem>();
	private TreeMap<Integer, Mensagem> queue = new TreeMap<>();
	public Janela(int tamanhoJanela) {
		// TODO Auto-generated constructor stub
		this.tamanhoJanela=tamanhoJanela;
	}
	public void setTamanhoJanela(int tamanhoJanela) {
		this.tamanhoJanela = tamanhoJanela;
	}
	public int getEspacoLivre() {
		// TODO Auto-generated method stub
		return tamanhoJanela;
	}
	public void aumentaTamanhoJanela(int incremento) {
		tamanhoJanela = tamanhoJanela + incremento;
	}
	
	public void diminuiTamanhoJanela(int decremento) {
		tamanhoJanela = tamanhoJanela - decremento;
	}
	
	public boolean cabeMensagem(Mensagem mensagem) {
		return (tamanhoJanela > mensagem.size());
	}
	public boolean contem(Mensagem mensagem) {
		return queue.containsKey(mensagem.getCalculaACK());
	}
	public boolean contem(int calculaACK) {
		return queue.containsKey(calculaACK);
	}
	public Mensagem getMensagem(int calculaACK) {
		if(contem(calculaACK)) {
			return queue.get(calculaACK);	
		}
		return null;
	}
	public ArrayList<Mensagem> getIterator(){
		ArrayList<Mensagem> retorno = new ArrayList<>();
		for(Map.Entry<Integer, Mensagem> entry : queue.entrySet()) {
			retorno.add(entry.getValue());
		}
		return retorno;
	}
	/*public Iterator<Mensagem> getIterator() {
		return queue.iterator();
	}*/
	public boolean add(Mensagem mensagem) {
		if(cabeMensagem(mensagem) && contem(mensagem)==false) {
			queue.put(mensagem.getCalculaACK(), mensagem);
			diminuiTamanhoJanela(mensagem.size());
			return true;
		}
		else {
			return false;
		}
	}
	public void remove(Mensagem mensagemJanelaReceptora) {
		queue.remove(mensagemJanelaReceptora.getCalculaACK());
		aumentaTamanhoJanela(mensagemJanelaReceptora.size());
	}
	public boolean temMensagens() {
		return (queue.size()>0);
	}
}