/**
 * *
 * Role.java
 * Created on 28 févr. 2010 23:00:51
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.broker;

/**
 * @author Didier SCHWAB
 *
 */
public class Role {


    public static final int WORDNET = 1;
    public static final int ISA_LEARNER = 2;
    public static final int SEMANTICNETWORK = 3;

    public static int convert(String S){

	String s = S.toUpperCase();

	if(s.equals("WORDNET"))
	    return WORDNET;
	else
	    if(s.equals("ISA_LEARNER"))
		return ISA_LEARNER;
	    else
		if(s.equals("SEMANTICNETWORK"))
		    return SEMANTICNETWORK;
		else
		    return -1;
    }

    public static String convert(int i){

	switch(i){

	case WORDNET : {

	    return "WORDNET";
	}
	case ISA_LEARNER : {

	    return "ISA_LEARNER";
	}
	case SEMANTICNETWORK : {

	    return "SEMANTICNETWORK";
	}
	}

	return null;
    }

}
