package org.getalp.blexisma.cli.servletinterogation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ServletFormatManager {
	public static String semanticAnalyseFormat(String ask, String lg) {
		Document doc = new Document();
		Element root = new Element("request");
		Element id = new Element("id");
		Element lang = new Element("language");
		Element title = new Element("title");
		Element text = new Element("text");
		XMLOutputter serializer = new XMLOutputter();
		String ret = null;
		
		id.addContent("1");
		title.addContent("");
		lang.addContent(lg);
		text.addContent(ask);
		
		root.addContent(id);
		root.addContent(lang);
		root.addContent(title);
		root.addContent(text);
		
		doc.setRootElement(root);
		
		

			try {
				ret = "data="+URLEncoder.encode(serializer.outputString(doc),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	
		return ret;
	}
	
	public static String proxFormat(String lemma, String rx, String lg) {
		Document doc = new Document();
		Element root = new Element("request");
		Element prox = new Element("prox");
		Element lang = new Element("language");
		Element regex = new Element("regex");
		XMLOutputter serializer = new XMLOutputter();
		String ret = null;
		
		prox.addContent(lemma);
		lang.addContent(lg);
		if (rx!=null) regex.addContent(rx);
		
		root.addContent(prox);
		root.addContent(lang);
		if (rx!=null) root.addContent(regex);
		
		
		doc.setRootElement(root);
		
		

			try {
				ret = "data="+URLEncoder.encode(serializer.outputString(doc),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	
		return ret;
	}
	
	public static String vectFormat(String lemma, String lg) {
		Document doc = new Document();
		Element root = new Element("request");
		Element vect = new Element("vectorlemma");
		Element lang = new Element("language");
		XMLOutputter serializer = new XMLOutputter();
		String ret = null;
		
		vect.addContent(lemma);
		lang.addContent(lg);
		
		root.addContent(vect);
		root.addContent(lang);
		
		doc.setRootElement(root);

		try {
				ret = "data="+URLEncoder.encode(serializer.outputString(doc),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	
		return ret;
	}
	
	public static String defFormat(String lemma, String lg) {
		Document doc = new Document();
		Element root = new Element("request");
		Element def = new Element("definition");
		Element lang = new Element("language");
		XMLOutputter serializer = new XMLOutputter();
		String ret = null;
		
		def.addContent(lemma);
		lang.addContent(lg);
		
		root.addContent(def);
		root.addContent(lang);
		
		doc.setRootElement(root);
		
		try {
				ret = "data="+URLEncoder.encode(serializer.outputString(doc),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	
		return ret;
	}
	
	public static String proxVectFormat(String xml, String lg, int nb) {
		SAXBuilder sb = new SAXBuilder();
		Document indoc = new Document();
		Document doc = new Document();
		Element root = new Element("request");
		Element prox = new Element("vectprox");
		Element nbprox = new Element("nb_prox");
		Element lang = new Element("language");
		
		try {
			indoc = sb.build(new ByteArrayInputStream(xml.getBytes()));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XMLOutputter serializer = new XMLOutputter();
		String ret = null;
		
		prox.addContent(indoc.getRootElement().getChildText("vect"));
		lang.addContent(lg);
		nbprox.addContent(new Integer(nb).toString());
		
		root.addContent(prox);
		root.addContent(lang);
		root.addContent(nbprox);
		
		doc.setRootElement(root);
		
		

			try {
				ret = "data="+URLEncoder.encode(serializer.outputString(doc),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		return ret;
	}
}
