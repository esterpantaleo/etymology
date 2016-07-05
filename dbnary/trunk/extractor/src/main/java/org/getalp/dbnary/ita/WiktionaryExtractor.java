/**
 * 
 */
package org.getalp.dbnary.ita;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.LangTools;
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

	protected final static String level2HeaderPatternString = "^==([^=].*[^=])==$";
		   
	protected final static String entrySectionPatternString ;	
	 
	static {
		 
		entrySectionPatternString = 
		            new StringBuilder().append("\\{\\{\\s*-")
		            .append("([^\\}\\|\n\r]*)-\\s*(?:\\|([^\\}\n\r]*))?")
		            .append("\\}\\}")
		            .toString();
		
	}

	protected final static String wikiSectionPatternString = "={2,4}\\s*([^=]*)\\s*={2,4}";

    private enum Block {NOBLOCK, IGNOREPOS, TRADBLOCK, DEFBLOCK, INFLECTIONBLOCK, ORTHOALTBLOCK, NYMBLOCK, PRONBLOCK}

	    // TODO: handle pronounciation
	    protected final static String pronounciationPatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";
	    
    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    // protected final static Pattern languageSectionPattern;
    //protected final static HashSet<String> nymMarkers;
    protected final static HashMap<String, String> nymMarkerToNymName;
    		
    static {

         // TODO: -acron-, -acronim-, -acronym-, -espr-, -espress- mark locution as phrases
                  
         nymMarkerToNymName = new HashMap<String,String>(20);
         nymMarkerToNymName.put("syn", "syn");
         nymMarkerToNymName.put("sin", "syn");
         nymMarkerToNymName.put("ant", "ant");
         nymMarkerToNymName.put("ipon", "hypo");
         nymMarkerToNymName.put("iperon", "hypo");
         nymMarkerToNymName.put("Hipônimos", "hypo");
         nymMarkerToNymName.put("Hiperônimos", "hyper");
         nymMarkerToNymName.put("Sinónimos", "syn");
         nymMarkerToNymName.put("Antónimos", "ant");
         nymMarkerToNymName.put("Hipónimos", "hypo");
         nymMarkerToNymName.put("Hiperónimos", "hyper");
       
    }

    protected final static Pattern sectionPattern;

    // TODO: handle pronunciation in italian
	private final static Pattern pronunciationPattern;
    protected final static Pattern level2HeaderPattern;

    static {
        level2HeaderPattern = Pattern.compile(level2HeaderPatternString, Pattern.MULTILINE);

        sectionPattern = Pattern.compile(entrySectionPatternString);
        pronunciationPattern = Pattern.compile(pronounciationPatternString);
    }

    private Block currentBlock;
    private int blockStart = -1;

    private String currentNym = null;

	private boolean isCorrectPOS;

    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
    	Matcher l1 = level2HeaderPattern.matcher(pageContent);
    	int itaStart = -1;
        wdh.initializePageExtraction(wiktionaryPageName);
    	while (l1.find()) {
    		// System.err.println(l1.group());
    		if (-1 != itaStart) {
    			// System.err.println("Parsing previous italian entry");
    			extractItalianData(itaStart, l1.start());
    			itaStart = -1;
    		}
    		if (isItalian(l1)) {
    			itaStart = l1.end();
    		}
    	}
    	if (-1 != itaStart) {
			//System.err.println("Parsing previous italian entry");
			extractItalianData(itaStart, pageContent.length());
		}
    	wdh.finalizePageExtraction();
    }
    
    private boolean isItalian(Matcher l1) {
        // log.debug("Considering header == {}",l1.group(1));
        String t = l1.group(1).trim();
		return (t.startsWith("{{-it-") || t.startsWith("{{it"));
	}

    // TODO: variants, pronunciations and other elements are common to the different entries in the page.
    protected void extractItalianData(int startOffset, int endOffset) {
        Matcher m = sectionPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        wdh.initializeEntryExtraction(wiktionaryPageName);
        currentBlock = Block.NOBLOCK;
        while(m.find()) {
            HashMap<String, Object> context = new HashMap<String, Object>();
            Block nextBlock = computeNextBlock(m, context);

            if (nextBlock == null) continue;
            // If current block is IGNOREPOS, we should ignore everything but a new DEFBLOCK/INFLECTIONBLOCK
            if (Block.IGNOREPOS != currentBlock || (Block.DEFBLOCK == nextBlock || Block.INFLECTIONBLOCK == nextBlock)) {
                leaveCurrentBlock(m);
                gotoNextBlock(nextBlock, context);
            }
        }
        // Finalize the entry parsing
        leaveCurrentBlock(m);
        wdh.finalizeEntryExtraction();
    }

    private Block computeNextBlock(Matcher m, Map<String, Object> context) {
        String title = m.group(1).trim();
        String nym;
        context.put("start", m.end());

        if (title.startsWith("trad1")) {
            context.put("start", m.start()); // Keep trad1 in block
            return Block.TRADBLOCK;
        } else if (title.equals("trad")) {
            return Block.TRADBLOCK;
        } else if (WiktionaryDataHandler.isValidPOS(title)) {
            context.put("pos", title);
            return Block.DEFBLOCK;
        } else if (title.equals("var")) {
            return Block.ORTHOALTBLOCK;
        } else if (null != (nym = nymMarkerToNymName.get(title))) {
            context.put("nym", nym);
            return Block.NYMBLOCK;
        } else if (title.equals("pron")) {
            return Block.PRONBLOCK;
        } else {
            // WARN: in previous implementation, L2 headers where considered as ignoredpos.
            log.debug("Ignoring content of section {} in {}", title, this.wiktionaryPageName);
            return Block.NOBLOCK;
        }
    }

    private void gotoNextBlock(Block nextBlock, HashMap<String, Object> context) {
        currentBlock = nextBlock;
        Object start = context.get("start");
        blockStart = (null == start) ? -1 : (int) start;
        switch (nextBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case DEFBLOCK:
                String pos = (String) context.get("pos");
                wdh.addPartOfSpeech(pos);
                break;
            case TRADBLOCK:
                break;
            case ORTHOALTBLOCK:
                break;
            case NYMBLOCK:
                currentNym = (String) context.get("nym");
                break;
            case PRONBLOCK:
                break;
            default:
                assert false : "Unexpected block while parsing: " + wiktionaryPageName;
        }

    }

    private void leaveCurrentBlock(Matcher m) {
        if (blockStart == -1) {
            return;
        }

        int end = computeRegionEnd(blockStart, m);

        switch (currentBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case DEFBLOCK:
                extractDefinitions(blockStart, end);
                break;
            case TRADBLOCK:
                extractTranslations(blockStart, end);
                break;
            case ORTHOALTBLOCK:
                extractOrthoAlt(blockStart, end);
                break;
            case NYMBLOCK:
                extractNyms(currentNym, blockStart, end);
                currentNym = null;
                break;
            case PRONBLOCK:
                extractPron(blockStart, end);
                break;
            default:
                assert false : "Unexpected block while parsing: " + wiktionaryPageName;
        }

        blockStart = -1;
    }
	
	
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
		.append("(:*\\*)")
		.append(")|(?:")
		.append(carPatternString)
		.append(")").toString();
    }
    protected final static Pattern macroOrLinkOrcarPattern;
	protected final static Pattern carPattern;
	static {
		carPattern = Pattern.compile(carPatternString);
		macroOrLinkOrcarPattern = Pattern.compile(macroOrLinkOrcarPatternString, Pattern.DOTALL);
	}

	protected final int INIT = 1;
	protected final int LANGUE = 2;
	protected final int TRAD = 3;  

	// TODO: delegate translation extraction to the appropriate wiki model
    private void extractTranslations(int startOffset, int endOffset) {
	    Matcher macroOrLinkOrcarMatcher = macroOrLinkOrcarPattern.matcher(pageContent);
		macroOrLinkOrcarMatcher.region(startOffset, endOffset);
		int ETAT = INIT;

		String currentGlose = null;
		String lang=null, word= ""; 
		String usage = "";       
		String langname = "";

        while (macroOrLinkOrcarMatcher.find()) {

			String g1 = macroOrLinkOrcarMatcher.group(1);
			String g3 = macroOrLinkOrcarMatcher.group(3);
			String g5 = macroOrLinkOrcarMatcher.group(5);
			String g6 = macroOrLinkOrcarMatcher.group(6);

			switch (ETAT) {

			case INIT:
				if (g1!=null) {
					if (g1.equalsIgnoreCase("trad1") || g1.equalsIgnoreCase("("))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else {
							currentGlose = null;
						}

					} else if (g1.equalsIgnoreCase("trad2") || g1.equalsIgnoreCase(")")) {
						currentGlose = null;
					} else if (g1.equalsIgnoreCase("mid")) {
						//ignore
					}
				} else if(g3!=null) {
					//System.err.println("Unexpected link while in INIT state.");
				} else if (g5 != null) {
					ETAT = LANGUE;
				} else if (g6 != null) {
					if (g6.equals(":")) {
						//System.err.println("Skipping ':' while in INIT state.");
					} else if (g6.equals("\n") || g6.equals("\r")) {

					} else if (g6.equals(",")) {
						//System.err.println("Skipping ',' while in INIT state.");
					} else {
						//System.err.println("Skipping " + g5 + " while in INIT state.");
					}
				}

				break;

			case LANGUE:

				if (g1!=null) {
					if (g1.equalsIgnoreCase("trad1") || g1.equalsIgnoreCase("("))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else {
							currentGlose = null;
						}
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (g1.equalsIgnoreCase("trad2") || g1.equalsIgnoreCase(")")) {
						currentGlose = null;
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else if (g1.equalsIgnoreCase("mid")) {
						langname = ""; word = ""; usage = "";
						ETAT = INIT;
					} else {
						langname = LangTools.normalize(g1);
					}
				} else if(g3!=null) {
					//System.err.println("Unexpected link while in LANGUE state.");
				} else if (g5 != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (g6 != null) {
					if (g6.equals(":")) {
						lang = langname.trim();
						lang=stripParentheses(lang);
						lang =ItalianLangToCode.threeLettersCode(lang);
						langname = "";
						ETAT = TRAD;
					} else if (g6.equals("\n") || g6.equals("\r")) {
						//System.err.println("Skipping newline while in LANGUE state.");
					} else if (g6.equals(",")) {
						//System.err.println("Skipping ',' while in LANGUE state.");
					} else {
						langname = langname + g6;
					}
				} 

				break ;
			case TRAD:
				if (g1!=null) {
					if (g1.equalsIgnoreCase("trad1") || g1.equalsIgnoreCase("("))  {
						if (macroOrLinkOrcarMatcher.group(2) != null) {
							currentGlose = macroOrLinkOrcarMatcher.group(2);
						} else {
							currentGlose = null;
						}
						//if (word != null && word.length() != 0) {
							//lang=stripParentheses(lang);
							//wdh.registerTranslation(lang, currentGlose, usage, word);
						//}
						langname = ""; word = ""; usage = ""; lang=null;
						ETAT = INIT;
					} else if (g1.equalsIgnoreCase("trad2") || g1.equalsIgnoreCase(")")) {
						if (word != null && word.length() != 0) {
							if(lang!=null) {
								wdh.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						currentGlose = null;
						langname = ""; word = ""; usage = ""; lang=null;
						ETAT = INIT;
					} else if (g1.equalsIgnoreCase("mid")) {
						if (word != null && word.length() != 0) {
							if(lang!=null){
							wdh.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						langname = ""; word = ""; usage = ""; lang = null;
						ETAT = INIT;
					} else {
						usage = usage + "{{" + g1 + "}}";
					}
				} else if (g3!=null) {
					word =word+" " + removeAnchor(g3);
				} else if (g5 != null) {
					//System.err.println("Skipping '*' while in LANGUE state.");
				} else if (g6 != null) {
					if (g6.equals("\n") || g6.equals("\r")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if(lang!=null){
							wdh.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						lang = null; 
						usage = "";
						word="";
						ETAT = INIT;
					} else if (g6.equals(",") || g6.equals(";")|| g6.equals("/")) {
						usage = usage.trim();
						// System.err.println("Registering: " + word + ";" + lang + " (" + usage + ") " + currentGlose);
						if (word != null && word.length() != 0) {
							if (lang!=null){
							wdh.registerTranslation(lang, currentGlose, usage, word);
							}
						}
						usage = "";
						word = "";
					} else {
						usage = usage + g6;
					}
				}
				break;
			default: 
				System.err.println("Unexpected state number:" + ETAT);
				break; 
			}
        	

        }
    }

    private String removeAnchor(String g3) {
        if (null == g3) return null;
        int hash = g3.indexOf('#');
        if (-1 == hash) {
            return g3;
        } else {
            return g3. substring(0, hash);
        }
    }

    // TODO: try to use gwtwiki to extract translations
//	private void extractTranslations(int startOffset, int endOffset) {
//       String transCode = pageContent.substring(startOffset, endOffset);
//       ItalianTranslationExtractorWikiModel dbnmodel = new ItalianTranslationExtractorWikiModel(this.wdh, this.wi, new Locale("pt"), "/${image}/"+wiktionaryPageName, "/${title}");
//       dbnmodel.parseTranslationBlock(transCode);
//   }
    
    private void extractPron(int startOffset, int endOffset) {
        String pronCode = pageContent.substring(startOffset, endOffset);
    	ItalianPronunciationExtractorWikiModel dbnmodel = new ItalianPronunciationExtractorWikiModel(this.wdh, this.wi, new Locale("it"), "/${image}", "/${title}");
        dbnmodel.parsePronunciation(pronCode);
	}
    
    @Override
	public void extractDefinition(String definition, int defLevel) {
		// TODO: properly handle macros in definitions.
        ItalianDefinitionExtractorWikiModel dbnmodel = new ItalianDefinitionExtractorWikiModel(this.wdh, this.wi, new Locale("it"), "/${image}", "/${title}");
        dbnmodel.parseDefinition(definition, defLevel);
	}

    @Override
    public void extractExample(String example) {
        ItalianExampleExtractorWikiModel dbnmodel = new ItalianExampleExtractorWikiModel(this.wdh, this.wi, new Locale("it"), "/${image}", "/${title}", this.wiktionaryPageName);
        dbnmodel.parseExample(example);
    }

}
