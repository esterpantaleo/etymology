package org.getalp.dbnary.nor;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.OliaOnt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    public WiktionaryDataHandler(String lang) {
        super(lang);
        aBox.setNsPrefix("olia", OliaOnt.getURI());
    }

    static {
        posAndTypeValueMap.put("sub", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("egen", new PosAndType(LexinfoOnt.properNoun, LexinfoOnt.ProperNoun));
        posAndTypeValueMap.put("verb", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));
        posAndTypeValueMap.put("fork", new PosAndType(LexinfoOnt.abbreviation, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("adj", new PosAndType(LexinfoOnt.adjective, LexinfoOnt.Adjective));
        posAndTypeValueMap.put("prep", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("inter", new PosAndType(LexinfoOnt.interjection, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("kon", new PosAndType(LexinfoOnt.conjunction, LexinfoOnt.Conjunction));
        posAndTypeValueMap.put("pref", new PosAndType(LexinfoOnt.prefix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("suff", new PosAndType(LexinfoOnt.suffix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("pron", new PosAndType(LexinfoOnt.pronoun, LexinfoOnt.Pronoun));
        posAndTypeValueMap.put("tall", new PosAndType(LexinfoOnt.numeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("art", new PosAndType(LexinfoOnt.article, LexinfoOnt.Article));
        posAndTypeValueMap.put("det", new PosAndType(LexinfoOnt.determiner, LexinfoOnt.Determiner));
        posAndTypeValueMap.put("ordtak", new PosAndType(LexinfoOnt.proverb, LemonOnt.Phrase));
        posAndTypeValueMap.put("uttrykk", new PosAndType(LexinfoOnt.expression, LemonOnt.Phrase));
        posAndTypeValueMap.put("idiom", new PosAndType(LexinfoOnt.idiom, LemonOnt.LexicalEntry));
    }

    public void addExtraPartOfSpeech(String pos) {
        if(pos.equals("PAGENAME") ||
                pos.startsWith("wikipedia")||
                pos.startsWith("Wikipedia")) {
            return;
        }
        PosAndType pat = posAndTypeValueMap.get(pos);
        if (null == pat) log.debug("Unknown Part Of Speech value {} --in-- {}", pos, this.currentLexEntry());
        if (null != typeResource(pat))
            aBox.add(aBox.createStatement(currentLexEntry, RDF.type, typeResource(pat)));
        if (null != posResource(pat))
            aBox.add(currentLexEntry, LexinfoOnt.partOfSpeech, posResource(pat));
    }

    protected void addSpecificExtraPOS(String[] tab){
        for(String t : tab){
            String tmp[] = t.split("=");
            if(tmp.length ==2){
                switch (tmp[0]) {
                    case "k":
                        addGender(tmp[1]);
                        break;
                    case "tittel":
                    case "norm":
                        break;
                    default:
                        log.debug("Unknown infl Info {} --in-- {}", t, this.currentLexEntry());
                }
                continue;
            }
            addInfo(t);
        }
    }

    public void addPOSinfo(String s) {
        String[] infos = s.split("\\|");
        if(infos.length > 0){
            if(infos[0].equals("se også") ||
                    infos[0].contains("wikipedia") ||
                    infos[0].equalsIgnoreCase("grunnstoffer") ||
                    infos[0].equalsIgnoreCase("andre former") ||
                    infos[0].equalsIgnoreCase("verb-no") ||
                    infos[0].contains("feilstaving") ||
                    infos[0].contains("bøyningsform") ||
                    infos[0].contains("-et")
                    ){
                return;
            }
            if(infos[0].equals("infl")){
                addSpecificExtraPOS(infos);
            }
            else {
                Set<String> set = posAndTypeValueMap.keySet();
                for(String t : set) {
                    if (infos[0].contains(t)) {
                        addExtraPartOfSpeech(t);
                        break;
                    }
                }
            }
            if(infos.length > 1) {
                String pair[] = infos[1].split("=");
                if(pair.length == 2){
                    switch (pair[0].trim()){
                        case "ref":
                            switch(pair[1].trim()){
                                case "x":
                                case "y":
                                    // log.debug("Unused reflexive info {} --in-- {}", infos[1], this.currentLexEntry());
                                    break;
                                case "ja":
                                    break;
                                default:
                                    log.debug("Unknown ref Info {} --in-- {}", infos[1], this.currentLexEntry());
                            }
                            break;
                        case "unormert":
                            // log.debug("Unused norm info {} --in-- {}", infos[1], this.currentLexEntry());
                            break;
                        case "ainf":
                        case "a-infinitiv":
                        case "pres":
                        case "presens":
                        case "pret":
                        case "preteritum":
                        case "presp":
                        case "presens partisipp":
                        case "perfp":
                        case "perfektum partisipp":
                        case "imp":
                        case "imperativ":
                            // log.debug("Unused tense info {} --in-- {}", infos[1], this.currentLexEntry());
                            break;
                        case "b":
                        case "c":
                        case "nb":
                        case "nn":
                        case "nrm":
                        case "norm":
                        case "nd":
                        case "nu":
                        case "tittel":
                        case "språk":
                        case "riksmål":
                            break;
                        default:
                            log.debug("Unknown POS Info {} --in-- {}", infos[1], this.currentLexEntry());
                    }
                }
                else {
                    if(infos[1].replaceAll("\\d","").equals("")){
                        return;
                    }
                    addInfo(infos[1]);
                }
            }
        }
    }

    protected void addInfo(String s){
        switch (s.trim()){
            case "tall":
            case "tallord":
                addExtraPartOfSpeech("tall");
                break;
            case "prefiks":
                addExtraPartOfSpeech("pref");
                break;
            case "suffiks":
                addExtraPartOfSpeech("suff");
                break;
            case "interjeksjon":
            case "inter":
                addExtraPartOfSpeech("inter");
                break;
            case "adj":
            case "adjektiv":
                addExtraPartOfSpeech("adj");
                break;
            case "adv":
            case "adverb":
                addExtraPartOfSpeech("adv");
                break;
            case "verb":
                addExtraPartOfSpeech("verb");
                break;
            case "sub":
            case "substantiv":
            case "-sub-":
            case "egennavn":
            case "egen":
            case "-egen-":
                addExtraPartOfSpeech("sub");
                break;
            case "artikkel":
                addExtraPartOfSpeech("art");
                break;
            case "preposisjon":
                addExtraPartOfSpeech("prep");
                break;
            case "konjunksjon":
            case "kon":
            case "konj":
            case "-kon-":
            case "-konj-":
                addExtraPartOfSpeech("kon");
                break;
            case "pronomen":
            case "pron":
            case "-pron-":
                addExtraPartOfSpeech("pron");
                break;
            case "fork":
            case "forkortelse":
            case "-fork-":
                addExtraPartOfSpeech("fork");
                break;
            case "deter":
            case "-deter-":
                addExtraPartOfSpeech("det");
                break;
            case "ordspråk":
            case "ordtak":
                addExtraPartOfSpeech("ordtak");
                break;
            case "m":
            case "f":
            case "n":
            case "mf":
            case "fm":
            case "mn":
            case "nm":
            case "fn":
            case "nf":
            case "mfn":
            case "fnm":
            case "nmf":
            case "fmn":
            case "mnf":
            case "nfm":
                addGender(s);
                break;
            default:
                // log.debug("Unknown pos Info {} --in-- {}", s, this.currentLexEntry());
        }
    }

    protected void addGender(String s){
        s = s.replaceAll("\'","");
        switch (s) {
            case "m":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                break;
            case "f":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "n":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "mf":
            case "fm":
            case "m eller f":
            case "f eller m":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "fn":
            case "nf":
            case "f eller n":
            case "n eller f":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "mn":
            case "nm":
            case "m eller n":
            case "n eller m":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "mfn":
            case "fnm":
            case "nmf":
            case "fmn":
            case "mnf":
            case "nfm":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            default:
                log.debug("Unknown gender {} --in-- {}", s, this.currentLexEntry());
        }
    }

    public void addWrittenRep(String word){
        if(currentLexEntry != null)
           aBox.add(currentLexEntry, LemonOnt.writtenRep, word, extractedLang);
    }

    public void addAbbrev(String word){
        if(currentLexEntry != null)
            aBox.add(currentLexEntry, LexinfoOnt.abbreviationFor, word);
    }

    public void registerExample(String ex){
        if(currentSense != null)
            registerExample(ex, new HashMap<Property, String>());
    }
}
