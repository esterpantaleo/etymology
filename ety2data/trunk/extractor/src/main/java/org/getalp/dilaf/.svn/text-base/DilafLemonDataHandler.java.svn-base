package org.getalp.dilaf;

import java.io.OutputStream;
import java.util.HashMap;

import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.tools.CounterSet;

import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DilafLemonDataHandler extends DbnaryModel {

	private String DILAF_NS = "http://kaiko.getalp.org/dilaf";
	private String twoLetterLanguageCode;
	private Resource lexvoLanguageElement;
	private Model aBox;
	private String NS;
	
	private CounterSet lexEntryCount = new CounterSet();
	private HashMap<String,Resource> lexicalEntries = new HashMap<String,Resource>();
	
	public DilafLemonDataHandler(String lang) {
		super();
		
		NS = DILAF_NS + "/" + lang + "/";
		
		twoLetterLanguageCode = LangTools.getPart1OrId(lang);
		lexvoLanguageElement = tBox.getResource(LEXVO + lang); 
		
		// Create aBox
		aBox = ModelFactory.createDefaultModel();

		aBox.setNsPrefix("dlf_" + lang, NS);
        aBox.setNsPrefix(lang, NS);
        aBox.setNsPrefix("dbnary", DBnaryOnt.getURI());
        aBox.setNsPrefix("lemon", LemonOnt.getURI());
        aBox.setNsPrefix("lexinfo", LexinfoOnt.getURI());
        aBox.setNsPrefix("rdfs", RDFS.getURI());
        aBox.setNsPrefix("dcterms", DCTerms.getURI());
        aBox.setNsPrefix("lexvo", LEXVO);
        aBox.setNsPrefix("rdf", RDF.getURI());
	}
	
	public Resource registerNewLexicalSense(String lemma, String pos, String pron, String senseNumber) {
		pos = normalizePartOfSpeech(pos);
		String encodedLemma = uriEncode(lemma + "__" + pos);
		Resource lexEntry = lexicalEntries.get(encodedLemma + "__" + pron) ;

		// get (and eventually create) the lexical entry;
		if (null == lexEntry) {
			int count = lexEntryCount.incr(encodedLemma);
			lexEntry = aBox.createResource(NS + encodedLemma + "__" + count);
			aBox.add(aBox.createStatement(lexEntry, RDF.type, LemonOnt.LexicalEntry));
			lexicalEntries.put(encodedLemma + "__" + pron, lexEntry);
			aBox.add(aBox.createStatement(lexEntry, DBnaryOnt.partOfSpeech, pos));
		}
		
		String lexEntryId = lexEntry.getLocalName();

		// Validate / eventually create a lexical form element
		// Check if the lexical form already exists before creating it...
		Statement alreadyRegisteredCanonicalForm = aBox.getProperty(lexEntry, LemonOnt.canonicalForm);
		if (null != alreadyRegisteredCanonicalForm) {
			// Check that it is the same form/pronounciation
			Statement oldWrittenRep = aBox.getProperty(alreadyRegisteredCanonicalForm.getResource(), LemonOnt.writtenRep );
			if (oldWrittenRep == null || ! oldWrittenRep.getString().equals(lemma)) {
				System.err.println("Old written representation is null or different from current representation.");
			}
			Statement oldPronunciation = aBox.getProperty(alreadyRegisteredCanonicalForm.getResource(), LexinfoOnt.pronunciation);
			if (oldPronunciation == null || ! oldPronunciation.getString().equals(pron)) {
				System.err.println("Old pronunciation is null or different from current representation.");
			}
		} else {
			Resource lexForm = aBox.createResource();
			aBox.add(aBox.createStatement(lexForm, LemonOnt.writtenRep, lemma, twoLetterLanguageCode));
			aBox.add(aBox.createStatement(lexForm, LexinfoOnt.pronunciation, pron));
			aBox.add(aBox.createStatement(lexEntry, LemonOnt.canonicalForm, lexForm));
		}
		// Create and register the lexical sense itself.
		Resource lexicalSense = aBox.createResource(createSenseId(lexEntryId, senseNumber));
		aBox.add(aBox.createStatement(lexEntry, RDF.type, LemonOnt.LexicalSense));
		aBox.add(aBox.createStatement(lexEntry, LemonOnt.sense, lexicalSense));
		aBox.add(aBox.createLiteralStatement(lexicalSense, DBnaryOnt.senseNumber, aBox.createTypedLiteral(Integer.parseInt(senseNumber))));

		
		return lexicalSense;
	}
	
	
	private String createSenseId(String encodedLemma, String senseNumber) {
		return NS + "__ws_" + senseNumber + "_" + encodedLemma;
	}

	private String normalizePartOfSpeech(String pos) {
		pos = pos.trim().replaceAll("\\.", "").replaceAll("/", "_");
		return pos;
	}

	public void dump(OutputStream out) {
		dump(out, null);
	}
    
	/**
	 * Write a serialized represention of this model in a specified language.
	 * The language in which to write the model is specified by the lang argument. 
	 * Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3". 
	 * The default value, represented by null, is "RDF/XML".
	 * @param out
	 * @param format
	 */
	public void dump(OutputStream out, String format) {
		aBox.write(out, format);
	}

	public void registerTranslations(Resource lexicalSense, String translations) {
		// TODO Auto-generated method stub
		
	}

	public void registerDefinition(Resource lexicalSense, String def) {
		// TODO Auto-generated method stub
		
	}

}
