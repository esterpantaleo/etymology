package org.getalp.blexisma.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexandre Labadié
 * 
 * Tool class allowing easier use of of Pattern and Matcher classes
 * */
public class Patterner 
{
	/**
	 * @param inTxt : input text to be modified
	 * @param expRegIn : regular expression to be replaced
	 * @param expRegOut : regular expression that has to replace expRegIn
	 * @return inTxt where every occurrence of expRegIn has been replaced by expRegOut
	 * */
	public final static String patterner(String inTxt, String expRegIn, String expRegOut)
	{
		Pattern p = null;
		Matcher m = null;
		boolean result = true;
		StringBuffer sb = null;

		/* First we compile the regular expression to be found as a pattern  */
		p = Pattern.compile(expRegIn);
		/* The pattern is then used to create a matcher from the input text */
		m = p.matcher(inTxt);
		/* As long as expRegIn can be found result will be true */
		result=m.find();
		/* We replace expRegIn by expRegOut and stock the result in a string buffer */
		sb = new StringBuffer();
		while (result)
		{
			m.appendReplacement(sb,expRegOut);
			result=m.find();
		}
		/* We put the tail of the input string in the buffer */
		m.appendTail(sb);
		/* And return the altered string */
		return new String(sb.toString());
	}
	
	/**
	 * @param test : string to be tested
	 * @param expRegPat : pattern to be tested
	 * @return Number of expRegPat occurrences in test
	 * */
	public final static int patternPresent(String test, String expRegPat)
	{
		boolean result = false;
		int cpt = 0;
		Pattern p = null;
		Matcher m = null;
		
		/* First we compile the regular expression to be found as a pattern  */
		p = Pattern.compile(expRegPat);
		/* The pattern is then used to create a matcher from the input text */
		m = p.matcher(test);
		/* As long as expRegPat can be found result will be true */
		result=m.find();
		/* We count the number of occurrences of expRegPat in test */
		while (result)
		{
			cpt++;
			result=m.find();
		}
		/* We return the number of occurrences of expRegPat in test */
		return cpt;
	}
}
