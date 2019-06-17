package cliente;

import ambos.Comunicacao;
import ambos.Config;
import ambos.Host;

public class Cliente implements Runnable {
	private Config config;
	private int tcp_maximum_segment_size;
	private String ipServidor;
	private int timeout;
	public Cliente(Config config, int tcp_maximum_segment_size, String ipServidor, int timeout) {
		this.config = config;
		this.tcp_maximum_segment_size = tcp_maximum_segment_size;
		this.ipServidor = ipServidor;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		try {
			Host host_servidor = new Host(ipServidor, config.porta_servidor);
			Comunicacao comunicacao = new Comunicacao(host_servidor, config.tamanhoJanela);
			ComunicacaoCliente comunicacaoCliente = new ComunicacaoCliente(comunicacao, config.nome_arquivo, tcp_maximum_segment_size, timeout);
			Thread cliente = new  Thread(comunicacaoCliente,"ComunicacaoCliente");
			cliente.start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
