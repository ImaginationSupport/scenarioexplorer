package com.imaginationsupport;
import java.util.Random;

public class TestObj {
        public int i;
        public String s;
        public double d;
        public static String alphabet="abcdefghijklmnopqrstuvwxyz";

        public TestObj(){
                Random r=new Random();
                i=r.nextInt();
                d=r.nextDouble();

                int c=r.nextInt(5000);
                for (int j=0;j<c;j++){
                        int x=r.nextInt(alphabet.length());
                        s+=alphabet.charAt(x);
                }
        }
}
