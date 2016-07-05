/**
 *
 */
package org.getalp.dbnary.bul;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author serasset
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {
	
	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);


    protected final static String languageSectionPatternString = "(\\{\\{\\-..\\-\\}\\})";
    protected final static String sectionPatternString = "(\\{\\{[^-][^\\}]*\\s*\\}\\})";

    // TODO: handle pronounciation
    protected final static String pronounciationPatternString = "\\{\\{pron\\|([^\\|\\}]*)(.*)\\}\\}";
    //protected final static HashSet<String> nymMarkers;


    protected final static Pattern languageSectionPattern;
    protected final static Pattern sectionPattern;
    private final static Pattern pronunciationPattern;

    static {
        sectionPattern = Pattern.compile(sectionPatternString);
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
        pronunciationPattern = Pattern.compile(pronounciationPatternString);
    }

    private final int NODATA = 0;
    int state = NODATA;
    private final int BULGARIANBLOCK = 1;
    protected boolean isCurrentlyExtracting = false;
    private int bulgarianBlockStart = -1;

   //  private boolean isCorrectPOS;

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    


	public boolean isCurrentlyExtracting() {
        return isCurrentlyExtracting;
    }

    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        while (languageFilter.find() && !isBulgarianLanguageHeader(languageFilter)) {
            ;
        }
        // Either the filter is at end of sequence or on French language header.
        if (languageFilter.hitEnd()) {
            // There is no Russian data in this page.
            return;
        }
        int bulgarianSectionStartOffset = languageFilter.end();
        // Advance till end of sequence or new language section
        languageFilter.find();
        int bulgarianSectionEndOffset = languageFilter.hitEnd() ? pageContent.length() : languageFilter.start();

        extractBulgarianData(bulgarianSectionStartOffset, bulgarianSectionEndOffset);
        wdh.finalizePageExtraction();
    }

    private boolean isBulgarianLanguageHeader(Matcher m) {
        return (null != m.group(1) && m.group(1).startsWith("{{-bg-"));
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


    private void gotoBulgarianBlock(Matcher m) {
        state = BULGARIANBLOCK;
        bulgarianBlockStart = m.start();
    }

    private void leaveBulgarianBlock(Matcher m) {
    	extractMorpho(bulgarianBlockStart, computeRegionEnd(bulgarianBlockStart, m));
        bulgarianBlockStart = -1;
        state = NODATA;
    }


    private void extractBulgarianData(int startOffset, int endOffset) {
        Matcher m = sectionPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        wdh.initializeEntryExtraction(wiktionaryPageName);
        while (m.find()) {
            switch (state) {
                case NODATA:
                    if (m.group(1).startsWith("{{") && !m.group(1).contains("{{Словоформи") && !m.group(1).contains("{{Уикипедия}}")) {
                        gotoBulgarianBlock(m);
                    }
                    break;
                case BULGARIANBLOCK:
                    if (m.group(1).startsWith("{{") && !m.group(1).contains("{{Словоформи")) {
                        leaveBulgarianBlock(m);
                        gotoBulgarianBlock(m);
                    } else {
                        leaveBulgarianBlock(m);
                        // if (isCorrectPOS) 
                        	gotoNoData(m);
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
            case BULGARIANBLOCK:
                leaveBulgarianBlock(m);
                break;
            default:
                assert false : "Unexpected state while ending extraction of entry: " + wiktionaryPageName;
        }
        wdh.finalizeEntryExtraction();
    }

    private void extractMorpho(int startOffset, int endOffset) {
        BulgarianWikiModel dbnmodel = new BulgarianWikiModel(this.wdh, this.wi, new Locale("bg"), "/${image}", "/${title}");
        dbnmodel.setPageName(this.wiktionaryPageName);
        dbnmodel.parseBulgarianBlock(pageContent.substring(startOffset, endOffset));
        if (log.isDebugEnabled()) {
        	dbnmodel.displayUsedTemplates();
        }
    }

}
