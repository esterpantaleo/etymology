package org.getalp.dbnary;

import java.io.OutputStream;
import java.util.Map;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface IWiktionaryDataHandler {

	enum Feature {MAIN, MORPHOLOGY};
    /**
     * Enable the extraction of morphological data in a second Model if available.
     */
    void enableFeature(Feature f);
	boolean isEnabled(Feature f);

    void initializePageExtraction(String wiktionaryPageName);
    void finalizePageExtraction();

    void initializeEntryExtraction(String wiktionaryPageName);
    void initializeEntryExtraction(String wiktionaryPageName, String lang);
    void finalizeEntryExtraction();

    void addPartOfSpeech(String pos);

    /**
     * 
     * @param def the not cleaned up version of the definition. This version contains macros (that may represent subject fields) and links.
     */
    // TODO: maybe pass the cleaned up and the original def, so that the extractor takes what fits its requirements.
    
   /**
    * Register definition def for the current lexical entry. 
    * 
    * This method will compute a sense number based on the rank of the definition in 
    * the entry.
    * 
    * It is equivalent to registerNewDefinition(def, 1);
    * @param def
    */
	void registerNewDefinition(String def);
	
	/**
	 * Register definition def for the current lexical entry. 
	 * 
	 * This method will compute a sense number based on the rank of the definition in 
	 * the entry, taking into account the level of the definition. 1, 1a, 1b, 1c, 2, etc.
	 * 
	 * @param def the definition string
	 * @param lvl an integer giving the level of the definition (1 or 2).
	 */
	void registerNewDefinition(String def, int lvl);
	
	/**
	 * Register example ex for the current lexical sense. 
	 * 
	 * @param ex the example string
	 * @param context map of property + object that are to be attached to the example object.
	 */
	Resource registerExample(String ex, Map<Property, String> context);

	
	/**
	 * Register definition def for the current lexical entry. 
	 * 
	 * This method will use senseNumber as a sense number for this definition.
	 * 
	 * @param def the definition string
	 * @param senseNumber a string giving the sense number of the definition.
	 */
	void registerNewDefinition(String def, String senseNumber);



	void registerAlternateSpelling(String alt);
    
    void registerNymRelation(String target, String synRelation);
    
    void registerNymRelation(String target, String synRelation, String gloss);
    void registerNymRelation(String target, String synRelation, String gloss,String usage);


    void registerTranslation(String lang, String currentGlose, String usage, String word);

    void registerPronunciation(String pron, String lang);

	int nbEntries();
	
	String currentLexEntry();

	/**
	 * Write a serialized represention of this model in a specified language.
	 * The language in which to write the model is specified by the lang argument. 
	 * Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3". 
	 * The default value, represented by null, is "RDF/XML".
	 * @param out
	 * @param format
	 */
	void dump(Feature f, OutputStream out, String format);

	void registerNymRelationOnCurrentSense(String target, String synRelation);
	void registerPropertyOnLexicalEntry(Property p, RDFNode r);

	void registerPropertyOnCanonicalForm(Property p, RDFNode r);

	void registerInflection(String languageCode,
	                        String pos,
	                        String inflection,
	                        String canonicalForm,
	                        int defNumber,
	                        HashSet<PropertyObjectPair> properties,
	                        HashSet<PronunciationPair> pronunciations);

	void registerInflection(String languageCode,
	                               String pos,
	                               String inflection,
	                               String canonicalForm,
	                               int defNumber,
	                               HashSet<PropertyObjectPair> properties);

	int currentDefinitionNumber();

	String currentWiktionaryPos();
    Resource currentLexinfoPos();

	

}
