package cliente;

import ambos.Comunicacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Classe responsavel por alimentar a comunicacao com os dados.
 * 
 * @author ribamar
 *
 */
public class ComunicacaoCliente implements Runnable {
  
  private static final long TEMPO_ESPERA_THREEWAYHANDSHAKE = 10000;
  private Comunicacao       comunicacao;
  private File              arquivo;
  
  /**
   * Recebe comunicacao e arquivo a ser transferido.
   * 
   * @param comunicacao = instancia ja iniciada da Comunicacao
   * @param arquivo     = arquivo válido a ser transmitido
   */
  public ComunicacaoCliente(Comunicacao comunicacao, File arquivo) {
    this.comunicacao = comunicacao;
    this.arquivo = arquivo;
  }
  
  @Override
  public void run() {
    
    while (comunicacao.threeWayHandShake == false) {
      comunicacao.enviaSyn();
      comunicacao.esperaThreeWayHandShake(TEMPO_ESPERA_THREEWAYHANDSHAKE);
    }
    
    if (comunicacao.threeWayHandShake) {
      
      iniciaTransmissor();
      
    } else {
      comunicacao.receptorAtivo = false;
      comunicacao.analisaReceptorAtivo = false;
      comunicacao.transmissorAtivo = false;
    }
    
  }
  
  private void iniciaTransmissor() {
    Thread transmissor = new Thread() {
      @Override
      public void run() {
        FileInputStream streamDoArquivo = null;
        try {
          streamDoArquivo = new FileInputStream(arquivo);
          int bytesToRead = streamDoArquivo.available();
          bytesToRead = comunicacao.janelaTransmissora.getTamanhoProximoPacote(
              comunicacao.tcpMaximumSegmentSize, streamDoArquivo.available());
          byte[] dados = new byte[bytesToRead];
          while (bytesToRead > 0) {
            streamDoArquivo.read(dados);
            comunicacao.enviaDados(bytesToRead, dados, comunicacao.timeout);
            bytesToRead = comunicacao.janelaTransmissora.getTamanhoProximoPacote(
                comunicacao.tcpMaximumSegmentSize, streamDoArquivo.available());
            dados = new byte[bytesToRead];
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          try {
            streamDoArquivo.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          comunicacao.enviaFin();
        }
      }
    };
    transmissor.start();
  }
}
