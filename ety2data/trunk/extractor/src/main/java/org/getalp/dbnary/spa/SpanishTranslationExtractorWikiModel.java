package org.getalp.dbnary.spa;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.WiktionaryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpanishTranslationExtractorWikiModel extends DbnaryWikiModel {
	
	private IWiktionaryDataHandler delegate;
	
	private Logger log = LoggerFactory.getLogger(SpanishTranslationExtractorWikiModel.class);

	public SpanishTranslationExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public SpanishTranslationExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
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


	private final static String senseNumberOrRangeRegExp = "(?:[\\s\\d\\,\\-—–\\?]|&ndash;)+" ;
	private final static Pattern senseNumberOrRangePattern = Pattern.compile(senseNumberOrRangeRegExp);
	private final Matcher senseNumberOrRangeMatcher = senseNumberOrRangePattern.matcher("");
	private static Set<String> gender = new HashSet<String>();
	static {
		gender.add("m");
		gender.add("f");
		gender.add("mf");
		gender.add("n");
		gender.add("c");
	}
	private static Set<String> pos = new HashSet<String>();
	static {
		pos.add("adj");
		pos.add("adj.");
		pos.add("sust.");
		pos.add("sust");
		pos.add("verb.");
		pos.add("verb");
		pos.add("adj & sust");
		pos.add("adj. & sust.");
		pos.add("adj. y sust.");
		pos.add("sust. y adj.");
		pos.add("sust y adj");
		pos.add("adj y sust");
		pos.add("sust & verb");
		pos.add("sust. & verb.");
		pos.add("sust. y verb.");
		pos.add("verb & sust");
		pos.add("verb. & sust.");
		pos.add("verb. y sust.");
		pos.add("sust y verb");
	}

	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		if ("t+".equals(templateName)) {
			String lang = LangTools.normalize(parameterMap.get("1"));
			int i = 2;
			String s = null;
			String usage = "", trans = null, currentGloss = null;
			if (parameterMap.get("tr") != null) usage = usage + "|tr=" + parameterMap.get("tr");
			while (i != 31 && (s = parameterMap.get(""+i)) != null) {
				s = s.trim();
				senseNumberOrRangeMatcher.reset(s);
                if ("".equals(s)) {
                    // Just ignore empty parameters
                } else if (",".equals(s)) {
					if (usage.length() == 0) {
						usage = null;
					} else {
						usage = usage.substring(1);
					}
					if (null != trans) delegate.registerTranslation(lang, currentGloss, usage, trans);
					trans = null;
					usage = "";
				} else if (senseNumberOrRangeMatcher.matches()) {
					// the current item is a senseNumber or range
					if (null != trans && null != currentGloss) {
						log.debug("Missing Comma after translation (was {}) when parsing a new gloss in {}", trans, delegate.currentLexEntry());
						if (usage.length() == 0) {
							usage = null;
						} else {
							usage = usage.substring(1);
						}
						delegate.registerTranslation(lang, currentGloss, usage, trans);
						trans = null;
						usage = "";
					}
					currentGloss = s;
				} else if (gender.contains(s)) {
					usage = usage + "|" + s;
				} else if ("p".equals(s)) {
					// plural
					usage = usage + "|" + s;
				} else if ("nota".equals(s)) {
					// nota
					i++;
					s = parameterMap.get(""+i);
					usage = usage + "|" + "nota=" + s;
				} else if (pos.contains(s)) {
					// Part Of Speech of target
					usage = usage + "|" + s;
				} else if ("tr".equals(s)) {
					// transcription
					i++;
					s = parameterMap.get(""+i);
					if (null != s && ! "".equals(s)) usage = usage + "|" + "tr=" + s;
				} else if ("nl".equals(s)) {
					// ?
					i++;
					s = parameterMap.get(""+i);
					usage = usage + "|" + "nl=" + s;
				} else {
					// translation
					if (null != trans) log.debug("Non null translation (was {}) when registering new translation {} in {}", trans, s, delegate.currentLexEntry());
					trans = s;
				}
				i++;
			}
			if (null != trans) {
				if (usage.length() == 0) {
					usage = null;
				} else {
					usage = usage.substring(1);
				}
				delegate.registerTranslation(lang, currentGloss, usage, trans);
			}
		} else if ("trad-arriba".equals(templateName)) {
			// nop
		} else if ("trad-centro".equals(templateName)) {
			// nop
		} else if ("trad-abajo".equals(templateName)) {
			// nop
		} else if ("l".equals(templateName)) {
            // Catch l template and expand it correctly as the template is now expanded before the
            // enclosing template
            int i = 2;
            StringBuffer text = new StringBuffer();
            String s;
            while (i <= 31 && (s = parameterMap.get(""+i)) != null) {
                s = s.trim();
                if (",".equals(s)) {
                    text.append(",");
                    // ignore next parameter which is the language
                    i++;
                } else {
                    text.append(s);
                }
                i++;
            }
            writer.append(text);
        } else {
			log.debug("Called template: {} while parsing translations of: {}", templateName, this.getImageBaseURL());
			// Just ignore the other template calls (uncomment to expand the template calls).
			// super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}
}
