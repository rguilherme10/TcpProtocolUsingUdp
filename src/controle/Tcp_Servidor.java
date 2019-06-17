package controle;

import ambos.Config;
import servidor.Servidor;

public class Tcp_Servidor {

	public static void main(String[] args) {
		
		Thread servidor = new Thread(new Servidor(new Config("D:\\teste\\teste.pdf", 6789, 1500000, -1)),"Servidor");
		servidor.start();
	}

}
