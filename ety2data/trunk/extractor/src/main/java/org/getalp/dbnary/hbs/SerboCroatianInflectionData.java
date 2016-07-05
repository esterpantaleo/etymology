package org.getalp.dbnary.hbs;


import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.OliaOnt;
import org.getalp.dbnary.PropertyObjectPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class SerboCroatianInflectionData {
    private Logger log = LoggerFactory.getLogger(SerboCroatianInflectionData.class);

    public enum Genre {MASCULIN,FEMININE,NEUTER,NOTHING}
    public enum Cas {NOMINATIF,GENITIF,DATIF,ACCUSATIF,VOCATIF,INSTRUMENTAL,LOCATIVE,NOTHING}
    public enum GNumber {SINGULAR,PLURAL,NOTHING}
    public enum Animate {ANIMATE,INANIMATE,NOTHING}
    public enum Degree {POSITIV,KOMPARTIV,SUPERLATIVE,NOTHING}
    public enum Tense {PREZENT,BUDUCNOST,PROSLOST,KONDICIONAL1,KONDICIONAL2,IMPERATIV,NOTHING}
    public enum SubTense {PREZENT,FUTUR1,FUTUR2,PERFEKT,PLUSKVAMPERFEKT,AORIST,IMPERFEKT,NOTHING}
    public enum Person {FIRST,SECOND,THIRD,NOTHING}
    public enum Mode {NEODREDENI,ODREDENI,NOTHING}

    public Genre genre;
    public Cas cas;
    public GNumber number;
    public Animate anim;
    public Degree deg;
    public Tense tense;
    public SubTense subTense;
    public Person person;
    public Mode mode;

    public SerboCroatianInflectionData(){
        init();
    }

    public void init(){
        genre = Genre.NOTHING;
        cas = Cas.NOTHING;
        number = GNumber.NOTHING;
        anim = Animate.NOTHING;
        deg = Degree.NOTHING;
        tense = Tense.NOTHING;
        subTense = SubTense.NOTHING;
        person = Person.NOTHING;
        mode = Mode.NOTHING;
    }

    public HashSet<PropertyObjectPair> toPropertyObjectMap() {
        HashSet<PropertyObjectPair> inflections = new HashSet<>();

        switch(cas){
            case NOMINATIF:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.Nominative));
                break;
            case GENITIF:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.GenitiveCase));
                break;
            case ACCUSATIF:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.Accusative));
                break;
            case DATIF:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.DativeCase));
                break;
            case VOCATIF:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.VocativeCase));
                break;
            case INSTRUMENTAL:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.InstrumentalCase));
                break;
            case LOCATIVE:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasCase, OliaOnt.LocativeCase));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for case", cas);
                break;
        }
        switch(genre){
            case MASCULIN:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasGender, OliaOnt.Masculine));
                break;
            case FEMININE:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasGender, OliaOnt.Feminine));
                break;
            case NEUTER:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasGender, OliaOnt.Neuter));
                break;
            case NOTHING:
                break;
            default :
                log.debug("Unexpected value {} for genre", genre);
                break;
        }
        switch(number){
            case SINGULAR:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasNumber, OliaOnt.Singular));
                break;
            case PLURAL:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasNumber, OliaOnt.Plural));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for number", number);
                break;
        }
        switch(anim){
            case ANIMATE:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.animacy, OliaOnt.Animate));
                break;
            case INANIMATE:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.animacy, OliaOnt.Inanimate));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for animacy", anim);
                break;
        }
        switch (tense){
            case PREZENT:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.present));
                break;
            case BUDUCNOST:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.future));
                break;
            case PROSLOST:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.past));
                break;
            case KONDICIONAL1:
            case KONDICIONAL2:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.conditional));
                break;
            case IMPERATIV:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.imperative));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for tense", tense);
                break;
        }
        switch(subTense){
            case PREZENT:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.present));
            case FUTUR1:
            case FUTUR2:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.future));
                break;
            case PERFEKT:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.perfective));
                break;
            case PLUSKVAMPERFEKT:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.perfective));
                break;
            case AORIST: // not found

                break;
            case IMPERFEKT:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.imperfect));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for person", person);
                break;
        }
        switch(person){
            case FIRST:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.firstPerson));
                break;
            case SECOND:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.secondPerson));
                break;
            case THIRD:
                inflections.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.thirdPerson));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for person", person);
                break;
        }
        switch(deg){
            case POSITIV:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasDegree, OliaOnt.Positive));
                break;
            case KOMPARTIV:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasDegree, OliaOnt.Comparative));
                break;
            case SUPERLATIVE:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasDegree, OliaOnt.Superlative));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for person", person);
                break;
        }
        switch (mode){
            case NEODREDENI:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasDefiniteness, LexinfoOnt.indefinite));
                break;
            case ODREDENI:
                inflections.add(PropertyObjectPair.get(OliaOnt.hasDefiniteness, LexinfoOnt.definite));
                break;
            case NOTHING:
                break;
            default:
                log.debug("Unexpected value {} for mode", mode);
                break;
        }
        return inflections;
    }
}
