package controle;

import cliente.ClienteConfig;
import cliente.ComunicacaoCliente;
import servidor.ComunicacaoServidor;
import servidor.ServidorConfig;

public class Teste {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServidorConfig servidorConfig = new ServidorConfig();
		Thread servidor = new  Thread(new ComunicacaoServidor(servidorConfig));
		servidor.start();
		
		ClienteConfig clienteConfig = new ClienteConfig();
		Thread cliente = new  Thread(new ComunicacaoCliente(clienteConfig));
		cliente.start();
	}

}
