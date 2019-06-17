package servidor;

import ambos.Comunicacao;
import ambos.Config;
import ambos.Mensagem;

public class Servidor implements Runnable {
	private Config config;
	private int tcp_maximum_segment_size = 1480;
	
	public Servidor(Config config) {
		this.config = config;
	}

	@Override
	public void run() {
		while(true) {
			Mensagem synRecebido = Mensagem.receberMensagem(config.porta_servidor, tcp_maximum_segment_size);
			if(synRecebido.isSyn()) {
				Comunicacao comunicacao = new Comunicacao(synRecebido, config.tamanhoJanela);
				ComunicacaoServidor comunicacaoServidor = new ComunicacaoServidor(comunicacao, config.nome_arquivo);
				Thread servidor = new  Thread(comunicacaoServidor,"ComunicacaoServidor");
				servidor.start();
			}
		}
	}

}
