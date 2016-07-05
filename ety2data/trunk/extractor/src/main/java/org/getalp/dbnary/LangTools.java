package org.getalp.dbnary;

import org.getalp.iso639.ISO639_3;
import org.getalp.iso639.ISO639_3.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LangTools {
    static Logger log = LoggerFactory.getLogger(LangTools.class);

    //h is a map mapping language name to language code that complements ISO639_3
    //defined in eng/EnglishLangToCode.java
    //same for other languages different than English
    public static String threeLettersCode(java.util.HashMap<String,String> h, String s) {
	if (s == null || s.equals("")) {
			return s;
		}

		s = s.trim();//.toLowerCase();//why to lower case?? e.g.: LL.
		String res = ISO639_3.sharedInstance.getIdCode(s);

		if (res == null && h != null && h.containsKey(s)){
		    s = h.get(s);
		    res = ISO639_3.sharedInstance.getIdCode(s);
                    if (res == null) return s;
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
	    String normLang = ISO639_3.sharedInstance.getIdCode(lang);
            return normLang == null? lang : normLang;
	}
	
    //private static String normalize(String languageCode, String fallback) {
    //		String normLanguageCode = getCode(languageCode);
    //
    //          return normLanguageCode == null? fallback : normLanguageCode;
    //}

	public static String inEnglish(String lang) {
		return ISO639_3.sharedInstance.getLanguageNameInEnglish(lang);
	}

	public static String getTerm2Code(String l) {
		return ISO639_3.sharedInstance.getTerm2Code(l);
	}


}
