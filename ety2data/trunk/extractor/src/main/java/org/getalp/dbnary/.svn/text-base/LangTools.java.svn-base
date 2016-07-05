package org.getalp.dbnary;

import org.getalp.iso639.ISO639_3;
import org.getalp.iso639.ISO639_3.Lang;

public class LangTools {
	public static String threeLettersCode(java.util.HashMap<String,String> h, String s) {
		if(s == null || s.equals("")) {
			return s;
		}

		s= s.trim();
		s=s.toLowerCase();
		String res = getCode(s);

		if (res == null && h != null && h.containsKey(s)) {
			s = h.get(s);
			res = getCode(s);
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
		return (null != l.getPart1()) ? l.getPart1() : l.getId();
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
