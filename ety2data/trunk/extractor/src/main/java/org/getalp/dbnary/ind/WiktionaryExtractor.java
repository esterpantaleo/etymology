package org.getalp.dbnary.ind;

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

    protected final static String languageSectionPatternString = "={2}\\s*\\{{2}=*([^\\}=]+)=*\\}{2}\\s*={2}|\\{{2}\\s*-([^-]+)-\\s*\\}{2}|={2}\\s*([^=]+)={2}\n";
    protected final static String blockPatternString = "\n\\{{2}([^\\}]+)\\}{2}|={3}\\s*([^=]+)={3}\n";
    protected final static String tradPatternString = "\\{{2}([^\\\\}]+)\\}{2}\\s*:\\s*\\[{2}([^\\]]+)\\]{2}|\\{{2}(t[^\\|][^\\}]+)\\}{2}";
    protected final static String nymsPatternString = "\\{{2}([^\\}]+)\\}{2}";
    protected final static String defPatternString = "#\\s*([^\\n]+)|\\'{5}Definisi\\'{5}\\s*:\\s*([^\\n]+)";
    protected final static String examplePatternString = "\\*\\s*([^\n]+)\n";

    protected final static Pattern languageSectionPattern;
    protected final static Pattern blockPattern;
    protected final static Pattern tradPattern;
    protected final static Pattern nymsPattern;
    protected final static Pattern defPattern;
    protected final static Pattern examplePattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);
        blockPattern = Pattern.compile(blockPatternString);
        tradPattern = Pattern.compile(tradPatternString);
        nymsPattern = Pattern.compile(nymsPatternString);
        defPattern = Pattern.compile(defPatternString);
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

        String nextLang = null, lang = null;

        while (languageFilter.find()) {
            if(languageFilter.group(1) != null) {
                nextLang = languageFilter.group(1);
            }
            else if(languageFilter.group(2) != null){
                nextLang = languageFilter.group(2);
            }
            else if(languageFilter.group(3) != null){
                nextLang = languageFilter.group(3);
            }
            if(nextLang != null) {
                nextLang = nextLang.replaceAll("[=-]", "").trim();
                nextLang = nextLang.replaceAll("[Bb]ahasa", "");
            }
            extractDataLang(startSection, languageFilter.start(), lang);
            lang = nextLang;
            startSection = languageFilter.end();
        }

        if (languageFilter.hitEnd()) {
           extractDataLang(startSection, pageContent.length(), lang);
        }
        wdh.finalizePageExtraction();
    }

    private enum Block {NOBLOCK, IGNOREPOS, POSBLOCK, EXAMPLEBLOCK, NYMBLOCK, TRADBLOCK, PRONBLOCK}

    protected static HashMap<String,Block> blockValue = new HashMap<>();

    static{
        blockValue.put("",Block.NOBLOCK); // noblock

        blockValue.put("a",Block.POSBLOCK); // adj
        blockValue.put("adj",Block.POSBLOCK);
        blockValue.put("k_s",Block.POSBLOCK);
        blockValue.put("k s",Block.POSBLOCK);

        blockValue.put("nom",Block.POSBLOCK); // noun
        blockValue.put("Nomina",Block.POSBLOCK);
        blockValue.put("k_b",Block.POSBLOCK);
        blockValue.put("n",Block.POSBLOCK);
        blockValue.put("k b",Block.POSBLOCK);

        blockValue.put("idproper noun",Block.POSBLOCK); // proper Noun

        blockValue.put("kan",Block.POSBLOCK); // Verb
        blockValue.put("verb",Block.POSBLOCK);
        blockValue.put("v",Block.POSBLOCK);
        blockValue.put("lihat v",Block.POSBLOCK);
        blockValue.put("k_k",Block.POSBLOCK);
        blockValue.put("k k",Block.POSBLOCK);

        blockValue.put("adv",Block.POSBLOCK);
        blockValue.put("k_kt",Block.POSBLOCK);
        blockValue.put("k kt",Block.POSBLOCK);

        blockValue.put("kontraksi",Block.POSBLOCK); // contraction
        blockValue.put("akronim",Block.POSBLOCK);

        blockValue.put("Singkatan",Block.POSBLOCK); //abbrev
        blockValue.put("singkatan",Block.POSBLOCK); //abbrev
        blockValue.put("huruf",Block.POSBLOCK); // letter
        blockValue.put("prep",Block.POSBLOCK); // preposition
        blockValue.put("k_d",Block.POSBLOCK);
        blockValue.put("k d",Block.POSBLOCK);
        blockValue.put("inter",Block.POSBLOCK); // interjection

        blockValue.put("pron",Block.POSBLOCK); // pronoun
        blockValue.put("k_g",Block.POSBLOCK); // pronoun
        blockValue.put("k g",Block.POSBLOCK); // pronoun

        blockValue.put("kon",Block.POSBLOCK); // conj
        blockValue.put("k_sb",Block.POSBLOCK);
        blockValue.put("k sb",Block.POSBLOCK);

        blockValue.put("num",Block.POSBLOCK); // number
        blockValue.put("k_bl",Block.POSBLOCK);
        blockValue.put("k bl",Block.POSBLOCK);

        blockValue.put("art",Block.POSBLOCK); // article
        blockValue.put("p",Block.POSBLOCK);   // particle
        blockValue.put("part",Block.POSBLOCK);   // particle

        blockValue.put("syn",Block.NYMBLOCK);
        blockValue.put("ant",Block.NYMBLOCK);

        blockValue.put("terjemahan",Block.TRADBLOCK);
        blockValue.put("kotak mulai",Block.TRADBLOCK);    // top
        blockValue.put("transtop",Block.TRADBLOCK);    // top
        blockValue.put("kotak tengah",Block.TRADBLOCK);   // mid
        blockValue.put("terjemah",Block.TRADBLOCK);       // mid
        blockValue.put("transmid",Block.TRADBLOCK);       // mid
        blockValue.put("kotak akhir",Block.IGNOREPOS);    // bottom
        blockValue.put("kotak selesai",Block.IGNOREPOS);  // bottom
        blockValue.put("transbottom",Block.IGNOREPOS);  // bottom

        blockValue.put("pengejaan",Block.PRONBLOCK);
        blockValue.put("ejaan",Block.PRONBLOCK);
        blockValue.put("ejaan:id",Block.PRONBLOCK);

        blockValue.put("Contoh",Block.EXAMPLEBLOCK);

        blockValue.put("drv",Block.IGNOREPOS); // derivative
        blockValue.put("drv2",Block.IGNOREPOS);
        blockValue.put("drv3",Block.IGNOREPOS);
        blockValue.put("drv-multi",Block.IGNOREPOS);
        blockValue.put("turunan",Block.IGNOREPOS);
        blockValue.put("varian",Block.IGNOREPOS);
        blockValue.put("footer",Block.IGNOREPOS);
        blockValue.put("Gambar",Block.IGNOREPOS); // image
        blockValue.put("KBBI",Block.IGNOREPOS);
        blockValue.put("var",Block.IGNOREPOS);
        blockValue.put("Ejaan",Block.IGNOREPOS); // ortho
        blockValue.put("Peribahasa",Block.IGNOREPOS); // proverb

        blockValue.put("etym",Block.IGNOREPOS);
        blockValue.put("etimologi",Block.IGNOREPOS);
        blockValue.put("e",Block.IGNOREPOS);

        blockValue.put("Galeri",Block.IGNOREPOS);
        blockValue.put("Dok",Block.IGNOREPOS);
        blockValue.put("Frasa",Block.IGNOREPOS);
        blockValue.put("frasa",Block.IGNOREPOS);
        blockValue.put("frasa2",Block.IGNOREPOS);
        blockValue.put("keluarga kata",Block.IGNOREPOS);
        blockValue.put("Abjad Latin",Block.IGNOREPOS);
        blockValue.put("imbuhan -kah",Block.IGNOREPOS);

        blockValue.put("indonesiamajemuk",Block.IGNOREPOS);
        blockValue.put("indonesia majemuk",Block.IGNOREPOS);
        blockValue.put("majemukindonesia",Block.IGNOREPOS);

        blockValue.put("Sulawesi Selatan",Block.IGNOREPOS);
        blockValue.put("lihat 2 an",Block.IGNOREPOS);
        blockValue.put("Indonesia",Block.IGNOREPOS);
        blockValue.put("majemuk indonesia",Block.IGNOREPOS);
        blockValue.put("variasi",Block.IGNOREPOS);
        blockValue.put("su",Block.IGNOREPOS);
        blockValue.put("en",Block.IGNOREPOS);
        blockValue.put("keluarga kata dasar",Block.IGNOREPOS);

        blockValue.put("Akar kata",Block.IGNOREPOS); // stem
        blockValue.put("akar",Block.IGNOREPOS); // stem

        blockValue.put("lihat",Block.IGNOREPOS); // see also
        blockValue.put("Lihat",Block.IGNOREPOS); // see also
        blockValue.put("Lihat pula",Block.IGNOREPOS); // see also
        blockValue.put("Kata terkait",Block.IGNOREPOS); // see also
        blockValue.put("terkait",Block.IGNOREPOS); // see also

        blockValue.put("Negara",Block.IGNOREPOS);
        blockValue.put("alt",Block.IGNOREPOS);
        blockValue.put("Unsur kimia",Block.IGNOREPOS); // chemical elements
        blockValue.put("Angka",Block.IGNOREPOS);
    }

    private Block getBlock(String blockString){
        Block res;
        String[] tmp = blockString.split("\\|");
        String blockS;
        if(tmp.length > 0){
            blockS = tmp[0];
        }
        else{
            blockS = blockString;
        }
        blockS = cleanBlockName(blockS, blockString);
        if((res = blockValue.get(blockS)) == null) {
            log.debug("Unknown block {} --in-- {}", blockString, this.wiktionaryPageName);
            res = Block.NOBLOCK;
        }
        return res;
    }

    protected String cleanBlockName(String blockS, String blockString){
        blockS = blockS.replaceAll("[-=]","").trim();
        if(blockS.contains("kan") ||
                blockS.contains("ber") ||
                blockS.contains("di")||
                blockS.endsWith("mei") ||
                blockS.endsWith("me") ||
                blockS.startsWith("lihat v")||
                blockS.startsWith("lihat2 v")) {
            blockS = "verb";
        }
        else if(blockS.equals("lihat 2")){
            blockS = "";
            String[] tab = blockString.split("\\|");
            if (tab.length >= 2) {
                String[] tmp = tab[1].split("=");
                if (tmp.length == 2) {
                    switch (tmp[0]) {
                        case "n":
                            blockS = "nom";
                            break;
                        case "v":
                            blockS = "verb";
                            break;
                        case "a":
                            blockS = "adj";
                            break;
                    }
                }
            }
        }
        else if(blockS.contains("imbuhan") || blockS.contains("sisipan")) {
            blockS = "nom";
            String[] tab = blockString.split("\\|");
            for (String t : tab) {
                if (t.startsWith("kelas")) {
                    String[] tmp = t.split("=");
                    if (tmp.length == 2) {
                        switch (tmp[1]) {
                            case "n":
                            case "nom":
                            case "noun":
                            case "nomina":
                            case "benda":
                            case "kata benda":
                                blockS = "nom";
                                break;
                            case "v":
                            case "verb":
                            case "verba":
                            case "kerja":
                            case "kata kerja":
                                blockS = "verb";
                                break;
                            case "a":
                            case "adj":
                            case "adjective":
                            case "adjektiva":
                            case "sifat":
                            case "kata sifat":
                                blockS = "adj";
                                break;
                            case "adv":
                                blockS = "adv";
                                break;
                            case "p":
                                blockS = "p";
                                break;
                        }
                    }
                }
            }
        }
        else if(blockS.contains("KBBI")){
            blockS = "KBBI";
        }
        else if(blockS.endsWith("se")){ // num
            blockS = "num";
        }
        else if(!blockS.equalsIgnoreCase("singkatan") && (blockS.contains("syn") || blockS.contains("sin"))){
            blockS = "syn";
        }
        else if(blockS.endsWith("em") ||
                blockS.equals("Kata sifat") ||
                blockS.startsWith("lihat2 a")){ // adj
            blockS = "adj";
        }
        else if(blockS.startsWith("lihat2 adv") ||
                blockS.startsWith("lihat adv")){
            blockS = "adv";
        }
        else if(blockS.endsWith("ke")){ // particle
            blockS = "p";
        }
        else if(blockS.contains("nya") ||
                blockS.contains("pe") ||
                blockS.contains("nomina") ||
                blockS.startsWith("lihat n")){
            blockS = "Nomina";
        }
        else if(blockS.contains("etym")){
            blockS = "etym";
        } else if(blockS.equals("#if:")){
            blockS = "";
        }
        else if (blockS.contains("ulang")){
            blockS = "nom";
            String[] tmp = blockS.split("\\|");
            if(tmp.length == 3){
                String tab[] = tmp[2].split("=");
                if(tab.length == 2 && tab[0].equalsIgnoreCase("kelas")){
                    blockS = tab[1];
                }
            }
        }
        return blockS;
    }

    protected void extractDataLang(int startOffset, int endOffset, String lang){
        if (lang == null) {
            return;
        }
        if (lang.contains("indonesia") || lang.equalsIgnoreCase("id")){
            wdh.initializeEntryExtraction(wiktionaryPageName);
        }
        else {
            // log.debug("Unused lang {}", lang);
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
            block = getBlock(blockString);
            extractDefinitions(startOffset,start);
        }
        while (m.find()) {
            extractDataBlock(start, m.start(), block, blockString);
            start = m.end();
            if(m.group(1) != null)
                blockString = m.group(1).trim();
            if(m.group(2) != null)
                blockString = m.group(2).trim();
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
            case PRONBLOCK: // not found ipa
                break;
            case POSBLOCK:
                extractPOS(blockString);
                extractDefinitions(startOffset,endOffset);
                break;
            case NYMBLOCK:
                extractNyms(startOffset, endOffset, blockString);
                break;
            case EXAMPLEBLOCK:
                extractExample(startOffset, endOffset);
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
        String tmp[] = blockString.split("\\|");
        String blockS;
        if(tmp.length > 1){
            blockS = tmp[0];
        }
        else{
            blockS = blockString;
        }
        blockS = cleanBlockName(blockS, blockString);
        dwdh.addPartOfSpeech(blockS);
        dwdh.addExtraPOSInfo(blockS);
    }

    protected void extractTranslations(int start, int end) {
        Matcher trad = tradPattern.matcher(pageContent);
        trad.region(start, end);
        String currentGloss = wiktionaryPageName;

        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        while (trad.find()) {
            if (trad.group(1) != null) {
                dwdh.addTranslation(trad.group(1), currentGloss, null, trad.group(2));
            }
            if (trad.group(3) != null) {
                String[] tmp = trad.group(3).split("\\|");
                if (tmp.length == 3)
                    dwdh.addTranslation(tmp[1], currentGloss, null, tmp[2]);
            }
        }
    }

    protected void extractNyms(int start, int end, String blockString) {
        Matcher m = nymsPattern.matcher(pageContent);
        m.region(start,end);
        String[] tab = blockString.split("\\|");
        String blockS = null;
        if(tab.length != 0)
            blockS = cleanBlockName(tab[0], blockString);

        if(tab.length > 1 && blockS != null) {
            for (int i = 1; i < tab.length; i++) {
                if(!tab[i].equals("id")) {
                    wdh.registerNymRelation(tab[i], blockS);
                }
            }
        }

        while (m.find()) {
            String[] tmp = m.group(1).split("\\|");
            if(tmp.length > 1 && blockS != null){
                for(int i = 1; i < tmp.length; i++){
                    if(!tmp[i].equals("id")) {
                        wdh.registerNymRelation(tmp[i], blockS);
                    }
                }
            }
        }
    }

    protected void extractDefinitions(int start, int end) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;
        Matcher m = defPattern.matcher(pageContent);
        m.region(start, end);

        int senseNum = 1;
        while (m.find()) {
            String tmp;
            if (m.group(1) != null) {
                tmp = m.group(1).trim();
            }
            else if (m.group(2) != null) {
                tmp = m.group(2).trim();
            }
            else {
                return;
            }
            if (!tmp.equals("")) {
                dwdh.addNewDefinition(cleanUpMarkup(tmp), "" + senseNum);
                senseNum++;
            }
        }
    }

    protected void extractExample(int start, int end){
        Matcher m = examplePattern.matcher(pageContent);
        m.region(start, end);

        while (m.find()) {
            wdh.registerExample(m.group(1),new HashMap<Property, String>());
        }
    }
}
