package org.getalp.dbnary.zho;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.iso639.ISO639_3;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.getalp.dbnary.wiki.WikiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChineseTranslationExtractorWikiModel extends DbnaryWikiModel {
	
	private IWiktionaryDataHandler delegate;
	
	public ChineseTranslationExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public ChineseTranslationExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public void parseTranslationBlock(String block) {
		initialize();
		if (block == null) {
			return;
		}
		WikipediaParser.parse(block, this, true, null);
		initialize();
	}

	private String currentGloss = null;

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		if ("trad".equals(templateName)) {
			// Trad macro contains a set of translations with no usage note.
			String lang = normalizeLang(parameterMap.get("1"));
            for (Entry<String, String> kv : parameterMap.entrySet()) {
				if ("1".equals(kv.getKey())) continue;
				delegate.registerTranslation(lang, currentGloss, null, kv.getValue());
			}
		} else if ("xlatio".equals(templateName) || "trad-".equals(templateName)) {
			// xlatio and trad- macro contains a translation and a transcription.
			String lang = normalizeLang(parameterMap.get("1"));
			delegate.registerTranslation(lang, currentGloss, parameterMap.get("3"), parameterMap.get("2"));
		} else if ("t".equals(templateName) || "t+".equals(templateName)) {
			// t macro contains a translation, a transcription and an usage note.
			String lang = normalizeLang(parameterMap.get("1"));
			String usage = parameterMap.get("3");
			if (null == usage) usage = "";
			String transcription = parameterMap.get("tr");
			if (null == transcription) transcription = parameterMap.get("4");
			if (null == transcription) transcription = "";

			if (! transcription.equals("")) {
				usage = "(" + transcription + "), " + usage;
			}
			delegate.registerTranslation(lang, currentGloss, usage, parameterMap.get("2"));
		} else if ("tradini".equals(templateName)) {
			currentGloss = parameterMap.get("1");
			if (null != currentGloss) currentGloss = currentGloss.trim();
		} else if ("tradini-checar".equals(templateName)) {
			currentGloss = null;
		}  else if ("tradmeio".equals(templateName)) {
			// nop
		} else if ("tradfim".equals(templateName)) {
			currentGloss = null;
		} else if (ISO639_3.sharedInstance.getIdCode(templateName) != null) {
			// This is a template for the name of a language, just ignore it...
		} else {

		}
	}

	private String normalizeLang(String lang) {
		String normLangCode;
		if ((normLangCode = ISO639_3.sharedInstance.getIdCode(lang)) != null) {
			lang = normLangCode;
		}
		return lang;
	}

}
