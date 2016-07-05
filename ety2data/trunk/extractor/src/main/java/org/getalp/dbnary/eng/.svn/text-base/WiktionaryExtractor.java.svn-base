/**
 *
 */
package org.getalp.dbnary.eng;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.*;
import org.getalp.dbnary.wiki.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serasset
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    //TODO: Handle Wikisaurus entries.
    //DONE: extract pronunciation
    //TODO: attach multiple pronounciation correctly
    static Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString = "==\\s*([^=]*)\\s*==";
    protected final static String sectionPatternString = "={2,5}\\s*([^=]*)\\s*={2,5}";
    protected final static String pronPatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";

    private enum Block {NOBLOCK, IGNOREPOS, TRADBLOCK, DEFBLOCK, INFLECTIONBLOCK, ORTHOALTBLOCK, NYMBLOCK, CONJUGATIONBLOCK, ETYMOLOGYBLOCK, PRONBLOCK}

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    protected static Pattern languageSectionPattern;
    protected final static Pattern sectionPattern;
    protected final static HashMap<String, String> nymMarkerToNymName;
    protected final static Pattern pronPattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);

        sectionPattern = Pattern.compile(sectionPatternString);
        pronPattern = Pattern.compile(pronPatternString);

        nymMarkerToNymName = new HashMap<String,String>(20);
        nymMarkerToNymName.put("Synonyms", "syn");
        nymMarkerToNymName.put("Antonyms", "ant");
        nymMarkerToNymName.put("Hyponyms", "hypo");
        nymMarkerToNymName.put("Hypernyms", "hyper");
        nymMarkerToNymName.put("Meronyms", "mero");
        nymMarkerToNymName.put("Holonyms", "holo");
        nymMarkerToNymName.put("Troponyms", "tropo");

        // TODO: Treat Abbreviations and Acronyms and contractions and Initialisms
        // TODO: Alternative forms
        // TODO: Extract quotations from definition block + from Quotations section

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
        // TODO: adapt extractor to allow extraction of foreign data.
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = sectionPattern.matcher(pageContent);
        while (languageFilter.find() && ! languageFilter.group(1).equals("English")) {
            ;
        }
        // Either the filter is at end of sequence or on English language header.
        if (languageFilter.hitEnd()) {
            // There is no english data in this page.
            return ;
        }
        int englishSectionStartOffset = languageFilter.end();
        // Advance till end of sequence or new language section
        while (languageFilter.find() && languageFilter.group().charAt(2) == '=') {
            ;
        }
        // languageFilter.find();
        int englishSectionEndOffset = languageFilter.hitEnd() ? pageContent.length() : languageFilter.start();
        
        extractEnglishData(englishSectionStartOffset, englishSectionEndOffset);
        wdh.finalizePageExtraction();
     }

    protected void extractEnglishData(int startOffset, int endOffset) {
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

        if (title.equals("Pronunciation")) {
            return Block.PRONBLOCK;
        } else if (WiktionaryDataHandler.isValidPOS(title)) {
            context.put("pos", title);
            return Block.DEFBLOCK;
        } else if (title.equals("Translations")) { // TODO: some sections are using Translation in the singular form...
            return Block.TRADBLOCK;
        } else if (title.equals("Alternative spellings")) {
            return Block.ORTHOALTBLOCK;
        } else if (title.equals("Conjugation")) {
            return Block.CONJUGATIONBLOCK;
        } else if (title.startsWith("Etymology")) {
            return Block.ETYMOLOGYBLOCK;
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
            case CONJUGATIONBLOCK:
                break;
            case ETYMOLOGYBLOCK:
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
            case CONJUGATIONBLOCK:
                extractConjugation(blockStart, end);
                break;
            case ETYMOLOGYBLOCK:
                extractEtymology(blockStart, end);
                break;
            default:
                assert false : "Unexpected block while parsing: " + wiktionaryPageName;
        }

        blockStart = -1;
    }

    private void extractEtymology(int blockStart, int end) {
        // TODO: extract the Etymology information
    }

    private void extractConjugation(int blockStart, int end) {
        // TODO: extract the Conjugation information
    }

    private void extractTranslations(int startOffset, int endOffset) {
        Matcher macroMatcher = WikiPatterns.macroPattern.matcher(pageContent);
        macroMatcher.region(startOffset, endOffset);
        String currentGloss = null;
        // TODO: there are templates called "qualifier" used to further qualify the translation check and evaluate if extracting its data is useful.
        while (macroMatcher.find()) {
            String g1 = macroMatcher.group(1);

           if (g1.equals("t+") || g1.equals("t-") || g1.equals("tø") || g1.equals("t")) {
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
                    lang=EnglishLangToCode.threeLettersCode(lang);
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
            } else if (g1.equals("checktrans-top")) {
                // forget glose.
                currentGloss = null;
            } else if (g1.equals("trans-mid")) {
                // just ignore it
            } else if (g1.equals("trans-bottom")) {
                // Forget the current glose
                currentGloss = null;
            }
        }
    }

    @Override
    public void extractExample(String example) {
        // TODO: current example extractor cannot handle English data where different lines are used to define the example.

    }

    private void extractPron(int startOffset, int endOffset) {

        Matcher pronMatcher = pronPattern.matcher(pageContent);
        pronMatcher.region(startOffset,endOffset);
        while (pronMatcher.find()) {
            String pron = pronMatcher.group(1);

            if (null == pron || pron.equals("")) return;

            if (! pron.equals("")) wdh.registerPronunciation(pron, "en-fonipa");
        }
    }

}
