package org.getalp.dbnary.lat;

import com.hp.hpl.jena.rdf.model.Resource;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.OliaOnt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by serasset on 17/09/14.
 */
public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {

        // French
        posAndTypeValueMap.put("nomen-subst", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("nomen", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("nomen-prop", new PosAndType(LexinfoOnt.properNoun, LemonOnt.Word));
        // posAndTypeValueMap.put("-prénom-", new PosAndType(LexinfoOnt.properNoun, LemonOnt.Word));
        posAndTypeValueMap.put("nomen-adj", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("verbum", new PosAndType(LexinfoOnt.verb, LemonOnt.Word));
        // TODO: check how to encode transitivity/intransitivity
        posAndTypeValueMap.put("verbum-tr", new PosAndType(LexinfoOnt.verb, LemonOnt.Word));
        posAndTypeValueMap.put("verbum-intr", new PosAndType(LexinfoOnt.verb, LemonOnt.Word));
        posAndTypeValueMap.put("adverbium", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        //posAndTypeValueMap.put("-loc-adv-", new PosAndType(LexinfoOnt.adverb, LemonOnt.Phrase));
        //posAndTypeValueMap.put("-loc-adj-", new PosAndType(LexinfoOnt.adjective, LemonOnt.Phrase));
        //posAndTypeValueMap.put("-loc-nom-", new PosAndType(LexinfoOnt.noun, LemonOnt.Phrase));
        //posAndTypeValueMap.put("-loc-verb-", new PosAndType(LexinfoOnt.verb, LemonOnt.Phrase));
        posAndTypeValueMap.put("coniunctio", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Word));
        posAndTypeValueMap.put("pronomen", new PosAndType(LexinfoOnt.pronoun, LemonOnt.Word));
        posAndTypeValueMap.put("participium", new PosAndType(LexinfoOnt.participle, LemonOnt.Word));
        posAndTypeValueMap.put("wikt-praep", new PosAndType(LexinfoOnt.preposition, LemonOnt.Word));
    }

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    @Override
    public void addPartOfSpeech(String pos) {
        // DONE: compute if the entry is a phrase or a word.
        PosAndType pat = posAndTypeValueMap.get(pos);
        Resource typeR = typeResource(pat);
        if (currentWiktionaryPageName.startsWith("se ")) {
            if (currentWiktionaryPageName.substring(2).trim().contains(" ")) {
                typeR = LemonOnt.Phrase;
            }
        } else if (currentWiktionaryPageName.contains(" ")) {
            typeR = LemonOnt.Phrase;
        }
        addPartOfSpeech(pos, posResource(pat), typeR);
    }


}
