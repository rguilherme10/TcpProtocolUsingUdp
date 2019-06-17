package cliente;

import java.io.FileInputStream;
import java.io.IOException;

import ambos.Comunicacao;

public class ComunicacaoCliente implements Runnable {
	
	private Comunicacao comunicacao;
	private String nomeArquivo;
	private int tcp_maximum_segment_size;
	private int timeout;
	public ComunicacaoCliente(Comunicacao comunicacao, String nomeArquivo, int tcp_maximum_segment_size, int timeout) {
		this.comunicacao = comunicacao;
		this.nomeArquivo = nomeArquivo;
		this.tcp_maximum_segment_size = tcp_maximum_segment_size;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		while(comunicacao.ThreeWayHandShake==false) {
			comunicacao.enviaSyn();
			comunicacao.esperaThreeWayHandShake(2000);
		}
		iniciaTransmissor();
	}
	private void iniciaTransmissor() {
		
		Thread transmissor = new Thread("ComunicacaoCliente.iniciaTransmissor") {
			@Override
			public void run() {
				FileInputStream streamDoArquivo = null;
				try {
					streamDoArquivo = new FileInputStream(nomeArquivo);
	                int bytesToRead = streamDoArquivo.available();
	                if(bytesToRead>tcp_maximum_segment_size) {
	                	bytesToRead = tcp_maximum_segment_size;
	                }
	                byte[] dados = new byte[bytesToRead];
	                while (bytesToRead>0) {
	                	streamDoArquivo.read(dados);
	                	comunicacao.enviaDados(bytesToRead, dados, timeout);
						try {
							sleep(comunicacao.tempo_espera);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						bytesToRead = streamDoArquivo.available();
		                if(bytesToRead>tcp_maximum_segment_size) {
		                	bytesToRead = tcp_maximum_segment_size;
		                }
		                dados = new byte[bytesToRead];
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
	                try {
						streamDoArquivo.close();
					} catch (IOException e) {}
					comunicacao.enviaFin();
				}
			}
		};
		transmissor.start();
	}
}
