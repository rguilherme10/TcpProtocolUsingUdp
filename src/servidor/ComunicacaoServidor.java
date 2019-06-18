package servidor;

import ambos.Comunicacao;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Classe responsavel por receber os dados e salvar no disco.
 * 
 * @author ribamar
 * @version 1.0.0.0
 */
public class ComunicacaoServidor implements Runnable {
  
  private static final long TEMPO_ESPERA_THREEWAYHANDSHAKE = 10000;
  private Comunicacao       comunicacao;
  private String            nomeArquivo;
  
  /**
   * Recebe comunicacao e arquivo a ser salvo.
   * 
   * @param comunicacao = instancia da Comunicacao
   * @param nomeArquivo = nome do arquivo a ser gerado na recepcao
   */
  public ComunicacaoServidor(Comunicacao comunicacao, String nomeArquivo) {
    this.comunicacao = comunicacao;
    this.nomeArquivo = nomeArquivo;
  }
  
  @Override
  public void run() {
    comunicacao.enviaSynAck();
    comunicacao.esperaThreeWayHandShake(TEMPO_ESPERA_THREEWAYHANDSHAKE);
    
    if (comunicacao.threeWayHandShake) {
      
      inicializaReceptor();
      
    } else {
      
      comunicacao.receptorAtivo = false;
      comunicacao.analisaReceptorAtivo = false;
      comunicacao.transmissorAtivo = false;
      
    }
  }
  
  private void inicializaReceptor() {
    Thread analisaRecebeuGravaNaOrdem = new Thread("ComunicacaoServidor.inicializaReceptor") {
      @Override
      public void run() {
        File file = new File(nomeArquivo);
        
        /*
         * Apagar arquivo, isto garante que o append não mantenha dados anteriores, caso
         * o arquivo exista;
         */
        if (file.exists()) {
          file.delete();
        }
        
        /*
         * Criar arquivo
         */
        try {
          file.createNewFile();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        while (comunicacao.analisaReceptorAtivo == true || comunicacao.dadosRecebidos.size() > 0) {
          Map.Entry<Integer, byte[]> dados = comunicacao.dadosRecebidos.pollFirstEntry();
          if (dados != null) {
            try {
              FileOutputStream streamDoArquivo = new FileOutputStream(file, true);
              streamDoArquivo.write(dados.getValue());
              streamDoArquivo.close();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
    };
    analisaRecebeuGravaNaOrdem.start();
  }
}
