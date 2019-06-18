package servidor;

import ambos.Comunicacao;
import ambos.Config;
import ambos.Mensagem;

/**
 * Classe responsavel por receber a configuracao e iniciar a comunicacao
 * receptiva.
 * 
 * @author ribamar
 *
 */
public class Servidor implements Runnable {
  private Config config;
  
  /**
   * Recebe configuração para servidor.
   * 
   * @param config = (String nomeArquivo, int portaUdp, int tamanhoJanela, double
   *               probabilidadeDeDescarte)
   */
  public Servidor(Config config) {
    this.config = config;
  }
  
  @Override
  public void run() {
    while (true) {
      /*
       * Escuta portaUdp, para cada SYN recebido inicia uma Comunicação para uma
       * Comunicação Servidor
       */
      Mensagem synRecebido = Mensagem.receberMensagem(config.portaUdp, 0);
      if (synRecebido.isSyn()) {
        Comunicacao comunicacao = new Comunicacao(synRecebido, config.tamanhoJanela);
        ComunicacaoServidor comunicacaoServidor = new ComunicacaoServidor(comunicacao,
            config.nomeArquivo);
        Thread servidor = new Thread(comunicacaoServidor);
        servidor.start();
      }
    }
  }
  
}
