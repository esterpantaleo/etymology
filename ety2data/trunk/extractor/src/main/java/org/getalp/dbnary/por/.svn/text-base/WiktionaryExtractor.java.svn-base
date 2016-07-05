/**
 * 
 */
package org.getalp.dbnary.por;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;

/**
 * @author serasset
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {


	protected final static String languageSectionPatternString = 
			"(?:=\\s*\\{\\{\\-([^=]*)\\-\\}\\}\\s*=)|(?:={1,5}\\s*([^=\\{\\]\\|\n\r]+)\\s*={1,5})";
	protected final static String level1HeaderPatternString = "^=([^=].*[^=])=$";

		protected final static String sectionPatternString = "={2,4}\\s*([^=]*)\\s*={2,4}";
	    private final int NODATA = 0;
	    private final int TRADBLOCK = 1;
	    private final int DEFBLOCK = 2;
	    private final int ORTHOALTBLOCK = 3;
	    private final int NYMBLOCK = 4;

	    // TODO: handle pronounciation
	    protected final static String pronounciationPatternString = "\\{\\{pron\\|([^\\|\\}]*)(.*)\\}\\}";
	    
	    // TODO: handle these:
		private final int IGNOREPOS = 7;
	    private final int PRONBLOCK = 5;
	    private final int MORPHOBLOCK = 6;

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    // protected final static Pattern languageSectionPattern;
    protected final static HashSet<String> posMarkers;
    //protected final static HashSet<String> nymMarkers;
    protected final static HashMap<String, String> nymMarkerToNymName;
    		
    static {
              
    	 posMarkers = new HashSet<String>(20);
         posMarkers.add("Substantivo");
         posMarkers.add("Adjetivo");
         posMarkers.add("Advérbio");
         posMarkers.add("Verbo");
                  
         nymMarkerToNymName = new HashMap<String,String>(20);
         nymMarkerToNymName.put("Sinônimos", "syn");
         nymMarkerToNymName.put("Antônimos", "ant");
         nymMarkerToNymName.put("Hipônimos", "hypo");
         nymMarkerToNymName.put("Hiperônimos", "hyper");
         nymMarkerToNymName.put("Sinónimos", "syn");
         nymMarkerToNymName.put("Antónimos", "ant");
         nymMarkerToNymName.put("Hipónimos", "hypo");
         nymMarkerToNymName.put("Hiperónimos", "hyper");
       
    }

    protected final static Pattern sectionPattern;
    protected final static Pattern languageSectionPattern;

    // TODO: handle pronunciation in portuguese
	private final static Pattern pronunciationPattern;
    protected final static Pattern level1HeaderPattern;

    static {
        level1HeaderPattern = Pattern.compile(level1HeaderPatternString, Pattern.MULTILINE);

        sectionPattern = Pattern.compile(sectionPatternString);
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
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
	private boolean isCorrectPOS;
   
    
    public boolean isCurrentlyExtracting() {
		return isCurrentlyExtracting;
	}

    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
    	Matcher l1 = level1HeaderPattern.matcher(pageContent);
    	int porStart = -1;
        wdh.initializeEntryExtraction(wiktionaryPageName);
    	while (l1.find()) {
    		// System.err.println(l1.group());
    		if (-1 != porStart) {
    			// System.err.println("Parsing previous portuguese entry");
    			extractPortugueseData(porStart, l1.start());
    			porStart = -1;
    		}
    		if (isPortuguese(l1)) {
    			porStart = l1.end();
    		}
    	}
    	if (-1 != porStart) {
			//System.err.println("Parsing previous portuguese entry");
			extractPortugueseData(porStart, pageContent.length());
		}
    	wdh.finalizeEntryExtraction();
        wdh.finalizePageExtraction();
    }
    
    private boolean isPortuguese(Matcher l1) {
		if (l1.group(1).trim().startsWith("{{-pt-}}")) return true;
		if (l1.group(1).trim().startsWith("Português")) return true;
		return false;
	}
    
//    private HashSet<String> unsupportedSections = new HashSet<String>(100);
    void gotoNoData(Matcher m) {
        state = NODATA;
    }

    
    void gotoTradBlock(Matcher m) {
        translationBlockStart = m.end();
        state = TRADBLOCK;
    }

    void gotoDefBlock(Matcher m){
        state = DEFBLOCK;
        definitionBlockStart = m.end();
        wdh.addPartOfSpeech(m.group(1));
    }
    
    void gotoOrthoAltBlock(Matcher m) {
        state = ORTHOALTBLOCK;    
        orthBlockStart = m.end();
    }
    
    void leaveDefBlock(Matcher m) {
    	int end = computeRegionEnd(definitionBlockStart, m);
    	// System.err.println(pageContent.substring(definitionBlockStart, end));
        extractDefinitions(definitionBlockStart, end);
        definitionBlockStart = -1;
    }
    
    void leaveTradBlock(Matcher m) {
        extractTranslations(translationBlockStart, computeRegionEnd(translationBlockStart, m));
        translationBlockStart = -1;
    }

    void leaveOrthoAltBlock(Matcher m) {
        extractOrthoAlt(orthBlockStart, computeRegionEnd(orthBlockStart, m));
        orthBlockStart = -1;
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

	private void gotoIgnorePos() {
		state = IGNOREPOS;
	}

	private void extractPortugueseData(int startOffset, int endOffset) {        
        Matcher m = sectionPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        gotoIgnorePos();
        while (m.find()) {
            switch (state) {
            case NODATA:
            	if (m.group(1).startsWith("Tradução")) {
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                	gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                	// Level 2 header that are not a correct POS, or Etimology or Pronunciation are considered as ignorable POS.
                	gotoIgnorePos();
                }
                
                break;
            case DEFBLOCK:
                // Iterate until we find a new section
            	if (m.group(1).equals("Tradução")) {
                    leaveDefBlock(m);
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                    leaveDefBlock(m);
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                    leaveDefBlock(m);
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    leaveDefBlock(m);
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                    leaveDefBlock(m);
                    gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                    leaveDefBlock(m);
                    gotoIgnorePos();
                } else {
                    leaveDefBlock(m);
                    gotoNoData(m);
                } 
                break;
            case TRADBLOCK:
            	if (m.group(1).equals("Tradução")) {
                    leaveTradBlock(m);
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                    leaveTradBlock(m);
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                    leaveTradBlock(m);
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    leaveTradBlock(m);
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                    leaveTradBlock(m);
                    gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                    leaveTradBlock(m);
                    gotoIgnorePos();
                } else {
                    leaveTradBlock(m);
                    gotoNoData(m);
                } 
                break;
            case ORTHOALTBLOCK:
            	if (m.group(1).equals("Tradução")) {
                    leaveOrthoAltBlock(m);
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                    leaveOrthoAltBlock(m);
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                    leaveOrthoAltBlock(m);
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    leaveOrthoAltBlock(m);
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                	leaveOrthoAltBlock(m);
                    gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                	leaveOrthoAltBlock(m);
                    gotoIgnorePos();
                } else {
                    leaveOrthoAltBlock(m);
                    gotoNoData(m);
                }
                break;
            case NYMBLOCK:
            	if (m.group(1).equals("Tradução")) {
                    leaveNymBlock(m);
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                    leaveNymBlock(m);
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                    leaveNymBlock(m);
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    leaveNymBlock(m);
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                	leaveNymBlock(m);
                    gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                	leaveNymBlock(m);
                    gotoIgnorePos();
                } else {
                    leaveNymBlock(m);
                    gotoNoData(m);
                }
            	break;
            case PRONBLOCK:
            	if (m.group(1).equals("Tradução")) {
                    leavePronBlock(m);
                    gotoTradBlock(m);
                } else if (posMarkers.contains(m.group(1))) {
                	leavePronBlock(m);
                    gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                	leavePronBlock(m);
                    gotoOrthoAltBlock(m);
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                	leavePronBlock(m);
                    gotoNymBlock(m);
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                	leavePronBlock(m);
                    gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                	leavePronBlock(m);
                    gotoIgnorePos();
                } else {
                	leavePronBlock(m);
                    gotoNoData(m);
                }
            	break;
            case IGNOREPOS:
            	if (m.group(1).equals("Tradução")) {
                } else if (posMarkers.contains(m.group(1))) {
                	gotoDefBlock(m);
                } else if (m.group(1).equals("Alternative spellings")) {
                } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                } else if (m.group(1).equals("{{pronúncia|pt}}")) {
                	gotoPronBlock(m);
                } else if (isLevel2Header(m)) {
                    gotoIgnorePos();
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
        case IGNOREPOS:
            break;
       default:
            assert false : "Unexpected state while ending extraction of entry: " + wiktionaryPageName;
        } 
    }
    
	private boolean isLevel2Header(Matcher m) {
		return m.group(0).startsWith("==") && ! m.group(0).startsWith("===") ;
	}


	private void extractTranslations(int startOffset, int endOffset) {
       String transCode = pageContent.substring(startOffset, endOffset);
       PortugueseTranslationExtractorWikiModel dbnmodel = new PortugueseTranslationExtractorWikiModel(this.wdh, this.wi, new Locale("pt"), "/${image}/"+wiktionaryPageName, "/${title}");
       dbnmodel.parseTranslationBlock(transCode);
   }
    
    private void extractPron(int startOffset, int endOffset) {
    	
	}
    
    @Override
	public void extractDefinition(String definition, int defLevel) {
		// TODO: properly handle macros in definitions.
        PortugueseDefinitionExtractorWikiModel dbnmodel = new PortugueseDefinitionExtractorWikiModel(this.wdh, this.wi, new Locale("pt"), "/${image}", "/${title}");
        dbnmodel.parseDefinition(definition, defLevel);
	}
    

}
