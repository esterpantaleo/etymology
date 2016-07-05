package org.getalp.dbnary.eng;

import java.io.*;   
import java.util.*;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;   

/**
 * @author pantaleo
 *         Support class for language codes that are recognised by Wiktionary
 */
public class WiktionaryLang {
    public static HashMap<String,String> map;
    public static HashMap<String,String> codeMap;
    private final static String linePatternString = "^(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)$"; 
    private final static Pattern linePattern = Pattern.compile(linePatternString); 
    public static WiktionaryLang data = new WiktionaryLang();

    private WiktionaryLang() {

        HashMap<String,String> newMap = new HashMap();
	HashMap<String,String> newCodeMap = new HashMap();
        InputStream fis = null;
        try { 
            String url = this.getClass().getResource("").getPath();
            fis = this.getClass().getResourceAsStream("eng_wiktionary_list_of_languages.tab");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8")); 
	   
            Matcher matcher = linePattern.matcher(new String("")); 
            
            String s = br.readLine();
            while (s != null) { 
	        matcher.reset(s);
		if (matcher.find()) {
                    //MAKE A MAP:
                    //FULL_NAME_OF_LANGUAGE (canonical or not) --> CODE (ISO or not)
                    //if multiple codes are associated to the same language, take the first one!
                    String[] codes = matcher.group(1).split(",");
                    String code = codes[0].trim();
                    //put "Canonical name" and "Code" in newMap  
                    String canonicalName = matcher.group(2);
                    newMap.put(canonicalName, code);
                    //for each "Other names" put name and "Code" in newMap
                    if (!(matcher.group(5).trim().equals(""))){ 
                        for (String otherNames: matcher.group(5).split(",")){
                            newMap.put(otherNames.trim(), code);
			}
		    }
                    //if in "Code" coluum here are multiple codes, map them to the first in the list
                    if (codes.length>1){
			for (int i=1; i<codes.length; i++){
			    newCodeMap.put(codes[i].trim(), codes[0].trim());
			}
		    }
		}
                s = br.readLine();
	    }
            map = newMap;
            codeMap = newCodeMap;
	} catch (UnsupportedEncodingException e) { 
	    // This should really never happen      
	} catch (IOException e) {     
	    // don't know what I should do here, as the data should be bundled with the code.
	    e.printStackTrace(); 
	} finally {
	    if (fis != null)
		try {     
		    fis.close();    
		} catch (IOException e) {   
		    // nop
		}
	}       
    }
}