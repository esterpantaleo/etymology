package org.getalp.dilaf;

import java.io.OutputStream;
import java.util.HashMap;

import org.getalp.dbnary.*;
import org.getalp.dbnary.tools.CounterSet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DilafLemonDataHandler extends DbnaryModel {

	private static final String DILAF_NS = "http://kaiko.getalp.org/dilaf";
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

		// aBox.setNsPrefix("dlf_" + lang, NS);
        aBox.setNsPrefix(lang, NS);
        aBox.setNsPrefix("dbnary", DBnaryOnt.getURI());
        aBox.setNsPrefix("lemon", LemonOnt.getURI());
        aBox.setNsPrefix("lexinfo", LexinfoOnt.getURI());
        aBox.setNsPrefix("olia", OliaOnt.getURI());
        aBox.setNsPrefix("rdfs", RDFS.getURI());
        aBox.setNsPrefix("dcterms", DCTerms.getURI());
        aBox.setNsPrefix("lexvo", LEXVO);
        aBox.setNsPrefix("rdf", RDF.getURI());
	}

    public Resource registerLexicalEntry(String id, String pos) {
        pos = normalizePartOfSpeech(pos);
        String encodedLexEntryURI = uriEncode(id, pos);
        Resource lexEntry = aBox.createResource(NS + encodedLexEntryURI, LemonOnt.LexicalEntry);
        aBox.add(aBox.createStatement(lexEntry, DBnaryOnt.partOfSpeech, pos));
        attacheLexInfoPOS(lexEntry, pos);
        return lexEntry;
    }


    private void attacheLexInfoPOS(Resource lexEntry, String pos) {
        switch (pos) {
            case "n":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Noun));
                break;
            case "adj":
            case "adjépith":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Adjective));
                break;
            case "adjpréd":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.PredicativeAdjective));
                break;
            case "v":
            case "vt":
            case "vi":
            case "vréf":
            case "vi_réf":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Verb));
                break;
            case "interj":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Interjection));
                break;
            case "n_adj":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Noun));
                break;
            case "adv":
                aBox.add(aBox.createStatement(lexEntry, LexinfoOnt.partOfSpeech, OliaOnt.Noun));
                break;
            default:
                System.err.format("Unknown POS: %s\n", pos);
        }
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

    public void setCanonicalForm(Resource lexicalEntry, String lemma) {
        Resource canonicalForm = aBox.createResource(lexicalEntry.getURI() + "__cf", LemonOnt.Form);
        aBox.add(aBox.createStatement(canonicalForm, LemonOnt.writtenRep, lemma, twoLetterLanguageCode));
        aBox.add(aBox.createStatement(lexicalEntry, LemonOnt.canonicalForm, canonicalForm));
    }

    public Resource registerLexicalSense(Resource lexicalEntry, String senseId, String terme, String usage, String nonUsage, String status, String emploi) {
        Resource lexicalSense = aBox.createResource(NS + senseId, LemonOnt.LexicalSense);
        aBox.add(aBox.createStatement(lexicalSense, RDF.type, LemonOnt.LexicalSense));
        aBox.add(aBox.createStatement(lexicalEntry, LemonOnt.sense, lexicalSense));
        // TODO: handle other attributes
        return lexicalSense;
    }

    public void registerDefinition(Resource sense, String text, String lang) {
        aBox.add(aBox.createStatement(sense, LemonOnt.definition, text, lang));
    }

    public Resource registerExample(Resource sense, String ba, String baTons, String fr, String usage) {
        // Create new word sense + a definition element
        Resource example = aBox.createResource();
        if (null != ba)
            aBox.add(aBox.createStatement(example, LemonOnt.value, ba, "bm"));
        if (null != fr)
            aBox.add(aBox.createStatement(example, LemonOnt.value, fr, "fr"));
        // TODO: how to represent bambara with tones ?
        if (null != usage)
            aBox.add(aBox.createStatement(example, DBnaryOnt.usage, usage, "bm"));

        aBox.add(aBox.createStatement(sense, LemonOnt.example, example));
        return example;
    }
}
