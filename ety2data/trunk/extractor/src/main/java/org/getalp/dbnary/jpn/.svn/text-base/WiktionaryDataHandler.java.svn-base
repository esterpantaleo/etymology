package org.getalp.dbnary.jpn;

import com.hp.hpl.jena.rdf.model.Resource;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.OliaOnt;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * Created by serasset on 17/09/14.
 */
public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {

        posAndTypeValueMap = new HashMap<>(20);
        posAndTypeValueMap.put("noun", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("名詞", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("idiom", new PosAndType(LexinfoOnt.idiom, LemonOnt.Phrase));
        posAndTypeValueMap.put("成句", new PosAndType(LexinfoOnt.idiom, LemonOnt.Phrase));
        posAndTypeValueMap.put("四字熟語", new PosAndType(LexinfoOnt.idiom, LemonOnt.Phrase));
        posAndTypeValueMap.put("verb", new PosAndType(LexinfoOnt.verb, LemonOnt.Word));
        posAndTypeValueMap.put("adj", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("adjective", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("形容詞", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("name", new PosAndType(LexinfoOnt.properNoun, LemonOnt.Word));
        posAndTypeValueMap.put("固有名詞", new PosAndType(LexinfoOnt.properNoun, LemonOnt.Word));
        posAndTypeValueMap.put("人名", new PosAndType(LexinfoOnt.properNoun, LemonOnt.Word));
        posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        posAndTypeValueMap.put("adverb", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        posAndTypeValueMap.put("副詞", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        posAndTypeValueMap.put("abbr", new PosAndType(LexinfoOnt.abbreviation, LemonOnt.Word));
        posAndTypeValueMap.put("略語", new PosAndType(LexinfoOnt.abbreviation, LemonOnt.Word));
        //        posAndTypeValueMap.put("prov", "Proverb");
        //        posAndTypeValueMap.put("熟語", "Proverb");
        //        posAndTypeValueMap.put("ことわざ", "Proverb");
        posAndTypeValueMap.put("形容動詞", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word)); // TODO: Find a better mapping; this is rather an "adjectival noun..."
        posAndTypeValueMap.put("adjectivenoun", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("感動詞", new PosAndType(LexinfoOnt.interjection, LemonOnt.Word));
        // Ignorable part of speech
        posAndTypeValueMap.put("助詞", new PosAndType(LexinfoOnt.particle, LemonOnt.Word)); // particle
        posAndTypeValueMap.put("conj", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Word)); // conj
        posAndTypeValueMap.put("接続詞", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Word)); // conj
        posAndTypeValueMap.put("代名詞", new PosAndType(LexinfoOnt.pronoun, LemonOnt.Word)); // pronoun


        // adjectivenoun, 形容動詞
        // 慣用句 (Idiom)
        // 和語の漢字表記 --> notes the kanji spelling of words from Japanese Origin. Sometimes, only points to the kana writing. (it may be used as a category)
        // 慣用句 --> Idiom ? What is the difference with other Idioms
        // 感動詞 --> Interjection
        // 人名 --> Person's name: Proper Noun
        // 成句 --> Idiomatic phrase --> Is this a pos or a section | 成句
        // conj
        // ===助詞=== particle

    }

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    public static String getValidPOS(String head, String pageName) {
        // TODO Check what is meant by the words that are given after the POS.
        // TODO: check if the POS macros are given some args.
        // TODO: treat: ==={{noun}}?{{adverb}}===
        // DONE: some pos (like idiom) may be used as a POS or as a sub section in the entry. => Check the header level.
        // Only keep level 3 headers ? --> No.
        // Heuristic is used: if entry length <= 2 then idiom is not a POS.
        PosAndType pos = null;
        String posKey = null;
        Matcher macro = WikiPatterns.macroPattern.matcher(head);
        if (macro.lookingAt()) { // the section starts by a wiki macro
            posKey = macro.group(1);
        } else {
            String [] h = head.split(":");
            posKey = h[0];
        }
        pos = posAndTypeValueMap.get(posKey);
        if (null != posKey && (posKey.equals("idiom") || posKey.equals("成句"))) {
            // When idiom is found on a 1 or 2 char entry, it is assumed to be a section giving the idioms build from the entry.
            // Otherwise it is believed to be a Part Of Speech.
            if (pageName.length() <= 2)
                pos = null;
        }

        return (null == pos) ? null : posKey;
    }

}
