package org.getalp.dbnary.bul;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.WiktionaryIndex;

import info.bliki.wiki.filter.PlainTextConverter;

public class DefinitionsWikiModel extends DbnaryWikiModel {
		
	private Set<String> templates = null;
	
	public DefinitionsWikiModel(Locale locale, String imageBaseURL, String linkBaseURL) {
		this((WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public DefinitionsWikiModel(WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
	}
	
	public DefinitionsWikiModel(WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL, Set<String> templates) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.templates = templates;
	}

	/**
	 * Convert a wiki code to plain text, while keeping track of all template calls.
	 * @param definition the wiki code
	 * @return
	 */
	public String expandAll(String definition) {
		try {
			return render(new PlainTextConverter(), definition).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Set<String> getTemplates() {
		return templates;
	}

	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
					throws IOException {
		if (templates != null) templates.add(templateName);
		if ("Noun".equalsIgnoreCase(templateName)) return;
		if ("Adverb".equalsIgnoreCase(templateName)) return;
		if ("Verb".equalsIgnoreCase(templateName)) return;
		if ("Adjective".equalsIgnoreCase(templateName)) return;
		
		// TODO: void Noun, and other templates.
		super.substituteTemplateCall(templateName, parameterMap, writer);
	}
	
}
