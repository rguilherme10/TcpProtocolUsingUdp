package utils;

import java.util.Random;

/**
 * Aleatorios.
 * 
 * @author ribamar
 *
 */
public class RandomInt {
  
  /**
   * Retorna numero aleatório entre inicio e inicio + qtd.
   * 
   * @param inicio -> inicio
   * @param qtd    -> fim = inicio + qtd
   * @return
   */
  public static int getInt(int inicio, int qtd) {
    Random generator = new Random();
    return inicio + generator.nextInt((int) (Math.random() * qtd + 1));
  }
}
