package org.getalp.dbnary.zho;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.blexisma.api.ISO639_3;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IEventListener;
import info.bliki.wiki.model.WikiModel;

public class ChineseDefinitionExtractorWikiModel extends DbnaryWikiModel {

	private IWiktionaryDataHandler delegate;
	
	
	public ChineseDefinitionExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public ChineseDefinitionExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public void parseDefinition(String definition) {
		// Render the definition to plain text, while ignoring the example template
		String def = render(new PlainTextConverter(), definition).trim();
		if (null != def && ! def.equals(""))
			delegate.registerNewDefinition(def);
	}
	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		// Currently just expand the definition to get the full text.
		super.substituteTemplateCall(templateName, parameterMap, writer);
	}

}

