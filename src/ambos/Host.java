package ambos;

import java.net.InetAddress;

/**
 * Classe para armazenamento do host tipo InetAddress.
 * 
 * @author ribamar
 *
 */
public class Host {
  public InetAddress endereco;
  public int         porta;
  
  /**
   * Construtor pelo endere�o em String.
   * 
   * @param ip    = endere�o IP do host
   * @param porta = porta do host
   * @throws Exception = pode n�o encontrar no DNS
   */
  public Host(String ip, int porta) throws Exception {
    // TODO Auto-generated constructor stub
    this.endereco = InetAddress.getByName(ip);
    this.porta = porta;
  }
  
  /**
   * Contrutor pelo InetAddress.
   * 
   * @param endereco = endereco do host
   * @param porta    = porta do host
   */
  public Host(InetAddress endereco, int porta) {
    // TODO Auto-generated constructor stub
    this.endereco = endereco;
    this.porta = porta;
  }
}
