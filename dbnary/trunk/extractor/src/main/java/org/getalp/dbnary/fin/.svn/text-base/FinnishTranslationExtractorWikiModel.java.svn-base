package org.getalp.dbnary.fin;

import info.bliki.wiki.filter.WikipediaParser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.*;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.getalp.dbnary.wiki.WikiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinnishTranslationExtractorWikiModel extends DbnaryWikiModel {
	
	private IWiktionaryDataHandler delegate;
	private Logger log = LoggerFactory.getLogger(FinnishTranslationExtractorWikiModel.class);

	public FinnishTranslationExtractorWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
		this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
	}
	
	public FinnishTranslationExtractorWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		this.delegate = we;
	}

	
	// TODO: handle entries where translations refer to the translations of another term (form: Kts. [[other entry]]).
	public void parseTranslationBlock(String block) {
		// Heuristics: if the translation block uses kohta macro, we assume that ALL translation data is available in the macro.
		if (block.contains("{{kohta")) {
			parseTranslationBlockWithBliki(block);
		} else {
			extractTranslations(block);
		}
	}
	
	public void parseTranslationBlockWithBliki(String block) {
		initialize();
		if (block == null) {
			return;
		}
		WikipediaParser.parse(block, this, true, null);
		initialize();
	}

	private static final HashSet<String> transMacroWithNotes = new HashSet<String>();
	static {
		transMacroWithNotes.add("xlatio");
		transMacroWithNotes.add("trad-");

	}
	
	@Override
	public void substituteTemplateCall(String templateName,
			Map<String, String> parameterMap, Appendable writer)
			throws IOException {
		if ("kohta".equals(templateName)) {
			// kohta macro contains a set of translations with no usage note.
			// Either: (1) arg 1 is the sens number and arg2 is the gloss, arg3 are translations and arg 4 is final
			// Or: arg1 is translations and arg 2 is final
			int translationPositionalArg = findTranslations(parameterMap); 
			String xans = parameterMap.get(Integer.toString(translationPositionalArg));
			String gloss = computeGlossValue(parameterMap, translationPositionalArg);
			extractTranslations(xans, gloss);
			
		} else if ("käännökset/korjattava".equalsIgnoreCase(templateName) || "kään/korj".equals(templateName) || "korjattava/käännökset".equals(templateName)) {
			// Missing translation message, just ignore it
		} else if (knownTranslationTemplates.contains(templateName)) {
            // Language name, resubstitute it with its own value
            writer.append("{-")
                    .append(templateName)
                    .append("|")
                    .append(WikiTool.toParameterString(parameterMap))
                    .append("-}");
        } else if (isALanguageName(templateName)) {
			// Language name, resubstitute it with its own value
			writer.append(templateName);
		} else if ("yhteys".equals(templateName) || "kuva".equals(templateName)) {
            super.substituteTemplateCall(templateName, parameterMap, writer);
        } else {
			 log.debug("Called template: {} while parsing translations of: {}", templateName, delegate.currentLexEntry());
			// Just ignore the other template calls (uncomment to expand the template calls).
			// super.substituteTemplateCall(templateName, parameterMap, writer);
		}
	}

	private boolean isALanguageName(String templateName) {
        return null != SuomiLangToCode.getCanonicalCode(templateName);
    }

	StringBuffer glossbuff = new StringBuffer();
	private String computeGlossValue(Map<String, String> parameterMap, int translationPositionalArg) {
		glossbuff.setLength(0);
		int i = 1;
		while (i != translationPositionalArg) {
			glossbuff.append(parameterMap.get(Integer.toString(i)).trim());
			glossbuff.append("|");
			i++;
		}
        while (glossbuff.length() > 0 && glossbuff.charAt(glossbuff.length()-1) == '|') {
            glossbuff.setLength(glossbuff.length()-1);
        }
		// if (glossbuff.length() > 0) glossbuff.setLength(glossbuff.length()-1);
		return glossbuff.toString();
	}

	private int findTranslations(Map<String, String> parameterMap) {
		// The number of args should no exceed 7 (arbitrary) 
		// Find last non null arg
		int p = 7;
		while (p != 1 && parameterMap.get(Integer.toString(p)) == null) {
			p--;
		}
		String v = parameterMap.get(Integer.toString(p));
		if (1 != p && ("".equals(v) || "loppu".equals(v))) {
			// The last parameter is the closing one... It should be mandatory
			p--;
		}
		return p;
	}	
	
	protected final static String carPatternString;
	protected final static String macroOrLinkOrcarPatternString;


	static {
		// DONE: Validate the fact that links and macro should be on one line or may be on several...
		// DONE: for this, evaluate the difference in extraction !
		
		// les caractères visible 
		carPatternString=
				new StringBuilder().append("(.)")
				.toString();

        // TODO: We should suppress multiline xml comments even if macros or line are to be on a single line.
        macroOrLinkOrcarPatternString = new StringBuilder()
                .append("(?:")
                // Macro-modified for translation extractions
                .append("\\{\\-")
                .append("([^\\}\\|\n\r]*)(?:\\|([^\\}\n\r]*))?")
                .append("\\-\\}")
                .append(")|(?:")
                .append(WikiPatterns.macroPatternString)
                .append(")|(?:")
                .append(WikiPatterns.linkPatternString)
                .append(")|(?:")
                .append("(:*\\*)")
                .append(")|(?:")
                .append("(\\*:)")
                .append(")|(?:")
                .append(carPatternString)
                .append(")").toString();



	}
	protected final static Pattern carPattern;
	protected final static Pattern macroOrLinkOrcarPattern;


	static {
		carPattern = Pattern.compile(carPatternString);
		macroOrLinkOrcarPattern = Pattern.compile(macroOrLinkOrcarPatternString, Pattern.MULTILINE|Pattern.DOTALL);
		
	}

	public void extractTranslations(String block) {
		extractTranslations(block, null);
	}

    public static Set<String> knownTranslationTemplates = new HashSet<String>();
    static {
        knownTranslationTemplates.add("ylä");
        knownTranslationTemplates.add("ala");
        knownTranslationTemplates.add("keski");
        knownTranslationTemplates.add("käännös");
        knownTranslationTemplates.add("l");
        knownTranslationTemplates.add("n");
        knownTranslationTemplates.add("m");
        knownTranslationTemplates.add("f");
        knownTranslationTemplates.add("mf");
        knownTranslationTemplates.add("ijekavica");
        knownTranslationTemplates.add("ekavica");
        knownTranslationTemplates.add("monikollinen");
        knownTranslationTemplates.add("arkikieltä");
        knownTranslationTemplates.add("ru-ia");
        knownTranslationTemplates.add("BrE");
        knownTranslationTemplates.add("AmE");
        knownTranslationTemplates.add("Am");
        knownTranslationTemplates.add("ru-pa");
        knownTranslationTemplates.add("ru-tr");
        knownTranslationTemplates.add("el-tr");
        knownTranslationTemplates.add("sv-3");
        knownTranslationTemplates.add("sv-4");
        knownTranslationTemplates.add("sv-5");
        knownTranslationTemplates.add("cs-ia");
        knownTranslationTemplates.add("de-a");

    }
	public void extractTranslations(String block, String gloss) {
		Matcher macroOrLinkOrcarMatcher = macroOrLinkOrcarPattern.matcher(block);
		final int INIT = 1;
		final int LANGUE = 2;
		final int TRAD = 3;

	
		
		int ETAT = INIT;

		String currentGlose = gloss;
		String lang=null, word= ""; 
		String usage = "";       
		String langname = "";
		String previousLang = null;
		
		while (macroOrLinkOrcarMatcher.find()) {

			String macro = macroOrLinkOrcarMatcher.group(1);
            if (null == macro) macro = macroOrLinkOrcarMatcher.group(3);
			String link = macroOrLinkOrcarMatcher.group(5);
			String star = macroOrLinkOrcarMatcher.group(7);
			String starcont = macroOrLinkOrcarMatcher.group(8);
			String character = macroOrLinkOrcarMatcher.group(9);

			switch (ETAT) {

			case INIT:
				if (macro!=null) {
					if (macro.equalsIgnoreCase("ylä"))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else if (macroOrLinkOrcarMatcher.group(4) != null) {
                            currentGlose = macroOrLinkOrcarMatcher.group(4);
                        } else {
                            currentGlose = null;
						}

					} else if (macro.equalsIgnoreCase("ala")) {
						currentGlose = null;
					} else if (macro.equalsIgnoreCase("keski")) {
						//ignore
					}
				} else if(link!=null) {
					//System.err.println("Unexpected link while in INIT state.");
				} else if (starcont != null) {
					log.debug("Unexpected point continuation while in INIT state.");
				} else if (star != null) {
					ETAT = LANGUE;
				} else if (character != null) {
					if (character.equals(":")) {
						//System.err.println("Skipping ':' while in INIT state.");
					} else if (character.equals("\n") || character.equals("\r")) {

					} else if (character.equals(",")) {
						//System.err.println("Skipping ',' while in INIT state.");
					} else {
						//System.err.println("Skipping " + g5 + " while in INIT state.");
					}
				}

				break;

			case LANGUE:

				if (macro!=null) {
					if (macro.equalsIgnoreCase("ylä"))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else {
							currentGlose = null;
						}
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("ala")) {
						currentGlose = null;
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("keski")) {
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else {
						langname = LangTools.normalize(macro);
					}
				} else if(link!=null) {
					//System.err.println("Unexpected link while in LANGUE state.");
				} else if (starcont != null) {
					lang = previousLang;
					ETAT = TRAD;
				} else if (star != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (character != null) {
					if (character.equals(":")) {
						lang = langname.trim();
						lang=AbstractWiktionaryExtractor.stripParentheses(lang);
						lang =SuomiLangToCode.threeLettersCode(lang);
						langname = "";
						ETAT = TRAD;
					} else if (character.equals("\n") || character.equals("\r")) {
						//System.err.println("Skipping newline while in LANGUE state.");
					} else if (character.equals(",")) {
						//System.err.println("Skipping ',' while in LANGUE state.");
					} else {
						langname = langname + character;
					}
				} 

				break ;
				// TODO: maybe extract words that are not linked (currently kept in usage, but dropped as translation word is null).
			case TRAD:
				if (macro!=null) {
					if (macro.equalsIgnoreCase("ylä"))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else {
							currentGlose = null;
						}
						//if (word != null && word.length() != 0) {
						//	if(lang!=null){
						//		delegate.registerTranslation(lang, currentGlose, usage, word);
						//	}
						//}
						langname = ""; word = ""; usage = ""; lang=null;
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("ala")) {
						if (word != null && word.length() != 0) {
							if(lang!=null){
								delegate.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						currentGlose = null;
						langname = ""; word = ""; usage = ""; lang=null;
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("keski")) {
						if (word != null && word.length() != 0) {
							if(lang!=null){
								delegate.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						langname = ""; word = ""; usage = ""; lang = null;
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("käännös") || macro.equalsIgnoreCase("l")) {
						Map<String,String> argmap = WikiTool.parseArgs(macroOrLinkOrcarMatcher.group(2));
						if (null != word && word.length() != 0) log.debug("Word is not null ({}) when handling käännös macro in {}", word, this.delegate.currentLexEntry());
						String l = argmap.get("1");
						if (null != l && (null != lang) && ! lang.equals(LangTools.getCode(l))) {
							log.debug("Language in käännös macro does not map language in list in {}", this.delegate.currentLexEntry());
						}
						word = argmap.get("2");
						argmap.remove("1"); argmap.remove("2");
						if (! argmap.isEmpty()) usage = argmap.toString();
					} else {
						usage = usage + "{{" + macro + "}}";
					}
				} else if (link!=null) {
					word = word + " " + link;
				} else if (starcont != null) {
					// System.err.println("Skipping '*:' while in LANGUE state.");
				} else if (star != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (character != null) {
					if (character.equals("\n") || character.equals("\r")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if(lang!=null){
								delegate.registerTranslation(lang, currentGlose, usage, word);
							}
						} else if (usage.length() != 0) {
							log.debug("Non empty usage ({}) while word is null in: {}", usage, delegate.currentLexEntry());
						}
						previousLang = lang;
						lang = null; 
						usage = "";
						word = "";
						ETAT = INIT;
					} else if (character.equals(",")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if(lang!=null){
								delegate.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						usage = "";
						word = "";
					} else {
						usage = usage + character;
					}
				}
				break;
			default: 
				log.error("Unexpected state number: {}", ETAT);
				break; 
			}

		}
	}  

}
