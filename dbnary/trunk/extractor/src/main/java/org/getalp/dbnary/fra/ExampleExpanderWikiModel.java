package org.getalp.dbnary.fra;

import info.bliki.wiki.filter.PlainTextConverter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.ExpandAllWikiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ExampleExpanderWikiModel extends ExpandAllWikiModel {

	static Set<String> ignoredTemplates = new HashSet<String>();
	static Logger log = LoggerFactory.getLogger(ExampleExpanderWikiModel.class);
	
	static {
		// ignoredTemplates.add("wikipedia");
	}

	private Map<Property, String> context;
	private ExpandAllWikiModel simpleExpander;

	public ExampleExpanderWikiModel(WiktionaryIndex wi, Locale locale,
			String imageBaseURL, String linkBaseURL) {
        super(wi, locale, imageBaseURL, linkBaseURL);
        simpleExpander = new ExpandAllWikiModel(wi, locale, imageBaseURL, linkBaseURL);
    }

    @Override
    public void setPageName(String pageTitle) {
        super.setPageName(pageTitle);
        simpleExpander.setPageName(pageTitle);
    }

    /**
	 * Convert an example wiki code to plain text, while keeping track of all template calls and of context definition (source, etc.).
	 * @param definition the wiki code
	 * @param templates if not null, the method will add all called templates to the set.
	 * @param context if not null, the method will add all contextual relation to the map.
	 * @return
	 */
	public String expandExample(String definition, Set<String> templates, Map<Property, String> context) {
		log.trace("extracting examples in {}", this.getPageName());
		this.context = context;
		return expandAll(definition, templates);
	}
	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
					throws IOException {
		if (ignoredTemplates.contains(templateName)) {
			; // NOP
		} else if ("source".equals(templateName)) {
			if (context != null) {
				String source = simpleExpander.expandAll(parameterMap.get("1"), this.templates);
				context.put(DBnaryOnt.exampleSource, source);
				parameterMap.remove("1");
				if (! parameterMap.isEmpty()) {
					log.debug("Non empty parameter map {} in {}", parameterMap, this.getPageName());
				}
			}
		} else {
			log.debug("Caught template call: {} --in-- {}", templateName, this.getPageName());
			super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}
	
	@Override
	public void addCategory(String categoryName, String sortKey) {
		log.debug("Called addCategory : " + categoryName);
		super.addCategory(categoryName, sortKey);
	}
}
