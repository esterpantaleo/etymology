package org.getalp.dbnary.spa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.getalp.dbnary.wiki.WikiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

	// TODO: {{grafía alternativa|hogaño|nota1=más usada|leng=es}}: 2450 résultats, 500 pour * '''Variante''', {{variantes si la prononciation est différente}}.
	
	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

	protected final static String languageSectionPatternString;
	protected final static String headerPatternString ;
	protected final static String spanishDefinitionPatternString;

	private static final int NODATA = 0;
	private static final int TRADBLOCK = 1;
	private static final int DEFBLOCK = 2;
	private static final int HEADERBLOCK = 5;
	private static final int IGNOREPOS = 6;

	public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
		super(wdh);
	}

	
	protected final static String sectionPatternString;
	protected final static Pattern languageSectionPattern;
	protected final static Pattern spanishDefinitionPattern;
	protected final static String multilineMacroPatternString;
	protected final static Pattern sectionPattern; // Combine macro pattern
	// and pos pattern.
	protected final static HashSet<String> posMarkers;
	protected final static HashSet<String> ignorableSectionMarkers;
	protected final static HashMap<String, String> nymMarkerToNymName;

	protected SpanishDefinitionExtractorWikiModel definitionExpander;

	static {

		languageSectionPatternString = new StringBuilder()
				.append("(?:")
				.append("^\\s*\\{\\{")
				.append("([\\p{Upper}\\-]*)(?:\\|([^\\}]*))?")
				.append("\\}\\}")
				.append(")|(?:")
				.append("^==\\s*\\{\\{lengua\\|(.*)\\}\\}\\s*==\\s*$")
				.append(")")
				.toString();

		languageSectionPattern = Pattern.compile(languageSectionPatternString, Pattern.MULTILINE);
		multilineMacroPatternString = 
				new StringBuilder().append("\\{\\{")
				.append("([^\\}\\|]*)(?:\\|([^\\}]*))?")
				.append("\\}\\}")
				.toString();

		headerPatternString = new StringBuilder()
		.append("(?:")
		.append("^==([^=].*)==\\s*$")
		.append(")|(?:")
		.append("^===([^=].*)===\\s*$")
		.append(")|(?:")
		.append("^====([^=].*)====\\s*$")
		.append(")").toString();

		sectionPatternString = new StringBuilder()
		.append("(?m)(?:").append(headerPatternString)
		.append(")").toString();

		sectionPattern = Pattern.compile(sectionPatternString);
		
		spanishDefinitionPatternString = new StringBuilder()
		.append("(?:")
		.append("^;([^:]*):([^\n\r]*)$")
		.append(")|(?:")
		.append(WikiPatterns.macroPatternString)
		.append(")|(?:")
		.append("^:([^\n\r]*)$")
		.append(")").toString();
		spanishDefinitionPattern = Pattern.compile(spanishDefinitionPatternString, Pattern.MULTILINE);

		posMarkers = new HashSet<String>(20);
		posMarkers.add("sustantivo"); 
		posMarkers.add("adjetivo");
		posMarkers.add("sustantivo propio");
		posMarkers.add("adverbio");
		posMarkers.add("verbo");

		ignorableSectionMarkers = new HashSet<String>(20);
		ignorableSectionMarkers.add("Silbentrennung");
		ignorableSectionMarkers.add("Aussprache");
		ignorableSectionMarkers.add("Herkunft");
		ignorableSectionMarkers.add("Gegenworte");
		ignorableSectionMarkers.add("Beispiele");
		ignorableSectionMarkers.add("Redewendungen");
		ignorableSectionMarkers.add("Abgeleitete Begriffe");
		ignorableSectionMarkers.add("Charakteristische Wortkombinationen");
		ignorableSectionMarkers.add("Dialektausdrücke (Deutsch)");
		ignorableSectionMarkers.add("Referenzen");
		ignorableSectionMarkers.add("Ähnlichkeiten");
		ignorableSectionMarkers.add("Anmerkung");
		ignorableSectionMarkers.add("Anmerkungen");
		ignorableSectionMarkers.add("Alte Rechtschreibung"); // TODO: Integrate
		// these in
		// alternative
		// spelling ?
		ignorableSectionMarkers.add("Nebenformen");
		ignorableSectionMarkers.add("Vokalisierung");
		ignorableSectionMarkers.add("Grammatische Merkmale");
		ignorableSectionMarkers.add("Abkürzungen"); // TODO: Integrate these in
		// alternative spelling ?
		ignorableSectionMarkers.add("Sinnverwandte Wörter"); // TODO: related
		// words (should I
		// keep it ?)
		ignorableSectionMarkers.add("Weibliche Wortformen");
		ignorableSectionMarkers.add("Männliche Wortformen");
		ignorableSectionMarkers.add("Verkleinerungsformen"); // TODO:
		// Diminutif...
		// qu'en faire ?
		ignorableSectionMarkers.add("Vergrößerungsformen");
		ignorableSectionMarkers.add("Kurzformen");
		ignorableSectionMarkers.add("Koseformen");
		ignorableSectionMarkers.add("Kurz- und Koseformen");
		ignorableSectionMarkers.add("Namensvarianten");
		ignorableSectionMarkers.add("Weibliche Namensvarianten");
		ignorableSectionMarkers.add("Männliche Namensvarianten");
		ignorableSectionMarkers.add("Bekannte Namensträger");
		ignorableSectionMarkers.add("Sprichwörter");
		ignorableSectionMarkers.add("Charakteristische Wortkombinationen");
		ignorableSectionMarkers.add("Abgeleitete Begriffe");

		nymMarkerToNymName = new HashMap<String, String>(20);
		nymMarkerToNymName.put("sinónimo", "syn");
		nymMarkerToNymName.put("sinónimos", "syn");
		nymMarkerToNymName.put("antónimo", "ant");
		nymMarkerToNymName.put("antónimos", "ant");
		nymMarkerToNymName.put("hipónimo", "hypo");
		nymMarkerToNymName.put("hipónimos", "hypo");
		nymMarkerToNymName.put("hiperónimo", "hyper");
		nymMarkerToNymName.put("hiperónimos", "hyper");

	}

	@Override
	public void setWiktionaryIndex(WiktionaryIndex wi) {
		super.setWiktionaryIndex(wi);
		definitionExpander = new SpanishDefinitionExtractorWikiModel(this.wdh, this.wi, new Locale("es"), "/${image}", "/${title}");
	}

	@Override
	public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
		Matcher l1 = languageSectionPattern.matcher(pageContent);
		int spaStart = -1;
		wdh.initializeEntryExtraction(wiktionaryPageName);
		while (l1.find()) {
			if (-1 != spaStart) {
				extractSpanishData(spaStart, l1.start());
				spaStart = -1;
			}
			if (isSpanish(l1)) {
				spaStart = l1.end();
			}
		}
		if (-1 != spaStart) {
			extractSpanishData(spaStart, pageContent.length());
		}

		wdh.finalizeEntryExtraction();
        wdh.finalizePageExtraction();
	}
	
	private boolean isSpanish(Matcher l1) {
		return (l1.group(1) != null && l1.group(1).toLowerCase().equals("es")
				|| (l1.group(3) != null && l1.group(3).toLowerCase().equals("es")));
	}


	int state = NODATA;
	int definitionBlockStart = -1;
	int orthBlockStart = -1;
	int translationBlockStart = -1;
	private int headerBlockStart = -1;


	void gotoNoData(Matcher m) {
		state = NODATA;
	}

	private int getHeaderLevel(Matcher m) {
		String l2 = m.group(1);
		String l3 = m.group(2);
		String l4 = m.group(3);
		
		if (l2 != null) return 2;
		if (l3 != null) return 3;
		if (l4 != null) return 4;
		return 0;
	}

	private String getHeaderLabel(Matcher m) {
		String l2 = m.group(1);
		String l3 = m.group(2);
		String l4 = m.group(3);
		
		if (l2 != null) return l2;
		if (l3 != null) return l3;
		if (l4 != null) return l4;
		return null;
	}

	private boolean isHeader(Matcher m) {
		return getHeaderLabel(m) != null;
	}
	
	// Part of speech section (Def block)    

	private String getValidPOS(Matcher m) {
		String h = getHeaderLabel(m);
		if (null == h) return null;
		if (getHeaderLevel(m) != 3) return null; // Only keep lvl 3 headings...
		String head = h.trim().toLowerCase();
		String pos = null;
		if ((head.startsWith("forma")) || head.startsWith("{{forma")) return "";
		for (String p : posMarkers) {
			if (head.contains(p)) return p;
		}
		
		return pos;
	}

	void gotoDefBlock(Matcher m, String pos) {
		state = DEFBLOCK;
		definitionBlockStart = m.end();
		wdh.addPartOfSpeech(pos);
	}

	void leaveDefBlock(Matcher m) {
		int end = computeRegionEnd(definitionBlockStart, m);
		extractDefinitions(definitionBlockStart, end);
		definitionBlockStart = -1;
	}

	// Translation section
	private boolean isTranslation(Matcher m) {
		String head = getHeaderLabel(m);
		if (null == head) return false;
        int lvl = getHeaderLevel(m);
		if (lvl != 2 && lvl != 3) return false; // Only keep lvl 2 headings...
		head = head.trim().toLowerCase();
		return "traducciones".equals(head) || "traducción".equals(head);
	}

	void gotoTradBlock(Matcher m) {
		translationBlockStart = m.end(); 
		state = TRADBLOCK;
	}

	void leaveTradBlock(Matcher m) {
		extractTranslations(translationBlockStart, computeRegionEnd(translationBlockStart, m));
		translationBlockStart = -1;
	}

	private void gotoHeaderBlock(Matcher m) {
		state = HEADERBLOCK; 
		// The header starts at the beginning of the region.
		headerBlockStart = m.regionStart();   
	}

	private void leaveHeaderBlock(Matcher m) {
		extractHeaderInfo(headerBlockStart, computeRegionEnd(headerBlockStart, m));
		headerBlockStart = -1;
	}

	private void gotoIgnorePos() {
		state = IGNOREPOS;
	}

	// TODO: variants, pronunciations and other elements are common to the different entries in the page.
	private void extractSpanishData(int startOffset, int endOffset) {        
		Matcher m = sectionPattern.matcher(pageContent);
		m.region(startOffset, endOffset);
		gotoHeaderBlock(m);
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
				} else if (isHeader(m)) {
					// Level 2 header that are not a correct POS, or Etimology or Pronunciation are considered as ignorable POS.
					// unknownHeaders.add(m.group(0));
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
				} else if (isHeader(m)) {
					leaveDefBlock(m);
					gotoNoData(m);
				} else {
					// unknownHeaders.add(m.group(0));
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
				} else if (isHeader(m)) {
					leaveTradBlock(m);
					gotoNoData(m);
				} else {
					// unknownHeaders.add(m.group(0));
				} 
				break;
			case HEADERBLOCK:
				if (isTranslation(m)) {
					leaveHeaderBlock(m);
					gotoTradBlock(m);
				} else if (null != (pos = getValidPOS(m))) {
					leaveHeaderBlock(m);
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isHeader(m)) {
					leaveHeaderBlock(m);
					gotoNoData(m);
				} else {
					// unknownHeaders.add(m.group(0));
				}
				break;
			case IGNOREPOS:
				if (isTranslation(m)) {
				} else if (null != (pos = getValidPOS(m))) {
					if (pos.length()==0) 
						gotoIgnorePos();
					else
						gotoDefBlock(m, pos);
				} else if (isHeader(m)) {
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
		case HEADERBLOCK:
			leaveHeaderBlock(m);
			break;
		case IGNOREPOS:
			break;
		default:
			assert false : "Unexpected state while ending extraction of entry: " + wiktionaryPageName;
		} 
	}


	private void extractTranslations(int startOffset, int endOffset) {
	       String transCode = pageContent.substring(startOffset, endOffset);
	       SpanishTranslationExtractorWikiModel dbnmodel = new SpanishTranslationExtractorWikiModel(this.wdh, this.wi, new Locale("es"), "/${image}/"+wiktionaryPageName, "/${title}");
	       dbnmodel.parseTranslationBlock(transCode);
	   }
	    

	Pattern senseNumPattern = Pattern.compile("(\\d+)");
	Pattern nonMacroRelationPattern = Pattern.compile("\\*\\s*'''([^']*)'''(.*)$");
	@Override
	protected void extractDefinitions(int startOffset, int endOffset) {
		Matcher definitionMatcher = spanishDefinitionPattern.matcher(this.pageContent);
		definitionMatcher.region(startOffset, endOffset);
		
		while (definitionMatcher.find()) {
			if (definitionMatcher.group(1) != null) {
				// Get a new definition
				String def = definitionMatcher.group(2);
				String senseNumber = "";
				String term = definitionMatcher.group(1);
				if (null == term || term.length() == 0) {
					log.debug("Null sense number in definition\"{}\" for entry {}", def, this.wiktionaryPageName);
				} else {
					term = term.trim();
					Matcher s = senseNumPattern.matcher(term);
					if (s.find()) {
						if (s.start() != 0) {
							log.debug("Sense number match after beginning of region \"{}\" for entry {}", term, this.wiktionaryPageName);
							def = term + "| " + def;
						} else {
							senseNumber = s.group(1);
							term = term.substring(s.end());
							term = term.trim();
							if (term.length() != 0) def = term + "| " + def;
						}
					} else {
						log.debug("No sense number in region \"{}\" for entry {}", term, this.wiktionaryPageName);
						def = term + "| " + def;
					}

					if (def != null && !def.equals("")) {
						extractDefinition(def, senseNumber);
					}
				}
			} else if (definitionMatcher.group(3) != null) {
				// It's a macro not inside a definition...
				String macro = definitionMatcher.group(3);
				String nym = null;
				if (null != (nym = nymMarkerToNymName.get(macro))) {
					Map<String,String> args = WikiTool.parseArgs(definitionMatcher.group(4));
					for (int i = 1; i <= 40; i++) {
						String val = args.get("" + i);
						String nota = args.get("nota" + i);
						String alt = args.get("alt" + i);
						if (val != null) {
							wdh.registerNymRelationOnCurrentSense(val, nym);
							if (null != nota || null != alt) log.debug("Non null alternate/nota \"{}/{}\"on nym relation for entry {}", nota, alt, this.wiktionaryPageName);
						}
					}
					// TODO: several other nyms may be found on the line, after the macro...
					int offset = definitionMatcher.end();
					while (offset != endOffset && this.pageContent.charAt(offset) != '\n' && this.pageContent.charAt(offset) != '\r') {
						offset++;
					}
					String vals = this.pageContent.substring(definitionMatcher.end(), offset);
					extractNyms(nym, vals);
					definitionMatcher.region(offset, endOffset);
				} 
			} else if (definitionMatcher.group(5) != null) {
				Matcher m = nonMacroRelationPattern.matcher(definitionMatcher.group(5));
				if (m.matches()) {
					String rel = m.group(1);
					rel = rel.replace(":", "").toLowerCase();
					String nym = nymMarkerToNymName.get(rel);
					String vals = m.group(2);
					if (null != nym) {
						extractNyms(nym, vals);
					}
				}
			}
		}
	}

	public void extractDefinition(String definition, String senseNumber) {
		definitionExpander.setPageName(wiktionaryPageName);
		definitionExpander.parseDefinition(definition, senseNumber);
	}

	Pattern nymValuesPattern = Pattern.compile("(?:" + WikiPatterns.linkPatternString + ")|(?:" + "(\\([^\\)]*\\))" + ")|(,)");
	private void extractNyms(String nym, String vals) {
		Matcher m = nymValuesPattern.matcher(vals);
		while (m.find()) {
			if (m.group(1) != null) {
				// Link
				wdh.registerNymRelationOnCurrentSense(m.group(1), nym);
			} else if (m.group(3) != null) {
				// Parens : just ignore it
			} else if (m.group(4) != null) {
				// Comma
			}
		}		
	}

	private void extractHeaderInfo(int startOffset, int endOffset) {
		String transCode = pageContent.substring(startOffset, endOffset);
		SpanishHeaderExtractorWikiModel dbnmodel = new SpanishHeaderExtractorWikiModel(this.wdh, this.wi, new Locale("es"), "/${image}/"+wiktionaryPageName, "/${title}");
		dbnmodel.parseHeaderBlock(transCode);
	}

}
