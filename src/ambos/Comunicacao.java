package ambos;

import servidor.ComunicacaoServidor;
import utils.RandomInt;

public class Comunicacao {
	Config config;
	
	int tamanhoJanelaReceptor;

	boolean conexaoAtiva = true;

	int numero_ack=0;
	Janela janelaReceptora;
	int porta_receptora=RandomInt.getInt(6790, 20000);

	int numero_de_sequencia=RandomInt.getInt(0, 65535);
	Janela janelaTransmissora;
	Host host;
	
	
	public Comunicacao(Mensagem synRecebido, Config config) {
		numero_ack = synRecebido.pacoteTCP.segmentoTCP.getNumeroSequencia() + synRecebido.pacoteTCP.size();
		
		System.out.println("Servidor: cliente com a porta "+ ((int) synRecebido.pacoteTCP.segmentoTCP.getPortaOrigem()));
		
		this.host = new Host(synRecebido.host.endereco , (int) synRecebido.pacoteTCP.segmentoTCP.getPortaOrigem());
		
		this.config = config;

		this.janelaReceptora = new Janela(config.tamanhoJanela);
		while(inicializaReceptora()) {
			// garatindo porta ok
		}

		this.janelaTransmissora = new Janela(config.tamanhoJanela);
		inicializaTransmissora();
		
	}
	
	public Comunicacao(Config config) {
		this.config = config;
	}
	
	private boolean inicializaReceptora(){
		try {

			Thread receptor = new Thread() {
				@Override
				public void run() {
					System.out.printf("Servidor: recebeu ack do cliente e começou a escutar a porta %s\r\n",porta_receptora);					
					while(conexaoAtiva) {
						Mensagem mensagemRecebida = Mensagem.receberMensagem(porta_receptora,1480);
						if(mensagemRecebida.pacoteTCP.segmentoTCP.fin==1) {
							conexaoAtiva = false;
						}
						else {
							System.out.println("Servidor: recebeu uma mensagem \r\n"+mensagemRecebida.pacoteTCP.segmentoTCP.dados.toString());
							//Testando se pode adicionar mensagem na Janela
							if(janelaReceptora.tamanhoJanela+mensagemRecebida.pacoteTCP.size()<janelaReceptora.tamanhoMaximoJanela) {
								janelaReceptora.queue.add(mensagemRecebida);
								janelaReceptora.tamanhoJanela = janelaReceptora.tamanhoJanela+mensagemRecebida.pacoteTCP.size();
							}
						}
					}
				}
			};
			receptor.start();
			return true;
		}
		catch(Exception e) { // Porta já está em uso
			porta_receptora = RandomInt.getInt(6790, 20000); // nova porta
		}
		return false;
	}
	private void inicializaTransmissora() {
		
	}
}
