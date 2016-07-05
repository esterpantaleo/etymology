package org.getalp.dbnary.nld;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author malick
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {



    static Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);
    protected final static String languageSectionPatternString = "\\{\\{=\\s*([^=}]*)\\s*=\\}\\}";
    
    protected final static String sectionPatternString ;	
    
    
    protected final static String pronPatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";
    
    
    private enum Block {NOBLOCK, IGNOREPOS, TRADBLOCK, DEFBLOCK, INFLECTIONBLOCK, ORTHOALTBLOCK, NYMBLOCK, PRONBLOCK}

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    protected final static Pattern languageSectionPattern;
    protected final static Pattern sectionPattern;
    static Pattern defOrExamplePattern ;
    protected final static HashMap<String, String> nymMarkerToNymName;
	protected final static Pattern pronPattern;

    static {
    	
    	String examplePatternString = 
                new StringBuilder().append("\\{\\{\\s*")
                .append("([^\\}\\|\n\r]*)\\s*\\|([^\n\r]*)")
                .append("(?:\\}\\})$")
                .toString();
    	
    	String defOrExamplePatternString = new StringBuilder()
    	    .append("(?:")
    	    .append(WikiPatterns.definitionPatternString)
    	    .append(")|(?:")
    	    .append(examplePatternString)
    	    .append(")").toString();
    	 
    	
    	sectionPatternString = 
                new StringBuilder().append("\\{\\{\\s*-")
                .append("([^\\}\\|\n\r]*)-\\s*(?:\\|([^\\}\n\r]*))?")
                .append("\\}\\}")
                .toString();
      
    	defOrExamplePattern = Pattern.compile(defOrExamplePatternString, Pattern.MULTILINE);
    	//defOrExamplePattern = Pattern.compile(examplePatternString);
  	    	
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
       
        sectionPattern = Pattern.compile(sectionPatternString);
        pronPattern = Pattern.compile(pronPatternString);

        nymMarkerToNymName = new HashMap<String,String>(20);
        nymMarkerToNymName.put("synoniems", "syn");
        nymMarkerToNymName.put("Antoniemen", "ant");
        nymMarkerToNymName.put("Hyponiemen", "hypo");
        /*nymMarkerToNymName.put("Hypernyms", "hyper");
        nymMarkerToNymName.put("Meronyms", "mero");
        nymMarkerToNymName.put("Holonyms", "holo");
        nymMarkerToNymName.put("Troponyms", "tropo");
       */
        
    }

    private Block currentBlock;
    private int blockStart = -1;

    private String currentNym = null;
    
    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
        extractData(false);
    }
    protected void extractData(boolean foreignExtraction) {
        wdh.initializePageExtraction(wiktionaryPageName);
        
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        
        int nldStart = -1;
        // on parcours la page pour trouver la partie netherlandais
        while (languageFilter.find()) {
        	if (languageFilter.group(1).equals("nld")) {
        		if (nldStart != -1) 
        			extractNetherlandData(nldStart, languageFilter.start());
        		nldStart = languageFilter.end();
        	} else {
        		if (nldStart != -1) 
        			extractNetherlandData(nldStart, languageFilter.start());
        		nldStart = -1;
        	}
        }
       
        // Either the filter is at end of sequence or on netherland language header.
        if (languageFilter.hitEnd()) {
            // There is no netherland data in this page.
        	if (nldStart != -1) 
    			extractNetherlandData(nldStart, pageContent.length());
    	
        }
        wdh.finalizePageExtraction();
     }

    protected void extractNetherlandData(int startOffset, int endOffset) {
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
        String title = m.group(1) ;
        String nym;
        context.put("start", m.end());
       
        if (title.equals("pron")) {
            return Block.PRONBLOCK;
        } else if (WiktionaryDataHandler.isValidPOS(title)) {
            context.put("pos", title);
            return Block.DEFBLOCK;
        } else if (title.equals("trans")) { 
            return Block.TRADBLOCK;
        } else if (null != (nym = nymMarkerToNymName.get(title))) {
        	
            context.put("nym", nym);
            return Block.NYMBLOCK;
        } else {
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

    private void extractTranslations(int startOffset, int endOffset) {
       Matcher macroMatcher = WikiPatterns.macroPattern.matcher(pageContent);
       macroMatcher.region(startOffset, endOffset);
       String currentGloss = null;
        // TODO: there are templates called "qualifier" used to further qualify the translation check and evaluate if extracting its data is useful.
       while (macroMatcher.find()) {
           String g1 = macroMatcher.group(1);

           if (g1.equals("trad")) {
               // DONE: Sometimes translation links have a remaining info after the word, keep it.
               String g2 = macroMatcher.group(2);
               int i1, i2;
               String lang, word;
               if (g2 != null && (i1 = g2.indexOf('|')) != -1) {
                   lang = LangTools.normalize(g2.substring(0, i1));

                   String usage = null;
                   if ((i2 = g2.indexOf('|', i1+1)) == -1) {
                       word = g2.substring(i1+1);
                   } else {
                       word = g2.substring(i1+1, i2);
                       usage = g2.substring(i2+1);
                   }
                 //  lang=NetherlandLangToCode.threeLettersCode(lang);
                   if(lang!=null){
                	   wdh.registerTranslation(lang, currentGloss, usage, word);
                   }
                  
               }
           } else if (g1.equals("trans-top")) {
               // Get the glose that should help disambiguate the source acception
               String g2 = macroMatcher.group(2);
               // Ignore glose if it is a macro
               if (g2 != null && ! g2.startsWith("{{")) {
                   currentGloss = g2;
               }
           } else if (g1.equals("trans-bottom")) {
               // Forget the current glose
               currentGloss = null;
           }
       }
    }

  
    private void extractPron(int startOffset, int endOffset) {
    	
    	Matcher pronMatcher = pronPattern.matcher(pageContent);
        pronMatcher.region(startOffset,endOffset);
    	while (pronMatcher.find()) {
    		String pron = pronMatcher.group(1);
    		if (null == pron || pron.equals("")) return;
    		
    		if (! pron.equals("")) 
    			wdh.registerPronunciation(pron, "nl-fonipa");
    	}
	}
    
	@Override
	protected void extractDefinitions(int startOffset, int endOffset) {
		    	
		        Matcher defOrExampleMatcher = defOrExamplePattern.matcher(pageContent);
		        defOrExampleMatcher.region(startOffset, endOffset);
		        while (defOrExampleMatcher.find()) {
		        	if (null != defOrExampleMatcher.group(1)) {
		        		extractDefinition(defOrExampleMatcher);        		
		        	} else if ( (null != defOrExampleMatcher.group(3)) && (defOrExampleMatcher.group(2).equals("bijv-1") ) ) { // Les exemples commencent toujours par bijv-1
		        		extractExample(defOrExampleMatcher);
		        	}
		        }
		   
	}
	
	@Override
	public void extractExample(Matcher definitionMatcher) {
		String example = definitionMatcher.group(3);
		extractExample(example);
	}
    
    


}
