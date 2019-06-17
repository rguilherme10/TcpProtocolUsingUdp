package utils;

import java.util.Random;

public class RandomInt {
	public static int getInt(int inicio, int qtd) {
		Random generator = new Random();
		return  inicio + generator.nextInt((int) (Math.random() * qtd + 1));
	}
}
