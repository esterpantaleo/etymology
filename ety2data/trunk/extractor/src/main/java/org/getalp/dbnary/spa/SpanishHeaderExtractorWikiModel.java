package org.getalp.dbnary.spa;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpanishHeaderExtractorWikiModel extends DbnaryWikiModel {
	
	private IWiktionaryDataHandler delegate;
	
	private Logger log = LoggerFactory.getLogger(SpanishHeaderExtractorWikiModel.class);

	public SpanishHeaderExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public SpanishHeaderExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public void parseHeaderBlock(String block) {
		initialize();
		if (block == null) {
			return;
		}
		WikipediaParser.parse(block, this, true, null);
		initialize();
	}


	String[] pronunciationVariants = new String[] {"s", "c", "ll", "y", "yc", "ys", "lls", "llc"};


	private boolean isApi(String s) {
		if (s == null) return true;
		s = s.toLowerCase().trim();
		return s.equals("-") || s.equals("afi") || s.equals("");
	}

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		if ("pronunciación".equals(templateName)) {
            String p1;
				if ((p1 = parameterMap.get("1")) != null && p1.length() > 0) {
					if (isApi(parameterMap.get("2"))) {
						if (! p1.equals("-") && ! p1.equals("&nbsp;"))
							delegate.registerPronunciation(parameterMap.get("1"), "es-ipa");
						parameterMap.remove("1"); parameterMap.remove("2");
						// DONE: maybe register alternate pronunciations
						for (String p : pronunciationVariants) {
							if (parameterMap.get(p) != null) {
								delegate.registerPronunciation(parameterMap.get(p), "es-" + p + "-ipa");
								parameterMap.remove(p);
							}
						}
						parameterMap.remove("leng"); parameterMap.remove("lang");
						if (parameterMap.size() != 0) log.debug("Remaining pronunciations : {} in {}", parameterMap, this.getImageBaseURL());
					} else {
						log.debug("Unknown pronunciation transcription {} in {}", parameterMap.get("2"), this.getImageBaseURL());
					}
				}
		} 
	}

}
