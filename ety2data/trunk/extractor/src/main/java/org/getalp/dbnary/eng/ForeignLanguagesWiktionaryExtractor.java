package org.getalp.dbnary.eng;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.iso639.ISO639_3;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForeignLanguagesWiktionaryExtractor extends WiktionaryExtractor {
    static Logger log = LoggerFactory.getLogger(ForeignLanguagesWiktionaryExtractor.class);

    protected final static String level2HeaderPatternString = "^==([^=].*[^=])==$";
    protected final static Pattern level2HeaderPattern;
    //protected final static Map<String, String> iso3LangMap= new HashMap() ;

    static {
        level2HeaderPattern = Pattern.compile(level2HeaderPatternString, Pattern.MULTILINE);
    //  for (ISO639_3.Lang l : ISO639_3.sharedInstance.knownLanguages()) {
    //      iso3LangMap.put(l.getEn(), l.getId());
    //  }
    }

    private ForeignLanguagesWiktionaryDataHandler flwdh; // English specific version of the data handler.

    public ForeignLanguagesWiktionaryExtractor(IWiktionaryDataHandler wdh) {
	super(wdh);
        if (wdh instanceof ForeignLanguagesWiktionaryDataHandler) {
            flwdh = (ForeignLanguagesWiktionaryDataHandler) wdh;
        } else {
            log.error("Foreign Language Wiktionary Extractor instanciated with a non foreign language data handler!");
        }
    }

    @Override
    public void extractData() {
        Matcher l1 = level2HeaderPattern.matcher(pageContent);
	int nonEnglishSectionStart = -1;
	wdh.initializePageExtraction(wiktionaryPageName);
        String lang = null;
	while (l1.find()) {
	    if (-1 != nonEnglishSectionStart) {
		// Parsing a previous non english section;
	        extractNonEnglishData(lang, nonEnglishSectionStart, l1.start());
	        nonEnglishSectionStart = -1;
	    }
	    ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	    if (null != (lang = getNonEnglishLanguageCode(l1))) {
		nonEnglishSectionStart = l1.end();
	    }
            //lang = EnglishLangToCode.getNonEnglishLanguageCode(l1.group(1).trim());
                       
            //if (lang != null){
	    //	nonEnglishSectionStart = l1.end();
	    //} else {
	    //   log.debug("Language header not recognized: ignoring language section {}", l1.group(1).trim());
	    //}
	}
	
	if (-1 != nonEnglishSectionStart) {
           extractNonEnglishData(lang, nonEnglishSectionStart, pageContent.length());
        }
        wdh.finalizePageExtraction();
    }

    private String getNonEnglishLanguageCode(Matcher l1) {
	String t = l1.group(1).trim();
	if (t.equals("English"))
	    return null;
	else {
	    String c = EnglishLangToCode.threeLettersCode(t);
	    if (null == c) log.debug("Unknown language : {} in {}", t, this.wiktionaryPageName);
	    return c;
	}
    }

    protected void extractNonEnglishData(String lang, int startOffset, int endOffset) {
	flwdh.setCurrentLanguage(lang);
        super.extractEnglishData(startOffset, endOffset);
    }
}
