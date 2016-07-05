package org.getalp.dbnary.pol;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import info.bliki.wiki.filter.PlainTextConverter;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.ExpandAllWikiModel;
import org.getalp.dbnary.wiki.WikiTool;

public class DefinitionExpanderWikiModel extends ExpandAllWikiModel {

	static Set<String> ignoredTemplates = new HashSet<String>();
	
	static {
		ignoredTemplates.add("wikipedia");
	}
	
	public DefinitionExpanderWikiModel(Locale locale, String imageBaseURL,
			String linkBaseURL) {
		super(locale, imageBaseURL, linkBaseURL);
	}

	public DefinitionExpanderWikiModel(WiktionaryIndex wi, Locale locale,
			String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
	}

	@Override
	public String expandAll(String definition, Set<String> templates) {
		String def = WikiTool.removeReferencesIn(definition);
		return super.expandAll(def, templates);
	}

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
					throws IOException {
		if (ignoredTemplates.contains(templateName)) {
			;
		} else if ("skrót".equals(templateName)){
			writer.append("(").append(parameterMap.get("2")).append(")");
		} else {
			super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}
}
