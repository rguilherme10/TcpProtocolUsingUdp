package controle;

import ambos.Config;
import cliente.Cliente;
import servidor.Servidor;

/**
 * Teste.
 * 
 * @author ribamar
 *
 */
public class Teste {
  /**
   * Teste.
   * 
   * @param args = configuração
   */
  public static void main(String[] args) {
    
    Thread servidor = new Thread(new Servidor(new Config("D:\\teste\\teste.pdf", 6789, 15000, -1)),
        "Servidor");
    servidor.start();

    Thread cliente = new Thread(new Cliente(new Config("D:\\teste\\pdf.pdf", 6789, 15000, -1), 1024,
        "localhost", false, 2000), "Cliente");
    cliente.start();
    
  }
  
}
