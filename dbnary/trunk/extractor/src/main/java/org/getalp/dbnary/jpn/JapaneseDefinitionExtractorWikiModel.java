package org.getalp.dbnary.jpn;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.model.WikiModelContentException;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.Namespace;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.WiktionaryIndex;

import info.bliki.wiki.filter.PlainTextConverter;
import org.getalp.dbnary.wiki.WikiTool;

public class JapaneseDefinitionExtractorWikiModel extends DbnaryWikiModel {

    // static Set<String> ignoredTemplates = new TreeSet<String>();
    // static {
    // 	ignoredTemplates.add("Wikipedia");
    // 	ignoredTemplates.add("Incorrect");
    // }

    private IWiktionaryDataHandler delegate;


    public JapaneseDefinitionExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
        this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
    }

    public JapaneseDefinitionExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
        super(wi, locale, imageBaseURL, linkBaseURL);
        this.delegate = we;
    }

    public void parseDefinition(String definition, int defLevel) {
        // Render the definition to plain text, while ignoring the example template
        String def = WikiTool.removeReferencesIn(definition);
        try {
            def = render(new PlainTextConverter(), def).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != def && !def.equals(""))
            delegate.registerNewDefinition(def, defLevel);
    }

    @Override
    public String getRawWikiContent(ParsedPageName parsedPagename, Map<String, String> map)
            throws WikiModelContentException {
        // BUGFIX for incorrect gwtwiki invocation of module User:Codecat/isValidPagename
        if (parsedPagename.namespace.toString().equals("User") && parsedPagename.pagename.equals("CodeCat/isValidPageName")) {
            return getRawWikiContent(new ParsedPageName(this.getNamespace().getModule(), "User:" + parsedPagename.pagename, true), map);
        }
        return super.getRawWikiContent(parsedPagename, map);
    }

    @Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		// Currently just expand the definition to get the full text.
		super.substituteTemplateCall(templateName, parameterMap, writer);
	}

}
