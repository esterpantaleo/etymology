package org.getalp.dbnary.por;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;

public class PortugueseMorphoExtractorWikiModel extends DbnaryWikiModel {
	
	static Set<String> russianPOS = new TreeSet<String>();
	static {
		russianPOS.add("сущ");
		russianPOS.add("прил");
		russianPOS.add("гл");
		russianPOS.add("adv");
		russianPOS.add("мест");
		russianPOS.add("числ");
	}
	
	private IWiktionaryDataHandler delegate;
	private boolean hasAPOS = false;
	
	public PortugueseMorphoExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public PortugueseMorphoExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public boolean parseMorphoBlock(String block) {
		initialize();
		if (block == null) {
			return false;
		}
		WikipediaParser.parse(block, this, true, null);
		initialize();
		boolean r = hasAPOS;
		hasAPOS = false;
		return r;
	}

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		String pos = getPOS(templateName);
		if (null != pos) {
			// This is a macro specifying the part Of Speech
			// TODO: extract other morphological information ?
			hasAPOS = true;
            delegate.addPartOfSpeech(pos);
		} else {
			// Just ignore the other template calls (uncomment to expand the template calls).
			// super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}
	
	private String getPOS(String templateName) {
		for (String p : russianPOS) {
			if (templateName.startsWith(p)) return p;
		}
		return null;
	}

}
