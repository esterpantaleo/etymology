package org.getalp.dbnary.jpn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.getalp.dbnary.wiki.WikiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JapaneseRelatedWordsExtractorWikiModel {
		
	private Logger log = LoggerFactory.getLogger(JapaneseRelatedWordsExtractorWikiModel.class);

	private IWiktionaryDataHandler delegate;
	
	
	public JapaneseRelatedWordsExtractorWikiModel(IWiktionaryDataHandler we) {
		this(we, (WiktionaryIndex) null);
	}
	
	public JapaneseRelatedWordsExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi) {
		this.delegate = we;
	}

	
	public void parseRelatedWords(String relatedWords) {
		// Render the definition to plain text, while ignoring the example template
		// DEBUG: this.delegate.registerAlternateSpelling(relatedWords);
		extractRelatedWords(relatedWords);
	}

	// TODO: 他動詞形: forme transitive (d'un verbe ?)
	
    protected final static String carPatternString;
	protected final static String macroOrLinkOrcarPatternString;
	
   
    static {
		// les caractères visible 
		carPatternString=
				new StringBuilder().append("(.)")
				.toString();

		// TODO: We should suppress multiline xml comments even if macros or line are to be on a single line.
		macroOrLinkOrcarPatternString = new StringBuilder()
		.append("(?:")
		.append(WikiPatterns.macroPatternString)
		.append(")|(?:")
		.append(WikiPatterns.linkPatternString)
		.append(")|(?:")
		.append("(:*\\*)")   // sub list
		.append(")|(?:")
		.append("^;([^:\\n\\r]*)") // Term definition
		.append(")|(?:")
		.append(carPatternString)
		.append(")")
		.toString();
    }

    protected final static Pattern macroOrLinkOrcarPattern;
	protected final static Pattern carPattern;
	static {
		carPattern = Pattern.compile(carPatternString);
		macroOrLinkOrcarPattern = Pattern.compile(macroOrLinkOrcarPatternString, Pattern.DOTALL + Pattern.MULTILINE);
	}

	protected final static HashMap<String, String> relMarkerToRelName;

	static {
		relMarkerToRelName = new HashMap<String,String>(20);
		relMarkerToRelName.put("syn", "syn");
		relMarkerToRelName.put("ant", "ant");
		relMarkerToRelName.put("hypo", "hypo");
		relMarkerToRelName.put("hyper", "hyper");
		relMarkerToRelName.put("類義語", "syn");
		relMarkerToRelName.put("同義語", "syn");
		relMarkerToRelName.put("対義語", "ant");
		relMarkerToRelName.put("下位語", "hypo");
		relMarkerToRelName.put("上位語", "hyper");
		relMarkerToRelName.put("別表記", "alt");

	}
	static HashSet<String> commonUsageMacros = new HashSet<String>();
	static {
		commonUsageMacros.add("m");
		commonUsageMacros.add("f");
		commonUsageMacros.add("p");
		commonUsageMacros.add("s");
		commonUsageMacros.add("n");
		commonUsageMacros.add("c");
		
	}

	protected static final int INIT = 1;
	protected static final int RELATION = 2;
	protected static final int VALUES = 3;  

    private void extractRelatedWords(String relatedWords) {
	Matcher macroOrLinkOrcarMatcher = macroOrLinkOrcarPattern.matcher(relatedWords);
		int ETAT = INIT;

		String currentGlose = null;
		String currentNym=null, word= ""; 
		String usage = "";       
		String currentRelation = "";

        while (macroOrLinkOrcarMatcher.find()) {

			String macro = macroOrLinkOrcarMatcher.group(1);
			String link = macroOrLinkOrcarMatcher.group(3);
			String star = macroOrLinkOrcarMatcher.group(5);
			String term = macroOrLinkOrcarMatcher.group(6);
			String car = macroOrLinkOrcarMatcher.group(7);

			switch (ETAT) {

			case INIT:
				if (macro!=null) {
					log.debug("RELWORDS: Got {} macro while in INIT state. for page: {}", macro, this.delegate.currentLexEntry());
				} else if(link!=null) {
					log.debug("RELWORDS: Unexpected link {} while in INIT state. for page: {}", link, this.delegate.currentLexEntry());
				} else if (star != null) {
					ETAT = RELATION;
				} else if (term != null) {
					currentGlose = term;  // TODO: are there any gloss in related terms section ?
				} else if (car != null) {
					if (car.equals(":")) {
						//System.err.println("Skipping ':' while in INIT state.");
					} else if (car.equals("\n") || car.equals("\r")) {

					} else if (car.equals(",")) {
						//System.err.println("Skipping ',' while in INIT state.");
					} else {
						//System.err.println("Skipping " + g5 + " while in INIT state.");
					}
				}

				break;

			case RELATION:
				if (macro!=null) {
					currentRelation = macro;
				} else if(link!=null) {
					// We have a link while we try to get a relation. It means that the link poits to a related word for which the relation is not specified.
					// TODO: should we keep these words with an un-specified relation ?
					// System.err.println("Unexpected link: " + link + " while in RELATION state.");
				} else if (star != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (term != null) {
					currentGlose = term;
					currentRelation = ""; word = ""; usage = "";
					ETAT = INIT;
				} else if (car != null) {
					if (car.equals(":")) {
						currentNym = currentRelation.trim();
						currentNym = AbstractWiktionaryExtractor.stripParentheses(currentNym);
						currentNym = relMarkerToRelName.get(currentNym);
						if (null == currentNym)
							log.debug("RELWORDS: Unknown relation: {} in page {}", currentRelation, this.delegate.currentLexEntry());
						currentRelation = "";
						ETAT = VALUES;
					} else if (car.equals("\n") || car.equals("\r")) {
						//System.err.println("Skipping newline while in LANGUE state.");
					} else if (car.equals(",")) {
						//System.err.println("Skipping ',' while in LANGUE state.");
					} else {
						currentRelation = currentRelation + car;
					}
				} 

				break ;
			case VALUES:
				if (macro!=null) {
					if ("ふりがな".equals(macro) || "おくりがな3".equals(macro)) {
						// Kanji word with yomi(s)
						// How could I keep these yomi(s) ?
						Map<String,String> argmap = WikiTool.parseArgs(macroOrLinkOrcarMatcher.group(2));
						word = argmap.get("1");
						argmap.remove("1");
						usage = argmap.toString();
						registerRelation(word, currentNym);
					} else {
						log.debug("RELWORDS: Got macro {} while in VALUE state in page {}", macro, this.delegate.currentLexEntry());
					}
				} else if (link!=null) {
					if (! isAnExternalLink(link)) {
						word = word + " " + ((macroOrLinkOrcarMatcher.group(4) == null) ? link : macroOrLinkOrcarMatcher.group(4));
					}
				} else if (star != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (term != null) {
					currentGlose = term;
					currentRelation = ""; word = ""; usage = ""; currentNym = null;
					ETAT = INIT;
				} else if (car != null) {
					if (car.equals("\n") || car.equals("\r")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						registerRelation(word, currentNym);
						currentNym = null; 
						usage = "";
						word="";
						ETAT = INIT;
					} else if (car.equals(",") || car.equals("、")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						registerRelation(word, currentNym);
						usage = "";
						word = "";
					} else {
						usage = usage + car;
					}
				}
				break;
			default: 
				log.error("Unexpected state number:" + ETAT);
				break; 
			}
        	

        }
    }
    
	// NOTE: trans-top is sometimes used.
	// Sometimes something is given after the {{trans}} macro to represent the entry of the translation, however, it seems to be redundant with the position.
    

	private void registerRelation(String word, String currentNym) {
		if (word != null && word.length() != 0) {
			if(currentNym!=null){
				if ("alt".equals(currentNym))
					this.delegate.registerAlternateSpelling(word);
				else
					this.delegate.registerNymRelation(word.trim(), currentNym);
			}
		}
	}

	private boolean isAnExternalLink(String link) {
		// TODO Auto-generated method stub
		return link.startsWith(":");
	}
}
