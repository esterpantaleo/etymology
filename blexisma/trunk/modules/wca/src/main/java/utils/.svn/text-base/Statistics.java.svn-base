/**
 * *
 * Statistics.java
 * Created on 10 oct. 2010 19:56:15
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package utils;

/**
 * @author Didier SCHWAB
 *
 */
public class Statistics {

    public static double max(double[] T){

	double max = Double.MIN_VALUE;

	for(int i = 0; i < T.length; i++){
	    if(max<T[i]){

		max = T[i];
	    }
	}
	return max;
    }

    public static double min(double[] T){

	double min = Double.MAX_VALUE;

	for(int i = 0; i < T.length; i++){
	    if(min>T[i]){

		min = T[i];
	    }
	}
	return min;
    }

    public static double moy(double[] T){

	double sum = 0;

	for(int i = 0; i < T.length; i++){

	    sum+=T[i];
	}
	return sum/T.length;
    }

    public static double esperance(double[] T){

	double moyenne = moy(T);
	double variance = 0;

	for(int i = 0; i < T.length; i++){

	    variance+=Math.pow(T[i]-moyenne,2);
	}

	return variance/T.length;
    }
    
    public static double ecartType(double[] T){
	
	return Math.sqrt(esperance(T));
    }
}