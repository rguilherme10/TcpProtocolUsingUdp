package cliente;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Random;

import ambos.DadosTCP;
import ambos.Janela;
import ambos.Mensagem;

public class ComunicacaoCliente implements Runnable {
	ClienteConfig clienteConfig;
	int cliente_porta;
	int numero_de_sequencia;
	int numero_ack;
	
	boolean conexaoAtiva = true;
	
	//JanelaDeslizante janelaReceptor;
	Janela janelaTransmissor;
	
	public ComunicacaoCliente(ClienteConfig clienteConfig) {
		// TODO Auto-generated constructor stub
		this.clienteConfig = clienteConfig;
		Random generator = new Random();
		this.numero_de_sequencia =  0 + generator.nextInt(65535);
		this.cliente_porta =  6790 + generator.nextInt(100);
		generator = null;
		
		janelaTransmissor = new Janela(clienteConfig.tamanho_janela);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(ThreeWayHandshake()) {
			
			System.out.println("Cliente: OK ThreeWayHandshake");
			
			inicializaTransmissor(); // Inicia as Threads de transmissão, começa a mandar o arquivo
			inicializaReceptor(); // Inicia as Threads para receber os ACKs
		}	
	}
	boolean ThreeWayHandshake() {
		
		System.out.println("Cliente: ThreeWayHandshake");
		
		enviaSyn();
		return esperaSynAckEnviaAck();
	}
	void enviaSyn() {
		
		System.out.println("Cliente: envia syn para servidor");
		
		DadosTCP pacoteTCP = new DadosTCP(cliente_porta,clienteConfig.host_servidor.porta,numero_de_sequencia, numero_ack,
				0,1,0,(janelaTransmissor.tamanhoMaximoJanela - janelaTransmissor.tamanhoJanela),null);
		Mensagem syn = new Mensagem(clienteConfig.host_servidor,pacoteTCP,clienteConfig.timeout);
		syn.enviarMensagem(clienteConfig.probabilidadeDescarte);
		numero_de_sequencia = numero_de_sequencia + syn.pacoteTCP.size() + 1;
	}
	boolean esperaSynAckEnviaAck() {
		
		System.out.printf("Cliente: enviou syn e vai esperar syn, ack do servidor  escutando a porta %s\r\n",cliente_porta);
		
		Mensagem mensagemRecebida = Mensagem.receberMensagem(cliente_porta,clienteConfig.tcp_maximum_segment_size);
		
		System.out.println("Cliente: servidor redirecionou a porta para "+((int) mensagemRecebida.pacoteTCP.segmentoTCP.getPortaOrigem()));
		
		clienteConfig.host_servidor.porta = (int) mensagemRecebida.pacoteTCP.segmentoTCP.getPortaOrigem();
		if(mensagemRecebida.pacoteTCP.segmentoTCP.getSYN()==1 && mensagemRecebida.pacoteTCP.segmentoTCP.getACK()==1) {
			numero_ack = mensagemRecebida.pacoteTCP.segmentoTCP.getNumeroSequencia() + mensagemRecebida.pacoteTCP.size();
			return enviaAck();
		}
		return false;
	}
	boolean enviaAck() {
		
		System.out.println("Cliente: recebeu syn, ack e vai enviar ack para servidor");
		
		DadosTCP pacoteTCP = new DadosTCP(cliente_porta,clienteConfig.host_servidor.porta,numero_de_sequencia, numero_ack,
				1,0,0,(janelaTransmissor.tamanhoMaximoJanela - janelaTransmissor.tamanhoJanela),null);
		Mensagem syn = new Mensagem(clienteConfig.host_servidor,pacoteTCP,clienteConfig.timeout);
		syn.enviarMensagem(clienteConfig.probabilidadeDescarte);
		numero_de_sequencia = numero_de_sequencia + syn.pacoteTCP.size() + 1;
		return true;
	}
	void enviaFin() {
		
		System.out.println("Cliente: enviar fin para servidor");
		
		DadosTCP pacoteTCP = new DadosTCP(cliente_porta,clienteConfig.host_servidor.porta,numero_de_sequencia, numero_ack,
				0,0,1,(janelaTransmissor.tamanhoMaximoJanela - janelaTransmissor.tamanhoJanela),null);
		Mensagem syn = new Mensagem(clienteConfig.host_servidor,pacoteTCP,clienteConfig.timeout);
		syn.enviarMensagem(0);
		conexaoAtiva = false;
	}
	private void inicializaTransmissor() {
		// TODO Auto-generated method stub
		
		Thread transmissor = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					System.out.println("Cliente: Começar a transmissão");
					
	                FileInputStream streamDoArquivo = new FileInputStream(clienteConfig.nome_arquivo);
	                byte[] pacoteTCP = new byte[clienteConfig.tcp_maximum_segment_size];
	                while (streamDoArquivo.read(pacoteTCP) != -1) {
	                	//while(janelaTransmissor.tamanhoJanela+pacoteTCP.length<janelaTransmissor.tamanhoMaximoJanela) {
							// espera abrir espaço na janela
						//}
						Mensagem mensagemParaEnviar = new Mensagem(clienteConfig.host_servidor,new DadosTCP(pacoteTCP),clienteConfig.timeout);
						numero_de_sequencia = numero_de_sequencia + mensagemParaEnviar.pacoteTCP.size() + 1;
						mensagemParaEnviar.enviarMensagem(clienteConfig.probabilidadeDescarte);
						janelaTransmissor.queue.add(mensagemParaEnviar);
						janelaTransmissor.tamanhoJanela = janelaTransmissor.tamanhoJanela + mensagemParaEnviar.pacoteTCP.size();

						System.out.printf("Cliente: enviou uma mensagem \r\n %s\r\n",pacoteTCP);
						
					}
	                streamDoArquivo.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					enviaFin();
				}
			}
		};
		transmissor.start();
		
		if(clienteConfig.tipoTimeout==0) {
			Thread analisaEstouroTimeout = new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(conexaoAtiva) {
						Iterator<Mensagem> iterator = janelaTransmissor.queue.iterator();
						while(iterator.hasNext()){
							Mensagem mensagemJanelaTransmissor = (Mensagem) iterator.next();
							if(mensagemJanelaTransmissor.timeout==0 && mensagemJanelaTransmissor.retornouAck==false) {
								mensagemJanelaTransmissor.timeout=clienteConfig.timeout;
								mensagemJanelaTransmissor.enviarMensagem(clienteConfig.probabilidadeDescarte);
							}
						}
					}
				}
			};
			analisaEstouroTimeout.start();
		}
	}
	private void inicializaReceptor() {
		
		Thread receptor = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(conexaoAtiva) {
					Mensagem mensagemRecebida = Mensagem.receberMensagem(cliente_porta,clienteConfig.tcp_maximum_segment_size);
					Mensagem primeiraMensagem = janelaTransmissor.queue.element();
					if(primeiraMensagem.pacoteTCP.segmentoTCP.getNumeroSequencia() + primeiraMensagem.pacoteTCP.size() == mensagemRecebida.pacoteTCP.segmentoTCP.getNumeroACK()) {
						
						System.out.println("Cliente: recebeu ack " + primeiraMensagem.pacoteTCP.segmentoTCP.getNumeroACK());
						
						janelaTransmissor.tamanhoJanela = janelaTransmissor.tamanhoJanela - primeiraMensagem.pacoteTCP.size();
						numero_ack = primeiraMensagem.pacoteTCP.segmentoTCP.getNumeroSequencia();
						janelaTransmissor.queue.remove(primeiraMensagem);
					}
				}
			}
		};
		receptor.start();
	}
}
