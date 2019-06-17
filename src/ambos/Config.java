package ambos;

public class Config {
	public String nome_arquivo="D:\\henrique.c";
	public int porta_servidor=6789;
	public int tamanhoJanela=1500000;
	public double probabilidadeDeDescarte=-1;
	public Config(String nome_arquivo, int porta_servidor, int tamanhoJanela, double probabilidadeDeDescarte) {
		this.nome_arquivo=nome_arquivo;
		this.porta_servidor=porta_servidor;
		this.tamanhoJanela=tamanhoJanela;
		this.probabilidadeDeDescarte=probabilidadeDeDescarte;
	}
}
