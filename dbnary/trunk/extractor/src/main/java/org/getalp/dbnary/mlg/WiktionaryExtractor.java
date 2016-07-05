package org.getalp.dbnary.mlg;

import com.hp.hpl.jena.rdf.model.Property;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author roques
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString = "={2}\\s*\\{{2}=*([^\\}]+)=*\\}{2}\\s*={2}";
    protected final static String blockPatternString = "\\{{2}-([^\\|\\}]+)|#\\s*-([^-]+)-|={2,3}\\s*([^=]+)={2,3}|\\{{2}(pron)";
    protected final static String tradPatternString = "\\{{2}([^\\|:]+\\|[^\\}]+)\\}{2}|#\\s*([^:]+):\\s*\\[{2}([^\\]]+)\\]{2}|" +
            ":\\s*\\[([^\\]]+)\\]\\s*:\\s*\\[{2}([^\\]]+)\\]{2}";
    protected final static String nymsPatternString = "#\\s*\\[{2}([^\\]]+)\\]{2}";
    protected final static String pronPatternString = "\\|*([^\\}]+)\\}{2}";
    protected final static String defPatternString = "#\\s*([^\\n#*]+)\\n\\n|([^:\\n]+\\s*:\\s*[^\\n]+)";

    protected final static Pattern languageSectionPattern;
    protected final static Pattern blockPattern;
    protected final static Pattern tradPattern;
    protected final static Pattern nymsPattern;
    protected final static Pattern pronPattern;
    protected final static Pattern defPattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
        blockPattern = Pattern.compile(blockPatternString);
        tradPattern = Pattern.compile(tradPatternString);
        nymsPattern = Pattern.compile(nymsPatternString);
        pronPattern = Pattern.compile(pronPatternString);
        defPattern = Pattern.compile(defPatternString);
    }

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        int startSection = -1;

        String nextLang , lang = null;

        while (languageFilter.find()) {
            nextLang = languageFilter.group(1).replaceAll("=","").trim();
            extractDataLang(startSection, languageFilter.start(), lang);
            lang = nextLang;
            startSection = languageFilter.end();
        }

        if (languageFilter.hitEnd()) {
           extractDataLang(startSection, pageContent.length(), lang);
        }
        wdh.finalizePageExtraction();
    }

    private enum Block {NOBLOCK, IGNOREPOS, DEFBLOCK, POSBLOCK, NYMBLOCK, TRADBLOCK, PRONBLOCK}

    protected static HashMap<String,Block> blockValue = new HashMap<>();
    protected static HashMap<String,String> blockName = new HashMap<>();

    static{
        blockName.put("",""); // noblock

        blockName.put("None","none");

        blockName.put("ana","ana"); // noun
        blockName.put("e-ana","ana");
        blockName.put("flex-nom","ana");
        blockName.put("ana-flex","ana");
        blockName.put("nom-pr","ana");
        blockName.put("nom","ana");

        blockName.put("mat", "mat"); // verb
        blockName.put("e-mat", "mat");
        blockName.put("verb", "mat");
        blockName.put("flex-verb", "mat");
        blockName.put("verb-mat", "mat");
        blockName.put("verb-flex", "mat");
        blockName.put("mg-verb-flex", "mat");
        blockName.put("matoanteny", "mat");

        blockName.put("adj", "adj"); // adj
        blockName.put("flex-adj", "adj");
        blockName.put("mpam", "adj");
        blockName.put("mpam-ana", "adj");

        blockName.put("adv", "adv"); // adv
        blockName.put("tamb", "adv");
        blockName.put("Mpamaritra matoanteny", "adv");

        blockName.put("trad", "trad"); // trad
        blockName.put("dika", "trad");
        blockName.put("Fiteny hafa", "trad");
        blockName.put("Fiteny afa", "trad");

        blockName.put("pron", "pron"); // pron
        blockName.put("fanononana", "pron");

        blockName.put("mpamp", "prep"); // prep
        blockName.put("mpampiankina", "prep");

        blockName.put("tovona", "prefix");
        blockName.put("tsof", "infix");
        blockName.put("mpanoritra", "article");
        blockName.put("root", "root");
        blockName.put("expr", "expr");

        blockName.put("Ankapobeny", "def");

        // NYMS
        blockName.put("syn", "syn");
        blockName.put("ant", "ant");

        // ignore
        blockName.put("vang-oha", "ignore");
        blockName.put("Tsiahy", "ignore");
        blockName.put("jereo", "ignore");
        blockName.put("holo", "ignore");
        blockName.put("voc", "ignore");
        blockName.put("etim", "ignore");
        blockName.put("étym", "ignore");
        blockName.put("etym", "ignore");
        blockName.put("fomba fiteny", "ignore");
        blockName.put("hadisoana", "ignore"); // mistakes
        blockName.put("anagr", "ignore"); // anagram
        blockName.put("rohy", "ignore"); // see also
        blockName.put("Rohy ivelany sy ny loharano", "ignore"); // see also
        blockName.put("Rohy ivelany", "ignore"); // see also

        blockValue.put("", Block.NOBLOCK);

        blockValue.put("ana", Block.POSBLOCK); // noun
        blockValue.put("mat", Block.POSBLOCK); // verb
        blockValue.put("adj", Block.POSBLOCK); // adj
        blockValue.put("adv", Block.POSBLOCK); // adv
        blockValue.put("prep", Block.POSBLOCK); // prep
        blockValue.put("prefix", Block.POSBLOCK);
        blockValue.put("infix", Block.POSBLOCK);
        blockValue.put("article", Block.POSBLOCK);
        blockValue.put("none", Block.POSBLOCK);
        blockValue.put("root", Block.POSBLOCK);
        blockValue.put("expr", Block.POSBLOCK);

        blockValue.put("trad", Block.TRADBLOCK);

        blockValue.put("pron", Block.PRONBLOCK);

        blockValue.put("def", Block.DEFBLOCK);

        blockValue.put("syn", Block.NYMBLOCK);
        blockValue.put("ant", Block.NYMBLOCK);

        blockValue.put("ignore", Block.IGNOREPOS);
    }

    private Block getBlock(String blockString){
        Block res;
        if((res = blockValue.get(blockString)) == null) {
            log.debug("Unknown block {} --in-- {}", blockString, this.wiktionaryPageName);
            res = Block.NOBLOCK;
        }
        return res;
    }

    private String getBlockName(String blockString){
        String res = blockString;
        if(blockString != null) {
            if (blockString.equals("-")) {
                blockString = "None";
            }
            if (blockString.charAt(blockString.length() - 1) == '-') {
                blockString = blockString.substring(0, blockString.length() - 1);
            }
            if(blockString.contains("ikan")){
                blockString = "dika";
            }
            if(blockString.equals("ay,")){
                blockString = "";
            }
            if ((res = blockName.get(blockString)) == null) {
                log.debug("Unknown blockName {} --in-- {}", blockString, this.wiktionaryPageName);
            }
        }
        return res;
    }

    protected void extractDataLang(int startOffset, int endOffset, String lang){
        if (lang == null) {
            return;
        }
        if (lang.equals("mg")){
            wdh.initializeEntryExtraction(wiktionaryPageName);
        }
        else {
            //log.debug("Unused lang {} --in-- {}", lang, this.wiktionaryPageName);
            return;
            // wdh.initializeEntryExtraction(wiktionaryPageName, lang);
        }


        Matcher m = blockPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        String blockString = null;
        Block block = Block.IGNOREPOS;
        int start = startOffset;

        if(m.find()){
            start = m.start();
            if(m.group(1) != null)
                blockString = m.group(1).trim();
            else if(m.group(2) != null)
                blockString = m.group(2).trim();
            else if(m.group(3) != null)
                blockString = m.group(3).trim();
            else if(m.group(4) != null)
                blockString = m.group(4).trim();
            blockString = getBlockName(blockString);
            block = getBlock(blockString);
        }
        while (m.find()) {
            extractDataBlock(start, m.start(), block, blockString);
            start = m.end();
            if(m.group(1) != null)
                blockString = m.group(1).trim();
            else if(m.group(2) != null)
                blockString = m.group(2).trim();
            else if(m.group(3) != null)
                blockString = m.group(3).trim();
            else if(m.group(4) != null)
                blockString = m.group(4).trim();
            blockString = getBlockName(blockString);
            block = getBlock(blockString);
        }

        extractDataBlock(start, endOffset, block, blockString);
        wdh.finalizeEntryExtraction();
    }

    protected void extractDataBlock(int startOffset, int endOffset, Block currentBlock, String blockString) {
        switch (currentBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case PRONBLOCK:
                extractPron(startOffset, endOffset);
                extractDefinitions(startOffset, endOffset);
                break;
            case POSBLOCK:
                extractPOS(blockString);
                extractDefinitions(startOffset,endOffset);
                break;
            case DEFBLOCK:
                extractDefinitions(startOffset,endOffset);
                break;
            case NYMBLOCK:
                extractNyms(startOffset, endOffset, blockString);
                break;
            case TRADBLOCK:
                extractTranslations(startOffset, endOffset);
                break;
            default:
                assert false : "Unexpected block while ending extraction of entry: " + wiktionaryPageName;
        }
    }

    protected void extractPOS(String blockString) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        dwdh.addPartOfSpeech(blockString);
        dwdh.addExtraPartOfSpeech(blockString);
    }

    protected void extractTranslations(int start, int end) {
        Matcher trad = tradPattern.matcher(pageContent);
        trad.region(start, end);
        String currentGloss = wiktionaryPageName;

        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        while (trad.find()) {

            if(trad.group(1) != null){
                String[] tmp = trad.group(1).split("\\|");
                if(tmp.length == 3){
                    dwdh.addTranslation(tmp[2], currentGloss, null, tmp[1]);
                }
                else{
                    if(trad.group(1).startsWith("ébauche-trad") || // missing
                            trad.group(1).startsWith("...") ||
                            trad.group(1).startsWith("T|") ||
                            trad.group(1).startsWith("vang") ||
                            trad.group(1).startsWith("Tsofoka")
                            ){
                        continue;
                    }
                    log.debug("Unknown Trad value {} --in-- {}", trad.group(1), wdh.currentLexEntry());
                }
            }
            else if(trad.group(2) != null){
                if(trad.group(2).startsWith("fototeny")){
                    continue;
                }
                String lang = trad.group(2).replaceAll("\\[", "");
                lang = lang.replaceAll("\\]", "");
                lang = lang.replaceAll("\\{", "");
                lang = lang.replaceAll("\\}", "");
                lang = lang.replaceAll("fiteny", "");
                lang = lang.replaceAll("#", "").trim();
                String lang3;
                if(lang.length() == 2){
                    lang3 = lang;
                }
                else {
                    lang3 = MalagasyLangToCode.threeLettersCode(lang);
                }
                if(lang3 != null){
                    dwdh.addTranslation(lang3, currentGloss, null, trad.group(3));
                }
                else{
                    if(trad.group(2).contains("...")){
                        continue;
                    }
                    log.debug("Unknown Trad2 lang value {} --in-- {}", lang, wdh.currentLexEntry());
                }
            }
            else if(trad.group(4) != null){
                dwdh.addTranslation(trad.group(4), currentGloss, null, trad.group(5));
            }
        }
    }

    protected void extractNyms(int start, int end, String blockString) {

        Matcher m = nymsPattern.matcher(pageContent);
        m.region(start,end);

        while (m.find()) {
            wdh.registerNymRelation(m.group(1), blockString);
        }
    }

    protected void extractPron(int start, int end){
        Matcher pron = pronPattern.matcher(pageContent);
        pron.region(start, end);

        while(pron.find()){
            String[] tmp = pron.group(1).split("\\|");
            if(tmp.length > 1 && !tmp[0].contains("X-SAMPA")
                    && !tmp[0].startsWith("\n#") && !tmp[0].contains("\\{")){
                wdh.registerPronunciation(tmp[0], "mg-fonipa");
            }
        }
    }

    protected void extractDefinitions(int start, int end) {
        Matcher m = defPattern.matcher(pageContent);
        m.region(start, end);

        int startOffset = -1;
        int senseNum = 1;
        while (m.find()) {
            String tmp;
            if (m.group(1) != null) {
                tmp = m.group(1).trim();
            }
            else if(m.group(2) != null){
                tmp = m.group(2).trim();
            }
            else {
                return;
            }
            if (!tmp.equals("")) {
                if(startOffset != -1){
                    extractExample(startOffset,m.start());
                    startOffset = -1;
                }
                else{
                    startOffset = m.end();
                }
                wdh.registerNewDefinition(cleanUpMarkup(tmp), "" + senseNum);
                senseNum++;
            }
        }
    }

    protected void extractExample(int start, int end){
        if(start >= end){
            return;
        }
        String ex = pageContent.substring(start, end);
        if(!ex.trim().equals(""))
            wdh.registerExample(ex.replaceAll("\n",""), new HashMap<Property, String>());
    }
}
