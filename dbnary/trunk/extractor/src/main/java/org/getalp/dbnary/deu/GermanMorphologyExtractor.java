package org.getalp.dbnary.deu;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.PropertyObjectPair;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.WikiText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import static org.getalp.dbnary.deu.GermanInflectionData.Degree.COMPARATIVE;
import static org.getalp.dbnary.deu.GermanInflectionData.Degree.POSITIVE;
import static org.getalp.dbnary.deu.GermanInflectionData.Degree.SUPERLATIVE;

/**
 * Created by serasset on 16/02/16.
 */
public class GermanMorphologyExtractor {

    private WikiText wikiText;
    private final WiktionaryIndex wi;
    private final IWiktionaryDataHandler wdh;
    protected final GermanDeklinationExtractorWikiModel deklinationExtractor;
    protected final GermanKonjugationExtractorWikiModel konjugationExtractor;
    protected final GermanSubstantiveDeklinationExtractorWikiModel substantivDeklinationExtractor;

    private static HashSet<String> ignoredTemplates;

    //	private boolean reflexiv=false;
    static {
        ignoredTemplates = new HashSet<String>();
        ignoredTemplates.add("Absatz");
        ignoredTemplates.add("Hebr");
        ignoredTemplates.add("Internetquelle");
        ignoredTemplates.add("Lautschrift");
        ignoredTemplates.add("Lit-Duden: Rechtschreibung");
        ignoredTemplates.add("Lit-Stielau: Nataler Deutsch");
        ignoredTemplates.add("Ref-Grimm");
        ignoredTemplates.add("Ref-Kruenitz");
        ignoredTemplates.add("Ref-Länderverzeichnis");
        ignoredTemplates.add("Ref-OWID");
        ignoredTemplates.add("Schachbrett");
        ignoredTemplates.add("Wort des Jahres");
    }

    private Logger log = LoggerFactory.getLogger(GermanMorphologyExtractor.class);

    public GermanMorphologyExtractor(IWiktionaryDataHandler wdh, WiktionaryIndex wi) {
        this.wdh = wdh;
        this.wi = wi;
        deklinationExtractor = new GermanDeklinationExtractorWikiModel(wdh, wi, new Locale("de"), "/${Bild}", "/${Titel}");
        konjugationExtractor = new GermanKonjugationExtractorWikiModel(wdh, wi, new Locale("de"), "/${Bild}", "/${Titel}");
        substantivDeklinationExtractor = new GermanSubstantiveDeklinationExtractorWikiModel(wdh, wi, new Locale("de"), "/${Bild}", "/${Titel}");
    }

    public void extractMorphologicalData(String wikiSourceText, String pageName) {
        this.wikiText = new WikiText(wikiSourceText);

        for (WikiText.Token t : wikiText.templates()) {
            WikiText.Template wt = (WikiText.Template) t;
            String templateName = wt.getName().trim();
            if (templateName.startsWith("Vorlage:")) templateName = templateName.substring(8);
            if (ignoredTemplates.contains(templateName)) continue;

            if ("Deutsch Substantiv Übersicht".equals(templateName) ||
                    "Deutsch Toponym Übersicht".equals(templateName) ||
                    "Deutsch Nachname Übersicht".equals(templateName)) {
                // TODO: extract the data from generated table, so that it is less fragile.
                extractFormsWithModel(wt.toString(), pageName, substantivDeklinationExtractor);
            } else if ("Deutsch Adjektiv Übersicht".equals(templateName)) {
                // DONE fetch and expand deklination page and parse all tables.
                // TODO: check if such template may be used on substantivs
                if (wdh.currentWiktionaryPos().equals("Substantiv")) log.debug("Adjectiv ubersicht in noun : {} ", wdh.currentLexEntry());
                // DONE: Extract comparative/Superlative from parametermap before fetching the full flexion page.
                if (extractAdjectiveDegree(wt.parseArgs())) {
                    String deklinationPageName = pageName + " (Deklination)";
                    extractFormsPageWithModel(deklinationPageName, pageName, deklinationExtractor);
                }
            } else if ("Deutsch Verb Übersicht".equals(templateName) || ("Verb-Tabelle".equals(templateName))) {
                // DONE get the link to the Konjugationnen page and extract data from the expanded tables
                String conjugationPage = pageName + " (Konjugation)";
                extractFormsPageWithModel(conjugationPage, pageName, konjugationExtractor);
            } else if (templateName.equals("Deutsch adjektivische Deklination")) {
                extractFormsWithModel(wt.toString(), pageName, substantivDeklinationExtractor);
            } else if (templateName.startsWith("Deutsch adjektivische Deklination ")) {
                // Will expand to Deutsch adjektivische Deklination that will be caught afterwards.
                extractFormsWithModel(wt.toString(), pageName, substantivDeklinationExtractor);
            } else {
                log.debug("Morphology Extraction: Caught template call: {} --in-- {}", templateName, pageName);
                // Should I expand every other templates ?
            }
        }
    }

    private boolean extractAdjectiveDegree(Map<String, String> parameterMap) {
        boolean noOtherForms = false;

        for (Map.Entry<String, String> e : parameterMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            GermanInflectionData inflection = new GermanInflectionData();

            if (key.contains("Bild") || key.matches("\\d+")) continue;
            if (key.equalsIgnoreCase("keine weiteren Formen")) {
                noOtherForms = true;
                continue;
            }

            if (key.contains("Positiv")) {
                inflection.degree = POSITIVE;
            } else if (key.contains("Komparativ")) {
                inflection.degree = COMPARATIVE;
            } else if (key.contains("Superlativ")) {
                inflection.degree = SUPERLATIVE;
            } else {
                log.debug("no known degree, neither singular in Substantiv Ubersicht: {} | {}", key, wdh.currentLexEntry());
            }

            value = value.replaceAll("<(?:/)?small>", "");
            for (String form : value.split("(?:<br(?: */)?>)|(?:,\\s*)")) {
                addForm(inflection.toPropertyObjectMap(), form);
            }
        }
        return noOtherForms;
    }

    private void addForm(HashSet<PropertyObjectPair> infl, String s) {
        if (s.length() == 0 || s.equals("—") || s.equals("-")) return;

        wdh.registerInflection("deu", wdh.currentWiktionaryPos(), s, wdh.currentLexEntry(), 1, infl);
    }

    private void extractFormsPageWithModel(String formsPageName, String pageName, GermanTableExtractorWikiModel model) {
        String subPageContent = wi.getTextOfPageWithRedirects(formsPageName);
        if (null == subPageContent) return;
        if(!subPageContent.contains("Deutsch")) return;

        extractFormsWithModel(subPageContent, pageName, model);
    }

    private void extractFormsWithModel(String wikiCode, String pageName, GermanTableExtractorWikiModel model) {
        model.setPageName(pageName);
        model.parseTables(wikiCode);
    }

}
