package org.getalp.dbnary.nor;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author roques
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    protected final static String languageSectionPatternString = "={1,2}([^=]+)={2}\\n";
    protected final static String blockPatternString = "={3,4}([^=]+)={3,4}";
    protected final static String posPatternString = "\\{{2}([^\\}]*)\\}{2}\n";
    protected final static String defPatternString = "#\\s*([^:=#]+)";
    protected final static String examplePatternString = "#:\\s*([^=#-]+)|\\*\\s*([^\\*\n]+)";
    protected final static String tradPatternString = "\\*\\s*\\{*([^:\\}]+)\\}*:\\s*\\[{2}([^\\]]+)\\]{2}|\\{{2}([^\\}]*)[^:]*\\}{2}";
    protected final static String nymPatternString = "([^\\[,\\]]+)";
    protected final static String writtenRepPatternString = "([^\\[,\\]]+)";
    protected final static String pronPatternString = "\\{{2}([^\\}]+)\\}{2}";
    protected final static String abbrevPatternString = "\\[{2}([^\\]]+)\\]{2}";

    protected final static Pattern languageSectionPattern;
    protected final static Pattern blockPattern;
    protected final static Pattern posPattern;
    protected final static Pattern defPattern;
    protected final static Pattern examplePattern;
    protected final static Pattern tradPattern;
    protected final static Pattern nymPattern;
    protected final static Pattern writtenRepPattern;
    protected final static Pattern pronPattern;
    protected final static Pattern abbrevPattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
        blockPattern = Pattern.compile(blockPatternString);
        posPattern = Pattern.compile(posPatternString);
        defPattern = Pattern.compile(defPatternString);
        examplePattern = Pattern.compile(examplePatternString);
        tradPattern = Pattern.compile(tradPatternString);
        nymPattern = Pattern.compile(nymPatternString);
        writtenRepPattern = Pattern.compile(writtenRepPatternString);
        pronPattern = Pattern.compile(pronPatternString);
        abbrevPattern = Pattern.compile(abbrevPatternString);
    }

    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        int startSection = -1;

        String nextLang , lang = null;

        while (languageFilter.find()) {
            nextLang = languageFilter.group(1);
            extractDataLang(startSection, languageFilter.start(), lang);
            lang = nextLang;
            startSection = languageFilter.end();
        }

        if (languageFilter.hitEnd()) {
            extractDataLang(startSection, pageContent.length(), lang);
        }
        wdh.finalizePageExtraction();
    }

    private enum Block {NOBLOCK, IGNOREPOS, DEFBLOCK, NYMBLOCK, TRADBLOCK, PRONBLOCK,
        ABBREVIATIONBLOCK, MORPHOBLOCK, WRITTENREP, EXAMPLEBLOCK}

    protected static HashMap<String,Block> blockValue = new HashMap<>();

    static{
        blockValue.put("Egennavn",Block.DEFBLOCK);        // Noun
        blockValue.put("ubstant",Block.DEFBLOCK);         // Noun
        blockValue.put("ubtant",Block.DEFBLOCK);         // Noun
        blockValue.put("Fellesnavn",Block.DEFBLOCK);      // Common noun
        blockValue.put("Verb",Block.DEFBLOCK);            // Verb
        blockValue.put("Gjerningsord",Block.DEFBLOCK);    // Verb
        blockValue.put("djektiv",Block.DEFBLOCK);         // Adj
        blockValue.put("djeltiv",Block.DEFBLOCK);         // Adj
        blockValue.put("dverb",Block.DEFBLOCK);           // Adv
        blockValue.put("Tallord",Block.DEFBLOCK);         // Numeral
        blockValue.put("Pronomen",Block.DEFBLOCK);        // Pronouns
        blockValue.put("Preposisjon",Block.DEFBLOCK);     // Preposition
        blockValue.put("Konjunksjon",Block.DEFBLOCK);     // conjunction
        blockValue.put("Interjeksjon",Block.DEFBLOCK);    // interjection
        blockValue.put("Suffiks",Block.DEFBLOCK);         // suffix
        blockValue.put("Prefiks",Block.DEFBLOCK);         // preffix
        blockValue.put("rtikkel",Block.DEFBLOCK);         // article
        blockValue.put("Lydord",Block.DEFBLOCK);          //  Onomatopoeia
        blockValue.put("Determinativ",Block.DEFBLOCK);    //  Determiner
        blockValue.put("Ordtak",Block.DEFBLOCK);          //  Proverb
        blockValue.put("Ordspråk",Block.DEFBLOCK);        //  Proverb
        blockValue.put("Idiom",Block.DEFBLOCK);           //  Idiom
        blockValue.put("ttrykk",Block.DEFBLOCK); // expression

        blockValue.put("Eksemp",Block.EXAMPLEBLOCK);
        blockValue.put("Brukseksempler",Block.EXAMPLEBLOCK);
        blockValue.put("Døme",Block.EXAMPLEBLOCK);
        blockValue.put("Forklaring",Block.EXAMPLEBLOCK);

        blockValue.put("versettelse",Block.TRADBLOCK);

        blockValue.put("ttale",Block.PRONBLOCK);

        blockValue.put("Synonym",Block.NYMBLOCK);
        blockValue.put("Hyponym",Block.NYMBLOCK);
        blockValue.put("Hyperonym",Block.NYMBLOCK);
        blockValue.put("Antonym",Block.NYMBLOCK);

        blockValue.put("Andre skrivemåter",Block.WRITTENREP);

        blockValue.put("Forkortelse",Block.ABBREVIATIONBLOCK);

        blockValue.put("Grammatikk",Block.MORPHOBLOCK);      // grammar
        blockValue.put("Bøyning",Block.MORPHOBLOCK);      // conjugation
        blockValue.put("Avledede",Block.MORPHOBLOCK); // derived term
        blockValue.put("Andre former",Block.MORPHOBLOCK); // other term
        blockValue.put("Avløserord",Block.MORPHOBLOCK); // other term

        blockValue.put("Se også",Block.IGNOREPOS); // see also
        blockValue.put("Beslektede termer",Block.IGNOREPOS); // related term
        blockValue.put("Område",Block.IGNOREPOS); // area
        blockValue.put("Etymologi",Block.IGNOREPOS);
        blockValue.put("Anagrammer",Block.IGNOREPOS); // anagrams
        blockValue.put("Sitat",Block.IGNOREPOS); // citation
        blockValue.put("mangler",Block.IGNOREPOS); // trad missing
    }

    protected static HashMap<String,String> blockName = new HashMap<>();

    static{
        blockName.put("adj","djektiv");
        blockName.put("idiom","Idiom");
        blockName.put("ordtak","Ordtak");
        blockName.put("ordspråk","Ordtak");
    }

    private Block getBlock(String blockString){
        Set<String> set = blockValue.keySet();
        for(String t : set){
            if(blockString.contains(t)){
                return blockValue.get(t);
            }
        }
        return Block.NOBLOCK;
    }

    protected void extractDataLang(int startOffset, int endOffset, String lang) {
        if (lang == null) {
            return;
        }


        if(lang.equals("Norsk"))
            wdh.initializeEntryExtraction(wiktionaryPageName);
        else{ // unused lang
            return;
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
            block = getBlock(blockString);
            extractDefinitions(startOffset,start);
        }
        while (m.find()) {
            extractDataBlock(start, m.start(), block, blockString);
            start = m.end();
            if(m.group(1) != null)
                blockString = m.group(1).trim();
            block = getBlock(blockString);
        }

        extractDataBlock(start, endOffset, block, blockString);
        wdh.finalizeEntryExtraction();
    }

    protected void extractDataBlock(int startOffset, int endOffset, Block currentBlock, String blockString) {
        switch (currentBlock) {
            case NOBLOCK:
                break;
            case IGNOREPOS:
                if(blockString != null && blockString.contains("tymol")){
                    extractDefinitions(startOffset,endOffset);
                }
                break;
            case DEFBLOCK:
                extractDefinitions(startOffset, endOffset, blockString);
                break;
            case EXAMPLEBLOCK:
                extractExample(startOffset,endOffset);
                break;
            case NYMBLOCK:
                extractNyms(startOffset, endOffset, blockString);
                break;
            case TRADBLOCK:
                extractTranslations(startOffset, endOffset);
                break;
            case PRONBLOCK:
                extractPronunciation(startOffset, endOffset);
                break;
            case WRITTENREP:
                extractWrittenRep(startOffset, endOffset);
                break;
            case ABBREVIATIONBLOCK:
                extractDefinitions(startOffset, endOffset, blockString);
                extractAbbrev(startOffset, endOffset);
                break;
            case MORPHOBLOCK:
                break;
            default:
                assert false : "Unexpected block while ending extraction of entry: " + wiktionaryPageName;
        }
    }

    protected void extractAbbrev(int start, int end) {
        Matcher m = abbrevPattern.matcher(pageContent);
        m.region(start, end);
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        while (m.find()) {
            dwdh.addAbbrev(m.group(1));
        }
    }


    protected void extractPronunciation(int start, int end) {
        Matcher m = pronPattern.matcher(pageContent);
        m.region(start, end);

        while(m.find()){
            String[] tmp = m.group(1).split("\\|");
            if(tmp[0].contains("Verb")){
                continue;
            }
            switch(tmp[0]){
                case "IPA":
                    if(tmp.length > 1)
                        wdh.registerPronunciation(tmp[1], "no-fonipa");
                    break;
                case "lyd":            // audio file
                case "audio":          // audio file
                case "uttale mangler": // missing
                case "uttale  mangler": // missing
                case "Uttale mangler": // missing
                case "mangler uttale": // missing
                case "lydfil mangler": // missing file
                case "oversettelser mangler": // missing file
                case "utt":
                case "X-SAMPA":
                case "SAMPA":
                case "Amerikanske delstater":
                case "Europa":
                case "Oppland":
                case "Fylke":
                case "suffiks":
                case "norm":
                case "p":
                case "etyl":
                    break;
                default:
                    log.debug("Unknown pron info {} --in-- {}", m.group(1), this.wiktionaryPageName);
            }
        }
    }


    protected void extractWrittenRep(int start, int end) {
        Matcher m = writtenRepPattern.matcher(pageContent);
        m.region(start, end);
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        while(m.find()){
            String written = m.group(1).replaceAll("\\*","").trim();
            if(!written.equals("")) {
                dwdh.addWrittenRep(written);
            }
        }
    }

    protected void extractNyms(int start, int end, String blockString) {
        Matcher m = nymPattern.matcher(pageContent);
        m.region(start, end);
        String nymRel = "";
        String nym = "";
        while(m.find()){
            if(blockString.contains("Synonym")){
                nymRel = "syn";
            }
            else if(blockString.contains("Hyperonym")){
                nymRel = "hyper";
            }
            else if(blockString.contains("Hyponym")){
                nymRel = "hypo";
            }
            else if(blockString.contains("Antonym")){
                nymRel = "ant";
            }
            if(m.group(1) != null){
                nym = m.group(1).replaceAll("\\*", "").trim();
            }
            if(!nym.equals(""))
                wdh.registerNymRelation(nym, nymRel);
        }
    }

    protected void extractDefinitions(int start, int end) {
        Matcher m = posPattern.matcher(this.pageContent);
        m.region(start, end);

        String blockString = null;
        while(m.find()){
            String[] tmps = m.group(1).split("\\|");
            String tmp = tmps[0].replaceAll("\\{","").trim();
            if(tmp.contains("overs") || tmp.contains("topp") || tmp.equals("o")){
                extractTranslations(start, end);
                return;
            }
            if(tmp.contains("mangler")){
                continue;
            }
            switch(tmp){
                case "-sub-": // noun
                case "no-sub": // noun
                case "nb-sub": // noun
                case "nn-sub": // noun
                case "-egennavn-": // noun
                case "no-egen": // noun
                case "no-sub-bøyningsform": // noun
                    blockString = "Substantiv";
                    break;
                case "nn-pron":
                case "nb-pron":
                case "no-pron":
                case "-pron-":
                    blockString = "Pronomen";
                    break;
                case "prep":
                case "preposisjon":
                case "-prep-":
                    blockString = "Preposisjon";
                    break;
                case "-adv-":
                case "no-adv":
                case "nb-adv":
                case "nn-adv":
                    blockString = "Adverb";
                    break;
                case "-verb-":
                case "no-verb":
                case "nb-verb":
                case "nn-verb":
                case "no-verb-et":
                case "no-verb-te":
                    blockString = "Verb";
                    break;
                case "-adj-":
                case "no-adj":
                case "nb-adj":
                case "nn-adj":
                    blockString = "Adjektiv";
                    break;
                case "tallord":
                case "-tall-":
                    blockString = "Tallord";
                    break;
                case "no-kon":
                case "nb-kon":
                case "nn-kon":
                case "-kon-":
                case "-konj-":
                case "konj":
                    blockString = "Konjunksjon";
                    break;
                case "eksempel":
                    blockString = "Eksemp";
                    break;
                case "-inter-":
                case "inter":
                    blockString = "Interjeksjon";
                    break;
                case "-fork-":
                case "fork":
                    blockString = "Forkortelse";
                    break;
                case "-lyd-":
                case "lyd":
                    blockString = "Lydord";
                    break;
                case "-prefiks-":
                case "prefiks":
                case "no-pref":
                case "nb-pref":
                case "nn-pref":
                    blockString = "Prefiks";
                    break;
                case "-suffiks-":
                case "suffiks":
                case "no-suff":
                case "nb-suff":
                case "nn-suff":
                    blockString = "Suffiks";
                    break;
                case "sitat":
                case "-sitat-":
                    blockString = "Sitat";
                    break;
                case "infl":
                    for(String t : tmps){
                        if(blockName.containsKey(t.trim())){
                            blockString = blockName.get(t.trim());
                            break;
                        }
                    }
                    break;
                case "term": // etimologia
                case "etyl":
                case "o-helt/heilt":
                case "sammensetning":
                case "verb-no": // morpho
                    return;
                default:
                   // log.debug("Unknown blockPos {} --in-- {}", tmp, this.wiktionaryPageName);
            }
            if(blockString != null){
                extractDefinitions(start, end, blockString);
            }
        }
    }

    protected void extractDefinitions(int start, int end, String blockString) {
        Matcher m = posPattern.matcher(pageContent);
        m.region(start, end);

        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        dwdh.addPartOfSpeech(blockString);

        int startSample = start;
        if(m.find()){
            dwdh.addPOSinfo(m.group(1).replaceAll("\\{\\}",""));
        }

        m = defPattern.matcher(pageContent);
        m.region(start, end);
        int senseNum = 1;
        while(m.find()) {
            extractExample(startSample, m.start());
            String def = cleanUpMarkup(m.group(1).trim());
            int index = def.indexOf("\n[[");
            if(index > 0)
                def = def.substring(0, def.indexOf("\n[["));
            wdh.registerNewDefinition(def, ""+senseNum);
            senseNum++;
            startSample = m.end();
        }

        if(m.hitEnd()){
            if(startSample < end) {
                extractExample(startSample, end);
            }
        }
    }

    protected void extractExample(int start, int end) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        Matcher exampleMatcher = examplePattern.matcher(this.pageContent);
        exampleMatcher.region(start, end);
        String ex = null;
        while(exampleMatcher.find()){
            if(exampleMatcher.group(1)!=null) {
                ex = exampleMatcher.group(1).trim();
                ex = ex.substring(2, ex.length());
                ex = ex.replaceAll("\'", "");
            }
            else if(exampleMatcher.group(2)!=null){
                ex = exampleMatcher.group(2).trim();
                ex = ex.replaceAll("\'", "");
            }
            if(ex != null) {
                if(ex.contains("Se også")){
                    return;
                }
                dwdh.registerExample(ex);
            }
        }
    }

    protected void extractTranslations(int start, int end){
        Matcher trad = tradPattern.matcher(pageContent);
        trad.region(start, end);
        String currentGloss = wiktionaryPageName;

        while(trad.find()) {
            if (trad.group(1) != null) {
                String lang = trad.group(1);
                if(lang.length() > 2){
                    lang = NorskLangToCode.threeLettersCode(trad.group(1));
                }
                String tmp[] = trad.group(2).split("\\|");
                String word;
                if(tmp.length == 2){
                    word = tmp[0];
                }
                else{
                    word = trad.group(2);
                }
                if(lang != null && word != null)
                    wdh.registerTranslation(lang, currentGloss, null, word);
            } else if (trad.group(3) != null) {
                String[] tTrad = trad.group(3).split("\\|");
                if (tTrad[0].startsWith("o:")) {
                    if (tTrad.length > 1)
                        wdh.registerTranslation(tTrad[0].substring(2), currentGloss, null, tTrad[1]);
                } else {
                    switch (tTrad[0]) {
                        case "overs":
                        case "o":
                        case "t+":
                        case "trad":
                        case "xlatio":
                        case "overs-sjå":
                        case "qualifier":
                            if (tTrad.length > 2)
                                wdh.registerTranslation(tTrad[1], currentGloss, null, tTrad[2]);
                            break;
                        case "overs-topp":
                        case "topp":
                        case "overs-trengersortering":
                        case "toppdrive propaganda":
                        case "trans-top":
                            if (tTrad.length > 1) {
                                currentGloss = tTrad[1];
                            }
                            break;
                        case "overs-midt":
                        case "trans-mid":
                        case "midt":
                        case "bunn":
                        case "overs-mangler": // trad missing
                        case "oversettelsr mangler": // trad missing
                        case "oversettelser mangler": // trad missing
                        case "etymologi mangler": // trad missing
                        case "overs-se":
                        case "refforbedreoversettelse":
                        case "Ukedagene norsk":
                        case "kontekst":
                        case "Afrika":
                        case "opprydning":
                        case "trenger referanse": // citation
                        case "m":
                        case "f":
                        case "n":
                        case "p":
                        case "l":
                        case "c":
                        case "Fylke":
                        case "Oslo":
                        case "wikipedia-no":
                        case "wikipediaartikkel":
                        case "Wikipediaartikkel":
                        case "Årets måneder norsk":
                        case "1-10 norsk":
                        case "refleksivt": // morpho
                        case "dativ": // morpho
                        case "FAchar":
                        case "etyl":
                        case "prefiks":
                        case "suffiks":
                            break;
                        case "overs-bunn":
                        case "trans-bottom":
                            extractDefinitions(trad.end(), end);
                            return;
                        default:
                            log.debug("Unknown Trad value {} --in-- {}", tTrad[0], wdh.currentLexEntry());
                    }
                }
            }
        }
    }

}
