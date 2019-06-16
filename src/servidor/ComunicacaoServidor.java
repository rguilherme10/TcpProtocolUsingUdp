package servidor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Random;

import ambos.Config;
import ambos.DadosTCP;
import ambos.Host;
import ambos.Janela;
import ambos.Mensagem;

public class ComunicacaoServidor implements Runnable {
	Comunicacao comunicacao;
	
	public ComunicacaoServidor(Comunicacao comunicacao) {
		this.comunicacao = comunicacao;
		Random generator = new Random();
		this.numero_de_sequencia =  0 + generator.nextInt(65535);
		this.servidor_porta =  6778 - generator.nextInt(100);
		generator = null;
		
		janelaReceptor = new Janela(servidorConfig.tamanhoJanela);
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(ThreeWayHandshake()) {
			
			System.out.println("Servidor: OK ThreeWayHandshake");
			
			inicializaReceptor(); // Inicia as Threads para receber os ACKs
		}
	}

	boolean ThreeWayHandshake() {
		
		System.out.println("Servidor: ThreeWayHandshake");
		
		return esperaSynEnviaSynAck();
	}
	boolean esperaSynEnviaSynAck() {
		
		System.out.println("Servidor: vai aguardar Syn");
		
		Mensagem mensagemRecebida = Mensagem.receberMensagem(servidorConfig.porta, 1480);
		if(mensagemRecebida.pacoteTCP!=null && mensagemRecebida.pacoteTCP.segmentoTCP.getSYN()==1) {
			numero_ack = mensagemRecebida.pacoteTCP.segmentoTCP.getNumeroSequencia() + mensagemRecebida.pacoteTCP.size();
			
			System.out.println("Servidor: cliente com a porta "+ ((int) mensagemRecebida.pacoteTCP.segmentoTCP.getPortaOrigem()));
			
			host_cliente = new Host(mensagemRecebida.host.endereco , (int) mensagemRecebida.pacoteTCP.segmentoTCP.getPortaOrigem());
			return enviaSynAckEsperaACK();
		}
		return false;
	}
	boolean enviaSynAckEsperaACK() {
		
		System.out.println("Servidor: Recebeu Syn e vai enviar syn, ack para o cliente");
		
		DadosTCP pacoteTCP = new DadosTCP(servidor_porta, host_cliente.porta,numero_de_sequencia,numero_ack,
				1,1,0,(janelaReceptor.tamanhoMaximoJanela - janelaReceptor.tamanhoJanela),null);
		Mensagem syn = new Mensagem(host_cliente,pacoteTCP,0);
		syn.enviarMensagem(servidorConfig.probabilidadeDescarte);
		numero_de_sequencia = numero_de_sequencia + pacoteTCP.size() + 1;
		return esperaACK();
	}

	boolean esperaACK() {
		
		System.out.println("Servidor: enviou syn, ack e vai aguardar ack");
		
		Mensagem mensagemRecebida = Mensagem.receberMensagem(servidorConfig.porta, 1480);
		if(mensagemRecebida.pacoteTCP!=null && mensagemRecebida.pacoteTCP.segmentoTCP.getACK()==1) {
			numero_ack = mensagemRecebida.pacoteTCP.segmentoTCP.getNumeroSequencia() + mensagemRecebida.pacoteTCP.size();
			return true;
		}
		return false;
	}
	private void inicializaReceptor() {
		
		Thread receptor = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				System.out.printf("Servidor: recebeu ack do cliente e começou a escutar a porta %s\r\n",servidor_porta);
				
				while(conexaoAtiva) {
					Mensagem mensagemRecebida = Mensagem.receberMensagem(servidor_porta,1480);
					if(mensagemRecebida.pacoteTCP.segmentoTCP.fin==1) {
						conexaoAtiva = false;
					}
					else {
					//if(janelaReceptor.tamanhoJanela+mensagemRecebida.pacoteTCP.size()<janelaReceptor.tamanhoMaximoJanela) {

						System.out.println("Servidor: recebeu uma mensagem \r\n"+mensagemRecebida.pacoteTCP.segmentoTCP.dados.toString());
						
						janelaReceptor.queue.add(mensagemRecebida);
						janelaReceptor.tamanhoJanela = janelaReceptor.tamanhoJanela+mensagemRecebida.pacoteTCP.size();
					//}
					}
				}
			}
		};
		receptor.start();
		
		Thread analisaRecebeuGravaNaOrdem = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					File file = new File(servidorConfig.nome_arquivo);
					while(conexaoAtiva) {
						Iterator<Mensagem> iterator = janelaReceptor.queue.iterator();
						while(iterator.hasNext()){
							Mensagem mensagemNaJanelaReceptor = (Mensagem) iterator.next();
							if(mensagemNaJanelaReceptor.pacoteTCP.segmentoTCP.getNumeroSequencia()==numero_ack+1) {
								FileOutputStream streamDoArquivo = new FileOutputStream(file,true);
								//streamDoArquivo.write(mensagemNaJanelaReceptor.pacoteTCP.segmentoTCP.dados);
								janelaReceptor.tamanhoJanela = janelaReceptor.tamanhoJanela - mensagemNaJanelaReceptor.pacoteTCP.size();

								numero_ack = mensagemNaJanelaReceptor.pacoteTCP.segmentoTCP.getNumeroSequencia() + mensagemNaJanelaReceptor.pacoteTCP.size();
								
								DadosTCP pacoteTCP = new DadosTCP(servidor_porta,host_cliente.porta,numero_de_sequencia,numero_ack,
										1,0,0,(janelaReceptor.tamanhoMaximoJanela - janelaReceptor.tamanhoJanela),null);
								Mensagem ack = new Mensagem(host_cliente,pacoteTCP,0);
								ack.enviarMensagem(servidorConfig.probabilidadeDescarte);
								numero_de_sequencia = numero_de_sequencia + pacoteTCP.size() + 1;
								
								janelaReceptor.queue.remove(mensagemNaJanelaReceptor);
								streamDoArquivo.close();
							}
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		analisaRecebeuGravaNaOrdem.start();
	}
}
