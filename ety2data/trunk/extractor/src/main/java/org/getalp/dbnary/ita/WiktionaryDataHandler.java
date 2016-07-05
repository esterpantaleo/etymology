package org.getalp.dbnary.ita;

import com.hp.hpl.jena.rdf.model.Resource;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {

        posAndTypeValueMap.put("noun", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("sost", new PosAndType(LexinfoOnt.noun, LemonOnt.Word));
        posAndTypeValueMap.put("loc noun", new PosAndType(LexinfoOnt.noun, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc nom", new PosAndType(LexinfoOnt.noun, LemonOnt.Phrase));
        posAndTypeValueMap.put("nome", new PosAndType(LexinfoOnt.properNoun, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("name", new PosAndType(LexinfoOnt.properNoun, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("adj", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("adjc", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("agg", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));
        posAndTypeValueMap.put("loc adjc", new PosAndType(LexinfoOnt.adjective, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc agg", new PosAndType(LexinfoOnt.adjective, LemonOnt.Phrase));
        posAndTypeValueMap.put("avv", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LemonOnt.Word));
        posAndTypeValueMap.put("loc avv", new PosAndType(LexinfoOnt.adverb, LemonOnt.Phrase));
        posAndTypeValueMap.put("verb", new PosAndType(LexinfoOnt.verb, LemonOnt.Word));
        posAndTypeValueMap.put("loc verb", new PosAndType(LexinfoOnt.verb, LemonOnt.Phrase));

        posAndTypeValueMap.put("agg num", new PosAndType(LexinfoOnt.numeral, LemonOnt.Word));
        posAndTypeValueMap.put("ord", new PosAndType(LexinfoOnt.ordinalAdjective, LemonOnt.Word));
        posAndTypeValueMap.put("agg poss", new PosAndType(LexinfoOnt.possessiveAdjective, LemonOnt.Word));
        posAndTypeValueMap.put("card", new PosAndType(LexinfoOnt.cardinalNumeral, LemonOnt.Word));
        posAndTypeValueMap.put("agg nom", new PosAndType(LexinfoOnt.adjective, LemonOnt.Word));

        posAndTypeValueMap.put("art", new PosAndType(LexinfoOnt.article, LemonOnt.Word));
        posAndTypeValueMap.put("cong", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Word));
        posAndTypeValueMap.put("conj", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Word));
        posAndTypeValueMap.put("inter", new PosAndType(LexinfoOnt.interjection, LemonOnt.Word));
        posAndTypeValueMap.put("interj", new PosAndType(LexinfoOnt.interjection, LemonOnt.Word));
        posAndTypeValueMap.put("loc cong", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc conj", new PosAndType(LexinfoOnt.conjunction, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc inter", new PosAndType(LexinfoOnt.interjection, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc interj", new PosAndType(LexinfoOnt.interjection, LemonOnt.Phrase));
        posAndTypeValueMap.put("loc prep", new PosAndType(LexinfoOnt.preposition, LemonOnt.Phrase));
        posAndTypeValueMap.put("posp", new PosAndType(LexinfoOnt.postposition, LemonOnt.Word));
        posAndTypeValueMap.put("prep", new PosAndType(LexinfoOnt.preposition, LemonOnt.Word));
        posAndTypeValueMap.put("pron poss", new PosAndType(LexinfoOnt.possessivePronoun, LemonOnt.Word));
        posAndTypeValueMap.put("pronome poss", new PosAndType(LexinfoOnt.possessivePronoun, LemonOnt.Word));
        posAndTypeValueMap.put("pronome", new PosAndType(LexinfoOnt.pronoun, LemonOnt.Word));

        // TODO: -acron-, -acronim-, -acronym-, -espr-, -espress- mark locution as phrases

        // Template:-abbr-

        // Template redirects
//        Template:-abbr-
//                Template:-acronim-
//                Template:-acronym-
//                Template:-esclam-
//                Template:-espress-
//                Template:-let-
//                Template:-loc noun form-
//                Template:-name form-
//                Template:-noun form-
//                Template:-prefix-
//                Template:-pronome form-
//                Template:-pronoun form-
        posAndTypeValueMap.put("pref", new PosAndType(LexinfoOnt.prefix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("prefix", new PosAndType(LexinfoOnt.prefix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("suff", new PosAndType(LexinfoOnt.suffix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("suffix", new PosAndType(LexinfoOnt.suffix, LemonOnt.LexicalEntry));



        posAndTypeValueMap.put("acron", new PosAndType(null, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("acronim", new PosAndType(null, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("acronym", new PosAndType(null, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("espr", new PosAndType(null, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("espress", new PosAndType(null, LemonOnt.LexicalEntry));



        // For translation glosses

    }

    public static boolean isValidPOS(String pos) {
        return posAndTypeValueMap.containsKey(pos);
    }

    Map<Resource, Set<Resource>> lexEntries = new HashMap<>();

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    @Override
    public void initializePageExtraction(String wiktionaryPageName) {
        super.initializePageExtraction(wiktionaryPageName);

    }

    @Override
    public void initializeEntryExtraction(String wiktionaryPageName) {
        super.initializeEntryExtraction(wiktionaryPageName);
        lexEntries.clear();
    }

    @Override
    public void finalizePageExtraction() {
        super.finalizePageExtraction();
    }

    @Override
    public void finalizeEntryExtraction() {
        super.finalizeEntryExtraction();
    }

    @Override
    public void registerTranslation(String lang, String currentGlose, String usage, String word) {
        if (lexEntries.size() == 0) {
            log.debug("Registering Translation when no lexical entry is defined in {}", currentWiktionaryPageName);
        } else if (lexEntries.size() == 1) {
            super.registerTranslation(lang, currentGlose, usage, word);
        } else if (null == currentGlose || currentGlose.length() == 0) {
            log.debug("Attaching translations to Vocable (Null gloss and several lexical entries) in {}", currentWiktionaryPageName);
            super.registerTranslationToEntity(currentMainLexEntry, lang, currentGlose, usage, word);
        } else {
            // TODO: guess which translation is to be attached to which entry/sense
            List<Resource> entries = getLexicalEntryUsingPartOfSpeech(currentGlose);
            if (entries.size() != 0) {
                log.warn("Attaching translations using part of speech in gloss : {}", currentWiktionaryPageName);
                if (entries.size() > 1) {
                    log.warn("Attaching translations to several entries in {}", currentWiktionaryPageName);
                }
                for (Resource entry : entries) {
                    super.registerTranslationToEntity(entry, lang, currentGlose, usage, word);
                }
            } else {
                log.debug("Several entries are defined in {} // {}", currentWiktionaryPageName, currentGlose);
                // TODO: disambiguate and attach to the correct entry.
                super.registerTranslationToEntity(currentMainLexEntry, lang, currentGlose, usage, word);
            }
        }
    }

    private List<Resource> getLexicalEntryUsingPartOfSpeech(String gloss) {
        gloss = gloss.trim();
        ArrayList<Resource> res = new ArrayList<>();
        if (gloss.startsWith("aggettivo numerale")) {
            addAllResourceOfPoS(res, LexinfoOnt.numeral);
        } else if (gloss.startsWith("aggettivo")) {
            addAllResourceOfPoS(res, LexinfoOnt.adjective);
        } else if (gloss.startsWith("avverbio")) {
            addAllResourceOfPoS(res, LexinfoOnt.adverb);
        } else if (gloss.startsWith("pronome")) {
            addAllResourceOfPoS(res, LexinfoOnt.pronoun);
        } else if (gloss.startsWith("sostantivo")) {
            addAllResourceOfPoS(res, LexinfoOnt.noun);
        } else if (gloss.startsWith("verbo")) {
            addAllResourceOfPoS(res, LexinfoOnt.verb);
        } else if (gloss.startsWith("agg. e sost.")) {
            addAllResourceOfPoS(res, LexinfoOnt.adjective);
            addAllResourceOfPoS(res, LexinfoOnt.noun);
        }
        return res;
    }

    private void addAllResourceOfPoS(ArrayList<Resource> res, Resource pos) {
        Set<Resource> ares = lexEntries.get(pos);
        if (ares != null)
            res.addAll(ares);
    }

    @Override
    public void addPartOfSpeech(String pos) {
        // TODO: Italian sometimes define translations for noun forms. If an entry is ambiguous,
        // TODO: then translations could be wrongly attached. The forms should be kept in lex entries
        // TODO: but not correspond to a valid resource. This will be usefull for later
        // drop of non useful translations.
        PosAndType pat = posAndTypeValueMap.get(pos);
        Resource entry = addPartOfSpeech(pos, posResource(pat), typeResource(pat));
        addLexEntry(posResource(pat), entry);
    }

    private void addLexEntry(Resource pos, Resource entry) {
        Set<Resource> entrySet;
        if (null != (entrySet = lexEntries.get(pos))) {
            entrySet.add(entry);
        } else {
            entrySet = new HashSet<Resource>();
            entrySet.add(entry);
            lexEntries.put(pos, entrySet);
        }
    }
}
