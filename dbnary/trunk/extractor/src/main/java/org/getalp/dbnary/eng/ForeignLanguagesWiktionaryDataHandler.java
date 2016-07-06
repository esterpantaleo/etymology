package org.getalp.dbnary.eng;

import java.util.HashMap;

import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.PronunciationPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForeignLanguagesWiktionaryDataHandler extends WiktionaryDataHandler {

    private Logger log = LoggerFactory.getLogger(ForeignLanguagesWiktionaryDataHandler.class);

    public ForeignLanguagesWiktionaryDataHandler(String lang) {
	super(lang);
    }
	
    public void initializeEntryExtraction(String wiktionaryPageName, String lang) {
	setCurrentLanguage(lang);
        super.initializeEntryExtraction(wiktionaryPageName);
    }

    @Override
    public void finalizeEntryExtraction() {
	currentEntryLanguage = null;
        currentPrefix = null;
    }

    @Override
    public String currentLexEntry() {
	// TODO Auto-generated method stub
        return currentWiktionaryPageName;
    }
	
    @Override
    public void registerPronunciation(String pron, String lang) {
        // Catch the call for foreign languages and disregard passed language
        lang = extractedLang + "-fonipa";
        if (null == currentCanonicalForm) {
            currentSharedPronunciations.add(new PronunciationPair(pron, lang));
        } else {
            registerPronunciation(currentCanonicalForm, pron, lang);
        }
    }

}
