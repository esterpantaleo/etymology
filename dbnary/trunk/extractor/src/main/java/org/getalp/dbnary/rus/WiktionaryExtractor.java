/**
 *
 */
package org.getalp.dbnary.rus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;

/**
 * @author serasset
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {


    protected final static String languageSectionPatternString = "(?m)^\\s*=\\s*([^=]*)\\}\\}\\s*=\\s*$";
    protected final static String sectionPatternString = "(?m)^={2,5}\\s*(.*?)\\s*={2,5}$";

    // TODO: handle pronounciation
    protected final static String pronounciationPatternString = "\\{\\{pron\\|([^\\|\\}]*)(.*)\\}\\}";

    protected static RussianDefinitionExtractorWikiModel definitionExtractor;
    protected static RussianTranslationExtractorWikiModel translationExtractor;

    private final int NODATA = 0;
    private final int TRADBLOCK = 1;
    protected final int DEFBLOCK = 2;
    private final int ORTHOALTBLOCK = 3;
    private final int NYMBLOCK = 4;
    private final int IGNOREPOS = 7;
    private final int PRONBLOCK = 5;
    private final int MORPHOBLOCK = 6;

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    @Override
    public void setWiktionaryIndex(WiktionaryIndex wi) {
        super.setWiktionaryIndex(wi);
        definitionExtractor = new RussianDefinitionExtractorWikiModel(wdh, wi, new Locale("ru"), "--DO NOT USE IMAGE BASE URL FOR DEBUG--", "");
        translationExtractor = new RussianTranslationExtractorWikiModel(wdh, wi, new Locale("ru"), "--DO NOT USE IMAGE BASE URL FOR DEBUG--", "");
    }

    // protected final static Pattern languageSectionPattern;
    protected final static HashSet<String> posMarkers;
    //protected final static HashSet<String> nymMarkers;
    protected final static HashMap<String, String> nymMarkerToNymName;

    static {

        posMarkers = new HashSet<String>(20);
        posMarkers.add("Noun");
        posMarkers.add("Adjective");
        posMarkers.add("Adverb");
        posMarkers.add("Verb");
        posMarkers.add("Proper noun");

        nymMarkerToNymName = new HashMap<String,String>(20);
        nymMarkerToNymName.put("Синонимы", "syn");
        nymMarkerToNymName.put("Антонимы", "ant");
        nymMarkerToNymName.put("Гипонимы", "hypo");
        nymMarkerToNymName.put("Гиперонимы", "hyper");
        nymMarkerToNymName.put("Meronyms", "mero");
        nymMarkerToNymName.put("Holonyms", "holo");

    }

    protected final static Pattern languageSectionPattern;
    protected final static Pattern sectionPattern;
    private final static Pattern pronunciationPattern;

    static {
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


    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        while (languageFilter.find() && ! isRussianLanguageHeader(languageFilter)) {
            ;
        }
        // Either the filter is at end of sequence or on French language header.
        if (languageFilter.hitEnd()) {
            // There is no Russian data in this page.
            return ;
        }
        int russianSectionStartOffset = languageFilter.end();
        // Advance till end of sequence or new language section
        languageFilter.find();
        int russianSectionEndOffset = languageFilter.hitEnd() ? pageContent.length() : languageFilter.start();

        extractRussianData(russianSectionStartOffset, russianSectionEndOffset);
        wdh.finalizePageExtraction();
    }

    private boolean isRussianLanguageHeader(Matcher m) {
        return (null != m.group(1) && m.group(1).startsWith("{{-ru-"));
    }

    public void startExtraction() {
        isCurrentlyExtracting = true;
        wdh.initializeEntryExtraction(wiktionaryPageName);
    }

    public void stopExtraction() {
        isCurrentlyExtracting = false;
    }



    //    private HashSet<String> unsupportedSections = new HashSet<String>(100);
    void gotoNoData(Matcher m) {
        state = NODATA;
//        try {
//            if (! unsupportedSections.contains(m.group(1))) {
//                unsupportedSections.add(m.group(1));
//                System.out.println(m.group(1));
//            }
//        } catch (IllegalStateException e) {
//            // nop
//        }
    }


    void gotoTradBlock(Matcher m) {
        translationBlockStart = m.end();
        state = TRADBLOCK;
    }

    void gotoDefBlock(Matcher m){
        state = DEFBLOCK;
        definitionBlockStart = m.end();
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

    private void gotoMorphoBlock(Matcher m) {
        state = MORPHOBLOCK;
        morphoBlockStart = m.end();
        isCorrectPOS = false;
    }

    private void leaveMorphoBlock(Matcher m) {
        isCorrectPOS = extractMorpho(morphoBlockStart, computeRegionEnd(morphoBlockStart, m)) ;
        morphoBlockStart = -1;
    }

    private void gotoIgnorePos() {
        state = IGNOREPOS;
    }

    private void extractRussianData(int startOffset, int endOffset) {
        Matcher m = sectionPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        wdh.initializeEntryExtraction(wiktionaryPageName);
        gotoIgnorePos();
        while (m.find()) {
            switch (state) {
                case NODATA:
                    if (m.group(1).startsWith("Морфологические")) {
                        gotoMorphoBlock(m);
                    } else if (m.group(1).startsWith("Перевод")) {
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        gotoPronBlock(m);
                    }

                    break;
                case DEFBLOCK:
                    // Iterate until we find a new section
                    if (m.group(1).startsWith("Морфологические")) {
                        leaveDefBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leaveDefBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        leaveDefBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leaveDefBlock(m);
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveDefBlock(m);
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        leaveDefBlock(m);
                        gotoPronBlock(m);
                    } else {
                        leaveDefBlock(m);
                        gotoNoData(m);
                    }
                    break;
                case TRADBLOCK:
                    if (m.group(1).startsWith("Морфологические")) {
                        leaveTradBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leaveTradBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        leaveTradBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leaveTradBlock(m);
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveTradBlock(m);
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        leaveTradBlock(m);
                        gotoPronBlock(m);
                    } else {
                        leaveTradBlock(m);
                        gotoNoData(m);
                    }
                    break;
                case ORTHOALTBLOCK:
                    if (m.group(1).startsWith("Морфологические")) {
                        leaveOrthoAltBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leaveOrthoAltBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        leaveOrthoAltBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leaveOrthoAltBlock(m);
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveOrthoAltBlock(m);
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        leaveOrthoAltBlock(m);
                        gotoPronBlock(m);
                    } else {
                        leaveOrthoAltBlock(m);
                        gotoNoData(m);
                    }
                    break;
                case NYMBLOCK:
                    if (m.group(1).startsWith("Морфологические")) {
                        leaveNymBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leaveNymBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        leaveNymBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leaveNymBlock(m);
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveNymBlock(m);
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        leaveNymBlock(m);
                        gotoPronBlock(m);
                    } else {
                        leaveNymBlock(m);
                        gotoNoData(m);
                    }
                    break;
                case PRONBLOCK:
                    if (m.group(1).startsWith("Морфологические")) {
                        leavePronBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leavePronBlock(m);
                        gotoTradBlock(m);
                    } else if (m.group(1).equals("Значение")) {
                        leavePronBlock(m);
                        gotoDefBlock(m);
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leavePronBlock(m);
                        gotoOrthoAltBlock(m);
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leavePronBlock(m);
                        gotoNymBlock(m);
                    } else if (m.group(1).equals("Произношение")) {
                        leavePronBlock(m);
                        gotoPronBlock(m);
                    } else {
                        leavePronBlock(m);
                        gotoNoData(m);
                    }
                    break;
                case MORPHOBLOCK:
                    if (m.group(1).startsWith("Морфологические")) {
                        leaveMorphoBlock(m);
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoTradBlock(m); else gotoIgnorePos();
                    } else if (m.group(1).equals("Значение")) {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoDefBlock(m); else gotoIgnorePos();
                    } else if (m.group(1).equals("Alternative spellings")) {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoOrthoAltBlock(m); else gotoIgnorePos();
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoNymBlock(m); else gotoIgnorePos();
                    } else if (m.group(1).equals("Произношение")) {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoPronBlock(m); else gotoIgnorePos();
                    } else {
                        leaveMorphoBlock(m);
                        if (isCorrectPOS) gotoNoData(m); else gotoIgnorePos();
                    }
                    break;
                case IGNOREPOS:
                    if (m.group(1).startsWith("Морфологические")) {
                        gotoMorphoBlock(m);
                    } else if (m.group(1).equals("Перевод")) {
                    } else if (m.group(1).equals("Значение")) {
                    } else if (m.group(1).equals("Alternative spellings")) {
                    } else if (nymMarkerToNymName.containsKey(m.group(1))) {
                    } else if (m.group(1).equals("Произношение")) {
                    } else {
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
            case MORPHOBLOCK:
                leaveMorphoBlock(m);
                break;
            case IGNOREPOS:
                break;
            default:
                assert false : "Unexpected state while ending extraction of entry: " + wiktionaryPageName;
        }
        wdh.finalizeEntryExtraction();
    }

    private void extractTranslations(int startOffset, int endOffset) {
        String transCode = pageContent.substring(startOffset, endOffset);
        translationExtractor.setPageName(wiktionaryPageName);
        translationExtractor.parseTranslationBlock(transCode);
    }

    private void extractPron(int startOffset, int endOffset) {

    }

    @Override
    public void extractDefinition(String definition, int defLevel) {
        // TODO: properly handle macros in definitions.
        definitionExtractor.setPageName(wiktionaryPageName);
        definitionExtractor.parseDefinition(definition, defLevel);
    }

    private boolean extractMorpho(int startOffset, int endOffset) {
        RussianMorphoExtractorWikiModel dbnmodel = new RussianMorphoExtractorWikiModel(this.wdh, this.wi, new Locale("ru"), "/${image}", "/${title}");
        return dbnmodel.parseMorphoBlock(pageContent.substring(startOffset, endOffset));
    }

}
