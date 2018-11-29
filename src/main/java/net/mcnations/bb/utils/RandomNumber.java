package net.mcnations.bb.utils;

import java.util.Random;

public class RandomNumber {
	
	public static int getRandomNumber(int range)
	{
		Random rnd = new Random();
		Random pos = new Random();
		
		int posorneg = pos.nextInt(2);
		
		if(posorneg == 0)
		return rnd.nextInt(range);
		else
		return rnd.nextInt(range)*-1;	
	}
	
}
