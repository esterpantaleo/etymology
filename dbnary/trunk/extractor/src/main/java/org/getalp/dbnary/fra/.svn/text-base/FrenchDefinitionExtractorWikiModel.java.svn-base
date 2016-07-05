package org.getalp.dbnary.fra;

import info.bliki.wiki.filter.PlainTextConverter;
import org.getalp.iso639.ISO639_3;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class FrenchDefinitionExtractorWikiModel extends DbnaryWikiModel {

    private Logger log = LoggerFactory.getLogger(FrenchDefinitionExtractorWikiModel.class);

    // static Set<String> ignoredTemplates = new TreeSet<String>();
	// static {
	// 	ignoredTemplates.add("Wikipedia");
	// 	ignoredTemplates.add("Incorrect");
	// }

	private IWiktionaryDataHandler delegate;


	public FrenchDefinitionExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}

	public FrenchDefinitionExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	public void parseDefinition(String definition, int defLevel) {
		// Render the definition to plain text, while ignoring the example template
        log.trace("extracting definitions in {}", this.getPageName());
		String def = null;
		try {
			def = render(new PlainTextConverter(), definition).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != def && ! def.equals(""))
			delegate.registerNewDefinition(def, defLevel);
	}
	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		// Currently just expand the definition to get the full text.
		if (templateName.equals("nom langue") || templateName.endsWith(":nom langue")) {
            // intercept this template as it leads to a very inefficient Lua Script.
            String langCode = parameterMap.get("1").trim();
            String lang = ISO639_3.sharedInstance.getLanguageNameInFrench(langCode);
            if (null != lang) writer.append(lang);
        } else if (templateName.contains("langues")) {
            log.debug("Got template {}\tin\t{}", templateName, this.getPageName());
        } else if ("pron".equals(templateName)) {
            // Ignore it as pronunciation is extracted independantly.
        } else {
            super.substituteTemplateCall(templateName, parameterMap, writer);
        }
	}

}
