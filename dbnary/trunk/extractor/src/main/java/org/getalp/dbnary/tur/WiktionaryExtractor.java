/**
 * 
 */
package org.getalp.dbnary.tur;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.getalp.dbnary.wiki.WikiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Barry
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {
	
	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString = "={2}\\s*\\{\\{Dil\\|([^\\}]*)\\}\\}\\s*={2}";
    protected final static String partOfSpeechPatternString = "={3}([^\\{]*)\\{\\{Söztürü\\|([^\\}\\|]*)(?:\\|([^\\}]*))?\\}\\}.*={3}";
    protected final static String macroPatternString = "\\{\\{([^\\}]*)\\}\\}";
    protected final static String definitionPatternString = "^:{1,3}\\[[^\\]]*]\\s*(.*)$";
    protected final static String pron1PatternString = "\\{\\{Çeviri Yazı\\|([^\\}\\|]*)(.*)\\}\\}";
    protected final static String pron2PatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";
    protected final static String pron3PatternString = ":?([^\\{\\|]*)";
    
    private final int NODATA = 0;
   
    private final int TRADBLOCK = 1;
   
    private final int DEFBLOCK = 2;
    
    private final int NYMBLOCK = 4;
    
    private final int PRONBLOCK = 5;
    


    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }
    protected final static Pattern languageSectionPattern;
    protected final static Pattern definitionPattern;
    protected final static HashSet<String> partOfSpeechMarkers;
    protected final static Pattern macroOrPOSPattern; 
    protected final static String macroOrPOSPatternString;
    protected final static Pattern pron1Pattern; 
    protected final static Pattern pron2Pattern; 
    protected final static Pattern pron3Pattern; 

    protected final static Pattern macroPattern; 

    protected final static HashMap<String, String> nymMarkerToNymName;

    static {
     
        macroPattern = Pattern.compile(macroPatternString);
        definitionPattern = Pattern.compile(definitionPatternString, Pattern.MULTILINE);
        languageSectionPattern = Pattern.compile(languageSectionPatternString);        
        
       /* pronPatternString = new StringBuilder()
    	.append("(?:").append(pron1PatternString).append(")")
    	.append("|(?:").append(pron2PatternString).append(")")
    	.append("|(?:").append(pron3PatternString).append(")")
        .toString();

        pronPattern = Pattern.compile(pronPatternString);*/
        
        pron1Pattern = Pattern.compile(pron1PatternString);
        pron2Pattern = Pattern.compile(pron2PatternString);
        pron3Pattern = Pattern.compile(pron3PatternString);


        macroOrPOSPatternString = new StringBuilder()
    	.append("(?:").append(macroPatternString)
    	.append(")|(?:").append(partOfSpeechPatternString).append(")")
        .toString();

        
        macroOrPOSPattern = Pattern.compile(macroOrPOSPatternString);

         partOfSpeechMarkers = new HashSet<String>(20);
        
        partOfSpeechMarkers.add("Fiil");// Verb
        partOfSpeechMarkers.add("Ad"); // Name
        partOfSpeechMarkers.add("Özel ad"); // Name ...
        partOfSpeechMarkers.add("Sıfat"); // Adjective 
        partOfSpeechMarkers.add("Zarf");//Adverb
        
        nymMarkerToNymName = new HashMap<String, String>(20);
        nymMarkerToNymName.put("sinonim", "syn");
        nymMarkerToNymName.put("Eş Anlamlılar", "syn");
        nymMarkerToNymName.put("Eş anlamlılar", "syn");
        nymMarkerToNymName.put("Karşıt Anlamlılar", "ant");
        nymMarkerToNymName.put("Karşıt anlamlılar", "ant");
        nymMarkerToNymName.put("Alt Kavramlar", "hypo");
        nymMarkerToNymName.put("Alt kavramlar", "hypo");
        nymMarkerToNymName.put("Üst Kavramlar", "hyper");
        nymMarkerToNymName.put("Üst kavramlar", "hyper");
        nymMarkerToNymName.put("Meronyms", "mero");
        
       
    	
    }
    	
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        // System.out.println(pageContent);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        while (languageFilter.find() && !languageFilter.group(1).equals("Türkçe")) {
            ;
        }
        // Either the filter is at end of sequence or on German language header.
        if (languageFilter.hitEnd()) {
            // There is no German data in this page.
            return;
        }
        int turkishSectionStartOffset = languageFilter.end();
        // Advance till end of sequence or new language section
        // WHY filter on section level ?: while (languageFilter.find() && (languageFilter.start(1) - languageFilter.start()) != 2) {
        languageFilter.find();
        // languageFilter.find();
        int turkishSectionEndOffset = languageFilter.hitEnd() ? pageContent.length() : languageFilter.start();

        extractTurkishData(turkishSectionStartOffset, turkishSectionEndOffset);
        wdh.finalizePageExtraction();
    }
	
    int state = NODATA;
    int partOfSpeechBlockStart = -1;
    int translationBlockStart = -1;
    private int nymBlockStart = -1;
    int pronBlockStart = -1;
    int defBlockStart = -1;
    private String currentNym = null;
    
    
    
    void gotoNoData(Matcher m) {
        state = NODATA;
    }

    void gotoTradBlock(Matcher m) {
        translationBlockStart = m.end();
        state = TRADBLOCK;
    }

    void leaveTradBlock(Matcher m) {
        extractTranslations(translationBlockStart, computeRegionEnd(translationBlockStart, m));
        translationBlockStart = -1;
    }
    
    

    private void gotoNymBlock(Matcher m) {
        state = NYMBLOCK;
        currentNym = nymMarkerToNymName.get(m.group(1));
        nymBlockStart = m.end();
    }

    private void leaveNymBlock(Matcher m) {
        extractNyms(currentNym, nymBlockStart, computeRegionEnd(nymBlockStart, m));
        currentNym = null;
        nymBlockStart = -1;
    }
    
    private void gotoPronBlock(Matcher m) {
        state = PRONBLOCK; 
        pronBlockStart = m.end();      
     }

    private void leavePronBlock(Matcher m) {
        extractPron(pronBlockStart, computeRegionEnd(pronBlockStart, m));
        pronBlockStart = -1;         
     }
    
    void gotoDefBlock(Matcher m) {
        state = DEFBLOCK;
        defBlockStart = m.end();
    }

    void leaveDefBlock(Matcher m) {
        extractDefinitions(defBlockStart, computeRegionEnd(defBlockStart, m));
        defBlockStart = -1;
    }
    
    // TODO: section {{Kısaltmalar}} gives abbreviations
    // TODO: section Yan Kavramlar gives related concepts (apparently not synonyms).
    private void extractTurkishData(int startOffset, int endOffset) {
        Matcher m = macroOrPOSPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        wdh.initializeEntryExtraction(wiktionaryPageName);
        gotoNoData(m);
        while (m.find()) {
            switch (state) {
            case NODATA:
                if (m.group(1) != null) {
                    // It's a macro
                    if (m.group(1).equals("Söyleniş")) { // Prononciation
                      gotoPronBlock(m);
                    } else if (m.group(1).equals("Anlamlar")) { // Definition_Meanings
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Çeviriler")) { // Traduction
                        gotoTradBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        gotoNymBlock(m);
                    }
                } else if (m.group(3) != null) {
                    if (partOfSpeechMarkers.contains(m.group(3))) {
                    	String def = m.group(3);
                    	        wdh.addPartOfSpeech(def);
                    	}
                	
                } else {
                	
                }

                break;
            case TRADBLOCK:
                if (m.group(1) != null) {
                    // It's a macro
                    if (m.group(1).equals("Söyleniş")) {
                      leaveTradBlock(m);
                       gotoPronBlock(m);
                    } else if (m.group(1).equals("Anlamlar")) {
                        leaveTradBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Çeviriler")) {
                        leaveTradBlock(m);
                        gotoTradBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveTradBlock(m);
                        gotoNymBlock(m);
                    }
                } else if (m.group(3) != null) {
                    leaveTradBlock(m);
                    String def = m.group(3);
                	        wdh.addPartOfSpeech(def);
                	gotoNoData(m);
                } else {
                	// Multiline macro
                	// System.out.println(m.group());
                }

                break;
            case NYMBLOCK:
                if (m.group(1) != null) {
                    // It's a macro
                    if (m.group(1).equals("Söyleniş")) {
                       leaveNymBlock(m);
                       gotoPronBlock(m);
                    } else if (m.group(1).equals("Çeviriler")) {
                        leaveNymBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Anlamlar")) {
                        leaveNymBlock(m);
                        gotoDefBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveNymBlock(m);
                        gotoNymBlock(m);
                    } 
                } else if (m.group(3) != null) {
                    leaveNymBlock(m);
                    String def = m.group(3);
                	        wdh.addPartOfSpeech(def);
                	gotoNoData(m);
                } else {
                	// Multiline macro
                	// System.out.println(m.group());
                }
                
                break;
                
            case DEFBLOCK:
                if (m.group(1) != null) {
                    // It's a macro
                    if (m.group(1).equals("Söyleniş")) {
                       leaveDefBlock(m);
                        gotoPronBlock(m);
                    } else if (m.group(1).equals("Çeviriler")) {
                        leaveDefBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Anlamlar")) {
                        leaveDefBlock(m);
                        gotoDefBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveDefBlock(m);
                        gotoNymBlock(m);
                    } 
                } else if (m.group(3) != null) {
                    leaveDefBlock(m);
                    String def = m.group(3);
                	        wdh.addPartOfSpeech(def);
                	gotoNoData(m);
                } else {
                	// Multiline macro
                	// System.out.println(m.group());
                }
                
                break;

            case PRONBLOCK:
                if (m.group(1) != null) {
                    // It's a macro
                    if (m.group(1).equals("Söyleniş")) {
                       leavePronBlock(m);
                        gotoPronBlock(m);
                    } else if (m.group(1).equals("Çeviriler")) {
                        leavePronBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Anlamlar")) {
                        leavePronBlock(m);
                        gotoDefBlock(m);
                    }else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leavePronBlock(m);
                        gotoNymBlock(m);
                    }
                 } else if (m.group(3) != null) {
                    leavePronBlock(m);
                    String def = m.group(3);
                	        wdh.addPartOfSpeech(def);
                	gotoNoData(m);
                } else {
                	// Multiline macro
                	// System.out.println(m.group());
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
        case PRONBLOCK:
            leavePronBlock(m);
            break;
        case TRADBLOCK:
            leaveTradBlock(m);
            break;
        case DEFBLOCK:
            leaveDefBlock(m);
            break;
        case NYMBLOCK:
            leaveNymBlock(m);
            break;
        default:
            assert false : "Unexpected state while extracting translations from dictionary.";
        }
        wdh.finalizeEntryExtraction();
    }

//    static final String glossOrMacroPatternString;
//    static final Pattern glossOrMacroPattern;

	protected final static String carPatternString;
	protected final static String macroOrLinkOrCarPatternString;

 
	static {   	

		// les caractères visible 
		carPatternString=
				new StringBuilder().append("(.)")
				.toString();

		// TODO: We should suppress multiline xml comments even if macros or line are to be on a single line.
		macroOrLinkOrCarPatternString = new StringBuilder()
		.append("(?:")
		.append(WikiPatterns.macroPatternString)
		.append(")|(?:")
		.append(WikiPatterns.linkPatternString)
		.append(")|(?:")
		.append("(:*\\*)")
		.append(")|(?:")
		.append("(\\*:)")
		.append(")|(?:")
		.append("\\[([^\\[][^\\]]*)\\]")
		.append(")|(?:")
		.append(carPatternString)
		.append(")").toString();

//		glossOrMacroPatternString = "(?:\\[([^\\][a-z]]*)\\])|(?:\\{\\{([^\\}\\|]*)\\|([^\\}\\|]*)\\|([^\\}\\|]*)\\}\\})";
//		glossOrMacroPattern = Pattern.compile(glossOrMacroPatternString);
	}

	protected final static Pattern carPattern;
	protected final static Pattern macroOrLinkOrCarPattern;


	static {
		carPattern = Pattern.compile(carPatternString);
		macroOrLinkOrCarPattern = Pattern.compile(macroOrLinkOrCarPatternString, Pattern.MULTILINE|Pattern.DOTALL);
	}

	public void extractTranslations(int startOffset, int endOffset) {
		Matcher macroOrLinkOrCarMatcher = macroOrLinkOrCarPattern.matcher(this.pageContent);
		macroOrLinkOrCarMatcher.region(startOffset, endOffset);
		
		final int INIT = 1;
		final int LANGUE = 2;
		final int TRAD = 3;

	
		
		int ETAT = INIT;

		String currentInlineGloss = "";
		String globalGloss = "";
		String lang=null, word= ""; 
		String usage = "";       
		String langname = "";
		String previousLang = null;
		
		while (macroOrLinkOrCarMatcher.find()) {

			String macro = macroOrLinkOrCarMatcher.group(1);
			String link = macroOrLinkOrCarMatcher.group(3);
			String star = macroOrLinkOrCarMatcher.group(5);
			String starcont = macroOrLinkOrCarMatcher.group(6);
			String inlineGloss = macroOrLinkOrCarMatcher.group(7);
			String character = macroOrLinkOrCarMatcher.group(8);

			switch (ETAT) {

			case INIT:
				if (macro!=null) {
					if (macro.equalsIgnoreCase("Üst"))  {
						if (macroOrLinkOrCarMatcher.group(2) != null) {
							globalGloss = macroOrLinkOrCarMatcher.group(2);
						} else {
							globalGloss = "";
						}

					} else if (macro.equalsIgnoreCase("Alt")) {
						globalGloss = "";
					} else if (macro.equalsIgnoreCase("Orta")) {
						//ignore
					}
				} else if(link!=null) {
					//System.err.println("Unexpected link while in INIT state.");
				} else if (starcont != null) {
					log.debug("Unexpected point continuation while in INIT state.");
				} else if (star != null) {
					ETAT = LANGUE;
				} else if (null != inlineGloss) {
					// Ignore glosses that are outside languages... Maybe add them to global gloss ?
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
					if (macro.equalsIgnoreCase("Üst"))  {
						if (macroOrLinkOrCarMatcher.group(2) != null) {
							globalGloss = macroOrLinkOrCarMatcher.group(2);
						} else {
							globalGloss = "";
						}
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("Alt")) {
						globalGloss = "";
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("Orta")) {
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
				} else if (null != inlineGloss) {
					// Ignore glosses that are outside languages... Maybe add them to global gloss ?
				} else if (character != null) {
					if (character.equals(":")) {
						lang = langname.trim();
						lang=AbstractWiktionaryExtractor.stripParentheses(lang);
						lang = TurkishLangtoCode.threeLettersCode(lang);
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
					if (macro.equalsIgnoreCase("Üst"))  {
						if (macroOrLinkOrCarMatcher.group(2) != null) {
							globalGloss = macroOrLinkOrCarMatcher.group(2);
						} else {
							globalGloss = "";
						}
						//if (word != null && word.length() != 0) {
						//	if(lang!=null){
						//		delegate.registerTranslation(lang, currentGlose, usage, word);
						//	}
						//}
						langname = ""; word = ""; usage = ""; lang=null; currentInlineGloss = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("Alt")) {
						if (word != null && word.length() != 0) {
							if(lang!=null){
								String gloss = (globalGloss.length() == 0) ? currentInlineGloss : globalGloss + "|" + currentInlineGloss;
								wdh.registerTranslation(lang, gloss, usage, word);
							}
						}
						globalGloss = "";
						langname = ""; word = ""; usage = ""; lang=null; currentInlineGloss = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("Orta")) {
						if (word != null && word.length() != 0) {
							if(lang!=null){
								String gloss = (globalGloss.length() == 0) ? currentInlineGloss : globalGloss + "|" + currentInlineGloss;
								wdh.registerTranslation(lang, gloss, usage, word);
							}
						}
						langname = ""; word = ""; usage = ""; lang = null; currentInlineGloss = "";
						ETAT = INIT;
					} else if (macro.equalsIgnoreCase("çeviri")) {
						Map<String,String> argmap = WikiTool.parseArgs(macroOrLinkOrCarMatcher.group(2));
						if (null != word && word.length() != 0) log.debug("Word is not null ({}) when handling çeviri macro in {}", word, wdh.currentLexEntry());
						String l = argmap.get("1");
						if (null != l && (null != lang) && ! lang.equals(LangTools.getCode(l))) {
							log.debug("Language in çeviri macro ({}) does not map language in list ({}) in {}", l, lang, wdh.currentLexEntry());
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
				} else if (null != inlineGloss) {
					currentInlineGloss = "[" + inlineGloss + "]";  // an inlinegloss invalidates the previous gloss (see if there 
					// are cases where several glosses are specified...
				} else if (character != null) {
					if (character.equals("\n") || character.equals("\r")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if(lang!=null){
								String gloss = (globalGloss.length() == 0) ? currentInlineGloss : globalGloss + "|" + currentInlineGloss;
								wdh.registerTranslation(lang, gloss, usage, word);
							}
						} else if (usage.length() != 0) {
							log.debug("Non empty usage ({}) while word is null in: {}", usage, wdh.currentLexEntry());
						}
						previousLang = lang;
						lang = null; 
						usage = "";
						word = "";
						currentInlineGloss = "";
						ETAT = INIT;
					} else if (character.equals(",") || character.equals(";")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if(lang!=null){
								wdh.registerTranslation(lang, currentInlineGloss, usage, word);
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

	
//    private void extractTranslationsOriginal(int startOffset, int endOffset) {
//        Matcher macroMatcher = glossOrMacroPattern.matcher(pageContent);
//        macroMatcher.region(startOffset, endOffset);
//        String currentGlose = null;
//        	
//        while (macroMatcher.find()) {
//        	String glose = macroMatcher.group(1);
//        	
//        	if(glose != null){
//
//        		currentGlose = glose ;
//        		
//        	} else {
//          
//        		String g1 = macroMatcher.group(2);
//        		String g2 = macroMatcher.group(3);
//        		String g3 = macroMatcher.group(4);
//    
//           if (g1.equals("çeviri")) {
//            	String lang;
//            	String word = null;
//            	String usage = null;
//
//            	lang = g2;
//        		// normalize language code
//              lang = LangTools.normalize(lang, lang);
//                  int i1;    
//                if ((i1 = g3.indexOf('|')) == -1) {
//                    word = g3;
//                } else {
//                    word = g3.substring(0, i1);
//                    usage = g3.substring(i1+1);
//                }
//            	lang=TurkishLangtoCode.threeLettersCode(lang);
//                if(lang!=null && word != null){
//             	   wdh.registerTranslation(lang, currentGlose, usage, word);
//                }
//           
//            } else if (g1.equals("Üst")) {
//                // German wiktionary does not provide a glose to disambiguate.
//                // Just ignore this marker.
//            } else if (g1.equals("Orta")) {
//                // just ignore it
//            } else if (g1.equals("Alt")) {
//                // Forget the current glose
//                currentGlose = null;
//            }
//        
//        }
//    }
// }
    
private void extractPron(int startOffset, int endOffset) {
    	
    	Matcher pron1Matcher = pron1Pattern.matcher(pageContent);
    	pron1Matcher.region(startOffset, endOffset);
    	String Pron1 = null;
    	
    	Matcher pron2Matcher = pron2Pattern.matcher(pageContent);
    	pron2Matcher.region(startOffset, endOffset);
    	String Pron2 = null;
    	
    	Matcher pron3Matcher = pron3Pattern.matcher(pageContent);
    	pron3Matcher.region(startOffset, endOffset);
    	String Pron3 = null; 
    	
    	 if (pron1Matcher.find()) {
    		 
    		 if(pron1Matcher.group(1) != null) {
        		 Pron1 = pron1Matcher.group(1);
        		 String pron = StringEscapeUtils.unescapeHtml4(Pron1); // Pour decoder ce qui est codé en caractère exemple: &#x02A7;&#x0251;
        		 
        		 if (null == pron || pron.equals("")) return;
    		
        		 if (! pron.equals("")) wdh.registerPronunciation(pron, "tur-fonipa");
    		 }
    	 } else if (pron2Matcher.find()) {
        	if(pron2Matcher.group(1) != null) {
       		 Pron2 = pron2Matcher.group(1);
       		 String pron = StringEscapeUtils.unescapeHtml4(Pron2); // Pour decoder ce qui est codé en caractère exemple: &#x02A7;&#x0251;

       		 if (null == pron || pron.equals("")) return;
   		
       		 if (! pron.equals("")) wdh.registerPronunciation(pron, "tur-fonipa");
        	} 
        } else if (pron3Matcher.find()) {
    		if(pron3Matcher.group(1) != null) {
    			Pron3 = pron3Matcher.group(1);
    			if(Pron3.indexOf('[') != -1){ // if pron3 = :[[Yardım:Söyleniş|Ses Dosyası]]: {{HP}} [[Media:Ocak.ogg|ocak]],  ''Çoğul:'' {{HP}} 
    				Pron3 = "";
    			}
    			String pron = StringEscapeUtils.unescapeHtml4(Pron3); // Pour decoder ce qui est codé en caractère exemple: &#x02A7;&#x0251;

    			if (null == pron || pron.equals("")) return;
		
    			if (! pron.equals("")) wdh.registerPronunciation(pron, "tur-fonipa");
    		}	
    	}
        	
	}
    
    
@Override
protected void extractDefinitions(int startOffset, int endOffset) {
	// TODO: The definition pattern is the only one that changes. Hence, we should normalize this processing and put the macro in language specific parameters.
	Matcher definitionMatcher = definitionPattern.matcher(this.pageContent);
    definitionMatcher.region(startOffset, endOffset);
    while (definitionMatcher.find()) {
        String def = cleanUpMarkup(definitionMatcher.group(1));
        if (def != null && !def.equals("")) {
        	wdh.registerNewDefinition(definitionMatcher.group(1));
        }
    }
}



}