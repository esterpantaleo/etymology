/**
 *
 */
package org.getalp.dbnary.hbs;

import com.hp.hpl.jena.rdf.model.Property;
import org.getalp.dbnary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author roques
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString;

    protected final static String languageSectionPatternString1 = "={2}\\s*([^=]+)\\s*={2}\\n|\\{{2}=([^=]*)=\\}{2}";

    protected final static String blockPatternString;
    protected final static String blockPatternStringLevel = "={3,5}\\s*([^=]+)\\s*={3,5}|\\{{2}-([^\\}]*)\\}{2}";

    protected final static String tradPatternString = "\\*\\s*([^:\\{\\}]*):[^\\[,^\\{]*[\\[,\\{]*([^\\],^\\}]*)|\\{{2}pr\\|([^\\|]*)\\|([^\\}]*)";

    protected final static String localdefinitionPatternString = "#\\s*([^:][^#]*)|:\\s*\\([^\\)]*\\)\\s*([^:]*)";
    protected final static String examplePatternString = "#:\\s*(.+)";

    protected final static String posPatternString = "(\\{{2}([^\\{]+)\\}{2})";

    protected final static String pronPatternString = "\\{{2}([^\\{]+)\\}{2}";

    protected final static String nymsPatternString = "#\\s*\\[{2}([^\\]]*)\\]{2}|\\*\\s*\\{{2}([^\\}]*)\\}{2}";

    protected final static String izvedenicePatternString = "\\{{2}l\\|sh\\|([^\\}]*)\\}{2}";
    protected final static String flektiraniPatternString = "#\\s*([^\\[]*)\\[{2}([^#]*)#";

    static {
        languageSectionPatternString = "("
                + languageSectionPatternString1
                + ")";

        blockPatternString = "("
                + blockPatternStringLevel
                + ")";

    }

    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
    }

    protected final static Pattern blockPattern;
    protected final static Pattern tradPattern;
    protected final static Pattern localdefinitionPattern;
    protected final static Pattern examplePattern;
    protected final static Pattern posPattern;
    protected final static Pattern pronPattern;
    protected final static Pattern nymsPattern;
    protected final static Pattern izvedenicePattern;
    protected final static Pattern flektiraniPattern;

    static {
        blockPattern = Pattern.compile(blockPatternString);
        tradPattern = Pattern.compile(tradPatternString);
        localdefinitionPattern = Pattern.compile(localdefinitionPatternString);
        examplePattern = Pattern.compile(examplePatternString);
        posPattern = Pattern.compile(posPatternString);
        pronPattern = Pattern.compile(pronPatternString);
        nymsPattern = Pattern.compile(nymsPatternString);
        izvedenicePattern = Pattern.compile(izvedenicePatternString);
        flektiraniPattern = Pattern.compile(flektiraniPatternString);
    }

    protected final static Pattern languageSectionPattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
    }

    @Override
    public void extractData() {
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = languageSectionPattern.matcher(pageContent);
        int startSection = -1;

        String nextLang = null, lang = null;

        while (languageFilter.find()) {
            if(languageFilter.group(2) != null) {
                nextLang = languageFilter.group(2);
            }
            else if(languageFilter.group(3) != null){
                nextLang = languageFilter.group(3);
            }
            extractDataLang(startSection, languageFilter.start(), lang);
            if(nextLang != null)
                lang = nextLang;
            startSection = languageFilter.end();
        }

        // Either the filter is at end of sequence or on hbs language header.
        if (languageFilter.hitEnd()) {
            extractDataLang(startSection, pageContent.length(), lang);
        }
        wdh.finalizePageExtraction();
    }

    private enum Block {NOBLOCK, IGNOREPOS, TRADBLOCK, DEFBLOCK, PRONBLOCK,
        DEKLINBLOCK, NYMSBLOCK, IZVEDENICE, FLEKTIRANI}

    private Block getBlock(String blockString){
        Block res = Block.IGNOREPOS;
        switch(blockString){
            case "Izgovor": // prononciation
                res = Block.PRONBLOCK;
                break;
            case "Imenica": // noun
            case "Именица": // noun
            case "srpskohrvatska imenica": // noun
            case "sh-imenica": // noun
            case "sh-imenica2": // noun
            case "sh-noun": // noun
            case "Vlastito ime": // proper noun
            case "sh-vlastito ime": // proper noun
            case "znači-imenica" : // means-noun ?
            case "Glagol" : // verb
            case "sh-glagol" : // verb
            case "Pridjev" : // adj
            case "sh-pridjev" : // adj
            case "Prilog" : // adv
            case "sh-prilog" : // adv
            case "znači-država" :
            case "znači-grad" :
                res = Block.DEFBLOCK;
                break;
            case "Prevod":
            case "Prevodi":
            case "prevod":
            case "Prijevod":
                res = Block.TRADBLOCK;
                break;
            case "Deklinacija":
            case "Sklonidba":
            case "Konjugacija" :
                res = Block.DEKLINBLOCK;
                break;
            case "Izvedenice": // other form
            case "Izvedeni oblici":
                res = Block.IZVEDENICE;
                break;
            case "Flektirani oblici" :
                res = Block.FLEKTIRANI;
                break;
            case "Sinonimi":
            case "sinonimi":
            case "Antonimi":
            case "antonimi":
                res = Block.NYMSBLOCK;
                break;
            case "etim":
            case "Etimologija":
            case "Etimologija 1":
            case "Etimologia 1":
            case "Etimologija 2":
            case "Etimilogija 2":
            case "Etimologija 3":
            case "Reference":
            case "Vidi": // see
            case "Vidi još": // see also
            case "Također pogledajte": // see also
            case "Srodne riječi": // see also
            case "tez":
            case "Vanjske veze": // external link
            case "Vanjske poveznice": // external link
                res = Block.IGNOREPOS;
                break;
            default:
                log.debug("Unknown block {} --in-- {}", blockString, this.wiktionaryPageName);
        }
        return res;
    }

    protected void extractDataLang(int startOffset, int endOffset, String lang){
        if (lang == null) {
            return;
        }
        lang = lang.trim();

        if (lang.toLowerCase().equals("srpskohrvatski") || lang.equals("sh")){
            wdh.initializeEntryExtraction(wiktionaryPageName);
        }
        else {
             return;
//            wdh.initializeEntryExtraction(wiktionaryPageName, lang);
        }

        Matcher m = blockPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        String blockString = null;
        Block block = Block.IGNOREPOS;
        int start = startOffset;

        if(m.find()){
            start = m.start();
            if(m.group(2) != null)
                blockString = m.group(2).trim();
            else if(m.group(3) != null)
                blockString = m.group(3).substring(0,m.group(3).length()-1); // rm last -
            block = getBlock(blockString);
            extractDefinitions(startOffset,start);
        }
        while (m.find()) {
            extractDataBlock(start, m.start(), block, blockString);
            start = m.end();
            if(m.group(2) != null)
                blockString = m.group(2).trim();
            else if(m.group(3) != null)
                blockString = m.group(3).substring(0,m.group(3).length()-1); // rm last -
            block = getBlock(blockString);
        }

        extractDataBlock(start, endOffset, block, blockString);
        wdh.finalizeEntryExtraction();
    }

    protected void extractPron(int start, int end){
        Matcher pron = pronPattern.matcher(this.pageContent);
        pron.region(start, end);

        while(pron.find()){
            String tab[] = pron.group(1).split("\\|");
            if(tab.length == 3 && tab[0].equals("IPA")){
                String t2[] = tab[2].split("=");
                if(t2.length == 2) {
                    wdh.registerPronunciation(tab[1], t2[1] + "-fonipa");
                }
                else{
                    wdh.registerPronunciation(tab[1], "sh-fonipa");
                }
            }
        }
    }

    protected void extractExample(int start, int end) {
        Matcher exampleMatcher = examplePattern.matcher(this.pageContent);
        exampleMatcher.region(start, end);

        if(exampleMatcher.find()){
            String example[] = exampleMatcher.group().substring(2).split("\\|");
            if(example.length > 1) {
                String ex = example[1];
                if (ex != null && !ex.equals("")) {
                    wdh.registerExample(ex, new HashMap<Property, String>());
                }
            }
        }
    }

    protected void extractDefinitions(int start, int end) {
        Matcher m = posPattern.matcher(this.pageContent);
        m.region(start, end);

        String blockString = null;
        if(m.find()){
            String[] tmps = m.group(1).split("\\|");
            String tmp = tmps[0].replaceAll("\\{","").trim();
            switch(tmp){
                case "sh-imenica":
                case "sh-imenica2": // noun
                case "-znači-imenica-": // noun
                case "sh-noun": // noun
                    blockString = "Imenica";
                    break;
                case "sh-vlastito ime": // proper noun
                    blockString = "Vlastito Imenica";
                    break;
                case "sh-pridjev": // adj
                    blockString = "Pridjev";
                    break;
                case "sh-prilog": // adv
                    blockString = "Prilog";
                    break;
                case "sh-glagol": // verb
                    blockString = "Glagol";
                    break;
                case "sh-imenica-deklinacija":
                    wdh.addPartOfSpeech("Imenica");
                    return;
                default:
                    log.debug("Unknown blockPos {} --in-- {}", tmp, this.wiktionaryPageName);
            }
        }
        if(blockString != null){
            extractDefinitions(start, end, blockString);
        }
    }

    protected void extractDefinitions(int start, int end, String blockString) {

        // Found and extract definition
        Matcher definitionMatcher = localdefinitionPattern.matcher(this.pageContent);
        definitionMatcher.region(start, end);
        int startSample = -1;
        int senseNum = 1;
        wdh.addPartOfSpeech(blockString);

        while (definitionMatcher.find()) {
            if(startSample == -1){
                extractPosInfo(start, definitionMatcher.start());
            }
            else{
                extractExample(startSample, definitionMatcher.start());
            }
            String def = "";
            if(definitionMatcher.group(1) != null) {
                def = cleanUpMarkup(definitionMatcher.group(1)).trim();
            }
            else if(definitionMatcher.group(2) !=null){
                def = cleanUpMarkup(definitionMatcher.group(2)).trim();
            }
            if(!def.equals("")) {
                wdh.registerNewDefinition(def, ""+senseNum);
                senseNum++;
            }
            startSample = definitionMatcher.end();
        }

        if(definitionMatcher.hitEnd()){
            if(startSample == -1){
                startSample = start;
            }
            if(startSample < end) {
                extractExample(startSample, end);
            }
        }
    }

    private void extractPosInfo(int start, int endPos){

        // add allInformation in currentLexicalEntry
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        // found extraInformation
        Matcher pos = posPattern.matcher(this.pageContent);
        if(endPos != -1) {
            pos.region(start, endPos);
        }

        while(pos.find()){
            dwdh.extractPOSandExtraInfos(pos.group(2));
        }
    }

    private void extractTranslations(int startOffset, int endOffset) {
        Matcher trad = tradPattern.matcher(pageContent);
        trad.region(startOffset, endOffset);

        String lang = null;
        String currentGloss = wdh.currentLexEntry();
        String word = "";

        while (trad.find()) {
            if(trad.group(1) != null) {
                lang = SerboCroatianLangToCode.threeLettersCode(trad.group(1));
                String[] t = trad.group(2).split("\\|");
                if(t.length == 2){
                    word = t[1].replaceAll("\\[","");
                }
                else {
                    word = trad.group(2);
                }
            }
            else if(trad.group(3) != null && trad.group(4)!= null){
                lang = trad.group(3);
                word = trad.group(4);
            }

            if(lang!=null){
                wdh.registerTranslation(lang, currentGloss, null, word);
            }
            else log.debug("Unknown lang {} --in-- {}", trad.group(1), this.wiktionaryPageName);

        }

    }

    protected void extractDeklinacija(int start, int end){
        if(wdh.currentWiktionaryPos() != null) {
            SerboCroatianMorphoExtractorWikiModel morpho = new SerboCroatianMorphoExtractorWikiModel(wdh, wi, new Locale("sh"), "/${Bild}", "/${Titel}");
            morpho.setPageName(wiktionaryPageName);
            morpho.extractOtherForm(pageContent.substring(start, end));
        }
        else{
            log.debug("currentWiktionaryPos is null --in-- {}", this.wiktionaryPageName);
        }
    }

    protected void extractIzvedenice(int start, int end) {
        if(wdh.currentWiktionaryPos() != null) {
            Matcher m = izvedenicePattern.matcher(pageContent);
            m.region(start, end);
            SerboCroatianInflectionData inf = new SerboCroatianInflectionData();
            while (m.find()) {
                wdh.registerInflection("hbs", wdh.currentWiktionaryPos(), m.group(1),
                        wdh.currentLexEntry(), 1, inf.toPropertyObjectMap());
            }
        }
        else{
            log.debug("currentWiktionaryPos is null --in-- {}", this.wiktionaryPageName);
        }
    }

    protected void extractFlektirani(int start, int end) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        Matcher m = flektiraniPattern.matcher(pageContent);
        m.region(start, end);

        while (m.find()) {
            if(dwdh.alreadyRegisteredFlexion.containsKey(m.group(2))){
                ArrayList<String> alreadyList = dwdh.alreadyRegisteredFlexion.get(m.group(2));
                if(!alreadyList.contains(wiktionaryPageName)){
                    SerboCroatianMorphoExtractorWikiModel morpho =
                            new SerboCroatianMorphoExtractorWikiModel(wdh, wi, new Locale("sh"), "/${Bild}", "/${Titel}");
                    morpho.add(m.group(2), wiktionaryPageName, m.group(1));
                }
            }
            else{
                HashMap<String, String> toberegister = new HashMap<>();
                if(dwdh.toBeRegisterFlexion.containsKey(m.group(2))){
                    toberegister = dwdh.toBeRegisterFlexion.get(m.group(2));
                }
                toberegister.put(wiktionaryPageName, m.group(1));
                dwdh.toBeRegisterFlexion.put(m.group(2), toberegister);
            }
        }
    }

    protected void extractNyms(int start, int end, String blockString) {
        Matcher m = nymsPattern.matcher(pageContent);
        m.region(start, end);
        String nymRel = "";
        String nym = "";
        while(m.find()){
            if(blockString.equals("Sinonimi")){
                nymRel = "syn";
            }
            else if(blockString.equals("Antonimi")){
                nymRel = "ant";
            }
            if(m.group(1) != null){
                nym = m.group(1);
            }
            else if(m.group(2) != null){
                nym = m.group(2).split("\\|")[2];
            }
            wdh.registerNymRelation(nym, nymRel);
        }
    }

    protected void extractDataBlock(int startOffset, int endOffset, Block currentBlock, String blockString){
        switch (currentBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case DEFBLOCK:
                extractDefinitions(startOffset, endOffset, blockString);
                break;
            case PRONBLOCK:
                extractPron(startOffset,endOffset);
                break;
            case TRADBLOCK:
                extractTranslations(startOffset, endOffset);
                break;
            case NYMSBLOCK:
                extractNyms(startOffset,endOffset,blockString);
                break;
            case DEKLINBLOCK:
                extractDeklinacija(startOffset, endOffset);
                break;
            case IZVEDENICE:
                extractIzvedenice(startOffset,endOffset);
                break;
            case FLEKTIRANI:
                extractFlektirani(startOffset,endOffset);
                break;
            default:
                assert false : "Unexpected block while ending extraction of entry: " + wiktionaryPageName;
        }
    }

}
