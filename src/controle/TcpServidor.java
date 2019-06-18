package controle;

import ambos.Config;
import servidor.Servidor;

public class TcpServidor {
  
  /**
   * Inicio via linha de comando: $ tcp_server fn sport wnd lp
   * .
   * @param args = String fn -> nome do arquivo a ser recebido e gravado em disco
   *             int sport -> porta UDP que o servidor deve escutar int wnd ->
   *             tamanho da janela do transmissor e receptor em bytes double lp ->
   *             probabilidade de um datagrama UDP ser descartado
   * @author ribamar
   */
  public static void main(String[] args) {
    if (args.length == 4) {
      
      String nomeDoArquivo = args[0];
      int portaUdp = Integer.getInteger(args[1]);
      int tamanhoDaJanela = Integer.getInteger(args[2]);
      double probabilidadeDeDescarte = new Double(args[3]);
      
      Thread servidor = new Thread(
          new Servidor(
              new Config(nomeDoArquivo, portaUdp, tamanhoDaJanela, probabilidadeDeDescarte)),
          "Servidor");
      servidor.start();
    } else {
      System.out.println("Erro: Falta argumentos para inicio do servidor");
    }
  }
  
}
