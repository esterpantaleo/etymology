package org.getalp.dbnary.ita;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;

public class ItalianPronunciationExtractorWikiModel extends DbnaryWikiModel {
	
	private IWiktionaryDataHandler delegate;
	
	public ItalianPronunciationExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public ItalianPronunciationExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public void parsePronunciation(String block) {
		initialize();
		if (block == null) {
			return;
		}
		WikipediaParser.parse(block, this, true, null);
		initialize();
	}

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		if ("IPA".equals(templateName)) {
            if (isValidPronunciation(parameterMap.get("4"))) delegate.registerPronunciation(parameterMap.get("4"), "it-fonipa");
            if (isValidPronunciation(parameterMap.get("3"))) delegate.registerPronunciation(parameterMap.get("3"), "it-fonipa");
            if (isValidPronunciation(parameterMap.get("2"))) delegate.registerPronunciation(parameterMap.get("2"), "it-fonipa");
            if (isValidPronunciation(parameterMap.get("1"))) delegate.registerPronunciation(parameterMap.get("1"), "it-fonipa");
		} if ("SAMPA".equals(templateName)) {
            // TODO !
		} else {
			// System.err.println("Called template: " + templateName + " while parsing translations of: " + this.getImageBaseURL());
			// Just ignore the other template calls (uncomment to expand the template calls).
			// super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}

    private boolean isValidPronunciation(String p) {
        return p != null && ! p.trim().equals("");
    }
}
