package org.getalp.dbnary.wiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.getalp.dbnary.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* @author ?, pantaleo  
*
*/

public class WikiTool {
    static Logger log = LoggerFactory.getLogger(WikiTool.class);

    static Pattern htmlRefElement = Pattern.compile("(<ref(?:\\s[^>]*|\\s*)>)|(</ref>)");
    // WARN: not synchronized !
    public static String removeReferencesIn(String definition) {
        StringBuffer def = new StringBuffer();
        Matcher m = htmlRefElement.matcher(definition);
        boolean mute = false;
        int previousPos = 0;
	while (m.find()) {
            if (null != m.group(1) && m.group().endsWith("/>")) {
	        // A opening/closing element
		if (! mute) def.append(definition.substring(previousPos, m.start()));
	    } else if (null != m.group(1)) {
                // An opening element
	        if (! mute) def.append(definition.substring(previousPos, m.start()));
	        mute = true;
	    } else if (null != m.group(2)) {
                // a closing element
                if (! mute) def.append(definition.substring(previousPos, m.start()));
                mute = false;
            }
            previousPos = m.end();
        }
        if (! mute) def.append(definition.substring(previousPos, definition.length()));
	    return def.toString();
    }

    /**
     * This function locates the start and end position of two symbols (enclosingStringStart and enclosingStringEnd) 
     * in input String s.
     * It can handle nested symbols
     * e.g., locateEnclosedString("string {{at}}","{{","}}") returns (7,13)
     * e.g., locateEnclosedString("string {{at {{position}} }}","{{","}}") returns (7,27) 
     * @param s the string to be parsed, this function returns the position of the second parameter enclosingStringStart and the position of the third parameter enclosingStringEnd in string s
     * @param enclosingStringStart this function returns the position of the String enclosingStringStart in String s
     * @param enclosingStringEnd this function returns the position of the String enclosingStringEn in String s  
     * @return an ArrayList with the start and ens positions of the enclosing Strings in input String s  
    */
    public static ArrayList<Pair> locateEnclosedString(String s, String enclosingStringStart, String enclosingStringEnd){
	int eSS = enclosingStringStart.length();
	int eSE = enclosingStringEnd.length();
	int numberOfEnclosings = 0, start=-1, end=-1;
	ArrayList<Pair> toreturn = new ArrayList<Pair>();
	for (int i=0; i+eSE<=s.length(); i++){
	    if (i+eSS+eSE<=s.length()){
		if (s.substring(i,i+eSS).equals(enclosingStringStart)){
		    if (start == -1){
			start = i;
		    }
		    numberOfEnclosings ++;
		    i += eSS-1;
		}
	    }
	    if (s.substring(i,i+eSE).equals(enclosingStringEnd)){
		numberOfEnclosings --;
		if (numberOfEnclosings==0 && start!=-1){
		    end = i+eSE;
		    toreturn.add(new Pair(start,end));
		    start = -1;//initialize start
		}
	    }
	}
	return toreturn;
    }
    
    /**
     * This function removes from String s text between the positions specified in each of the Pair-s in ArrayList l
     * @param s the input string
     * @param l an ArrayList of Pairs, each Pair specifies a start and an end position
     * @return a substring of s without the text contained between each of the positions specified in l 
     */
    public static String removeTextWithin(String s, ArrayList<Pair> l){
	int lsize = l.size();
	for (int i=0; i<lsize; i++){
	    int j = lsize-i-1;
	    log.debug("Removing text {}", s.substring(l.get(j).start, l.get(j).end));
	    s = s.substring(0, l.get(j).start) + s.substring(l.get(j).end, s.length());
	}
	return s;
    }

    /**                                             
     * @deprecated                                 
     * Parse the args of a Template, e.g., parses a string like xxx=yyy|zzz=ttt
     * It can handle nested parentheses, e.g., xxx=yyy|zzz={{aaa=bbb|ccc=ddd}}|kkk=hhh
     * and xxx=yyy|zzz=[[aaa|bbb|ccc]]|kkk=hhh. 
     * @param argsString the String containing all the args (the part of a template contained after the first pipe).              
     * @return a Map associating each argument name with its value.          
     */
    public static Map<String,String> parseArgs(String argsString) {
	HashMap<String,String> argsMap = new HashMap<String,String>();
	if (null == argsString || "" == argsString) return argsMap;

	//locate wiki templates in argsString
	ArrayList<Pair> templatesLocation = locateEnclosedString(argsString,"{{","}}");
	//locate wiki links in argsString
	ArrayList<Pair> linksLocation = locateEnclosedString(argsString,"[[","]]");

	//split argsString by character "|" (unless character "|" is contained in a wiki template)
	//into argsArray
	ArrayList<String> argsArray = new ArrayList<String>();
	int i = 0, j = 0;//iterate over characters in string argsString
	while (j < argsString.length()-1){
	    if (argsString.charAt(j)=='|'){
		if (j==argsString.length()-1){
		    argsArray.add(argsString.substring(i,j).trim());
		} else {
		    Pair p = new Pair(j,j+1);
		    //System.out.format("%s\n", templatesLocation);
		    if (templatesLocation.size()==0 || (!(p.containedIn(templatesLocation)) && !(p.containedIn(linksLocation)))){
			argsArray.add(argsString.substring(i,j).trim());
			i = j+1;
		    }
		}
	    }
	    j++;

	}
	if (j==argsString.length()-1){
	    if (argsString.charAt(j)=='|'){
		argsArray.add(argsString.substring(i,j).trim());
	    } else { //includes case: argsString is a single character
		argsArray.add(argsString.substring(i,j+1).trim());
	    }
	}

	//then consider each argument argString
	//split each element of argsArray (i.e. each argument arg) by "=" (unless "=" is contained in a wiki template)
	//into argsMap, the returned map
	int n = 1; // number for positional args.
	String argString;
	for (int h = 0; h < argsArray.size(); h++) {//iterate over all arguments in argsArray
	    argString = argsArray.get(h); //an argument in argsArray
	    templatesLocation = locateEnclosedString(argString, "{{","}}");
	    j = 0;
	    while (j < argString.length()){//iterate over characters in string argString
		if (argString.charAt(j)=='='){
		    Pair p = new Pair(j, j+1);
		    if (templatesLocation.size()==0 || !(p.containedIn(templatesLocation))){
			if (j==argString.length()-1){
			    argsMap.put(argString.substring(0, j).trim(), "");
			} else {
			    argsMap.put(argString.substring(0, j).trim(), argString.substring(j+1, argString.length()).trim());
			}
			break;
		    }
		}
		j++;
	    }
	    if (j == argString.length()){//"=" not found in argument argString
		argsMap.put(""+n, argString);
		n++;
	    }
	}
	return argsMap;
    }

    public static String toParameterString(Map<String, String> parameterMap) {
	StringBuffer buf = new StringBuffer();
	for (Map.Entry<String, String> stringStringEntry : parameterMap.entrySet()) {
	    buf.append(stringStringEntry.getKey())
		.append("=")
		.append(stringStringEntry.getValue())
		.append("|");
	}
	if (buf.length() > 0) buf.delete(buf.length()-1,buf.length());
	return buf.toString();
    }
}
