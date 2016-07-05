/**
 *
 */
package org.getalp.dbnary.lit;

import com.hp.hpl.jena.rdf.model.Property;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.filter.WikiTextReader;
import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.fra.FrenchExtractorWikiModel;
import org.getalp.dbnary.hbs.SerboCroatianMorphoExtractorWikiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author roques
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString = "={2}\\s*\\{{2}([^\\}\\|]*)\\}{2}\\s*={2}";
    protected final static String blockPatternString = "={2,4}([^=]*)={2,4}";
    protected final static String posPatternString = "\\{{2}([^\\}]*)\\}{2}";
    protected final static String defPatternString = "#\\s*([^#<]*)\\s*|\\*\\s*([^\\*<]*)\\s*";
    protected final static String tradPatternString = "\\{{2}([^\\}]*)\\}{2}";
    protected final static String tradContentPatternString = "\\[{2}([^\\]]*)\\]{2}";
    protected final static String nymPatternString = "\\{{2}([^\\}]*)\\}{2}";
    protected final static String relPatternString = "\\{{2}([^\\}]*)\\}{2}";
    protected final static String pronPatternString = "\\{{2}([^\\|]*)\\|\\(\\[([^]]*)\\]\\)\\}{2}";
    protected final static String examplePatternString = "\\*\\s*\\[{2}([^\\]]*)";

    protected final static Pattern languageSectionPattern;
    protected final static Pattern blockPattern;
    protected final static Pattern posPattern;
    protected final static Pattern defPattern;
    protected final static Pattern tradPattern;
    protected final static Pattern tradContentPattern;
    protected final static Pattern nymPattern;
    protected final static Pattern relPattern;
    protected final static Pattern pronPattern;
    protected final static Pattern examplePattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
        blockPattern = Pattern.compile(blockPatternString);
        posPattern = Pattern.compile(posPatternString);
        defPattern = Pattern.compile(defPatternString);
        tradPattern = Pattern.compile(tradPatternString);
        tradContentPattern = Pattern.compile(tradContentPatternString);
        nymPattern = Pattern.compile(nymPatternString);
        relPattern = Pattern.compile(relPatternString);
        pronPattern = Pattern.compile(pronPatternString);
        examplePattern = Pattern.compile(examplePatternString);
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
        RELATEDTERMBLOCK, EXAMPLEBLOCK}

    protected static HashMap<String,Block> blockValue = new HashMap<>();

    static{
        blockValue.put("",Block.NOBLOCK);

        blockValue.put("Daiktavardis",Block.DEFBLOCK);    // Noun
        blockValue.put("Daikto vardis",Block.DEFBLOCK);   // Name of a thing
        blockValue.put("Veiksmažodis",Block.DEFBLOCK);    // verb
        blockValue.put("Būdvardis",Block.DEFBLOCK);       // adj
        blockValue.put("Prieveiksmis",Block.DEFBLOCK);    // adv
        blockValue.put("prieveiksmis",Block.DEFBLOCK);
        blockValue.put("Dalyvis",Block.DEFBLOCK);         // participle
        blockValue.put("Pusdalyvis",Block.DEFBLOCK);      // participle
        blockValue.put("Padalyvis",Block.DEFBLOCK);       // preposition ?
        blockValue.put("Prielinksnis",Block.DEFBLOCK);    // preposition
        blockValue.put("Žodžių junginys",Block.DEFBLOCK); // phrase
        blockValue.put("Jungtukas",Block.DEFBLOCK);       // conjunction
        blockValue.put("Įvardis",Block.DEFBLOCK);         // Pronoun
        blockValue.put("Skaitvardis",Block.DEFBLOCK);     // numeral
        blockValue.put("Raidė",Block.DEFBLOCK);           // letter
        blockValue.put("Santrumpa",Block.DEFBLOCK);       // abbreviation
        blockValue.put("Dalelytė",Block.DEFBLOCK);        // element
        blockValue.put("Priešdėlis",Block.DEFBLOCK);      // Prefix
        blockValue.put("Priesaga",Block.DEFBLOCK);        // Suffix
        blockValue.put("Jaustukas",Block.DEFBLOCK);       // interjection
        blockValue.put("Ištiktukas",Block.DEFBLOCK);      // interjection
        blockValue.put("Kiekinis skaitvardis",Block.DEFBLOCK); // cardinal
        blockValue.put("\"Kiekinis skaitvardis\"",Block.DEFBLOCK); // cardinal
        blockValue.put("Kelintinis skaitvardis",Block.DEFBLOCK); // ordinal number
        blockValue.put("Simbolis",Block.DEFBLOCK);         // symbol

        blockValue.put("Tarimas",Block.PRONBLOCK);

        blockValue.put("Sinonimai",Block.NYMBLOCK);     // syn
        blockValue.put("Hipersąvokos",Block.NYMBLOCK);  // syn
        blockValue.put("Subsąvokos",Block.NYMBLOCK);    // syn
        blockValue.put("Supersąvokos",Block.NYMBLOCK);  // syn
        blockValue.put("Antonimai",Block.NYMBLOCK);     // ant

        blockValue.put("Susiję terminai",Block.RELATEDTERMBLOCK);

        blockValue.put("Pavyzdžiai",Block.EXAMPLEBLOCK);

        blockValue.put("Vertimai",Block.TRADBLOCK);

        blockValue.put("Etimologija",Block.IGNOREPOS); // etimology
        blockValue.put("Dar žiūrėk",Block.IGNOREPOS); // = see also
        blockValue.put("Taip pat žiūrėkite",Block.IGNOREPOS); // = see also
        blockValue.put("Dar žiūrėti",Block.IGNOREPOS); // = see also
        blockValue.put("Dar žiūrekite",Block.IGNOREPOS); // = see also
        blockValue.put("Dar žiūrėkite",Block.IGNOREPOS); // = see also
        blockValue.put("!Taip pat žiūrėkite!",Block.IGNOREPOS); // = see also
        blockValue.put("Taip pat žiūrėk",Block.IGNOREPOS); // = see also
        blockValue.put("Žiūrėkite taip pat",Block.IGNOREPOS); // = see also
        blockValue.put("Susiję žodžiai",Block.IGNOREPOS); // = related word
        blockValue.put("Šaltiniai",Block.IGNOREPOS); // = references
        blockValue.put("Nuorodos",Block.IGNOREPOS); // = references
        blockValue.put("Išvestiniai žodžiai",Block.IGNOREPOS); // derivative
        blockValue.put("Išraiškos ir posakiai",Block.IGNOREPOS); // Expressions et énonciations
        blockValue.put("Išraiškos arba posakiai",Block.IGNOREPOS); // Expressions or sayings
        blockValue.put("Lietuvių kalbos kiekinių skaitvardžių kaitymas",Block.IGNOREPOS); // inflexion
        blockValue.put("Bendra kaitymo lentelė",Block.IGNOREPOS); // inflexion
        blockValue.put("Išvestiniai  žodžiai",Block.IGNOREPOS); // inflexion
        blockValue.put("Kiti rašymo būdai",Block.IGNOREPOS); // Other ways of writing
        blockValue.put("Išnašos",Block.IGNOREPOS); // notes
        blockValue.put("Frazeologizmai",Block.IGNOREPOS); // idiom ?
        blockValue.put("Vartosena",Block.IGNOREPOS); // usage
        blockValue.put("Būdinys",Block.IGNOREPOS); // describe and syllable
        blockValue.put("Pastabos",Block.IGNOREPOS); // comments
        blockValue.put("Siekinys",Block.IGNOREPOS);
    }

    private Block getBlock(String blockString){
        Block res = Block.IGNOREPOS;
        if(blockString.contains("Skaitvardžio")){
            return res;
        }
        if(blockString.contains("[[")){
            int index = blockString.indexOf("]]")+2;
            blockString  = blockString.substring(index,blockString.length()).trim();
        }
        if((res = blockValue.get(blockString)) == null) {
            log.debug("Unknown block {} --in-- {}", blockString, this.wiktionaryPageName);
            res = Block.NOBLOCK;
        }
        return res;
    }

    protected void extractDataLang(int startOffset, int endOffset, String lang){
        if (lang == null) {
            return;
        }
        if (lang.equals("ltv")){
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
            if (blockString != null) {
                blockString = blockString.replaceAll("\'", "").replaceAll(":","");
            }
            block = getBlock(blockString);
        }
        while (m.find()) {
            extractDataBlock(start, m.start(), block, blockString);
            start = m.end();
            if(m.group(1) != null)
                blockString = m.group(1).trim();
            if (blockString != null) {
                blockString = blockString.replaceAll("\'", "").replaceAll(":", "");
            }
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
                break;
            case DEFBLOCK:
                extractDefinitions(startOffset, endOffset, blockString);
                break;
            case NYMBLOCK:
                extractNyms(startOffset, endOffset, blockString);
                break;
            case RELATEDTERMBLOCK:
                extractRelatedTerm(startOffset,endOffset);
                break;
            case EXAMPLEBLOCK:
                extractExample(startOffset,endOffset);
                break;
            case TRADBLOCK:
                extractTranslations(startOffset, endOffset);
                break;
            default:
                assert false : "Unexpected block while ending extraction of entry: " + wiktionaryPageName;
        }
    }

    protected void extractDefinitions(int start, int end, String blockString) {
        Matcher m = posPattern.matcher(pageContent);
        m.region(start, end);

        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;


        dwdh.addPartOfSpeech(blockString);
        if(m.find()){
            if(m.group(1).contains("vikipedija")){
                if(m.find()){
                    if (m.group(1).length() > 1 && m.group(1).contains("Kategorija")) {
                        dwdh.addPOSinfo(m.group(1));
                    }
                }
            }
            else {
                if (m.group(1).length() > 1 && !m.group(1).contains("Kategorija")) {
                    dwdh.addPOSinfo(m.group(1));
                }
            }
        }

        m = defPattern.matcher(pageContent);
        m.region(start, end);
        int senseNum = 1;
        while(m.find()) {
            String tmp;
            if(m.group(1) != null) {
                tmp = m.group(1).trim();
            }else if(m.group(2) != null){
                tmp = m.group(2).trim();
            }
            else{
                return;
            }
            if(!tmp.equals("")) {
                if(tmp.contains("Pvz.")){
                    String def = tmp.substring(0,tmp.indexOf("Pvz.")).replaceAll("\'", "").trim();
                    String ex = tmp.substring(tmp.indexOf("Pvz.") + 4, tmp.length() - 1).replaceAll("\'", "").trim();
                    wdh.registerNewDefinition(cleanUpMarkup(def), ""+senseNum);
                    wdh.registerExample(ex, new HashMap<Property, String>());
                    senseNum++;
                }
                else{
                    wdh.registerNewDefinition(cleanUpMarkup(tmp), ""+senseNum);
                    senseNum++;
                }

            }
        }
    }

    protected void extractExample(int start, int end) {
        Matcher ex = examplePattern.matcher(pageContent);
        ex.region(start, end);
        while (ex.find()) {
            wdh.registerExample(ex.group(1), new HashMap<Property, String>());
        }
    }

    protected void extractTranslations(int start, int end) {
        parseTrans(pageContent.substring(start, end));
    }

    protected void parseTrans(String content){
        Matcher trad = tradPattern.matcher(content);
        String usage = null;
        String currentGloss = wiktionaryPageName;
        while(trad.find()){
            String[] tTrad = trad.group(1).split("\\|");
            switch(tTrad[0]){
                case "t+":
                    if(tTrad.length > 2)
                        wdh.registerTranslation(tTrad[1], currentGloss, usage, tTrad[2]);
                    break;
                case "trans-top":
                case "trans-mid":
                case "trans-bottom":
                case "ltrans-top":
                case "ltrans-mid":
                case "ltrans-bottom":
                    if(tTrad.length > 1){
                        String[] tmp = tTrad[1].split("=");
                        if(tmp.length == 2) {
                            currentGloss = tmp[1];
                        }
                        else{
                            currentGloss = tmp[0];
                        }
                    }
                    break;
                case "f":
                case "versk":
                    break;
                default:
                    if(tTrad[0].startsWith("Sąrašas:Vertimai/lt/")){
                        // search and extract Vertimai block in url tTrad[0]
                        String c = getTradContent(tTrad[0]);
                        if( c!= null)
                            parseTrans(c);
                    }
                    else if(!tTrad[0].contains("1") &&
                            !tTrad[0].contains("2") &&
                            !tTrad[0].contains("3")){
                        log.debug("Unknown Trad value {} --in-- {}", tTrad[0], wdh.currentLexEntry());
                    }
            }
        }
    }

    protected String getTradContent(String template){
        String res = null;
        String c = wi.getTextOfPage(template);
        if(c != null && c.startsWith("#PERADRESAVIMAS")) {
            Matcher content = tradContentPattern.matcher(c);
            if (content.find()) {
                if (content.group(1) != null) {
                    res = getTradContent(content.group(1));
                } else
                    res = c;
            }
        }
        else{
            if(c == null){
                log.debug("Content null : {} --in-- {}", template, wdh.currentLexEntry());
            }
            res = c;
        }
        return res;
    }

    protected void extractNyms(int start, int end, String blockString) {
        parseNyms(pageContent.substring(start, end), blockString);
    }

    protected void parseNyms(String content, String blockString){
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;
        String nymRel = "";
        String gloss = wdh.currentLexEntry();
        Matcher m = nymPattern.matcher(content);
        while(m.find()){
            String[] tNyms = m.group(1).split("\\|");
            switch(tNyms[0]){
                case "t+":
                case "versk":
                    dwdh.addNymInfo(nymRel, tNyms, gloss);
                    break;
                case "sin-top":
                case "sin-mid":
                case "sin-bottom":
                case "lsin-top":
                case "lsin-mid":
                case "lsin-bottom":
                    nymRel = "syn";
                    if(tNyms.length > 1){
                        String[] tmp = tNyms[1].split("=");
                        if(tmp.length == 2) {
                            gloss = tmp[1];
                        }
                        else{
                            gloss = tmp[0];
                        }
                    }
                    break;
                case "ant-top":
                case "ant-mid":
                case "ant-bottom":
                    nymRel = "ant";
                    if(tNyms.length > 1){
                        String[] tmp = tNyms[1].split("=");
                        if(tmp.length == 2) {
                            gloss = tmp[1];
                        }
                        else{
                            gloss = tmp[0];
                        }
                    }
                    break;
                case "rel-top":
                case "rel-mid":
                case "rel-bottom":
                case "trans-top":
                case "trans-mid":
                case "trans-bottom":
                    switch(blockString){
                        case "Sinonimai":
                        case "Hipersąvokos":
                        case "Subsąvokos":
                        case "Supersąvokos":
                            nymRel = "syn";
                            break;
                        case "Antonimai":
                            nymRel = "ant";
                            break;
                        default:
                            log.debug("Unknown Nyms level {} --in-- {}", tNyms[0], wdh.currentLexEntry());
                            return;
                    }
                    if(tNyms.length > 1){
                        String[] tmp = tNyms[1].split("=");
                        if(tmp.length == 2) {
                            gloss = tmp[1];
                        }
                        else{
                            gloss = tmp[0];
                        }
                    }
                    break;
                case "p":
                case "š":
                case "t":
                case "ž":
                case "l":
                case "x":
                case "xx":
                case "la":
                case "ltv1":
                case "žarg.":
                case "Kategorija":
                case "Commons":
                case "cleanup":
                    break;
                default:
                    if(tNyms[0].startsWith("Sąrašas:Sinonimai/lt/")){
                        // search and extract sinoniai block in url tNyms[0]
                        parseNyms(wi.getTextOfPage(tNyms[0]), blockString);
                    }
                    else {
                        log.debug("Unknown Nyms level {} --in-- {}", tNyms[0], wdh.currentLexEntry());
                    }
            }
        }
    }

    protected void extractRelatedTerm(int start, int end) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        Matcher m = relPattern.matcher(pageContent);
        m.region(start, end);
        String nymRel = "";
        while(m.find()){
            String[] tRel = m.group(1).split("\\|");
            switch(tRel[0]){
                case "t+":
                    dwdh.addRelatedTermInfo(tRel);
                    break;
                case "rel-top":
                case "rel-mid":
                case "rel-bottom":
                    break;
                default:
                    log.debug("Unknown Related Term info {} --in-- {}", tRel[0], wdh.currentLexEntry());
            }
        }
    }

    protected void extractPron(int start, int end){
        Matcher pron = pronPattern.matcher(this.pageContent);
        pron.region(start, end);

        while(pron.find()){
            if(!pron.group(2).equals(""))
                switch(pron.group(1)) {
                    case "rtarimas":
                        wdh.registerPronunciation(pron.group(2), "lt-fonipa");
                        break;
                    default:
                        log.debug("Unknown Pron {} --in-- {}", pron.group(1), wdh.currentLexEntry());
                }
        }
    }
}
