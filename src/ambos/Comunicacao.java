package ambos;

import java.util.TreeMap;

import utils.RandomInt;

public class Comunicacao {
	
	public boolean ThreeWayHandShake = false;
	
	public long tempo_espera = 0;

	public int numero_ack = 0;
	public Janela janelaReceptora;
	int porta_receptora = RandomInt.getInt(6790, 20000);

	int numero_de_sequencia = RandomInt.getInt(0, 65535);
	private Janela janelaTransmissora;
	private Host host;
	
	private boolean tipoTimeout=false;
	public int timeout = 0;
	private double probabilidadeDeDescarte;
	
	public boolean receptorAtivo = true;
	public boolean analisaReceptorAtivo = true;
	boolean transmissorAtivo = true;
	public boolean analisaTransmissorAtivo = true;
	
	boolean enviouFinAck = false;
	
	//public Queue<byte[]> dadosRecebidos = new PriorityQueue<byte[]>();
	
	public final TreeMap<Integer, byte[]> dadosRecebidos = new TreeMap<>();

	public Comunicacao(Mensagem synRecebidoDoCliente, int tamanhoJanela) {
		
		numero_ack = synRecebidoDoCliente.getCalculaACK();

		this.host = new Host(synRecebidoDoCliente.host.endereco, (int) synRecebidoDoCliente.getPortaOrigem());

		iniciaComunicacao(tamanhoJanela);
		
	}

	public Comunicacao(Host host_servidor, int tamanhoJanela) {
		
		this.host = host_servidor;
		
		iniciaComunicacao(tamanhoJanela);
		
	}
	
	public void esperaThreeWayHandShake(){
		while(ThreeWayHandShake==false) {
			// espera ThreeWayHandShake
			try {
				Thread.sleep(tempo_espera);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void esperaThreeWayHandShake(long tempo_espera){
		try {
			Thread.sleep(tempo_espera);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void enviaSyn() {
		System.out.println(porta_receptora + " enviou Syn " + + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 0, 1, 0,
				(janelaReceptora.getEspacoLivre()), 0, null);
		janelaTransmissora.add(new Mensagem(host, pacote));
	}

	public void enviaSynAck() {
		System.out.println(porta_receptora + " enviou SynAck " + + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 1, 1, 0,
				(janelaReceptora.getEspacoLivre()), 0, null);
		janelaTransmissora.add(new Mensagem(host, pacote));
	}

	public void enviaAck() {
		System.out.println(porta_receptora + " retornou Ack " + + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 1, 0, 0,
				(janelaReceptora.getEspacoLivre()), 0, null);
		janelaTransmissora.add(new Mensagem(host, pacote));
	}
	
	public void enviaDados(int tamanhoDados, byte[] dados, int timeout) {
		System.out.println(porta_receptora + " enviou dados " + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 0, 0, 0,
				(janelaReceptora.getEspacoLivre()), tamanhoDados, dados);
		Mensagem mensagemParaEnviar = new Mensagem(host, pacote);
		while(janelaTransmissora.cabeMensagem(mensagemParaEnviar)==false) {
			//espera caber mensagem na janela
		}
		janelaTransmissora.add(mensagemParaEnviar);
	}

	public void enviaFin() {
		while(janelaTransmissora.temMensagens()) {
			try {
				Thread.sleep(tempo_espera);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(porta_receptora + " enviou Fin " + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 0, 0, 1,
				(janelaReceptora.getEspacoLivre()), 0, null);
		janelaTransmissora.add(new Mensagem(host, pacote));
	}
	
	public void enviaFinAck() {
		while(janelaTransmissora.temMensagens()) {
			try {
				Thread.sleep(tempo_espera);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(porta_receptora + " enviou FinAck " + numero_de_sequencia + " " + numero_ack);
		Pacote pacote = new Pacote(porta_receptora, host.porta, numero_de_sequencia, numero_ack, 1, 0, 1,
				(janelaReceptora.getEspacoLivre()), 0, null);
		janelaTransmissora.add(new Mensagem(host, pacote));
	}

	private void iniciaComunicacao(int tamanhoJanela) {

		this.janelaReceptora = new Janela(tamanhoJanela);
		/*while () {
			// garatindo porta ok
		}*/
		inicializaReceptora();

		this.janelaTransmissora = new Janela(tamanhoJanela);
		inicializaTransmissora();

	}

	private boolean inicializaReceptora() {
		try {
			Thread receptor = new Thread("Comunicacao.inicializaReceptora " + porta_receptora) {
				@Override
				public void run() {
					while (receptorAtivo) {
						Mensagem mensagemRecebida = Mensagem.receberMensagem(porta_receptora, 1480);
						if(mensagemRecebida!=null) {
							janelaTransmissora.setTamanhoJanela(mensagemRecebida.getTamanhoJanelaReceptor());
							if(janelaReceptora.add(mensagemRecebida)) {
								System.out.println(porta_receptora + " recebeu " + mensagemRecebida.getNumeroSequencia());
							}
							if(mensagemRecebida.isFin() || mensagemRecebida.isFinAck()) {
								receptorAtivo = false;
							}
						}
					}
					System.out.println(porta_receptora + " encerrou recepcao");
				}
			};
			receptor.start();
			
			Thread analisaRecepcoes = new Thread("Comunicacao.analisaRecepcoes " + porta_receptora) {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(analisaReceptorAtivo) {
						for(Mensagem mensagemJanelaReceptora : janelaReceptora.getIterator()){
							if(janelaTransmissora.contem(mensagemJanelaReceptora.getNumeroACK())) {
								Mensagem mensagemJanelaTransmissora = janelaTransmissora.getMensagem(mensagemJanelaReceptora.getNumeroACK());
								mensagemJanelaTransmissora.retornouAck=true;
							}


							numero_ack = mensagemJanelaReceptora.getCalculaACK();
							
							System.out.println(porta_receptora + " analisaRecepcao " + mensagemJanelaReceptora.getNumeroSequencia() + " " + mensagemJanelaReceptora.getNumeroACK());

							if(mensagemJanelaReceptora.isControle()){
								if(mensagemJanelaReceptora.isAck()) {
									ThreeWayHandShake = true;
								}
								else if(mensagemJanelaReceptora.isSynAck()) {
									host = new Host(mensagemJanelaReceptora.host.endereco, (int) mensagemJanelaReceptora.getPortaOrigem());
									enviaAck();
									ThreeWayHandShake = true;
								}
								else if(mensagemJanelaReceptora.isFin()) {
									enviaAck();
									enviaFinAck();
									analisaReceptorAtivo = false;
								}
								else if(mensagemJanelaReceptora.isFinAck()) {
									enviaAck();
									analisaReceptorAtivo = false;
								}
							}
							else{
								if(mensagemJanelaReceptora.size()>0) {
									dadosRecebidos.put(mensagemJanelaReceptora.getNumeroSequencia(), mensagemJanelaReceptora.getDados());
								}
								enviaAck();
							}
							janelaReceptora.remove(mensagemJanelaReceptora);
						}
						try {
							sleep(tempo_espera);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			analisaRecepcoes.start();
			return true;
		} catch (Exception e) { // Porta já está em uso
			porta_receptora = RandomInt.getInt(6791, 20000); // nova porta
		}
		return false;
	}
	
	private void inicializaTransmissora() {
		Thread transmissor = new Thread("Comunicacao.inicializaTransmissora " + porta_receptora) {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (transmissorAtivo) {
					for(Mensagem mensagemJanelaTransmissora : janelaTransmissora.getIterator()) {
						try {
							if (mensagemJanelaTransmissora.enviada == false) {// && mensagemJanelaTransmissora.getNumeroSequencia()==numero_de_sequencia){
								mensagemJanelaTransmissora.enviarMensagem(probabilidadeDeDescarte, timeout);
								if(mensagemJanelaTransmissora.isFin() || mensagemJanelaTransmissora.isFinAck()) {
									enviouFinAck = true;
								}
								incrementaNumeroSequencia(mensagemJanelaTransmissora.size() + 1);
							}
						}
						catch(Exception e) {}
					}
					if(enviouFinAck && janelaTransmissora.temMensagens()==false) {
						transmissorAtivo = false;
					}
					try {
						sleep(tempo_espera);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(porta_receptora + " encerrou transmissao");
			}
		};
		transmissor.start();
		
		Thread analisaTransmissoes = new Thread("Comunicacao.analisaTransmissoes " + porta_receptora) {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(analisaTransmissorAtivo) {
					for(Mensagem mensagemJanelaTransmissora : janelaTransmissora.getIterator()){
						try {
							if(mensagemJanelaTransmissora.enviada) {
								if(mensagemJanelaTransmissora.retornouAck || mensagemJanelaTransmissora.isAck() || mensagemJanelaTransmissora.isFinAck()) {
									System.out.println(porta_receptora + " removeu transmissora " + mensagemJanelaTransmissora.getNumeroSequencia() + " " + mensagemJanelaTransmissora.getNumeroACK());
									janelaTransmissora.remove(mensagemJanelaTransmissora);
								}
								else if(tipoTimeout && mensagemJanelaTransmissora.deuTimeout) {
									mensagemJanelaTransmissora.enviarMensagem(probabilidadeDeDescarte, timeout);
								}
							}
						}
						catch(Exception e) {}
					}
					if(transmissorAtivo==false) {
						analisaTransmissorAtivo = false;
					}
					try {
						sleep(tempo_espera);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		analisaTransmissoes.start();
	}

	private void incrementaNumeroSequencia(int incremento) {
		numero_de_sequencia = numero_de_sequencia + incremento;
	}
}
