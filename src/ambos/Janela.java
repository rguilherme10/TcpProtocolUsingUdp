package ambos;

import java.util.TreeMap;

/**
 * Classe controla e armazenana as mensagens.
 * 
 * @author ribamar
 *
 */
public class Janela {
  private int tamanhoJanela;
  // private Queue<Mensagem> queue = new PriorityQueue<Mensagem>();
  TreeMap<Integer, Mensagem> queue = new TreeMap<>();
  
  public Janela(int tamanhoJanela) {
    // TODO Auto-generated constructor stub
    this.tamanhoJanela = tamanhoJanela;
  }
  
  public void setTamanhoJanela(int tamanhoJanela) {
    this.tamanhoJanela = tamanhoJanela;
  }
  
  public int getEspacoLivre() {
    // TODO Auto-generated method stub
    return tamanhoJanela;
  }
  
  private void aumentaTamanhoJanela(int incremento) {
    tamanhoJanela = tamanhoJanela + incremento;
  }
  
  private void diminuiTamanhoJanela(int decremento) {
    tamanhoJanela = tamanhoJanela - decremento;
  }
  
  public boolean cabeMensagem(Mensagem mensagem) {
    return (tamanhoJanela > mensagem.size());
  }
  
  public boolean contem(Mensagem mensagem) {
    return queue.containsKey(mensagem.getCalculaAck());
  }
  
  public boolean contem(int calculaAck) {
    return queue.containsKey(calculaAck);
  }
  
  /**
   * Retorna mensagem se existir na janela.
   * 
   * @param calculaAck = chave do TreeMap
   * @return mensagem encontrada ou null se não encontrou
   */
  public Mensagem getMensagem(int calculaAck) {
    if (contem(calculaAck)) {
      return queue.get(calculaAck);
    }
    return null;
  }
  
  /*
   * public ArrayList<Mensagem> toArray(){ ArrayList<Mensagem> retorno = new
   * ArrayList<Mensagem>(); synchronized (queue) { for(Entry<Integer, Mensagem>
   * mensagem : queue.entrySet()) { retorno.add(mensagem.getValue()); } } return
   * retorno;
   * 
   * }
   */
  
  /**
   * Insere mensagem na janela.
   * 
   * @param mensagem = mensagem a ser adicionada
   * @return true se mensagem foi adicionada
   */
  public boolean add(Mensagem mensagem) {
    if (cabeMensagem(mensagem) && contem(mensagem) == false) {
      queue.put(mensagem.getCalculaAck(), mensagem);
      diminuiTamanhoJanela(mensagem.size());
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Remove mensagem da janela e recalcula espaço livre.
   * 
   * @param mensagemJanelaReceptora = mensagem a ser removida
   */
  public void remove(Mensagem mensagemJanelaReceptora) {
    synchronized (queue) {
      queue.remove(mensagemJanelaReceptora.getCalculaAck());
      aumentaTamanhoJanela(mensagemJanelaReceptora.size());
      queue.notifyAll();
    }
  }
  
  public boolean temMensagens() {
    return (queue.size() > 0);
  }
  
  /**
   * Calcula a quantidade máxima de bytes que a janela do receptor pode receber.
   * 
   * @param tcpMaximumSegmentSize = tcpMaximumSegmentSize
   * @param qtdRestanteDados      = quantidade de bytes que faltam transmitir
   * @return quantidade máxima de bytes que a janela do receptor pode receber
   */
  public int getTamanhoProximoPacote(int tcpMaximumSegmentSize, int qtdRestanteDados) {
    int menor = tcpMaximumSegmentSize;
    if (tamanhoJanela < menor) {
      menor = tamanhoJanela;
    }
    if (qtdRestanteDados < menor) {
      menor = qtdRestanteDados;
    }
    return menor;
  }
  
  /**
   * espera Janela ficar vazia.
   */
  public void esperaFicarVazia() {
    while (this.temMensagens()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * espera Caber e adiciona mensagem na janela.
   * 
   * @param mensagem = mensagem a ser adicionada
   */
  public void esperaCaberAdicionaMensagem(Mensagem mensagem) {
    while (this.cabeMensagem(mensagem) == false) {
      // TODO espera caber mensagem na janela
    }
    add(mensagem);
  }
}