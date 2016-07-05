/**
 * *
 * Languages.java
 * Created on 28 févr. 2010 14:57:51
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.broker;

/**
 * @author Didier SCHWAB
 *
 */
public class Language {

    public static final int ALL = 0;
    public static final int ENGLISH = 1;
    public static final int FRENCH = 2;
    public static final int UNL = 3;

    public static int convert(String S){

	String s = S.toUpperCase();

	if(s.equals("ALL"))
	    return ALL;
	else
	    if(s.equals("ENGLISH"))
		return ENGLISH;
	    else
		if(s.equals("FRENCH"))
		    return FRENCH;
		else
		    if(s.equals("UNL"))
			return UNL;
		    else
			return -1;
    }

    public static String convert(int i){

	switch(i){
	
	case ALL:{
	    
	    return "ALL";
	}

	case ENGLISH : {

	    return "ENGLISH";
	}
	case FRENCH : {

	    return "FRENCH";
	}
	case UNL : {

	    return "UNL";
	}

	}

	return null;
    }
}
