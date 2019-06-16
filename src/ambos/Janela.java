package ambos;

import java.util.PriorityQueue;
import java.util.Queue;

public class Janela {
	public int tamanhoMaximoJanela;
	public int tamanhoJanela=0;
	public Queue<Mensagem> queue = new PriorityQueue<Mensagem>();
	public Janela(int tamanhoMaximoJanela) {
		// TODO Auto-generated constructor stub
		this.tamanhoMaximoJanela=tamanhoMaximoJanela;
	}
}
