package org.getalp.dbnary.deu;

import info.bliki.wiki.filter.PlainTextConverter;
import org.getalp.dbnary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import static org.getalp.dbnary.deu.GermanInflectionData.Cas.*;
import static org.getalp.dbnary.deu.GermanInflectionData.Degree.*;
import static org.getalp.dbnary.deu.GermanInflectionData.GNumber.*;
import static org.getalp.dbnary.deu.GermanInflectionData.InflectionType.*;


public class GermanMorphologyExtractorWikiModel extends GermanDBnaryWikiModel {

	private final static String germanRegularVerbString="Deutsch Verb regelmäßig";
	private final static String germanNonRegularVerbString=" unregelmäßig";

	private  boolean isPhrasal=false;

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

	private Logger log = LoggerFactory.getLogger(GermanMorphologyExtractorWikiModel.class);

	private IWiktionaryDataHandler wdh;
	protected GermanDeklinationExtractorWikiModel deklinationExtractor;
	protected GermanKonjugationExtractorWikiModel konjugationExtractor;

	public GermanMorphologyExtractorWikiModel(IWiktionaryDataHandler wdh, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
		deklinationExtractor = new GermanDeklinationExtractorWikiModel(wdh, wi, new Locale("de"), "/${Bild}", "/${Titel}");
		konjugationExtractor = new GermanKonjugationExtractorWikiModel(wdh, wi, new Locale("de"), "/${Bild}", "/${Titel}");
		this.wdh=wdh;
	}

	private int isOtherForm=0;

	public void parseInflectedForms(String page, String normalizedPOS){
		System.out.println("page : "+page);
	}
	

	public void parseOtherForm(String page,String originalPos) {
		// Render the definition to plain text, while ignoring the example template
        // this.setPageName(page);
        try {
            render(new PlainTextConverter(), page).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void substituteTemplateCall(String templateName,
									   Map<String, String> parameterMap, Appendable writer)
			throws IOException {
        try {
            if (templateName.startsWith("Vorlage:")) templateName = templateName.substring(8);
            if (ignoredTemplates.contains(templateName)) {
                ; // NOP
            } else if ("Deutsch Substantiv Übersicht".equals(templateName)) {
                // TODO: extract the data from generated table, so that it is less fragile.
                extractSubstantiveForms(parameterMap);
            } else if ("Deutsch Toponym Übersicht".equals(templateName)) {
                // TODO: how do I encode toponyms in DBnary ?
                // TODO: extract such toponym morphology
                log.debug("Morphology Extraction: Toponym morphology not yet handled --in-- {}", this.getPageName());
            } else if ("Deutsch Nachname Übersicht".equals(templateName)) {
                // ?
                if (parameterMap.containsKey("Genus"))
                    log.debug("Morphology Extraction: Nachname with Genus --in-- {}", this.getPageName());
                log.debug("Morphology Extraction: Nachname morphology not yet handled --in-- {}", this.getPageName());
            } else if ("Deutsch Adjektiv Übersicht".equals(templateName)) {
                // DONE fetch and expand deklination page and parse all tables.
                // TODO: check if such template may be used on substantivs
                if (wdh.currentWiktionaryPos().equals("Substantiv")) log.debug("Adjectiv ubersicht in noun : {} ", wdh.currentLexEntry());
                // DONE: Extract comparative/Superlative from parametermap before fetching the full flexion page.
                if (extractAdjectiveDegree(parameterMap)) {
                    String deklinationPageName = this.getPageName() + " (Deklination)";
                    extractAdjectiveForms(deklinationPageName);
                }
            } else if ("Deutsch Verb Übersicht".equals(templateName) || ("Verb-Tabelle".equals(templateName))) {
                // DONE get the link to the Konjugationnen page and extract data from the expanded tables
                String conjugationPage = this.getPageName() + " (Konjugation)";
                extractVerbForms(conjugationPage);
            } else if (templateName.equals("Deutsch adjektivische Deklination")) {
                extractSubstantiveForms(parameterMap);
            } else if (templateName.startsWith("Deutsch adjektivische Deklination ")) {
                // Will expand to Deutsch adjektivische Deklination that will be caught afterwards.
                super.substituteTemplateCall(templateName, parameterMap, writer);
            } else if (templateName.equalsIgnoreCase("flexlink")) {
                // This should be expanded to keep link titles (that are usually written forms).
                super.substituteTemplateCall(templateName, parameterMap, writer);
            } else {
                log.debug("Morphology Extraction: Caught template call: {} --in-- {}", templateName, this.getPageName());
                // Should I expand every other templates ?
                // super.substituteTemplateCall(templateName, parameterMap, writer);
            }
        } catch (RuntimeException e) {
            log.debug("Runtime Exception in {}", this.getPageName());
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

    private void extractSubstantiveForms(Map<String, String> parameterMap) {

        for (Map.Entry<String, String> e : parameterMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            GermanInflectionData inflection = new GermanInflectionData();

            // TODO: pass if key is an image or non morphological parameter
            if (key.contains("Bild") || key.matches("\\d+")) continue;
            if (key.equals("kein Singular")) {
                continue;
            }
            if (key.equals("kein Plural")) {
                continue;
            }

            if (key.equals("Genus")) {
                if (value.equals("m"))
                    wdh.registerPropertyOnLexicalEntry(OliaOnt.hasGender, OliaOnt.Masculine);
                else if (value.equals("f"))
                    wdh.registerPropertyOnLexicalEntry(OliaOnt.hasGender, OliaOnt.Feminine);
                else if (value.equals("n"))
                    wdh.registerPropertyOnLexicalEntry(OliaOnt.hasGender, OliaOnt.Neuter);
                else
                    log.debug("unknown Genus in Substantiv Ubersicht: {} | {}", value, wdh.currentLexEntry());
                continue;
            }

            if (key.contains("Singular")) {
                inflection.number = SINGULAR;
            } else if (key.contains("Plural")) {
                inflection.number = PLURAL;
            } else  {
                log.debug("no plural, neither singular in Substantiv Ubersicht: {} | {}", key, wdh.currentLexEntry());
            }

            if (key.contains("Nominativ")) {
                inflection.cas = NOMINATIF;
            } else if (key.contains("Genitiv")) {
                inflection.cas = GENITIF;
            } else if (key.contains("Dativ")) {
                inflection.cas = DATIF;
            } else if (key.contains("Akkusativ")) {
                inflection.cas = ACCUSATIF;
            } else {
                log.debug("no known case in Substantiv Ubersicht: {} | {}", key, wdh.currentLexEntry());
            }

            if (key.contains("stark")) {
                inflection.inflectionType = STRONG;
            } else if (key.contains("schwach")) {
                inflection.inflectionType = WEAK;
            } else if (key.contains("gemischt")) {
                inflection.inflectionType = MIXED;
            }

            value = value.replaceAll("<(?:/)?small>", "");
            for (String form : value.split("<br(?: */)?>")) {
                addForm(inflection.toPropertyObjectMap(), form);
            }

        }
    }

    private void extractAdjectiveForms(String deklinationPageName) {
		String deklinationPageContent = wi.getTextOfPageWithRedirects(deklinationPageName);
        if (null == deklinationPageContent) return;
		if(!deklinationPageContent.contains("Deutsch")) return;

		deklinationExtractor.setPageName(this.getPageName());
		deklinationExtractor.parseTables(deklinationPageContent);
	}


	private void extractVerbForms(String conjugationPage) {
		String konjugationPageContent = wi.getTextOfPageWithRedirects(conjugationPage);
        if (null == konjugationPageContent) return;
		if(!konjugationPageContent.contains("Deutsch")) return;

		konjugationExtractor.setPageName(this.getPageName());
		konjugationExtractor.parseTables(konjugationPageContent);
	}


	private void addForm(HashSet<PropertyObjectPair> infl, String s) {
		// TODO: check to see what this line is trimming out and do it in a better way...
		// s=s.replace("]", "").replace("[","").replaceAll(".*\\) *","").replace("(","").trim();
		if (s.length() == 0 || s.equals("—") || s.equals("-")) return;

		wdh.registerInflection("deu", wdh.currentWiktionaryPos(), s, wdh.currentLexEntry(), 1, infl);
	}

}
