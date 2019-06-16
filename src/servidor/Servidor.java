package servidor;

import ambos.Comunicacao;
import ambos.Config;
import ambos.Mensagem;

public class Servidor {

	public static void main(String[] args) {
		Config config = new Config();
		while(true) {
			@SuppressWarnings("unused")
			Comunicacao comunicacao = new Comunicacao(Mensagem.receberMensagem(config.porta_servidor,1480),config);
		}
	}

}
