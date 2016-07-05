package org.getalp.dbnary.wiki;

import java.util.*;
import org.getalp.dbnary.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

/** 
* @author someoneelse, pantaleo  
* 
*/

public class WikiTool {

    static Logger log = LoggerFactory.getLogger(WikiTool.class);
    //example:                                                                     
    //locateEnclosedString(s,"{{","}}", false) with:                                  
    //* s="string {{t}}" returns (9,10)                                              
    //* s= ="string {{with some {{text}} enclosed in curly {{brackets}}}}" returns (9,56)   
    //locateEnclosedString(s,"{{","}}", true) with:                                       
    //* s="string {{t}}" returns (7,18)                                                
    //* s= ="string {{with some {{text}} enclosed in curly {{brackets}}}}" returns (7,58)    
    public static ArrayList<Pair> locateEnclosedString(String s, String enclosingStringStart, String enclosingStringEnd){
        int eSS = enclosingStringStart.length();
        int eSE = enclosingStringEnd.length();
        int numberOfEnclosings = 0, start=-1, end=-1;
        ArrayList<Pair> toreturn = new ArrayList<Pair>();
        //boolean inside = false;
        for (int i=0; i+eSE<=s.length(); i++){
            if (i+eSS+eSE<=s.length()){
                if (s.substring(i,i+eSS).equals(enclosingStringStart)){
                    if (start == -1){
                        start = i;
                    }
                    //System.out.format("found start at %s\n", i);
                    numberOfEnclosings ++;
                    i+=eSS-1;
		}
	    }
	    if (s.substring(i,i+eSE).equals(enclosingStringEnd)){
		//                System.out.format("found %s\n", enclosingStringEnd);
	        numberOfEnclosings --;
	        if (numberOfEnclosings==0 && start!=-1){
		    end = i+eSE;
                    //System.out.format("found end at %s\n", end);
		    toreturn.add(new Pair(start,end));
		    start = -1;//initialize start               
	        }
	    }
        }
        return toreturn;
    }

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
     * Parse the args of a Template.  
     * @param args the String containing all the args (the part of a templae contained after the first pipe).
     * @return a Map associating each argument name with its value.
     */
    // Parse a string of args, like: xxx=yyy|zzz=ttt
    // This function can parse args like xxx=yyy|zzz={{aaa=bbb|ccc=ddd}}|kkk=hhh (this template argument is encountered in etymology sections
    // e.g. xxx="compound", zzz="word1"
    // or like xxx=yyy|zzz=[[aaa|bbb|ccc]]|kkk=hhh (this template argument is encountered in etymology sections  
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
        //System.out.format("argsMap=%s\n", argsMap);
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

