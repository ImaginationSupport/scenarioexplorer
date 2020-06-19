package com.imaginationsupport.data;

import java.util.Random;

public class Uid {
	public static final String alphabet="01234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final Random rand=new Random();
	
	public static String getUid(){
		StringBuilder id=new StringBuilder();
		for(int i=0;i<16;i++){
			id.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return id.toString();
	}	
}