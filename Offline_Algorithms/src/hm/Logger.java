package com.multiKnapsackAlgorithm.hm;

public class Logger {
    public static boolean useConsole;
    {
        useConsole=false;
    }
    public static void  message(Object text ){
        if (useConsole){
            System.out.println(text);
        }
    }
}
