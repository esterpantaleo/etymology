package org.getalp.dbnary.hbs;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SerboCroatianMorphoExtractorWikiModel  extends DbnaryWikiModel{

    private Logger log = LoggerFactory.getLogger(SerboCroatianMorphoExtractorWikiModel.class);

    IWiktionaryDataHandler wdh;

    SerboCroatianInflectionData inflectionData;

    public SerboCroatianMorphoExtractorWikiModel(IWiktionaryDataHandler wdh,WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
        super(wi, locale, imageBaseURL, linkBaseURL);
        this.wdh = wdh;
        inflectionData = new SerboCroatianInflectionData();
    }

    public void extractOtherForm(String templateCall) {
        Document doc = Jsoup.parse(expandWikiCode(templateCall));

        if (doc == null) {
            return;
        }

        Elements elts = doc.select("div");
        for (Element elt : elts) {
            Elements tables = elt.select("table");
            String context;
            if(tables != null){
                if(tables.size() == 0) {
                    context = elt.text().trim();
                }
                else{
                    context = null;
                }
                for(Element eltT : tables) {
                    parseTable(eltT, context);
                }
            }
        }

        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;

        String pageName = getPageName();
        if(dwdh.toBeRegisterFlexion.containsKey(pageName)){
            HashMap<String, String> tmp = dwdh.toBeRegisterFlexion.get(pageName);
            for(String key : tmp.keySet()){
                add(pageName, key, tmp.get(key));
                dwdh.toBeRegisterFlexion.remove(pageName);
            }
        }
    }

    public void add(String canonicalForm, String flexionForm, String flexionProperties){
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;
        if(dwdh.alreadyRegisteredFlexion.containsKey(canonicalForm)){
            ArrayList<String> tmp = dwdh.alreadyRegisteredFlexion.get(canonicalForm);
            if(!tmp.contains(flexionForm)){
                registerInflexion(canonicalForm,flexionForm,flexionProperties);
                tmp.add(flexionForm);
                dwdh.alreadyRegisteredFlexion.put(canonicalForm,tmp);
            }
        }else{
            ArrayList<String> tmp = new ArrayList<>();
            registerInflexion(canonicalForm,flexionForm,flexionProperties);
            tmp.add(flexionForm);
            dwdh.alreadyRegisteredFlexion.put(canonicalForm,tmp);
        }
    }

    protected void registerInflexion(String canonicalForm, String flexionForm, String flexionProperties) {
        WiktionaryDataHandler dwdh = (WiktionaryDataHandler) wdh;
        inflectionData.init();
        String pos = "";

        String[] tmp = flexionProperties.split(" ");
        for(String t : tmp){
            switch(t){
                case "imenice":
                    pos = "Imenica";
                    break;
                case "pridjeva":
                    pos = "Pridjev";
                    break;
                case "priloga":
                    pos = "Prilog";
                    break;
                case "glagola":
                case "glagolskog":
                    pos = "Glagol";
                    break;
                case "nominativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.NOMINATIF;
                    break;
                case "genitiv":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.GENITIF;
                    break;
                case "dativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.DATIF;
                    break;
                case "akuzativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.ACCUSATIF;
                    break;
                case "vokativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.VOCATIF;
                    break;
                case "lokativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.LOCATIVE;
                    break;
                case "instrumental":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.INSTRUMENTAL;
                    break;
                case "sadašnjost":
                case "prezenta":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.PREZENT;
                    break;
                case "budućnost":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.BUDUCNOST;
                    break;
                case "prošlost":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.PROSLOST;
                    break;
                case "kondicional I":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.KONDICIONAL1;
                    break;
                case "kondicional II":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.KONDICIONAL2;
                    break;
                case "imperativa":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.IMPERATIV;
                    break;
                case "Futur I":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.FUTUR1;
                    break;
                case "Futur II":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.FUTUR2;
                    break;
                case "perfekta":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.PERFEKT;
                    break;
                case "pluskvamperfekt":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.PLUSKVAMPERFEKT;
                    break;
                case "aorista":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.AORIST;
                    break;
                case "imperfekta":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.IMPERFEKT;
                    break;
                case "jednine":
                case "jednina":
                    inflectionData.number = SerboCroatianInflectionData.GNumber.SINGULAR;
                    break;
                case "množine":
                case "množina":
                    inflectionData.number = SerboCroatianInflectionData.GNumber.PLURAL;
                    break;
                case "prvo":
                    inflectionData.person = SerboCroatianInflectionData.Person.FIRST;
                    break;
                case "drugo":
                    inflectionData.person = SerboCroatianInflectionData.Person.SECOND;
                    break;
                case "treće":
                    inflectionData.person = SerboCroatianInflectionData.Person.THIRD;
                    break;
                case "muškog":
                    inflectionData.genre = SerboCroatianInflectionData.Genre.MASCULIN;
                    break;
                case "ženskog":
                    inflectionData.genre = SerboCroatianInflectionData.Genre.FEMININE;
                    break;
                case "srednjeg":
                    inflectionData.genre = SerboCroatianInflectionData.Genre.NEUTER;
                    break;
                case "pozitiva":
                    inflectionData.deg = SerboCroatianInflectionData.Degree.POSITIV;
                    break;
                case "neodređenog":
                    inflectionData.mode = SerboCroatianInflectionData.Mode.NEODREDENI;
                    break;
                case "određenog":
                    inflectionData.mode = SerboCroatianInflectionData.Mode.ODREDENI;
                    break;
                case "komparativa":
                    inflectionData.deg = SerboCroatianInflectionData.Degree.KOMPARTIV;
                    break;
                case "superlativa":
                    inflectionData.deg = SerboCroatianInflectionData.Degree.SUPERLATIVE;
                    break;
                case "lice": // ignored here
                case "roda":
                case "vida":
                case "radnog":
                case "trpnog":
                    break;
                default:
                    log.debug("Unknown flexionProperty {} --in-- {}", t, flexionForm);

            }
        }

       dwdh.registerInflection("hbs", pos, flexionForm,
                canonicalForm, 1, inflectionData.toPropertyObjectMap());
    }

    protected void parseTable(Element table, String contextDiv) {
        Elements rows = table.select("tr");
        ArrayList<ArrayList<String>> contextTop = new ArrayList<>();
        ArrayList<String> contextLeft = new ArrayList<>();
        int rawspan = 1;
        int rawCurr = 0;
        int curr = 1;
        int size;
        boolean hasContextTop = false;
        String prevContext = null;
        for(Element cells : rows){
            if(isHeader(cells)){
                ArrayList<String> ctxtTmp = new ArrayList<>();
                for (Element cell : cells.children()) {
                    ctxtTmp.add(cell.text());
                }
                contextTop.add(ctxtTmp);
                hasContextTop = true;
            }
            else {
                if(!hasContextTop){
                    ArrayList<String> ctxtTmp = new ArrayList<>();
                    boolean allHeader = true;
                    for (Element cell : cells.children()) {
                        if(isHeader(cell))
                            ctxtTmp.add(cell.text());
                        else
                            allHeader = false;
                        if(!allHeader)
                            break;
                    }
                    if(allHeader) {
                        contextTop = new ArrayList<>();
                        contextTop.add(ctxtTmp);
                    }
                }
                boolean toogleHeader = false;
                Elements cellsL = cells.children();
                size = cellsL.size();
                for (Element cell : cellsL) {
                    if(isHeader(cell)) {
                        if(!toogleHeader){
                            contextLeft = new ArrayList<>();
                            int r = getRawspan(cell);
                            if(r != 1){
                                prevContext = cell.text();
                                rawspan = r;
                            }
                            toogleHeader = true;
                        }
                        if(prevContext != null && contextLeft.size() == 0 && rawCurr != 1){
                            contextLeft.add(prevContext);
                        }
                        if(rawCurr == rawspan+1){
                            rawspan = 1;
                            rawCurr = 0;
                            prevContext = null;
                        }
                        rawCurr++;
                        contextLeft.add(cell.text());
                        size--;
                    }
                    else{
                        if(toogleHeader) {
                            curr = 0;
                            toogleHeader = false;
                        }
                        curr++;
                        checkOtherForm(cell.text(), curr, size, contextTop, contextLeft, contextDiv);
                    }
                }
            }
        }
    }

    protected void checkOtherForm(String word, int curr, int size,
                                  ArrayList<ArrayList<String>> contextTop,
                                  ArrayList<String> contextLeft,
                                  String contextDiv){
        inflectionData.init();
        addContextDiv(contextDiv);
        addContextLeft(contextLeft);
        for(ArrayList<String> contextTopCurr : contextTop){
            int sizeContextCurr = contextTopCurr.size()-1;
            if(sizeContextCurr > 0) {
                int sizeSubDiv = size / sizeContextCurr;
                int col = 1;
                int j = curr;
                while (sizeSubDiv > 0 && j > sizeSubDiv) {
                    col++;
                    j -= sizeSubDiv;
                }
                if(contextLeft.contains("neživo živo")){
                    String[] tmp = word.split(" ");
                    if(tmp.length == 2){
                        inflectionData.anim = SerboCroatianInflectionData.Animate.INANIMATE;
                        addOtherForm(tmp[0], contextTopCurr.get(0), contextTopCurr.get(col));
                        inflectionData.anim = SerboCroatianInflectionData.Animate.ANIMATE;
                        addOtherForm(tmp[1], contextTopCurr.get(0), contextTopCurr.get(col));
                        inflectionData.anim = SerboCroatianInflectionData.Animate.NOTHING;
                    }
                    else{
                        addOtherForm(word, contextTopCurr.get(0), contextTopCurr.get(col));
                    }
                }
                else
                    addOtherForm(word, contextTopCurr.get(0), contextTopCurr.get(col));
            }
        }
    }

    protected void addOtherForm(String word, String contextHeader, String contextTop){
        if(contextHeader.startsWith("Infinitiv")){
            return;
        }
        switch(contextHeader){
            case "jednina":
            case "Jednina":
                inflectionData.number = SerboCroatianInflectionData.GNumber.SINGULAR;
                break;
            case "množina":
            case "Množina":
                inflectionData.number = SerboCroatianInflectionData.GNumber.PLURAL;
                break;
            case "": // ignored here
            case "Broj":
            case "broj":
            case "Lice":
            case "lice":
            case "Gramatičko vrijeme / način":
            case "Osoba":
            case "osoba":
            case "Glagolski oblici":
            case "Padež":
                break;
            default:
                log.debug("Unknown contextHeader {} --in-- {}", contextHeader, wdh.currentLexEntry());
        }

        switch (contextTop){
            case "jednina":
            case "Jednina":
                inflectionData.number = SerboCroatianInflectionData.GNumber.SINGULAR;
                break;
            case "množina":
            case "Množina":
                inflectionData.number = SerboCroatianInflectionData.GNumber.PLURAL;
                break;
            case "prvo":
            case "ja":
            case "mi":
            case "1.":
                inflectionData.person = SerboCroatianInflectionData.Person.FIRST;
                break;
            case "drugo":
            case "ti":
            case "vi":
            case "2.":
                inflectionData.person = SerboCroatianInflectionData.Person.SECOND;
                break;
            case "treće":
            case "on / ona ono":
            case "on / ona / ono":
            case "oni / one ona":
            case "oni / one / ona":
            case "3.":
                inflectionData.person = SerboCroatianInflectionData.Person.THIRD;
                break;
            case "muški rod":
                inflectionData.genre = SerboCroatianInflectionData.Genre.MASCULIN;
                break;
            case "ženski rod":
                inflectionData.genre = SerboCroatianInflectionData.Genre.FEMININE;
                break;
            case "srednji rod":
                inflectionData.genre = SerboCroatianInflectionData.Genre.NEUTER;
                break;
            default:
                log.debug("Unknown contextTop {} --in-- {}", contextTop, wdh.currentLexEntry());
        }

        if(word != null &&
                !word.equals(("")) &&
                !word.contains("1   Standardni hrvatski zapis;") &&
                !word.contains("2   Za muški rod; u slučaju vršitelja radnje")){
            word = word.replaceAll("\\d","");
            wdh.registerInflection("hbs", wdh.currentWiktionaryPos(), word,
                    wdh.currentLexEntry(), 1, inflectionData.toPropertyObjectMap());
        }
    }

    protected void addContextDiv(String contextDiv){
        if(contextDiv == null){
            return;
        }

        switch (contextDiv){
            case "oblici pozitiva, neodređeni vid":
                inflectionData.deg = SerboCroatianInflectionData.Degree.POSITIV;
                inflectionData.mode = SerboCroatianInflectionData.Mode.NEODREDENI;
                break;
            case "oblici pozitiva, određeni vid":
                inflectionData.deg = SerboCroatianInflectionData.Degree.POSITIV;
                inflectionData.mode = SerboCroatianInflectionData.Mode.ODREDENI;
                break;
            case "oblici komparativa":
                inflectionData.deg = SerboCroatianInflectionData.Degree.KOMPARTIV;
                break;
            case "oblici superlativa":
                inflectionData.deg = SerboCroatianInflectionData.Degree.SUPERLATIVE;
                break;
            default:
                log.debug("Unknown contextDiv {} --in-- {}", contextDiv, wdh.currentLexEntry());
        }
    }

    protected void addContextLeft(ArrayList<String> contextLeft){
        for(String ctxtLeft : contextLeft){
            if(ctxtLeft.startsWith("Glagolski")){
                continue;
            }
            switch(ctxtLeft){
                case "nominativ":
                case "Nominativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.NOMINATIF;
                    break;
                case "genitiv":
                case "Genitiv":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.GENITIF;
                    break;
                case "dativ":
                case "Dativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.DATIF;
                    break;
                case "akuzativ":
                case "Akuzativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.ACCUSATIF;
                    break;
                case "vokativ":
                case "Vokativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.VOCATIF;
                    break;
                case "lokativ":
                case "Lokativ":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.LOCATIVE;
                    break;
                case "instrumental":
                case "Instrumental":
                    inflectionData.cas = SerboCroatianInflectionData.Cas.INSTRUMENTAL;
                    break;
                case "Sadašnjost":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.PREZENT;
                    break;
                case "Budućnost":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.BUDUCNOST;
                    break;
                case "Prošlost":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.PROSLOST;
                    break;
                case "Kondicional I":
                case "Kondicional I.":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.KONDICIONAL1;
                    break;
                case "Kondicional II":
                case "Kondicional II.":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.KONDICIONAL2;
                    break;
                case "Imperativ":
                    inflectionData.tense = SerboCroatianInflectionData.Tense.IMPERATIV;
                    break;
                case "Prezent": // first or second
                    if(contextLeft.size() == 2){
                        inflectionData.subTense = SerboCroatianInflectionData.SubTense.PREZENT;
                    }
                    else{
                        inflectionData.tense = SerboCroatianInflectionData.Tense.PREZENT;
                    }
                    break;
                case "Futur I":
                case "Futur I.":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.FUTUR1;
                    break;
                case "Futur II":
                case "Futur II.":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.FUTUR2;
                    break;
                case "Perfekt":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.PERFEKT;
                    break;
                case "Pluskvamperfekt":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.PLUSKVAMPERFEKT;
                    break;
                case "Aorist":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.AORIST;
                    break;
                case "Imperfekt":
                    inflectionData.subTense = SerboCroatianInflectionData.SubTense.IMPERFEKT;
                    break;
                case "neživo živo": // ignored here
                    break;
                default:
                    log.debug("Unknown contextLeft {} --in-- {}", ctxtLeft, wdh.currentLexEntry());
            }
        }
    }

    protected boolean isHeader(Element cell){
        return !cell.attr("bgcolor").trim().equals("") || cell.attr("style").startsWith("background");
    }

    protected int getRawspan(Element cell){
        String rspan = cell.attr("rowspan");
        if(rspan != null && !rspan.equals("")){
            return Integer.parseInt(rspan);
        }
        else return 1;
    }
}
