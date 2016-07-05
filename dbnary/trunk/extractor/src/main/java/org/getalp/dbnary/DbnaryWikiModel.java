package org.getalp.dbnary;

import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;

import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.*;

import info.bliki.wiki.model.WikiModelContentException;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.INamespace.INamespaceValue;
import info.bliki.wiki.namespaces.INamespace.NamespaceCode;
import org.getalp.dbnary.tools.CounterSet;
import org.xml.sax.InputSource;

import org.xml.sax.SAXException;
import java.io.IOException;

import org.w3c.dom.*;

import java.io.StringReader;
import info.bliki.wiki.filter.HTMLConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbnaryWikiModel extends WikiModel {

	private static Logger log = LoggerFactory.getLogger(DbnaryWikiModel.class);

	protected WiktionaryIndex wi = null;

	
	public DbnaryWikiModel(Locale locale, String imageBaseURL, String linkBaseURL) {
		this((WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
		
	public DbnaryWikiModel(WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(new Configuration(), locale, imageBaseURL, linkBaseURL);
		this.wi = wi;
	}

	private static DocumentBuilder docBuilder = null;
	private static InputSource docSource = null;

	
	/* @Override
	public void addCategory(String categoryName, String sortKey) {
		System.err.println("Called addCategory : " + categoryName);
		super.addCategory(categoryName, sortKey);
	}

	@Override
	public void addLink(String topicName) {
		System.err.println("Called addLink: " + topicName);
		super.addLink(topicName);
	}

	@Override
	public boolean addSemanticAttribute(String attribute, String attributeValue) {
		System.err.println("Called addSemanticAttribute : " + attribute);
		return super.addSemanticAttribute(attribute, attributeValue);
	}

	@Override
	public boolean addSemanticRelation(String relation, String relationValue) {
		System.err.println("Called addSemanticRelation");
		return super.addSemanticRelation(relation, relationValue);
	}

	@Override
	public void addTemplate(String template) {
		System.err.println("Called addTemplate: " + template);
		super.addTemplate(template);
	}

	@Override
	public void appendInternalLink(String topic, String hashSection,
			String topicDescription, String cssClass, boolean parseRecursive) {
		System.err.println("Called appendInternalLink: " + topic + "#" + hashSection);
		super.appendInternalLink(topic, hashSection, topicDescription, cssClass,
				parseRecursive);
	}


	@Override
	public void parseInternalImageLink(String imageNamespace,
			String rawImageLink) {
		System.err.println("Called parseInternalImageLink");
		super.parseInternalImageLink(imageNamespace, rawImageLink);
	}

	@Override
	public boolean replaceColon() {
		System.err.println("Called replaceColon");
		return super.replaceColon();
	}

	@Override
	public void setUp() {
		System.err.println("Called setUp");
		super.setUp();
	}
*/

	// get the DOM representation of the HTML code corresponding
	// to the wikicode given in arguments
	public Document wikicodeToHtmlDOM (String wikicode) {
		if (docBuilder == null) {
			try {
				docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				System.err.println("got a ParserConfigurationException in the DBnaryWikiModel class.");
				return null;
			}

			docSource = new InputSource();
		}

		String html = expandWikiCode(wikicode);

		docSource.setCharacterStream(new StringReader("<div>" + html + "</div>"));

		Document doc = null;

		try {
			doc = docBuilder.parse(docSource);
		} catch (SAXException e) {
			log.error("Unable to parse template call in DBnaryWikiModel.");
		} catch (IOException e) {
			log.error("got IOException in DBnaryWikiModel ‽");
		}

		return doc;
	}

	protected String expandWikiCode(String wikicode) {
		try {
			return render(new HTMLConverter(), wikicode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    protected static CounterSet trace = new CounterSet();

    public static void logCounters() {
        trace.logCounters(log);
    }
    
	@Override
    public String getRawWikiContent(ParsedPageName parsedPagename, Map<String, String> map)
			throws WikiModelContentException {
		String result = super.getRawWikiContent(parsedPagename, map);
		if (result != null) {
			// found magic word template
			return result;
		}
        trace.incr(parsedPagename.fullPagename());

        if (null != wi) {
			String rawText = wi.getTextOfPageWithRedirects(parsedPagename.fullPagename());
			if (parsedPagename.namespace.isType(NamespaceCode.TEMPLATE_NAMESPACE_KEY))
				rawText = prepareForTransclusion(rawText);
			String name;
			if (null == rawText && !(name = parsedPagename.pagename.trim()).equals(parsedPagename.pagename)) {
				rawText = wi.getTextOfPageWithRedirects(parsedPagename.namespace + ":" + name);
				if (parsedPagename.namespace.isType(NamespaceCode.TEMPLATE_NAMESPACE_KEY))
					rawText = prepareForTransclusion(rawText);
			}
			// TODO: should I try with: name = encodeTitleToUrl(articleName, true);
			return rawText;
		}

		log.debug("getRawWikiContent return null for {} in {}", parsedPagename.fullPagename(), this.getPageName());
		return null;
	}

    public String prepareForTransclusion(String rawWikiText) {
        if (null == rawWikiText) return null;

        int noIncludeOffset = rawWikiText.indexOf("<noinclude>");
        if (-1 != noIncludeOffset) {
            int noIncludeEndOffset = rawWikiText.indexOf("</noinclude>", noIncludeOffset);
            if (-1 != noIncludeEndOffset)
                return prepareForTransclusion(new StringBuffer().append(rawWikiText.substring(0, noIncludeOffset))
                        .append(rawWikiText.substring(noIncludeEndOffset + "</noinclude>".length())).toString());
        }
		int onlyIncludeOffset = rawWikiText.indexOf("<onlyinclude>");
		if (-1 != onlyIncludeOffset) {
			int onlyIncludeEndOffset = rawWikiText.indexOf("</onlyinclude>", onlyIncludeOffset);
			if (-1 != onlyIncludeEndOffset)
				return rawWikiText.substring(onlyIncludeOffset + "<onlyinclude>".length(), onlyIncludeEndOffset);
		}
		int includeOnlyOffset = rawWikiText.indexOf("<includeonly>");
        if (-1 != includeOnlyOffset) {
            int includeOnlyEndOffset = rawWikiText.indexOf("</includeonly>", noIncludeOffset);
            if (-1 != includeOnlyEndOffset) {
				String removeTags = new StringBuffer()
						.append(rawWikiText.substring(0, includeOnlyOffset))
						.append(rawWikiText.substring(includeOnlyOffset + "<includeonly>".length(), includeOnlyEndOffset))
						.append(rawWikiText.substring(includeOnlyEndOffset + "</includeonly>".length())).toString();
				return prepareForTransclusion(removeTags);
			}
		}
        return rawWikiText;

    }
}
