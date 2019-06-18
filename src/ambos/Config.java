package ambos;

/**
 * Classe Config para gerar configuração inicial dos dados.
 * 
 * @author ribamar
 *
 */
public class Config {
  public String nomeArquivo             = "D:\\teste\\testeRecebido.pdf";
  public int    portaUdp                = 6789;
  public int    tamanhoJanela           = 4500;
  public double probabilidadeDeDescarte = 0.01;
  
  /**
   * Construtor.
   * 
   * @param nomeArquivo             = nome do arquivo a ser recebido ou enviado
   * @param portaUdp                = porta UDP do servidor
   * @param tamanhoJanela           = tamanho da janela do transmissor e receptor
   *                                em bytes
   * @param probabilidadeDeDescarte = probabilidade de um datagrama UDP ser
   *                                descartado
   */
  public Config(String nomeArquivo, int portaUdp, int tamanhoJanela,
      double probabilidadeDeDescarte) {
    this.nomeArquivo = nomeArquivo;
    this.portaUdp = portaUdp;
    this.tamanhoJanela = tamanhoJanela;
    this.probabilidadeDeDescarte = probabilidadeDeDescarte;
  }
}
