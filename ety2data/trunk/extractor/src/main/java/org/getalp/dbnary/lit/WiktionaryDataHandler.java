package org.getalp.dbnary.lit;


import com.hp.hpl.jena.vocabulary.RDF;
import org.getalp.dbnary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {
        posAndTypeValueMap.put("ltdkt", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("ltvks", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));
        posAndTypeValueMap.put("ltdlv", new PosAndType(LexinfoOnt.participle, LexinfoOnt.Particle));
        posAndTypeValueMap.put("ltpsdlv", new PosAndType(LexinfoOnt.participle, LexinfoOnt.Particle));
        posAndTypeValueMap.put("ltbdv", new PosAndType(LexinfoOnt.adjective, LexinfoOnt.Adjective));
        posAndTypeValueMap.put("ltprv", new PosAndType(LexinfoOnt.adverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("ltpdlv", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("ltprl", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("ltjung", new PosAndType(LexinfoOnt.phraseologicalUnit, LemonOnt.Phrase));
        posAndTypeValueMap.put("ltjng", new PosAndType(LexinfoOnt.conjunction, LexinfoOnt.Conjunction));
        posAndTypeValueMap.put("ltįvrd", new PosAndType(LexinfoOnt.pronoun, LexinfoOnt.Pronoun));
        posAndTypeValueMap.put("ltskt", new PosAndType(LexinfoOnt.numeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("ltrad", new PosAndType(LexinfoOnt.letter, LexinfoOnt.Symbol));
        posAndTypeValueMap.put("ltsant", new PosAndType(LexinfoOnt.abbreviation, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltdll", new PosAndType(LexinfoOnt.baseElement, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltprd", new PosAndType(LexinfoOnt.prefix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltprs", new PosAndType(LexinfoOnt.suffix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltjst", new PosAndType(LexinfoOnt.interjection, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltišt", new PosAndType(LexinfoOnt.interjection, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("ltskt-kiek", new PosAndType(LexinfoOnt.cardinalNumeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("ltskt-kelint", new PosAndType(LexinfoOnt.indefiniteOrdinalNumeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("ltskt-kelint-laips", new PosAndType(LexinfoOnt.indefiniteOrdinalNumeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("ltsmb", new PosAndType(LexinfoOnt.symbol, LexinfoOnt.Symbol));
    }

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    public void addPOSinfo(String s) {
        String infos[] = s.split("\\|");
        if (infos.length > 0) {
            int i = 1;
            while (i < infos.length) {
                String[] pair = infos[i].split("=");
                if (pair.length == 2 && !pair[0].equals("")) {
                    if (!pair[1].equals(""))
                        switch (pair[0]) {
                            case "forma":
                                String forma[] = pair[1].split("-");
                                for (String tmp : forma) {
                                    switch (tmp) {
                                        case "vyr": // m
                                        case "mot": // f
                                        case "bev": // n
                                            addGender(tmp);
                                            break;
                                        case "f": // forma
                                            break;
                                        default:
                                           // log.debug("Unused forma info {} in {} --in-- {}", tmp, infos[0], this.currentLexEntry());
                                    }
                                }
                                break;
                            case "gim":
                                if(pair[1].length() > 5)
                                    addGender(pair[1].substring(2,5));
                                else
                                    log.debug("Unknown Gim:gender {} --in-- {}", pair[1], this.currentLexEntry());
                                break;
                            case "vnsdgst": // Gnumber : singular or plural
                                 //  log.debug("Unused Gnumber info {} in {} --in-- {}", pair[1], infos[0], this.currentLexEntry());
                                break;
                            case "šakn": // root
                            case "šakn2":
                            case "šakn3":
                            case "šakn4":
                                aBox.add(LexinfoOnt.radical,LexinfoOnt.root,pair[1].trim());
                                break;
                            case "bšakn":    // alt root ?
                            case "eslšakn":  // alt root ?
                            case "btklšakn": // alt root ?
                            case "liepšakn": // alt root ?
                                 //  log.debug("Unknown root info {} in {} --in-- {}", pair[1], infos[0], this.currentLexEntry());
                                break;
                            case "žodis": // word
                            case "tikr": // check
                                break;
                            case "pform":
                            case "pform1":
                            case "pform2":
                            case "pform3":
                            case "pformm":
                            case "pformv":
                            case "pfrom":
                            case "ppform":
                              //  log.debug("Unused pform {} in {} --in-- {}", pair[1], infos[0], this.currentLexEntry());
                                break;
                            case "skiem": // syllable
                             //   log.debug("Unused syllable info {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "el":  // present tense
                            case "bkl": // past simple tense
                            case "bdl": // frequency past time
                            case "bl":  // future tense
                                // log.debug("Unused inflection {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "nl": // not comparable degree
                               //  log.debug("Unused comparable info {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "vm":
                            case "vf":
                            case "dm":
                            case "df":
                               //  log.debug("Unused inflexion {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "dalyviai":
                               //  log.debug("Unknown value {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "tipas": // type
                               //  log.debug("Unused type form {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                break;
                            case "dkirt":
                            case "dkirt-typ":
                            case "vkirt":
                            case "l1": // gradual in ltprv
                            case "l2": // gradual in ltprv
                            case "l3": // gradual in ltprv
                                break;
                            default:
                                if(!pair[0].startsWith("v") && !pair[0].startsWith("d")){
                                    log.debug("Unknown value {} in {} --in-- {}", infos[i], infos[0], this.currentLexEntry());
                                }
                        }
                }
                i++;
            }
            if(infos[0].equals("ltskt")){
                String[] pair = infos[1].split("=");
                if(pair.length == 2) {
                    addExtraPartOfSpeech(infos[0] + "-" + pair[1]);
                }
            }
            else {
                addExtraPartOfSpeech(infos[0]);
            }
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

    private void addGender(String info) {
        switch (info) {
            case "vyr":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                break;
            case "mot":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "bev":
            case "ben":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            default:
                log.debug("Unknown gender {} --in-- {}", info, this.currentLexEntry());
        }
    }

    public void addNymInfo(String nymRel, String[] tNyms, String gloss) {
        if(!nymRel.equals("") && tNyms.length >2) {
            if(tNyms[1].equals("lt")) {
               registerNymRelation(tNyms[2], nymRel, gloss);
            } else {
                log.debug("Unused Nyms:lang {} --in-- {}", tNyms[1], this.currentLexEntry());
            }
        }
    }

    public void addRelatedTermInfo(String[] tRel) {
        if(tRel.length >2) {
            if(tRel[1].equals("lt")) {
                aBox.add(LexinfoOnt.relatedTerm, LexinfoOnt.relatedTerm, tRel[2]);
            } else {
                log.debug("Unused RelatedTerm:lang {} --in-- {}", tRel[1], this.currentLexEntry());
            }
        }
    }
}
