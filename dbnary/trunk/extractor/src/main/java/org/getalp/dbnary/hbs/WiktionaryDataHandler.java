package org.getalp.dbnary.hbs;

import com.hp.hpl.jena.vocabulary.RDF;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.OliaOnt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;


public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {

        posAndTypeValueMap.put("sh-pridjev", new PosAndType(LexinfoOnt.adjective, LexinfoOnt.Adjective));

        posAndTypeValueMap.put("sh-prilog", new PosAndType(LexinfoOnt.adverb, LexinfoOnt.Adverb));

        posAndTypeValueMap.put("sh-imenica", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("sh-imenica2", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("-znači-imenica-", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("-znači-država-", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("-znači-grad-", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("sh-noun", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("sh-vlastito ime", new PosAndType(LexinfoOnt.properNoun, LexinfoOnt.ProperNoun));

        posAndTypeValueMap.put("sh-glagol", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));
    }

    public HashMap<String, ArrayList<String>> alreadyRegisteredFlexion = new HashMap<>();
    public HashMap<String, HashMap<String,String>> toBeRegisterFlexion = new HashMap<>();

    public WiktionaryDataHandler(String lang) {
        super(lang);
        aBox.setNsPrefix("olia", OliaOnt.getURI());
    }

    public void extractPOSandExtraInfos(String posInfos){
        String t[] = posInfos.split("\\|");
        if(t.length > 0){
            switch(t[0]) {
                case "sh-pridjev": // adj
                case "sh-prilog": // adv
                case "sh-imenica": // noun
                case "sh-imenica2": // noun
                case "-znači-imenica-": // noun
                case "sh-noun": // noun
                case "sh-vlastito ime": // proper noun
                case "sh-glagol": // verb
                case "-znači-država-":
                case "-znači-grad-":
                    addExtraInfo(posInfos);
                    break;
                case "m":
                case "mn":
                case "ž":
                case "f":
                case "g=f":
                    addGender(t[0]);
                    return;
                case "svrš.": // purpose
                case "nesvrš.": // not purpose
                case "PAGENAME":
                    return;
                default:
                    log.debug("Unknown posMaccro {} --in-- {}", t[0], this.currentLexEntry());
                    return;
            }
            addExtraPartOfSpeech(t[0]);
        }
    }

    public void addExtraPartOfSpeech(String pos) {
        PosAndType pat = posAndTypeValueMap.get(pos);
        if (null == pat) log.debug("Unknown Part Of Speech value {} --in-- {}", pos, this.currentLexEntry());
        if (null != typeResource(pat))
            aBox.add(aBox.createStatement(currentLexEntry, RDF.type, typeResource(pat)));
        if (null != posResource(pat))
            aBox.add(currentLexEntry, LexinfoOnt.partOfSpeech, posResource(pat));
    }

    private void addExtraInfo(String posInfos) {
        String[] infos = posInfos.split("\\|");
        int i = 1;
        while(i < infos.length){
            String[] pair = infos[i].split("=");
            if(pair.length == 2 ){
                if(!pair[1].equals(""))
                switch (pair[0]) {
                    case "head":
                      //  log.debug("Head unused : {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                        break;
                    case "m":
                        // log.debug("Plural form unused : {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                        break;
                    case "r":
                        addGender(pair[1]);
                        break;
                    case "g":
                        if(infos[0].equals("sh-imenica")) {
                            switch (pair[1]){
                                case "e":
                                    log.debug("Pronunciation ekavski unused in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                    break;
                                case "ije":
                                    log.debug("Pronunciation ijekavski unused in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                    break;
                                case "i":
                                    log.debug("Pronunciation ikavski unused in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                    break;
                                default:
                                    log.debug("Pronunciation unused : {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                            }
                        }
                        else{
                            addGender(pair[1]);
                        }
                        break;
                    case "g2":
                        addGender(pair[1]);
                        break;
                    case "v":
                        // TODO Geographic parameter
                       //  log.debug("Version/Variant unused : {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                        break;
                    case "p":
                        log.debug("Paradigme unused : {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                        break;
                    case "def":
                    case"def_a":
                    case"comp":
                    case"comp_a":
                        // TODO
                        // log.debug("Unknown value {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                        break;
                    default:
                        log.debug("Unknown value {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                }
            }
            else{
                if(!infos[i].contains("=")){ // neutralise g= or v=
                    switch (infos[i]) {
                        case "r":
                            String cirilica = null;
                            i++;
                            if (i < infos.length) {
                                cirilica = infos[i];
                            }
                            i++;
                            if (cirilica != null) {
                                aBox.add(currentLexEntry, LemonOnt.writtenRep, cirilica, extractedLang);
                            } else {
                                log.debug("r (Cirilica) unused in {} --in-- {}", infos[0], this.currentLexEntry());
                            }
                            break;
                        default:
                            log.debug("Unused info {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                    }
                }
            }
            i++;
        }
    }

    private void addGender(String info){
        switch (info) {
            case "m":
            case "m-p":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                break;
            case "ž":
            case "z":
            case "f":
            case "f-p":
            case "g=f":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "s":
            case "nt":
            case "n":
            case "n-p":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "mn":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            default:
                log.debug("Unknown gender {} --in-- {}", info, this.currentLexEntry());
        }
    }
}
