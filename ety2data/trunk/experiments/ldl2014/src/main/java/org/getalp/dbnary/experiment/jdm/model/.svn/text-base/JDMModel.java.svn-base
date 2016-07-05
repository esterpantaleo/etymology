package org.getalp.dbnary.experiment.jdm.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class JDMModel {

	
	public static final String JDM_NS_PREFIX = "http://kaiko.getalp.org/jeuxdemots";
	public static final String DBNARY = JDM_NS_PREFIX+"#";
	public static final String LEMON = LemonOnt.getURI();
	public static final String LEXINFO = LexinfoOnt.getURI();

	public static final Resource lexEntryType;
	public static final Resource wordEntryType;
	public static final Resource phraseEntryType;

	public static final Resource lexicalFormType;
	public static final Resource lexicalSenseType;
	// protected Resource definitionType;
	// protected Resource lexicalEntryRelationType;

	public static final Property canonicalFormProperty;
	public static final Property lexicalVariantProperty;
	public static final Property writtenRepresentationProperty;


	// LEMON properties
	public static final Property posProperty;
	public static final Property lemonSenseProperty;
	public static final Property lemonDefinitionProperty;
	public static final Property lemonValueProperty;
	public static final Property languageProperty;
	public static final Property pronProperty;

	//LMF properties
	// protected Property formProperty;
	public static final Property targetLanguageProperty;
	public static final Property targetLanguageCodeProperty;
	public static final Property equivalentTargetProperty;
	public static final Property glossProperty;
	public static final Property usageProperty;
	// protected static final Property textProperty;
	public static final Property senseNumberProperty;
	// protected static final Property entryRelationTargetProperty;

	// protected static final Property entryRelationLabelProperty;

    //Jeux de Mots properties
    public static List<Property> jdmProperties;

    //Properties pertaining to relations
    public static Property relationNameProperty;
    public static Property relationExtNameProperty;
    public static Property relationInfoProperty;
    public static Property relationProperty;

    public static Resource relation;


	public static Model tBox;



    static {
		// Create T-Box and read rdf schema associated to it.
		tBox = ModelFactory.createDefaultModel();
		// InputStream fis = LemonBasedRDFDataHandler.class.getResourceAsStream("LMF-rdf-rev14.xml");
		// tBox.read( fis, LMF );
		InputStream lis = JDMModel.class.getResourceAsStream("lemon.ttl");
		tBox.read( lis, LEMON, "TURTLE");

		lexEntryType = tBox.getResource(LEMON + "LexicalEntry");
		wordEntryType = tBox.getResource(LEMON + "Word");
		phraseEntryType = tBox.getResource(LEMON + "Phrase");

		lexicalFormType = tBox.getResource(LEMON + "LexicalForm");
		lexicalSenseType = tBox.getResource(LEMON + "LexicalSense");
		canonicalFormProperty = tBox.getProperty(LEMON + "canonicalForm");
		lemonSenseProperty = tBox.getProperty(LEMON + "sense");
		lexicalVariantProperty = tBox.getProperty(LEMON + "lexicalVariant");
		writtenRepresentationProperty =  tBox.getProperty(LEMON + "writtenRep");
		lemonDefinitionProperty = tBox.getProperty(LEMON + "definition");
		lemonValueProperty = tBox.getProperty(LEMON + "value");
		languageProperty = tBox.getProperty(LEMON + "language");

		// definitionType = tBox.getResource(LMF + "Definition");
		// lexicalEntryRelationType = tBox.getResource(NS + "LexicalEntryRelation");

		// formProperty = tBox.getProperty(NS + "writtenForm");
		targetLanguageProperty = tBox.getProperty(DBNARY + "targetLanguage");
		targetLanguageCodeProperty = tBox.getProperty(DBNARY + "targetLanguageCode");
		equivalentTargetProperty = tBox.getProperty(DBNARY + "writtenForm");
		glossProperty = tBox.getProperty(DBNARY + "gloss");
		usageProperty = tBox.getProperty(DBNARY + "usage");
		// textProperty = tBox.getProperty(DBNARY + "text");
		senseNumberProperty = tBox.getProperty(DBNARY + "senseNumber");
		// entryRelationLabelProperty = tBox.getProperty(DBNARY + "label");
		// entryRelationTargetProperty = tBox.getProperty(DBNARY + "target");

		posProperty = tBox.getProperty(LEXINFO + "partOfSpeech");

		pronProperty = tBox.getProperty(LEXINFO + "pronunciation");

        jdmProperties = new ArrayList<>();


        relationProperty = tBox.getProperty(JDM_NS_PREFIX + "relationProperty");
        relationNameProperty = tBox.getProperty(JDM_NS_PREFIX+"relationNameProperty");
        relationExtNameProperty = tBox.getProperty(JDM_NS_PREFIX+"relationExtNameProperty");
        relationInfoProperty = tBox.getProperty(JDM_NS_PREFIX+"relationInfoProperty");

        relation = tBox.getResource(JDM_NS_PREFIX+"Relation");


	}
	
	
	protected static String uriEncode(String s) {
		StringBuffer res = new StringBuffer();
		uriEncode(s, res);
		return res.toString();
	}
	
	protected static void uriEncode(String s, StringBuffer res) {
		int i = 0;
		while (i != s.length()) {
			char c = s.charAt(i);
			if (Character.isSpaceChar(c))
				res.append('_');
			else if ((c >= '\u00A0' && c <= '\u00BF') ||
					(c == '<') || (c == '>') || (c == '%') ||
					(c == '"') || (c == '#') || (c == '[') || 
					(c == ']') || (c == '\\') || (c == '^') ||
					(c == '`') || (c == '{') || (c == '|') || 
					(c == '}') || (c == '\u00D7') || (c == '\u00F7')
					)
				try {
					res.append(URLEncoder.encode("" + c, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// Should never happen
					e.printStackTrace();
				}
			else if (Character.isISOControl(c))
				; // nop
			else if (c == '\u200e' || c == '\u200f') {
				; // ignore rRLM and LRM.
			} else
				res.append(c);
			i++;
		}
	}
	
	protected static String uriEncode(String s, String pos) {
		StringBuffer res = new StringBuffer();
		uriEncode(s, res);
		res.append("__");
		int i = 0;
		while (i != pos.length()) {
			char c = pos.charAt(i);
			if (Character.isSpaceChar(c))
				res.append('_');
			else if ((c >= '\u00A0' && c <= '\u00BF') ||
					(c == '<') || (c == '>') || (c == '%') ||
					(c == '"') || (c == '#') || (c == '[') || 
					(c == ']') || (c == '\\') || (c == '^') ||
					(c == '`') || (c == '{') || (c == '|') || 
					(c == '}') || (c == '\u00D7') || (c == '\u00F7') || 
					(c == '-') || (c == '_') || 
					Character.isISOControl(c))
				; // nop
			else if (c == '\u200e' || c == '\u200f') {
				; // ignore rRLM and LRM.
			} else
				res.append(c);
			i++;
		}
		return res.toString();
	}

}
