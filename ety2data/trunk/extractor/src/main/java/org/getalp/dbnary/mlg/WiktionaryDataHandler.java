package org.getalp.dbnary.mlg;


import com.hp.hpl.jena.vocabulary.RDF;
import org.getalp.dbnary.LemonBasedRDFDataHandler;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiktionaryDataHandler extends LemonBasedRDFDataHandler {

    private Logger log = LoggerFactory.getLogger(WiktionaryDataHandler.class);

    static {
        posAndTypeValueMap.put("ana", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("mat", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));
        posAndTypeValueMap.put("adj", new PosAndType(LexinfoOnt.adjective, LexinfoOnt.Adjective));
        posAndTypeValueMap.put("adv", new PosAndType(LexinfoOnt.adverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("prep", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("prefix", new PosAndType(LexinfoOnt.prefix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("infix", new PosAndType(LexinfoOnt.infix, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("article", new PosAndType(LexinfoOnt.article, LexinfoOnt.Article));
        posAndTypeValueMap.put("none", new PosAndType(LemonOnt.LexicalEntry, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("root", new PosAndType(LexinfoOnt.radical, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("expr", new PosAndType(LexinfoOnt.expression, LemonOnt.Phrase));
    }

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    public void addExtraPartOfSpeech(String pos) {
        PosAndType pat = posAndTypeValueMap.get(pos);
        if (null == pat) log.debug("Unknown Part Of Speech value {} --in-- {}", pos, this.currentLexEntry());
        if (null != typeResource(pat))
            aBox.add(aBox.createStatement(currentLexEntry, RDF.type, typeResource(pat)));
        if (null != posResource(pat))
            aBox.add(currentLexEntry, LexinfoOnt.partOfSpeech, posResource(pat));
    }

    public void addTranslation(String lang, String gloss, String usage, String word){
        if(currentLexEntry == null){
           addPartOfSpeech("none");
        }
        registerTranslation(lang, gloss, usage, word);
    }

}
