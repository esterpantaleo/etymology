package org.getalp.dbnary.deu;

import com.hp.hpl.jena.vocabulary.RDF;
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
        // German
        posAndTypeValueMap.put("Substantiv", new PosAndType(LexinfoOnt.noun, LexinfoOnt.Noun));
        posAndTypeValueMap.put("Nachname", new PosAndType(LexinfoOnt.properNoun, LexinfoOnt.ProperNoun));
        posAndTypeValueMap.put("Vorname", new PosAndType(LexinfoOnt.properNoun, LexinfoOnt.ProperNoun));
        posAndTypeValueMap.put("Eigenname", new PosAndType(LexinfoOnt.properNoun, LexinfoOnt.ProperNoun));

        posAndTypeValueMap.put("Buchstabe", new PosAndType(LexinfoOnt.letter, LexinfoOnt.Symbol));

        posAndTypeValueMap.put("Adjektiv", new PosAndType(LexinfoOnt.adjective, LexinfoOnt.Adjective));
        posAndTypeValueMap.put("Verb", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));
        posAndTypeValueMap.put("Adverb", new PosAndType(LexinfoOnt.adverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("Pronominaladverb", new PosAndType(LexinfoOnt.pronominalAdverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("Modaladverb", new PosAndType(LexinfoOnt.Adverb, LexinfoOnt.Adverb));
        posAndTypeValueMap.put("Lokaladverb", new PosAndType(LexinfoOnt.Adverb, LexinfoOnt.Adverb));

        posAndTypeValueMap.put("Partizip I", new PosAndType(LexinfoOnt.participle, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("Partizip II", new PosAndType(LexinfoOnt.participle, LemonOnt.LexicalEntry));


        posAndTypeValueMap.put("Abkürzung", new PosAndType(LexinfoOnt.abbreviation, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("Toponym", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));

        posAndTypeValueMap.put("Hilfsverb", new PosAndType(LexinfoOnt.verb, LexinfoOnt.Verb));

        // TODO: check following POS in German...
        posAndTypeValueMap.put("Artikle", new PosAndType(LexinfoOnt.article, LexinfoOnt.Article));
        posAndTypeValueMap.put("Artikel", new PosAndType(LexinfoOnt.article, LexinfoOnt.Article));
        posAndTypeValueMap.put("Conjunction", new PosAndType(LexinfoOnt.conjunction, LexinfoOnt.Conjunction));
        posAndTypeValueMap.put("Determiner", new PosAndType(LexinfoOnt.determiner, LexinfoOnt.Determiner));

        posAndTypeValueMap.put("Numeral", new PosAndType(LexinfoOnt.numeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("Cardinal numeral", new PosAndType(LexinfoOnt.cardinalNumeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("Cardinal number", new PosAndType(LexinfoOnt.cardinalNumeral, LexinfoOnt.Numeral));

        posAndTypeValueMap.put("Numeral", new PosAndType(LexinfoOnt.numeral, LexinfoOnt.Numeral));
        posAndTypeValueMap.put("Particle", new PosAndType(LexinfoOnt.particle, LexinfoOnt.Particle));
        posAndTypeValueMap.put("Partikel", new PosAndType(LexinfoOnt.particle, LexinfoOnt.Particle));

        posAndTypeValueMap.put("Preposition", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("Präposition", new PosAndType(LexinfoOnt.preposition, LexinfoOnt.Preposition));
        posAndTypeValueMap.put("Postposition", new PosAndType(LexinfoOnt.postposition, LexinfoOnt.Postposition));

        posAndTypeValueMap.put("Prepositional phrase", new PosAndType(null, LemonOnt.Phrase));

        posAndTypeValueMap.put("Pronoun", new PosAndType(LexinfoOnt.pronoun, LexinfoOnt.Pronoun));
        posAndTypeValueMap.put("Indefinitpronomen", new PosAndType(LexinfoOnt.indefinitePronoun, LexinfoOnt.Pronoun));
        posAndTypeValueMap.put("Demonstrativpronomen", new PosAndType(LexinfoOnt.demonstrativePronoun, LexinfoOnt.Pronoun));

        posAndTypeValueMap.put("Symbol", new PosAndType(LexinfoOnt.symbol, LexinfoOnt.Symbol));

        posAndTypeValueMap.put("Prefix", new PosAndType(LexinfoOnt.prefix, LexinfoOnt.Prefix));
        posAndTypeValueMap.put("Präfix", new PosAndType(LexinfoOnt.prefix, LexinfoOnt.Prefix));

        posAndTypeValueMap.put("Suffix", new PosAndType(LexinfoOnt.suffix, LexinfoOnt.Suffix));
        posAndTypeValueMap.put("Affix", new PosAndType(LexinfoOnt.affix, LexinfoOnt.Affix));
        posAndTypeValueMap.put("Infix", new PosAndType(LexinfoOnt.infix, LexinfoOnt.Infix));
        posAndTypeValueMap.put("Interfix", new PosAndType(LexinfoOnt.affix, LexinfoOnt.Affix));
        posAndTypeValueMap.put("Circumfix", new PosAndType(LexinfoOnt.affix, LexinfoOnt.Affix));

        posAndTypeValueMap.put("Proverb", new PosAndType(LexinfoOnt.proverb, LemonOnt.Phrase));
        posAndTypeValueMap.put("Interjection", new PosAndType(LexinfoOnt.interjection, LexinfoOnt.Interjection));
        posAndTypeValueMap.put("Interjektion", new PosAndType(LexinfoOnt.interjection, LexinfoOnt.Interjection));

        posAndTypeValueMap.put("Phrase", new PosAndType(LexinfoOnt.phraseologicalUnit, LemonOnt.Phrase));
        posAndTypeValueMap.put("Idiom", new PosAndType(LexinfoOnt.idiom, LemonOnt.Phrase));

        posAndTypeValueMap.put("Wortverbindung", new PosAndType(null, LemonOnt.Phrase));
        posAndTypeValueMap.put("Redewendung", new PosAndType(null, LemonOnt.Phrase));
        posAndTypeValueMap.put("Numerale", new PosAndType(LexinfoOnt.numeral, LexinfoOnt.Numeral));


        posAndTypeValueMap.put("Toponym", new PosAndType(LexinfoOnt.noun, LemonOnt.LexicalEntry));
        posAndTypeValueMap.put("Grußformel", new PosAndType(null, LemonOnt.LexicalEntry));

        // Fokuspartikel
        // Interrogativadverb
        // Interrogativpronomen
        //
        // Reflexivpronomen
        // Sprichwort
        // Antwortpartikel
        // Klitikon
        /*
        Enklitikon
        Konjunktionaladverb
        Gradpartikel
        Konjunktion
        Ortsnamengrundwort
        Subjunktion
        Zahlklassifikator
        Geflügeltes Wort
        Onomatopoetikum

        Gebundenes Lexem
         */

    }

    public WiktionaryDataHandler(String lang) {
        super(lang);
    }

    public static boolean isValidPOS(String pos) {
        return posAndTypeValueMap.containsKey(pos);
    }

    public void addExtraPartOfSpeech(String pos) {
        PosAndType pat = posAndTypeValueMap.get(pos);
        if (null == pat) log.debug("Unknown Part Of Speech value {} --in-- {}", pos, this.currentLexEntry());
        if (null != typeResource(pat))
            aBox.add(aBox.createStatement(currentLexEntry, RDF.type, typeResource(pat)));
        if (null != posResource(pat))
            aBox.add(currentLexEntry, LexinfoOnt.partOfSpeech, posResource(pat));
    }

    public void addExtraInformation(String info) {
        switch (info.trim()) {
            case "n":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "m":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                break;
            case "f":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "mf":
            case "fm":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                break;
            case "nm":
            case "mn":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.masculine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "fn":
            case "nf":
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.feminine));
                aBox.add(aBox.createStatement(currentLexEntry, LexinfoOnt.gender, LexinfoOnt.neuter));
                break;
            case "indekl.":
            case "indeklinabel":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasInflectionType, OliaOnt.Uninflected));
                break;
            case "Modalverb mit Infinitiv":
                aBox.add(aBox.createStatement(currentLexEntry, RDF.type, OliaOnt.ModalVerb));
                break;
            case "Vollverb":
                aBox.add(aBox.createStatement(currentLexEntry, RDF.type, OliaOnt.MainVerb));
                break;
            case "trans.":
            case "transitiv":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasValency, OliaOnt.Transitive));
                break;
            case"intrans.":
            case "intransitiv":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasValency, OliaOnt.Intransitive));
                break;
            case "trennbar":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasSeparability, OliaOnt.Separable));
                break;
            case "untrennbar":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasSeparability, OliaOnt.NonSeparable));
                break;
            case "refl.":
            case "reflexiv":
                aBox.add(aBox.createStatement(currentLexEntry, OliaOnt.hasVoice, OliaOnt.ReflexiveVoice));
                break;
            case "unregelmäßig":
            case "unreg.":
            case "regelmäßig":
            case "Onomatopoetikum":
            case "Zahlklassifikator":
            case "adjektivische Deklination":
            case "adjektivische":
            case "Deklination":
            case "ohne Artikel":
                break;
            default:
                log.debug("Unknown additional information {} --in-- {}", info, this.currentLexEntry());
            break;

        }
    }
}
