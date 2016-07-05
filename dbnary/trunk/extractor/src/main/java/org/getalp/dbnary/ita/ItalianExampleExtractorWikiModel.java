package org.getalp.dbnary.ita;

import info.bliki.wiki.filter.PlainTextConverter;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ItalianExampleExtractorWikiModel extends DbnaryWikiModel {

	// static Set<String> ignoredTemplates = new TreeSet<String>();
	// static {
	// 	ignoredTemplates.add("Wikipedia");
	// 	ignoredTemplates.add("Incorrect");
	// }

	private IWiktionaryDataHandler delegate;

	public ItalianExampleExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL, String pageName) {
		super(wi, locale, imageBaseURL, linkBaseURL);
        this.setPageName(pageName);
		this.delegate = we;
	}

	public void parseExample(String example) {
		// Render the definition to plain text, while ignoring the example template
		String ex = null;
		try {
			ex = render(new PlainTextConverter(), example).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != ex && ! ex.equals(""))
			delegate.registerExample(ex, null);
	}
	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		// Currently just expand the definition to get the full text.
		super.substituteTemplateCall(templateName, parameterMap, writer);
	}

}
