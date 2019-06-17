package servidor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import ambos.Comunicacao;

public class ComunicacaoServidor implements Runnable {
	
	private Comunicacao comunicacao;
	private String nomeArquivo;
	public ComunicacaoServidor(Comunicacao comunicacao, String nomeArquivo) {
		this.comunicacao = comunicacao;	
		this.nomeArquivo = nomeArquivo;
	}

	@Override
	public void run() {
		comunicacao.enviaSynAck();
		comunicacao.esperaThreeWayHandShake();
		inicializaReceptor();
	}
	
	private void inicializaReceptor() {
		Thread analisaRecebeuGravaNaOrdem = new Thread("ComunicacaoServidor.inicializaReceptor") {
			@Override
			public void run() {
				File file = new File(nomeArquivo);
				
				if(file.exists()) {
					file.delete();
				}

				//criar arquivo
				try{
					file.createNewFile();
				}
				catch(Exception e) {}
				
				while(comunicacao.analisaReceptorAtivo==true || comunicacao.dadosRecebidos.size()>0) {
					try {
						sleep(comunicacao.tempo_espera);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Map.Entry<Integer, byte[]> dados = comunicacao.dadosRecebidos.firstEntry();
					if(dados!=null) {
						try {
							FileOutputStream streamDoArquivo = new FileOutputStream(file,true);
							streamDoArquivo.write(dados.getValue());
							streamDoArquivo.close();
							comunicacao.dadosRecebidos.remove(dados.getKey());
						}
						catch(Exception e) {}
					}
					try {
						sleep(comunicacao.tempo_espera);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		analisaRecebeuGravaNaOrdem.start();
	}
}
