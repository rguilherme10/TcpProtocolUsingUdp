package controle;

import ambos.Config;
import cliente.Cliente;

public class TcpCliente {
  
  /**
   * Inicio via linha de comando: $ tcp_client fn sip sport wnd rto mss dupack lp
   * .
   * @param args = String fn: nome do arquivo a ser enviado String sip: endereço
   *             IP do servidor int sport: porta UDP do servidor int wnd: tamanho
   *             da janela do transmissor e receptor em bytes int rto: valor
   *             inicial de timeout para retransmissão de um segmento em
   *             milisegundos int mss: TCP Maximum Segment Size boolean dupack:
   *             deve ser um para usar retransmissão via ACKs duplicados e zero
   *             caso contrário double lp: probabilidade de um datagrama UDP ser
   *             descartado
   * @author ribamar
   */
  public static void main(String[] args) {
    if (args.length == 8) {
      
      String nomeDoArquivo = args[0];
      String enderecoIpDoServidor = args[1];
      int portaUdp = Integer.parseInt(args[2]);
      int tamanhoDaJanela = Integer.parseInt(args[3]);
      int timeout = Integer.parseInt(args[4]);
      int tcpMaximumSegmentSize = Integer.parseInt(args[5]);
      boolean retransmissaoAckDuplicado = (Integer.parseInt(args[6]) == 1);
      double probabilidadeDeDescarte = new Double(args[7]);
      
      Thread cliente = new Thread(
          new Cliente(new Config(nomeDoArquivo, portaUdp, tamanhoDaJanela, probabilidadeDeDescarte),
              tcpMaximumSegmentSize, enderecoIpDoServidor, retransmissaoAckDuplicado, timeout),
          "Cliente");
      cliente.start();
    } else {
      System.out.println("Erro: Falta argumentos para inicio do cliente");
    }
  }
  
}
