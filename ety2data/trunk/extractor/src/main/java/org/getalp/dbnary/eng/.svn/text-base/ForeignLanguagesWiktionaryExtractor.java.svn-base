package org.getalp.dbnary.eng;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.iso639.ISO639_3;
import org.getalp.dbnary.IWiktionaryDataHandler;


public class ForeignLanguagesWiktionaryExtractor extends WiktionaryExtractor {

	protected final static String level2HeaderPatternString = "^==([^=].*[^=])==$";
	protected final static Pattern level2HeaderPattern;
    protected final static Map<String, String> enNames2Lang= new HashMap() ;

    static {
		level2HeaderPattern = Pattern.compile(level2HeaderPatternString, Pattern.MULTILINE);
        for (ISO639_3.Lang l : ISO639_3.sharedInstance.knownLanguages()) {
            enNames2Lang.put(l.getEn(), l.getId());
        }
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
			// System.err.println(l1.group());
			if (-1 != nonEnglishSectionStart) {
				// Parsing a previous non english section;
				extractNonEnglishData(lang, nonEnglishSectionStart, l1.start());
				nonEnglishSectionStart = -1;
			}
			if (null != (lang = getNonEnglishLanguageCode(l1))) {
				nonEnglishSectionStart = l1.end();
			}
		}
		if (-1 != nonEnglishSectionStart) {
			//System.err.println("Parsing previous italian entry");
            extractNonEnglishData(lang, nonEnglishSectionStart, pageContent.length());
		}
		wdh.finalizePageExtraction();
	}

    private String getNonEnglishLanguageCode(Matcher l1) {
        // log.debug("Considering header == {}",l1.group(1));
        String t = l1.group(1).trim();
        if (t.equals("English"))
            return null;
        else
            return enNames2Lang.get(t);
    }

	protected void extractNonEnglishData(String lang, int startOffset, int endOffset) {
		flwdh.setCurrentLanguage(lang);
        super.extractEnglishData(startOffset, endOffset);
	}



}
