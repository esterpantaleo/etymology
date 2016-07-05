/**
 * 
 */
package org.getalp.dbnary.jpn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serasset
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

	protected final static String level2HeaderPatternString = "^==([^=].*[^=])==\\s*$";
	protected final static String wikiSectionPatternString = "^={3,}\\s*([^=]*)\\s*={3,}\\s*$";


	private final int NODATA = 0;
	private final int TRADBLOCK = 1;
	private final int DEFBLOCK = 2;
	private final int ORTHOALTBLOCK = 3;
	private final int NYMBLOCK = 4;
	private final int PRONBLOCK = 5;
	private final int MORPHOBLOCK = 6;
	private final int RELBLOCK = 7;
	private final int IGNOREPOS = 8;

	// TODO: handle pronounciation
	protected final static String pronounciationPatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";

	public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
		super(wdh);
	}

	// protected final static Pattern languageSectionPattern;
	//protected final static HashSet<String> nymMarkers;



	protected final static Pattern sectionPattern;

	private final static Pattern pronunciationPattern;
	protected final static Pattern level2HeaderPattern;

	static {
		level2HeaderPattern = Pattern.compile(level2HeaderPatternString, Pattern.MULTILINE);

		sectionPattern = Pattern.compile(wikiSectionPatternString, Pattern.MULTILINE);
		pronunciationPattern = Pattern.compile(pronounciationPatternString);
	}

	int state = NODATA;
	int definitionBlockStart = -1;
	int translationBlockStart = -1;
	int orthBlockStart = -1;
	private int nymBlockStart = -1;
	private int pronBlockStart = -1;
	private int morphoBlockStart = -1;

	private String currentNym = null;

	protected boolean isCurrentlyExtracting = false;

	private HashSet<String> unknownHeaders;
	private int relBlockStart;

	public boolean isCurrentlyExtracting() {
		return isCurrentlyExtracting;
	}

	/* (non-Javadoc)
	 * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
	 */
	@Override
	public void extractData() {
		unknownHeaders = new HashSet<String>();
		Matcher l1 = level2HeaderPattern.matcher(pageContent);
		int jpnStart = -1;
        wdh.initializePageExtraction(wiktionaryPageName);
        // TODO: should I initialize the entry in the japanese extraction method ?
		wdh.initializeEntryExtraction(wiktionaryPageName);
		while (l1.find()) {
			if (-1 != jpnStart) {
				extractJapaneseData(jpnStart, l1.start());
				jpnStart = -1;
			}
			if (isJapanese(l1)) {
				jpnStart = l1.end();
			}
		}
		if (-1 != jpnStart) {
			extractJapaneseData(jpnStart, pageContent.length());
		}

		wdh.finalizeEntryExtraction();
        wdh.finalizePageExtraction();
		for (String h : unknownHeaders) {
			log.debug("--> {}", h);
		}
	}

	private boolean isJapanese(Matcher l1) {
		if (l1.group(1).trim().startsWith("{{ja")) return true;
		if (l1.group(1).trim().startsWith("{{jpn")) return true;
		if (l1.group(1).trim().startsWith("日本語")) return true;
		return false;
	}

	//	private static String posPatternString = "\\s*(\\{\\{([^\\}\\|\n\r]*)(\\|[^\\}\n\r]*)?\\}\\}(.*)|[^=]*)\\s*";
	//	private static Pattern posPattern = Pattern.compile(posPatternString);
	//	private static String macroPatternString = "\\s*\\{\\{([^\\}\\|\n\r]*)(\\|[^\\}\n\r]*)?\\}\\}\\s*";
	//	private static Pattern macroPattern = Pattern.compile(macroPatternString);

	//    private HashSet<String> unsupportedSections = new HashSet<String>(100);
	void gotoNoData(Matcher m) {
		state = NODATA;
	}

	private boolean isLevel3Header(Matcher m) {

		if (m.group(0).startsWith("===") && ! m.group(0).startsWith("====")) {
			unknownHeaders.add(m.group(0));
			return true;
		} else {
			return false;
		}
	}

	// Part of speech section (Def block)    

	private String getValidPOS(Matcher m) {
		// TODO Check what is meant by the words that are given after the POS.
		// TODO: check if the POS macros are given some args.
		// TODO: treat: ==={{noun}}?{{adverb}}===
		// DONE: some pos (like idiom) may be used as a POS or as a sub section in the entry. => Check the header level.
		// Only keep level 3 headers ? --> No.
		// Heuristic is used: if entry length <= 2 then idiom is not a POS.
		String head = m.group(1).trim();

		return WiktionaryDataHandler.getValidPOS(head, wiktionaryPageName);
	}

	void gotoDefBlock(Matcher m, String pos) {
		state = DEFBLOCK;
		definitionBlockStart = m.end();
		wdh.addPartOfSpeech(pos);
	}

	void leaveDefBlock(Matcher m) {
		// TODO: computeRegionEnd does have errors when category links are put in the middle of some entries...
		int end = computeRegionEnd(definitionBlockStart, m);
		extractDefinitions(definitionBlockStart, end);
		definitionBlockStart = -1;
	}

	// Alternate Spelling    
	private boolean isAlternate(Matcher m) {
		String head = m.group(1).trim();
		return "別表記".equals(head);
	}

	void gotoOrthoAltBlock(Matcher m) {
		state = ORTHOALTBLOCK;    
		orthBlockStart = m.end();
	}

	void leaveOrthoAltBlock(Matcher m) {
		extractOrthoAlt(orthBlockStart, computeRegionEnd(orthBlockStart, m));
		orthBlockStart = -1;
	}

	// Translation section
	private boolean isTranslation(Matcher m) {
		String head = m.group(1).trim();
		Matcher trans = WikiPatterns.macroPattern.matcher(head);
		if (trans.find())
			return (trans.group(1).equals("trans"));
		else
			return "訳語".equals(head);
	}

	void gotoTradBlock(Matcher m) {
		translationBlockStart = m.end(); 
		state = TRADBLOCK;
	}

	void leaveTradBlock(Matcher m) {
		extractTranslations(translationBlockStart, computeRegionEnd(translationBlockStart, m));
		translationBlockStart = -1;
	}


	// Nyms
	private boolean isNymHeader(Matcher m) {
		Matcher nym = WikiPatterns.macroPattern.matcher(m.group(1).trim());

		if (nym.matches())
			return JapaneseRelatedWordsExtractorWikiModel.relMarkerToRelName.containsKey(nym.group(1));
		else
			return false;
	}

	private void gotoNymBlock(Matcher m) {
		state = NYMBLOCK; 
		nymBlockStart = m.end();      
		Matcher nym = WikiPatterns.macroPattern.matcher(m.group(1).trim());
		if (nym.matches())
			currentNym = JapaneseRelatedWordsExtractorWikiModel.relMarkerToRelName.get(nym.group(1));
		else
			log.error("WARNING: non matching nym...");
	}

	private void leaveNymBlock(Matcher m) {
		extractNyms(currentNym, nymBlockStart, computeRegionEnd(nymBlockStart, m));
		currentNym = null;
		nymBlockStart = -1;         
	}

	// Related Words
	private boolean isRelatedHeader(Matcher m) {
		Matcher rel = WikiPatterns.macroPattern.matcher(m.group(1).trim());
		if (rel.matches())
			return rel.group(1).trim().equals("rel");
		else
			return false;
	}

	private void gotoRelBlock(Matcher m) {
		state = RELBLOCK; 
		relBlockStart = m.end();
	}

	private void leaveRelBlock(Matcher m) {
		extractRelatedWords(relBlockStart, computeRegionEnd(relBlockStart, m));
		relBlockStart = -1;         
	}


	// Pronounciation section
	private boolean isPronounciation(Matcher m) {
		Matcher pron = WikiPatterns.macroPattern.matcher(m.group(1).trim());

		if (pron.matches())
			if (pron.group(1).equals("pron")) return true;
		return false;
	}

	private void gotoPronBlock(Matcher m) {
		state = PRONBLOCK; 
		pronBlockStart = m.end();      
	}

	private void leavePronBlock(Matcher m) {
		extractPron(pronBlockStart, computeRegionEnd(pronBlockStart, m));
		pronBlockStart = -1;
	}

	private void gotoIgnorePos() {
		state = IGNOREPOS;
	}

	// TODO: variants, pronunciations and other elements are common to the different entries in the page.
	private void extractJapaneseData(int startOffset, int endOffset) {        
		Matcher m = sectionPattern.matcher(pageContent);
		m.region(startOffset, endOffset);
		gotoNoData(m);
		String pos = null;
		while (m.find()) {
			switch (state) {
			case NODATA:
				if (isTranslation(m)) {
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					// Level 2 header that are not a correct POS, or Etimology or Pronunciation are considered as ignorable POS.
					unknownHeaders.add(m.group(0));
					gotoNoData(m);
				} else {
					// unknownHeaders.add(m.group(0));
				}
				break;
			case DEFBLOCK:
				// Iterate until we find a new section
				if (isTranslation(m)) {
					leaveDefBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveDefBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m,pos);
				} else if (isAlternate(m)) {
					leaveDefBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leaveDefBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leaveDefBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leaveDefBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leaveDefBlock(m);
					gotoNoData(m);
				} else {
					leaveDefBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				} 
				break;
			case TRADBLOCK:
				if (isTranslation(m)) {
					leaveTradBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveTradBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					leaveTradBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leaveTradBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leaveTradBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leaveTradBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leaveTradBlock(m);
					gotoNoData(m);
				} else {
					leaveTradBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				} 
				break;
			case ORTHOALTBLOCK:
				if (isTranslation(m)) {
					leaveOrthoAltBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveOrthoAltBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					leaveOrthoAltBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leaveOrthoAltBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leaveOrthoAltBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leaveOrthoAltBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leaveOrthoAltBlock(m);
					gotoNoData(m);
				} else {
					leaveOrthoAltBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				}
				break;
			case NYMBLOCK:
				if (isTranslation(m)) {
					leaveNymBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveNymBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else 
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					leaveNymBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leaveNymBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leaveNymBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leaveNymBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leaveNymBlock(m);
					gotoNoData(m);
				} else {
					leaveNymBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				}
				break;
			case PRONBLOCK:
				if (isTranslation(m)) {
					leavePronBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leavePronBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					leavePronBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leavePronBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leavePronBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leavePronBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leavePronBlock(m);
					gotoNoData(m);
				} else {
					leavePronBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				}
				break;
			case RELBLOCK:
				if (isTranslation(m)) {
					leaveRelBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveRelBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
					leaveRelBlock(m);
					gotoOrthoAltBlock(m);
				} else if (isNymHeader(m)) {
					leaveRelBlock(m);
					gotoNymBlock(m);
				} else if (isPronounciation(m)) {
					leaveRelBlock(m);
					gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					leaveRelBlock(m);
					gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					leaveRelBlock(m);
					gotoNoData(m);
				} else {
					leaveRelBlock(m);
					// unknownHeaders.add(m.group(0));
					gotoNoData(m);
				}
				break;
			case IGNOREPOS:
				if (isTranslation(m)) {
				} else if (null != (pos = getValidPOS(m))) {
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isAlternate(m)) {
				} else if (isNymHeader(m)) {
				} else if (isPronounciation(m)) {
					// gotoPronBlock(m);
				} else if (isRelatedHeader(m)) {
					// gotoRelBlock(m);
				} else if (isLevel3Header(m)) {
					// gotoIgnorePos();
				} else {
					// unknownHeaders.add(m.group(0));
				}
				break;
			default:
				assert false : "Unexpected state while extracting translations from dictionary.";
			} 
		}
		// Finalize the entry parsing
		switch (state) {
		case NODATA:
			break;
		case DEFBLOCK:
			leaveDefBlock(m);
			break;
		case TRADBLOCK:
			leaveTradBlock(m);
			break;
		case ORTHOALTBLOCK:
			leaveOrthoAltBlock(m);
			break;
		case NYMBLOCK:
			leaveNymBlock(m);
			break;
		case PRONBLOCK:
			leavePronBlock(m);
			break;
		case RELBLOCK:
			leaveRelBlock(m);
			break;
		case IGNOREPOS:
			break;
		default:
			assert false : "Unexpected state while ending extraction of entry: " + wiktionaryPageName;
		} 
	}




	// TODO: try to use gwtwiki to extract translations
	//	private void extractTranslations(int startOffset, int endOffset) {
	//       String transCode = pageContent.substring(startOffset, endOffset);
	//       ItalianTranslationExtractorWikiModel dbnmodel = new ItalianTranslationExtractorWikiModel(this.wdh, this.wi, new Locale("pt"), "/${image}/"+wiktionaryPageName, "/${title}");
	//       dbnmodel.parseTranslationBlock(transCode);
	//   }
	
	private void extractRelatedWords(int startOffset, int endOffset) {
		String relCode = pageContent.substring(startOffset, endOffset);
		JapaneseRelatedWordsExtractorWikiModel dbnmodel = new JapaneseRelatedWordsExtractorWikiModel(this.wdh, this.wi);
		dbnmodel.parseRelatedWords(relCode);
	}

	private void extractPron(int startOffset, int endOffset) {
		String pronCode = pageContent.substring(startOffset, endOffset);
		// TODO: Attention, certaines prononciations sont complexes (Kanjis...), d'autres sont globales (avant la catégorie) et d'autres sont à l'intérieur des entrées.
		//    	JapanesePronunciationExtractorWikiModel dbnmodel = new JapanesePronunciationExtractorWikiModel(this.wdh, this.wi, new Locale("pt"), "/${image}", "/${title}");
		//        dbnmodel.parsePronunciation(pronCode);
	}

	@Override
	public void extractDefinition(String definition, int defLevel) {
		// TODO: properly handle macros in definitions.
		JapaneseDefinitionExtractorWikiModel dbnmodel = new JapaneseDefinitionExtractorWikiModel(this.wdh, this.wi, new Locale("ja"), "/${image}", "/${title}");
		dbnmodel.parseDefinition(definition, defLevel);
	}

	public void extractTranslations(int startOffset, int endOffset) {
		String transCode = pageContent.substring(startOffset, endOffset);
		JapaneseTranslationsExtractorWikiModel dbnmodel = new JapaneseTranslationsExtractorWikiModel(this.wdh, this.wi);
		dbnmodel.parseTranslations(transCode);
	}

}
