package org.getalp.dbnary;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

import org.getalp.dbnary.tools.CounterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import javax.xml.bind.DatatypeConverter;
import java.util.AbstractMap.SimpleImmutableEntry;

public class LemonBasedRDFDataHandler extends DbnaryModel implements IWiktionaryDataHandler {

    public ArrayList<Resource> etymologyPos;
    public String etymologyString;
	protected static class PosAndType {
		protected Resource pos;
		protected Resource type;
		public PosAndType(Resource p, Resource t) {this.pos = p; this.type = t;}
	}
	private Logger log = LoggerFactory.getLogger(LemonBasedRDFDataHandler.class);
	
	protected Model aBox;
        protected Map<Feature,Model> featureBoxes;

	// States used for processing    
	protected Resource currentLexEntry;
	protected Resource currentLexinfoPos;
	protected String currentWiktionaryPos;
        private String currentEntryLanguageCode = null;
        private String currentPrefix = null;
    
	protected Resource currentSense;
	protected int currentSenseNumber;
	protected int currentSubSenseNumber;
        protected int currentEtymologyNumber;
	protected CounterSet translationCount = new CounterSet();
	private CounterSet reifiedNymCount = new CounterSet();
	protected String extractedLang;
	protected Resource lexvoExtractedLanguage;
        protected String mainLanguageIsoCode;
        protected Resource currentEtymologyEntry;
        public HashMap<String,String> prefixes = new HashMap<String,String>();
    
	private Set<Statement> heldBackStatements = new HashSet<Statement>();

	protected int nbEntries = 0;
	private String NS;
	protected String currentEncodedPageName;
	protected String currentWiktionaryPageName;
	protected CounterSet currentLexieCount = new CounterSet();
	protected Resource currentMainLexEntry;
	protected Resource currentCanonicalForm;
	
	protected Set<PronunciationPair> currentSharedPronunciations;
//	private String currentSharedPronunciation;
//	private String currentSharedPronunciationLang;

	private HashMap<SimpleImmutableEntry<String,String>, HashSet<HashSet<PropertyObjectPair>>> heldBackOtherForms = new HashMap<SimpleImmutableEntry<String,String>, HashSet<HashSet<PropertyObjectPair>>>();

	private static HashMap<String,Property> nymPropertyMap = new HashMap<String,Property>();
	protected static HashMap<String,PosAndType> posAndTypeValueMap = new HashMap<String,PosAndType>();

	static {
				
		nymPropertyMap.put("syn", DBnaryOnt.synonym);
		nymPropertyMap.put("ant", DBnaryOnt.antonym);
		nymPropertyMap.put("hypo", DBnaryOnt.hyponym);
		nymPropertyMap.put("hyper", DBnaryOnt.hypernym);
		nymPropertyMap.put("mero", DBnaryOnt.meronym);
		nymPropertyMap.put("holo", DBnaryOnt.holonym);
		nymPropertyMap.put("qsyn", DBnaryOnt.approximateSynonym);
        nymPropertyMap.put("tropo", DBnaryOnt.troponym);

		// Portuguese
		posAndTypeValueMap.put("Substantivo", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Adjetivo", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Verbo", new PosAndType(LexinfoOnt.verb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Advérbio", new PosAndType(LexinfoOnt.adverb, LemonOnt.LexicalEntry));

		// Italian
		posAndTypeValueMap.put("noun", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("sost", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));

		posAndTypeValueMap.put("adjc", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("agg", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("verb", new PosAndType(LexinfoOnt.verb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LemonOnt.LexicalEntry));

		// Finnish
		posAndTypeValueMap.put("Substantiivi", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Adjektiivi", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Verbi", new PosAndType(LexinfoOnt.verb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Adverbi", new PosAndType(LexinfoOnt.adverb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("Erisnimi", new PosAndType(LexinfoOnt.properNoun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("subs", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("adj", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("verbi", new PosAndType(LexinfoOnt.verb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LemonOnt.LexicalEntry));
		
		// Greek
		posAndTypeValueMap.put("επίθετο", new PosAndType(LexinfoOnt.adjective, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("επίρρημα", new PosAndType(LexinfoOnt.adverb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("ουσιαστικό", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("ρήμα", new PosAndType(LexinfoOnt.verb, LemonOnt.LexicalEntry));
		posAndTypeValueMap.put("κύριο όνομα", new PosAndType(LexinfoOnt.properNoun, LemonOnt.LexicalEntry));

	//	posAndTypeValueMap.put("", new PosAndType(null, LemonOnt.LexicalEntry)); // other Part of Speech

	}
	
	// Map of the String to lexvo language entity
    private HashMap<String,Resource> languages = new HashMap<String, Resource>();//////!!!new

    public String getPrefixe(String lang){
	return getPrefixe(lang, false);
    }

    public String getPrefixe(String languageIsoCode, Boolean dontcheck){
	if (dontcheck)
	    return this.prefixes.get(languageIsoCode);
	if(this.prefixes.containsKey(languageIsoCode)){
	    return this.prefixes.get(languageIsoCode);
	}
	String prefix = DBNARY_NS_PREFIX + "/" + languageIsoCode + "/" + mainLanguageIsoCode + "/";
	prefixes.put(languageIsoCode, prefix);
	aBox.setNsPrefix(languageIsoCode + "-" + mainLanguageIsoCode, prefix);
	return prefix;
    }

        @Override
	public String getMainLanguageIsoCode(){
	    return mainLanguageIsoCode;
	}

        @Override
	public String getCurrentEntryLanguageCode(){
	    return currentEntryLanguageCode;
	}

    public void setCurrentLanguage(String lang) {
	setCurrentLanguage(lang, false);
    }

    public void setCurrentLanguage(String languageCode, Boolean dontcheck) {
	if (languageCode == null){
	    return;
	}
	if (languageCode.equals(mainLanguageIsoCode)){
	    currentEntryLanguageCode = null;
	    currentPrefix = null;
	    return;
	}
	if (!currentEntryLanguageCode.equals(languageCode)){
	    currentEntryLanguageCode = languageCode;
	    currentPrefix = getPrefixe(languageCode, dontcheck);
	}
    }

    public String getPrefix() {
	if (currentPrefix != null){
	    return currentPrefix;
	}
	return NS;
    }
    
	
	public LemonBasedRDFDataHandler(String lang) {
		super();
		NS = DBNARY_NS_PREFIX + "/" + lang + "/";
                mainLanguageIsoCode = lang; 
		extractedLang = LangTools.getPart1OrId(lang);

		lexvoExtractedLanguage = tBox.createResource(LEXVO + lang);
		
		// Create aBox
		aBox = ModelFactory.createDefaultModel();
        aBox.setNsPrefix(lang, NS);
        aBox.setNsPrefix("dbnary", DBnaryOnt.getURI());
        aBox.setNsPrefix("lemon", LemonOnt.getURI());
        aBox.setNsPrefix("lexinfo", LexinfoOnt.getURI());
        aBox.setNsPrefix("rdfs", RDFS.getURI());
        aBox.setNsPrefix("dcterms", DCTerms.getURI());
        aBox.setNsPrefix("lexvo", LEXVO);
        aBox.setNsPrefix("rdf", RDF.getURI());
		aBox.setNsPrefix("olia", OliaOnt.getURI());


		featureBoxes = new HashMap<>();
        featureBoxes.put(Feature.MAIN, aBox);
	}

    @Override
    public void enableFeature(Feature f) {
        Model box = ModelFactory.createDefaultModel();
        fillInPrefixes(aBox, box);
        featureBoxes.put(f, box);
    }

    @Override
    public boolean isEnabled(Feature f) {
        return featureBoxes.containsKey(f);
    }

    @Override
    public void initializePageExtraction(String wiktionaryPageName) {
        currentLexieCount.resetAll();
    }

    @Override
    public void finalizePageExtraction() {

    }

    @Override
    public void extractEtymology(String pageContent, int start, int end){
	etymologyString = pageContent.substring(start, end);
    }

    @Override
    public void cleanEtymology(){
	etymologyString = null;
	etymologyPos.clear();
	currentEtymologyEntry = null;
    }

    @Override
    public String getEtymology(){
	return etymologyString;
    }
    
    private void fillInPrefixes(Model aBox, Model morphoBox) {
        for (Map.Entry<String, String> e : aBox.getNsPrefixMap().entrySet()) {
            morphoBox.setNsPrefix(e.getKey(), e.getValue());
        }
    }

    @Override
	public void initializeEntryExtraction(String wiktionaryPageName) {
        currentSense = null;
        currentSenseNumber = 0;
        currentSubSenseNumber = 0;
        currentWiktionaryPageName = wiktionaryPageName;
        currentLexinfoPos = null;
        currentWiktionaryPos = null;
        translationCount.resetAll();
        reifiedNymCount.resetAll();
        currentCanonicalForm = null;
        currentSharedPronunciations = new HashSet<PronunciationPair>();

        // Create a dummy lexical entry that points to the one that corresponds to a part of speech
        currentMainLexEntry = getVocableResource(wiktionaryPageName, true);


        // Retain these statements to be inserted in the model when we know that the entry corresponds to a proper part of speech
        heldBackStatements.add(aBox.createStatement(currentMainLexEntry, RDF.type, DBnaryOnt.Vocable));

        currentEncodedPageName = null;
        currentLexEntry = null;
    }

	@Override
	public void finalizeEntryExtraction() {
		// Clear currentStatements. If statemenents do exist-s in it, it is because, there is no extractable part of speech in the entry.
		heldBackStatements.clear();
		promoteNymProperties();
	}

	public static String getEncodedPageName(String pageName, String pos, int defNumber) {
		return uriEncode(pageName, pos) + "__" + defNumber;
	}

	public Resource getLexEntry(String languageCode, String pageName, String pos, int defNumber) {
		//FIXME this doesn't use its languageCode parameter
		return getLexEntry(
			getEncodedPageName(pageName, pos, defNumber),
			typeResource(pos)
		);
	}

	public Resource getLexEntry(String encodedPageName, Resource typeResource) {
		return aBox.createResource(getPrefix() + encodedPageName, typeResource);
	}

	public int currentDefinitionNumber() {
		return currentLexieCount.get(currentWiktionaryPos);
	}

    @Override
	public String currentWiktionaryPos() {
		return currentWiktionaryPos;
	}

    @Override
    public Resource currentLexinfoPos() {
        return currentLexinfoPos;
    }

	public Resource addPartOfSpeech(String originalPOS, Resource normalizedPOS, Resource normalizedType) {
        // DONE: create a LexicalEntry for this part of speech only and attach info to it.
		currentWiktionaryPos = originalPOS;
		currentLexinfoPos = normalizedPOS;
		
        nbEntries++;

		currentEncodedPageName = getEncodedPageName(currentWiktionaryPageName, originalPOS, currentLexieCount.incr(currentWiktionaryPos));
		currentLexEntry = getLexEntry(currentEncodedPageName, normalizedType);

        if (! normalizedType.equals(LemonOnt.LexicalEntry)) {
            // Add the Lexical Entry type so that users may refer to all entries using the top hierarchy without any reasoner.
            aBox.add(aBox.createStatement(currentLexEntry, RDF.type, LemonOnt.LexicalEntry));
        }
        
		// import other forms
		SimpleImmutableEntry<String,String> keyOtherForms = new SimpleImmutableEntry<String,String>(currentWiktionaryPageName, originalPOS);
		HashSet<HashSet<PropertyObjectPair>> otherForms = heldBackOtherForms.get(keyOtherForms);

        // TODO: check that other forms point to valid entries and log faulty entries for wiktionary correction.
		if (otherForms != null) {
			for (HashSet<PropertyObjectPair> otherForm : otherForms) {
				addOtherFormPropertiesToLexicalEntry(currentLexEntry, otherForm);
			}
		}

        // All translation numbers are local to a lexEntry
        translationCount.resetAll();
        reifiedNymCount.resetAll();

        currentCanonicalForm = aBox.createResource(getPrefix() + "__cf_" + currentLexEntry.getLocalName(), LemonOnt.Form);
        
        // If a pronunciation was given before the first part of speech, it means that it is shared amoung pos/etymologies
        for (PronunciationPair p : currentSharedPronunciations) {
            if (null != p.lang && p.lang.length() > 0)  {
                aBox.add(currentCanonicalForm, LexinfoOnt.pronunciation, p.pron, p.lang);
            } else {
                aBox.add(currentCanonicalForm, LexinfoOnt.pronunciation, p.pron);
            }
        }

		aBox.add(currentLexEntry, LemonOnt.canonicalForm, currentCanonicalForm);
		aBox.add(currentCanonicalForm, LemonOnt.writtenRep, currentWiktionaryPageName, extractedLang);
		aBox.add(currentLexEntry, DBnaryOnt.partOfSpeech, currentWiktionaryPos);
		if (null != currentLexinfoPos)
			aBox.add(currentLexEntry, LexinfoOnt.partOfSpeech, currentLexinfoPos);

		aBox.add(currentLexEntry, LemonOnt.language, extractedLang);
		aBox.add(currentLexEntry, DCTerms.language, lexvoExtractedLanguage);

		// Register the pending statements.
		for (Statement s: heldBackStatements) {
			aBox.add(s);
		}
		heldBackStatements.clear();
		aBox.add(currentMainLexEntry, DBnaryOnt.refersTo, currentLexEntry);
        return currentLexEntry;
	}

	public Resource posResource(PosAndType pat) {
		return (null == pat) ? null : pat.pos;
	}

	public Resource typeResource(PosAndType pat) {
		return (pat == null) ? LemonOnt.LexicalEntry : pat.type;
	}

	public Resource posResource(String pos) {
		return posResource(posAndTypeValueMap.get(pos));
	}

	public Resource typeResource(String pos) {
		return typeResource(posAndTypeValueMap.get(pos));
	}

	@Override
	public void addPartOfSpeech(String pos) {
		PosAndType pat = posAndTypeValueMap.get(pos);
		addPartOfSpeech(pos, posResource(pat), typeResource(pat));
	}

    private String computeEtymologyId(int etymologyNumber) {
	return  getPrefix() + uriEncode(currentWiktionaryPageName) + "__Etymology_" + etymologyNumber;
    }

            @Override
	    //type = 0 etymologically equivalent
	    //type = 1 etymologically derives from
	    //type = 2 derives from
	    //type = 3 descendent of
	    public boolean registerEtymology(Map<String, String> args1, Map<String, String> args2, int type){
		if (args1.size() == 0 || args2.size() == 0){
		    return false;
		}
		System.out.println("currentEtymologyEntry="+currentEtymologyEntry+"\n");
		System.out.println("args1="+args1+"args2="+args2+"\n");
		if (currentEtymologyEntry != null){
		    currentEtymologyNumber ++;
		    currentEtymologyEntry = aBox.createResource(computeEtymologyId(currentEtymologyNumber), DBnaryOnt.EtymologyEntry);
		    System.out.println("etymologyPos.size()="+etymologyPos.size()+"/n"); 
		    for (int i=0; i<etymologyPos.size(); i++){
			aBox.add(currentEtymologyEntry, DBnaryOnt.refersTo, etymologyPos.get(i));
		    }
		}

		System.out.println("currentEntryLanguageCode, mainLanguageIsoCode="+currentEntryLanguageCode+","+mainLanguageIsoCode+"\n");
		String currentLanguageCode = currentEntryLanguageCode == null? mainLanguageIsoCode : currentEntryLanguageCode;
		if (type == 0 || type == 1){
		    setCurrentLanguage(args1.get("lang"));
		}
		System.out.println("currentEntryLanguageCode, mainLanguageIsoCode="+currentEntryLanguageCode+","+mainLanguageIsoCode+"\n");
		System.out.println("args1.get(word1).split(,)[0].trim()="+args1.get("word1").split(",")[0].trim()+"\n");
		Resource vocable1;
		if (args1.get("isCurrentEtymologyEntry")!=null){
		    vocable1 = currentEtymologyEntry;
		} else {
		    vocable1 = getVocableResource(args1.get("word1").split(",")[0].trim(), true);
		}
		//if args2 represents a compound word return true
		int counter = 0;
		for (String key : args2.keySet()){
		    if (key.startsWith("word")){
			counter++;
			if (type == 0){
			    if (counter > 1){
				//it cannot be a compound word
				System.out.format("Warning: word etymologically equivalent to a compound word; returning\n");
				break;
			    }
			} else if (type == 1 || type == 2 || type == 3){
			    setCurrentLanguage(args2.get("lang"));
			}
			if (type == 0 || type == 1){
			    //split args2.get(key) and for each entry register it as an etymology entry and as etymologically equivalent to entry 0
			    String[] words = args2.get(key).split(",");
			    //entry 0
			    System.out.println("words[0].trim()="+words[0].trim()+"\n");
			    Resource vocable2_0 = getVocableResource(words[0].trim(), true);
			    aBox.add(vocable2_0, RDF.type, DBnaryOnt.EtymologyEntry);
			    if (type == 0){
				aBox.add(vocable1, DBnaryOnt.etymologicallyEquivalentTo, vocable2_0);
			    } else if (type == 1){
				aBox.add(vocable1, DBnaryOnt.etymologicallyDerivesFrom, vocable2_0);
			    }
			    if (words.length>1){
				for (int i=1; i<words.length; i++){
				    Resource vocable2 = getVocableResource(words[i].trim(), true);
				    aBox.add(vocable2, RDF.type, DBnaryOnt.EtymologyEntry);
				    aBox.add(vocable2_0, DBnaryOnt.etymologicallyEquivalentTo, vocable2);
				}
			    }
			} else if (type == 2 || type == 3){
			    Resource vocable2 = getVocableResource(args2.get(key), true);
			    aBox.add(vocable2, RDF.type, DBnaryOnt.EtymologyEntry);
			    if (type == 2){
				aBox.add(vocable2, DBnaryOnt.derivesFrom, vocable1);
			    } else if (type == 3){
				aBox.add(vocable2, DBnaryOnt.descendsFrom, vocable1);
			    }
			}
		    }
		}
		setCurrentLanguage(currentLanguageCode, true);
		return counter > 1 ? true : false;
	    }
    
	@Override
	public void registerPropertyOnCanonicalForm(Property p, RDFNode r) {
		if (null == currentLexEntry) {
			log.debug("Registering property when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}

		Resource canonicalForm = currentLexEntry.getPropertyResourceValue(LemonOnt.canonicalForm);

		if (canonicalForm == null) {
			log.debug("Registering property when lex entry's canonicalForm is null in \"{}\".", this.currentMainLexEntry);
			return;
		}

		aBox.add(canonicalForm, p, r);
	}


	@Override
	public void registerPropertyOnLexicalEntry(Property p, RDFNode r) {
		if (null == currentLexEntry) {
			log.debug("Registering property on null lex entry in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}

		aBox.add(currentLexEntry, p, r);
	}


	@Override
    public void registerAlternateSpelling(String alt) {
		if (null == currentLexEntry) {
			log.debug("Registering Alternate Spelling when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}

    	Resource altlemma = aBox.createResource();
    	aBox.add(currentLexEntry, LemonOnt.lexicalVariant, altlemma);
    	aBox.add(altlemma, LemonOnt.writtenRep, alt, extractedLang);
    }
    
	@Override
	public void registerNewDefinition(String def) {
		this.registerNewDefinition(def, 1);
	}

    @Override
	public void registerNewDefinition(String def, int lvl) {
		if (null == currentLexEntry) {
			log.debug("Registering Word Sense when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}
		if (lvl > 1) {
			currentSubSenseNumber++;
		} else {
            currentSenseNumber++;
            currentSubSenseNumber = 0;
		}
        registerNewDefinition(def, computeSenseNum());
    }

	public void registerNewDefinition(String def, String senseNumber) {
		if (null == currentLexEntry) {
			log.debug("Registering Word Sense when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}
		
		// Create new word sense + a definition element 
    	currentSense = aBox.createResource(computeSenseId(senseNumber), LemonOnt.LexicalSense);
    	aBox.add(currentLexEntry, LemonOnt.sense, currentSense);
    	aBox.add(aBox.createLiteralStatement(currentSense, DBnaryOnt.senseNumber, aBox.createTypedLiteral(senseNumber)));
    	// pos is not usefull anymore for word sense as they should be correctly linked to an entry with only one pos.
    	// if (currentPos != null && ! currentPos.equals("")) {
        //	aBox.add(currentSense, LexinfoOnt.partOfSpeech, currentPos);
        //}

    	Resource defNode = aBox.createResource();
    	aBox.add(currentSense, LemonOnt.definition, defNode);
    	// Keep a human readable version of the definition, removing all links annotations.
    	aBox.add(defNode, LemonOnt.value, AbstractWiktionaryExtractor.cleanUpMarkup(def, true), extractedLang);

    	// TODO: Extract domain/usage field from the original definition.

	}

	private String computeSenseId(String senseNumber) {
		return getPrefix() + "__ws_" + senseNumber + "_" + currentEncodedPageName;
	}
	
	private String computeSenseNum() {
		return "" + currentSenseNumber + ((currentSubSenseNumber == 0) ? "" : (char) ('a' + currentSubSenseNumber - 1));
	}

    protected Resource registerTranslationToEntity(Resource entity, String lang, String currentGlose, String usage, String word) {
		if (null == entity) {
			log.debug("Registering Translation when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return null; // Don't register anything if current lex entry is not known.
		}
		word = word.trim();
		// Do not register empty translations
		if (word.length() == 0 && (usage == null || usage.length() == 0)) {
			return null;
		}
		// Ensure language is in its standard form.
		String tl = LangTools.getPart1OrId(lang);
		lang = LangTools.normalize(lang);

		Resource trans = aBox.createResource(computeTransId(lang, entity), DBnaryOnt.Translation);
    	aBox.add(trans, DBnaryOnt.isTranslationOf, entity);
    	aBox.add(createTargetLanguageProperty(trans, lang));

		if (null == tl) {
			aBox.add(trans, DBnaryOnt.writtenForm, word);
		} else {
			aBox.add(trans, DBnaryOnt.writtenForm, word, tl);
		}

		if (currentGlose != null && ! currentGlose.equals("")) {
			aBox.add(trans, DBnaryOnt.gloss, currentGlose, extractedLang);
		}

		if (usage != null && ! usage.equals("")) {
			aBox.add(trans, DBnaryOnt.usage, usage);
		}
    	return trans;
	}

	@Override
    public void registerTranslation(String lang, String currentGlose, String usage, String word) {
		registerTranslationToEntity(currentLexEntry, lang, currentGlose, usage, word);
	}

	public String getVocableResourceName(String vocable) {
		return getPrefix() + uriEncode(vocable);
	}
	public Resource getVocableResource(String vocable, boolean dontLinkWithType) {
		if (dontLinkWithType) {
			return aBox.createResource(getVocableResourceName(vocable));
		}
		return aBox.createResource(getVocableResourceName(vocable), DBnaryOnt.Vocable);
	}

	public Resource getVocableResource(String vocable) {
		return getVocableResource(vocable, false);
	}

	protected void mergePropertiesIntoResource(HashSet<PropertyObjectPair> properties, Resource res) {
		for (PropertyObjectPair p : properties) {
			if (!res.getModel().contains(res, p.getKey(), p.getValue())) {
                res.getModel().add(res, p.getKey(), p.getValue());
			}
		}
	}

	private boolean incompatibleProperties(Property p1, Property p2, boolean applyCommutativity) {
		return (
			p1 == LexinfoOnt.mood && p2 == LexinfoOnt.gender
		) || (applyCommutativity && incompatibleProperties(p2, p1, false));
	}

	private boolean incompatibleProperties(Property p1, Property p2) {
		return incompatibleProperties(p1, p2, true);
	}

	private boolean isResourceCompatible(Resource r, HashSet<PropertyObjectPair> properties) {
		for (PropertyObjectPair pr : properties) {
			Property p = pr.getKey();

			Statement roStat = r.getProperty(p);

			if (roStat != null) {
				RDFNode ro = roStat.getObject();

				if (ro != null && !ro.equals(pr.getValue())) {
					return false;
				}

				StmtIterator i = r.listProperties();
				while (i.hasNext()) {
					if (incompatibleProperties(p, i.nextStatement().getPredicate())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected void addOtherFormPropertiesToLexicalEntry(Resource lexEntry, HashSet<PropertyObjectPair> properties) {
		boolean foundCompatible = false;
        Model morphoBox = featureBoxes.get(Feature.MORPHOLOGY);

        if (null == morphoBox) return;

        lexEntry = lexEntry.inModel(morphoBox);

        // DONE: Add other forms to a morphology dedicated model.
		StmtIterator otherForms = lexEntry.listProperties(LemonOnt.otherForm);

		while (otherForms.hasNext() && !foundCompatible) {
			Resource otherForm = otherForms.next().getResource();
			if (isResourceCompatible(otherForm, properties)) {
				foundCompatible = true;
				mergePropertiesIntoResource(properties, otherForm);
			}
		}

		if (!foundCompatible) {
            String otherFormNodeName = computeOtherFormResourceName(lexEntry,properties);
			Resource otherForm = morphoBox.createResource(getPrefix() + otherFormNodeName, LemonOnt.Form);
            morphoBox.add(lexEntry, LemonOnt.otherForm, otherForm);
			mergePropertiesIntoResource(properties, otherForm);
		}
	}

    protected String computeOtherFormResourceName(Resource lexEntry, HashSet<PropertyObjectPair> properties) {
        String lexEntryLocalName = lexEntry.getLocalName();
        String compactProperties = DatatypeConverter.printBase64Binary(BigInteger.valueOf(properties.hashCode()).toByteArray()).replaceAll("[/=\\+]", "-");

        return "__wf_" + compactProperties + "_" + lexEntryLocalName;
    }

    public void registerInflection(String languageCode,
	                               String pos,
	                               String inflection,
	                               String canonicalForm,
	                               int defNumber,
	                               HashSet<PropertyObjectPair> props,
	                               HashSet<PronunciationPair> pronunciations) {

		if (pronunciations != null) {
			for (PronunciationPair pronunciation : pronunciations) {
				props.add(PropertyObjectPair.get(LexinfoOnt.pronunciation, aBox.createLiteral(pronunciation.pron, pronunciation.lang)));
			}
		}

		registerInflection(languageCode, pos, inflection, canonicalForm, defNumber, props);
	}

	public void registerInflection(String languageCode,
	                               String pos,
	                               String inflection,
	                               String canonicalForm,
	                               int defNumber,
	                               HashSet<PropertyObjectPair> props) {

		Resource posResource = posResource(pos);

		PropertyObjectPair p = PropertyObjectPair.get(LemonOnt.writtenRep, aBox.createLiteral(inflection, extractedLang));

		props.add(p);

		if (defNumber == 0) {
			// the definition number was not specified, we have to register this
			// inflection for each entry.

			// First, we store the other form for all the existing entries
			Resource vocable = getVocableResource(canonicalForm, true);

			StmtIterator entries = vocable.listProperties(DBnaryOnt.refersTo);

			while (entries.hasNext()) {
				Resource lexEntry = entries.next().getResource();
				if (aBox.contains(lexEntry, LexinfoOnt.partOfSpeech, posResource)) {
					addOtherFormPropertiesToLexicalEntry(lexEntry, props);
				}
			}

			// Second, we store the other form for future possible matching entries
			SimpleImmutableEntry<String,String> key = new SimpleImmutableEntry<String,String>(canonicalForm, pos);

			HashSet<HashSet<PropertyObjectPair>> otherForms = heldBackOtherForms.get(key);

			if (otherForms == null) {
				otherForms = new HashSet<HashSet<PropertyObjectPair>>();
				heldBackOtherForms.put(key, otherForms);
			}

			otherForms.add(props);
		} else {
			// the definition number was specified, this makes registration easy.
			addOtherFormPropertiesToLexicalEntry(
				getLexEntry(languageCode, canonicalForm, pos, defNumber),
				props
			);
		}
	}

	private Statement createTargetLanguageProperty(Resource trans, String lang) {
		lang = lang.trim();
		if (isAnISO639_3Code(lang)) {
			return aBox.createStatement(trans, DBnaryOnt.targetLanguage, getLexvoLanguageResource(lang));
		} else {
			return aBox.createStatement(trans, DBnaryOnt.targetLanguageCode, lang);
		}
	}

    private final static Pattern iso3letters = Pattern.compile("\\w{3}");
	private boolean isAnISO639_3Code(String lang) {
		// TODO For the moment, only check if the code is a 3 letter code...
		return iso3letters.matcher(lang).matches();
	}

	private String computeTransId(String lang, Resource entity) {
		lang = uriEncode(lang);
		return getPrefix() + "__tr_" + lang + "_" + translationCount.incr(lang) + "_" + entity.getURI().substring(getPrefix().length());
	}

	private Resource getLexvoLanguageResource(String lang) {
		Resource res = languages.get(lang);
		if (res == null) {
			res = tBox.createResource(LEXVO + lang);
			languages.put(lang, res);
		}
		return res;
	}

	public void registerNymRelationToEntity(String target, String synRelation, Resource entity) {
		if (null == entity) {
			log.debug("Registering Lexical Relation when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}
		// Some links point to Annex pages or Images, just ignore these.
		int colon = target.indexOf(':');
		if (colon != -1) {
			return;
		}
		int hash = target.indexOf('#');
		if (hash != -1) {
			// The target contains an intra page href. Remove it from the target uri and keep it in the relation.
			target = target.substring(0,hash);
			// TODO: keep additional intra-page href
			// aBox.add(nym, isAnnotatedBy, target.substring(hash));
		}
		
		Property nymProperty = nymPropertyMap.get(synRelation);
		
		Resource targetResource = getVocableResource(target);
		
		aBox.add(entity, nymProperty, targetResource);
	}

	@Override
	public void registerNymRelation(String target, String synRelation) {
		registerNymRelationToEntity(target, synRelation, currentLexEntry);
    }

	@Override
	public void registerNymRelation(String target, String synRelation, String gloss) {
		registerNymRelation(target, synRelation, gloss, null);
	}
	
	@Override
	public void registerNymRelation(String target, String synRelation, String gloss, String usage) {
		if (null == currentLexEntry) {
			log.debug("Registering Lexical Relation when lex entry is null in \"{}\".", this.currentMainLexEntry);
			return; // Don't register anything if current lex entry is not known.
		}
		// Some links point to Annex pages or Images, just ignore these.
		int colon = target.indexOf(':');
		if (colon != -1) {
			return;
		}
		int hash = target.indexOf('#');
		if (hash != -1) {
			// The target contains an intra page href. Remove it from the target uri and keep it in the relation.
			target = target.substring(0,hash);
			// TODO: keep additional intra-page href
			// aBox.add(nym, isAnnotatedBy, target.substring(hash));
		}
		Property nymProperty = nymPropertyMap.get(synRelation);
		
		Resource targetResource = getVocableResource(target);
		
		Statement nymR = aBox.createStatement(currentLexEntry, nymProperty, targetResource);
    	aBox.add(nymR);
    	
    	if(gloss == null && usage == null)
    		return;
    	
	    ReifiedStatement rnymR = nymR.createReifiedStatement(computeNymId(synRelation));
	    if(gloss != null)
	    	rnymR.addProperty(DBnaryOnt.gloss, gloss);
		if(usage != null)
			rnymR.addProperty(DBnaryOnt.usage, usage);
			
    	
	}

	private String computeNymId(String nym) {
		return getPrefix() + "__" + nym + "_" + reifiedNymCount.incr(nym) + "_" + currentEncodedPageName;
	}

	@Override
	public void registerNymRelationOnCurrentSense(String target, String synRelation) {
		if (null == currentSense) {
			log.debug("Registering Lexical Relation when current sense is null in \"{}\".", this.currentMainLexEntry);
			registerNymRelation(target, synRelation);
			return ; // Don't register anything if current lex entry is not known.
		}
		// Some links point to Annex pages or Images, just ignore these.
		int colon = target.indexOf(':');
		if (colon != -1) {
			return;
		}
		int hash = target.indexOf('#');
		if (hash != -1) {
			// The target contains an intra page href. Remove it from the target uri and keep it in the relation.
			target = target.substring(0,hash);
			// TODO: keep additional intra-page href
	    	// aBox.add(nym, isAnnotatedBy, target.substring(hash));
		}
		
		Property nymProperty = nymPropertyMap.get(synRelation);
		
		Resource targetResource = getVocableResource(target);

		aBox.add(currentSense, nymProperty, targetResource);
	}

	@Override
	public void registerPronunciation(String pron, String lang) {
		if (null == currentCanonicalForm) {
			currentSharedPronunciations.add(new PronunciationPair(pron, lang));
		} else {
			registerPronunciation(currentCanonicalForm, pron, lang);
		}
	}

	protected void registerPronunciation(Resource writtenRepresentation, String pron, String lang) {
		if (null != lang && lang.length() > 0) {
			aBox.add(writtenRepresentation, LexinfoOnt.pronunciation, pron, lang);
		} else {
			aBox.add(writtenRepresentation, LexinfoOnt.pronunciation, pron);
		}
	}

	private void promoteNymProperties() {
		StmtIterator entries = currentMainLexEntry.listProperties(DBnaryOnt.refersTo);
		HashSet<Statement> toBeRemoved = new HashSet<Statement>();
		while (entries.hasNext()) {
			Resource lu = entries.next().getResource();
			List<Statement> senses = lu.listProperties(LemonOnt.sense).toList();
			if (senses.size() == 1) {
				Resource s = senses.get(0).getResource();
				HashSet<Property> alreadyProcessedNyms = new HashSet<Property>();
				for (Property nymProp: nymPropertyMap.values()) {
					if (alreadyProcessedNyms.contains(nymProp)) continue;
					alreadyProcessedNyms.add(nymProp);
					StmtIterator nyms = lu.listProperties(nymProp);
					while (nyms.hasNext()) {
						Statement nymRel = nyms.next();
						aBox.add(s, nymProp, nymRel.getObject());
						toBeRemoved.add(nymRel);
					}
				}
			}
		}
		for (Statement s: toBeRemoved) {
			s.remove();
		}
	}

    @Override
    public void dump(Feature f, OutputStream out, String format) {
        Model box = featureBoxes.get(f);
        if (null != box) {
            box.write(out, format);
        }
    }

	@Override
	public int nbEntries() {
		return nbEntries;
	}

	@Override
	public String currentLexEntry() {
		// TODO Auto-generated method stub
		return currentWiktionaryPageName;
	}

	@Override
	public void initializeEntryExtraction(String wiktionaryPageName, String lang) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Cannot initialize a foreign language entry.");
	}

	@Override
	public Resource registerExample(String ex, Map<Property, String> context) {
		if (null == currentSense) {
			log.debug("Registering example when lex sense is null in \"{}\".", this.currentMainLexEntry);
			return null; // Don't register anything if current lex entry is not known.
		}
		
		// Create new word sense + a definition element 
    	Resource example = aBox.createResource();	
    	aBox.add(aBox.createStatement(example, LemonOnt.value, ex, extractedLang));
        if (null != context) {
            for (Map.Entry<Property, String> c : context.entrySet()) {
                aBox.add(aBox.createStatement(example,c.getKey(),c.getValue(),extractedLang));
            }
        }
    	aBox.add(aBox.createStatement(currentSense, LemonOnt.example, example));
		return example;

	}
}
