package org.getalp.dbnary.eng;

import java.util.HashMap;

import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.PronunciationPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForeignLanguagesWiktionaryDataHandler extends WiktionaryDataHandler {

	private Logger log = LoggerFactory.getLogger(ForeignLanguagesWiktionaryDataHandler.class);
	
	private HashMap<String,String> prefixes = new HashMap<String,String>();

	private String currentEntryLanguage = null;
	private String currentPrefix = null;

	public ForeignLanguagesWiktionaryDataHandler(String lang) {
		super(lang);
	}
	
	public void initializeEntryExtraction(String wiktionaryPageName, String lang) {
		setCurrentLanguage(lang);
		super.initializeEntryExtraction(wiktionaryPageName);
    }

	public void setCurrentLanguage(String lang) {
		extractedLang = LangTools.getPart1OrId(lang);

		lexvoExtractedLanguage = tBox.createResource(LEXVO + lang);
		currentEntryLanguage = lang;
		currentPrefix = getPrefixe(lang);
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
	public String getPrefix() {
		return currentPrefix;
	}
	
	public String getPrefixe(String lang){
		if(this.prefixes.containsKey(lang))
			return this.prefixes.get(lang);

		lang = LangTools.normalize(EnglishLangToCode.threeLettersCode(lang));
		String prefix = DBNARY_NS_PREFIX + "/eng/" + lang + "/";
		prefixes.put(lang, prefix);
		aBox.setNsPrefix(lang + "-eng", prefix);
		return prefix;
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
