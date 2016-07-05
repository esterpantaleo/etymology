/**
 * 
 */
package org.getalp.dbnary.eng;

import java.util.HashMap;

import org.getalp.dbnary.LangTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pantaleo
 *
 */
public class EnglishLangToCode extends LangTools {
    static Logger log = LoggerFactory.getLogger(EnglishLangToCode.class);

    public static String threeLettersCode(String languageCode){
        return threeLettersCode(WiktionaryLang.codeMap, languageCode);
    }

    public static String getNonEnglishLanguageCode(String wiktionaryLanguageHeader) {
        // log.debug("Considering header == {}",langInEng.group(1));      
        if (wiktionaryLanguageHeader.equals("English")){
	    return null;
        }
        String toreturn = getLanguageCode(wiktionaryLanguageHeader);
        if (toreturn==null){
	    log.debug("language {} not found in wiktionary language tab file", wiktionaryLanguageHeader);
        }
        return toreturn;
    }

    public static String getLanguageCode(String languageName){
        String languageCode = WiktionaryLang.data.map.get(languageName);
        if (languageCode == null){//language name not found in wiktionary language file            
           languageCode = threeLettersCode(null, languageCode);
	}
        return languageCode;
    }

    //public static String getLanguageName(String languageCode){
    //  log.debug("in function getLanguageName languageCode {} ", languageCode);
    //String languageName = WiktionaryLang.data.inverseMap.get(languageCode);
    //log.debug("language name for language code {} in WiktionaryLang is {}", languageCode, languageName);
    //if (languageName == null){//look into iso3 set of names
    //    languageName = LangTools.inEnglish(languageCode);
    //      log.debug("language name for language code {} in iso3 is {}", languageCode, languageName);
    //}
    //  return languageName;
    //}

    
}