package org.getalp.dbnary;

import org.getalp.iso639.ISO639_3;
import org.getalp.iso639.ISO639_3.Lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * @author ?, pantaleo
 *  
 */
public class LangTools {

    private static Logger log = LoggerFactory.getLogger(LemonBasedRDFDataHandler.class);
    
    /**
     * @param h a map mapping language name to language code that complements ISO639_3; 
     * h is defined in eng/EnglishLangToCode.java
     * @param s the language name (e.g., "Afrikaans")
     * @return the three letter language code, if available (e.g., "afr") 
     * otherwise returns the two letter language code, if available (e.g., "af"), 
     * otherwise returns the input language name s and prints a warning
     */
    public static String threeLettersCode(java.util.HashMap<String,String> h, String s) {
	//if the input language name is null or empty return null or empty
        if (s == null || s.equals("")) {
	    log.debug("empty or null language string");
	    return s;
	}
	
	//parse the input language name s, get the corresponding code from
	//ISO639_3.sharedInstance.getIdCode
	s = s.trim();
	s = s.toLowerCase();
        String res = getCode(s); 

	//if the input language name s is not in ISO639_3.sharedInstance.getIdCode
	//look up in the input HashMap
	if (res == null){
	    if (h != null && h.containsKey(s)) {
	        s = h.get(s); //example: if s == "Afrikaans", h.get(s)="af"; getCode(s)="afr"
	        res = getCode(s);
      
	        if (res == null){
		    log.debug("language {} not found in wiktionary language tab file", s);
		    return s;
	        }
	    } else {
		log.debug("language {} not found in wiktionary language tab file, using {}", s, s);
		return s;
	    }
	}

	return res;
    }

    public static String threeLettersCode(String s) {
	return threeLettersCode(null, s);
    }

    public static String getCode(String lang) {
        return ISO639_3.sharedInstance.getIdCode(lang);
    }

    public static String getPart1(String language) {
        Lang l = ISO639_3.sharedInstance.getLang(language);

        if (l == null) {
            return null;
        }
        return l.getPart1();
    }

    public static String getPart1OrId(String lang) {
        Lang l = ISO639_3.sharedInstance.getLang(lang);

        if (l == null) {
            return null;
        }
	String p1 = l.getPart1();
	return (null != p1 && ! "".equals(p1.trim())) ? l.getPart1() : l.getId();
    }

    public static String normalize(String lang) {
	return normalize(lang, lang);
    }
	
    private static String normalize(String lang, String fallback) {
	String normLangCode = getCode(lang);

	if (normLangCode == null) {
	    return fallback;
	}

        return normLangCode;
    }

    public static String inEnglish(String lang) {
	return ISO639_3.sharedInstance.getLanguageNameInEnglish(lang);
    }

    public static String getTerm2Code(String l) {
	return ISO639_3.sharedInstance.getTerm2Code(l);
    }
}
