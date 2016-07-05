package org.getalp.dbnary;

import java.io.OutputStream;
import java.util.Map;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface IWiktionaryDataHandler {

	public enum Feature {MAIN, MORPHOLOGY};
    /**
     * Enable the extraction of morphological data in a second Model if available.
     */
    void enableFeature(Feature f);

    public void initializePageExtraction(String wiktionaryPageName);
    public void finalizePageExtraction();

    public void initializeEntryExtraction(String wiktionaryPageName);
    public void initializeEntryExtraction(String wiktionaryPageName, String lang);
    public void finalizeEntryExtraction();

    public void addPartOfSpeech(String pos);

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
	public void registerNewDefinition(String def);
	
	/**
	 * Register definition def for the current lexical entry. 
	 * 
	 * This method will compute a sense number based on the rank of the definition in 
	 * the entry, taking into account the level of the definition. 1, 1a, 1b, 1c, 2, etc.
	 * 
	 * @param def the definition string
	 * @param lvl an integer giving the level of the definition (1 or 2).
	 */
	public void registerNewDefinition(String def, int lvl);
	
	/**
	 * Register example ex for the current lexical sense. 
	 * 
	 * @param ex the example string
	 * @param context map of property + object that are to be attached to the example object.
	 */
	public Resource registerExample(String ex, Map<Property, String> context);

	
	/**
	 * Register definition def for the current lexical entry. 
	 * 
	 * This method will use senseNumber as a sense number for this definition.
	 * 
	 * @param def the definition string
	 * @param senseNumber a string giving the sense number of the definition.
	 */
	public void registerNewDefinition(String def, String senseNumber);



	public void registerAlternateSpelling(String alt);
    
    public void registerNymRelation(String target, String synRelation);
    
    public void registerNymRelation(String target, String synRelation, String gloss);
    public void registerNymRelation(String target, String synRelation, String gloss,String usage);


    public void registerTranslation(String lang, String currentGlose, String usage, String word);

    public void registerPronunciation(String pron, String lang);

	public int nbEntries();
	
	public String currentLexEntry();

	/**
	 * Write a serialized represention of this model in a specified language.
	 * The language in which to write the model is specified by the lang argument. 
	 * Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3". 
	 * The default value, represented by null, is "RDF/XML".
	 * @param out
	 * @param format
	 */
	public void dump(Feature f, OutputStream out, String format);

	public void registerNymRelationOnCurrentSense(String target, String synRelation);
	public void registerPropertyOnLexicalEntry(Property p, RDFNode r);

	public void registerPropertyOnCanonicalForm(Property p, RDFNode r);

	public void registerInflection(String languageCode,
	                               String pos,
	                               String inflection,
	                               String canonicalForm,
	                               int defNumber,
	                               HashSet<PropertyObjectPair> properties,
	                               HashSet<PronunciationPair> pronunciations);

	public void registerInflection(String languageCode,
	                               String pos,
	                               String inflection,
	                               String canonicalForm,
	                               int defNumber,
	                               HashSet<PropertyObjectPair> properties);

	public int currentDefinitionNumber();

	public String currentWiktionaryPos();
    public Resource currentLexinfoPos();

	

}
