package org.getalp.dbnary.fra;

import java.util.Locale;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.*;

import org.getalp.dbnary.IWiktionaryDataHandler;

import com.hp.hpl.jena.rdf.model.Literal;

import org.w3c.dom.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jakse
 */

public class FrenchExtractorWikiModel extends DbnaryWikiModel {
	private static Logger log = LoggerFactory.getLogger(FrenchExtractorWikiModel.class);

    private IWiktionaryDataHandler delegate;

	public static final Literal trueLiteral = DbnaryModel.tBox.createTypedLiteral(true);

// 	public static final Property extractedFromConjTable       = DbnaryModel.tBox.createProperty(DBnaryOnt.getURI() + "extractedFromConjTable");
// 	public static final Property extractedFromFrenchSentence  = DbnaryModel.tBox.createProperty(DBnaryOnt.getURI() + "extractedFromFrenchSentence");
// 	public static final Property extractedFromInflectionTable = DbnaryModel.tBox.createProperty(DBnaryOnt.getURI() + "extractedFromInflectionTable");

	private static Pattern frAccordPattern = Pattern.compile("^\\{\\{(?:fr-accord|fr-rég)");

	public FrenchExtractorWikiModel(IWiktionaryDataHandler wdh, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL);
        this.delegate = wdh;
        setPageName(wdh.currentLexEntry());
	}

	public static Element adjacentDiv (Node ele) {
		ele = ele.getNextSibling();
		while (ele != null && !ele.equals("div")) {
			ele = ele.getNextSibling();
		}

		return (Element) ele;
	}

	public static boolean hasRealPreviousSibling(Node ele) {
		do {
			ele = ele.getPreviousSibling();
		} while (ele != null && ele.getNodeType() != Node.ELEMENT_NODE);

		return ele != null;
	}

	private boolean getMoodTense(Element table, HashSet<PropertyObjectPair> infos, NodeList lines) {
		if (lines.getLength() < 1) {
			log.debug("Missing lines in the conjugation table for '" + delegate.currentLexEntry() + "'");
			return false;
		}

		String mood = null;

		String tense = lines.item(0).getTextContent().trim().toLowerCase(WiktionaryExtractor.frLocale);

		if (tense.startsWith("indicatif") || tense.startsWith("subjonctif") || tense.startsWith("conditionnel") || tense.startsWith("impératif")) {
			int sep = tense.indexOf(' ');
			mood  = tense.substring(0, sep).trim();
			tense = tense.substring(sep + 1).trim();
		}

		if (tense.startsWith("passé composé")
		 || tense.startsWith("plus-que-parfait")
		 || tense.startsWith("passé antérieur")
		 || tense.startsWith("futur antérieur")
		 || tense.startsWith("passé 1e forme")
		 || tense.startsWith("passé 1re forme")
		 || tense.startsWith("passé 2e forme")
		 || tense.startsWith("conjugaison en français")
		 || tense.startsWith("passé")) {
			return false;
		} else if (!(
		    tense.startsWith("futur simple")
		 || tense.startsWith("passé simple")
		 || tense.startsWith("présent")
		 || tense.startsWith("imparfait")
		)) {
			log.debug("Unexpected tense '" + tense + "' while parsing table for '" + delegate.currentLexEntry() + "'");
			return false;
		}

		// tense
		WiktionaryExtractor.addAtomicMorphologicalInfo(
			infos,
			tense
		);

		if (mood == null) {
			Node parent = table.getParentNode();
			while (parent != null && parent.getNodeName().toLowerCase() != "div") {
				parent = parent.getParentNode();
			}

			if (parent == null) {
				log.debug("Cannot find mood in the conjugation table for '" + delegate.currentLexEntry() + "'");
				return false;
			} else if (parent.getParentNode() != null && parent.getParentNode().getNodeName().toLowerCase() == "td") {
				parent = parent.getParentNode();
				for (int i = 0; i < 3 && parent != null; i++) { //tr, table, div
					parent = parent.getParentNode();
				}
			}

			Node title = parent;

			while (title != null && title.getNodeName().toLowerCase() != "h3") {
				title = title.getPreviousSibling();
			}

			if (title == null) {
				title = parent.getParentNode();

				while (title != null && title.getNodeName().toLowerCase() != "h3") {
					title = title.getPreviousSibling();
				}

				if (title == null) {
					log.debug("Cannot find mood title in the conjugation table for '" + delegate.currentLexEntry() + "'");
					return false;
				}
			}

			mood = title.getTextContent().trim().toLowerCase(WiktionaryExtractor.frLocale);
		}

		if (mood.indexOf("(défectif)") != -1) {
			return false;
		}

		WiktionaryExtractor.addAtomicMorphologicalInfo(
			infos,
			mood
		);

		return true;
	}

	public void getPerson(HashSet<PropertyObjectPair> infos, String person, int rowNumber, int rowCount) {
		switch (person) {
		case "j’":
		case "que j’":
		case "je":
		case "je me":
		case "je m’":
		case "que je":
		case "que je me":
		case "que je m’":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.firstPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.singular));
			break;
		case "tu":
		case "tu t’":
		case "tu te":
		case "que tu":
		case "que tu te":
		case "que tu t’":
		case "-toi":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.secondPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.singular));
			break;
		case "il":
		case "qu’il":
		case "il s’":
		case "qu’il s’":
		case "il/elle/on":
		case "il/elle/on se":
		case "il/elle/on s’":
		case "qu’il/elle/on":
		case "qu’il/elle/on se":
		case "qu’il/elle/on s’":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.thirdPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.singular));
			break;
		case "nous":
		case "nous nous":
		case "que nous":
		case "que nous nous":
		case "-nous":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.firstPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.plural));
			break;
		case "vous":
		case "vous vous":
		case "que vous":
		case "que vous vous":
		case "-vous":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.secondPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.plural));
			break;
		case "ils/elles":
		case "qu’ils/elles":
		case "ils/elles se":
		case "qu’ils/elles se":
		case "ils/elles s’":
		case "qu’ils/elles s’":
			infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.thirdPerson));
			infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.plural));
			break;
		default:
			if ((person.equals("") || person.charAt(0) == '-') && rowCount == 4) {
				// imperative
				switch (rowNumber) {
				case 1:
					infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.secondPerson));
					infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.singular));
					return;
				case 2:
					infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.firstPerson));
					infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.plural));
					return;
				case 3:
					infos.add(PropertyObjectPair.get(LexinfoOnt.person, LexinfoOnt.secondPerson));
					infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.plural));
					return;
				default:
					log.debug("BUG: unexpected row number '" + rowNumber + "' while parsing imperative table for '" + delegate.currentLexEntry() + "'");
					return;
				}
			} else {
				log.debug("Unexpected person '" + person + "' for '" + delegate.currentLexEntry() + "' (row count: " + rowCount + ")");
			}
			break;
		}
	}

	public void handleConjugationTable(NodeList tables, int tableIndex) {
		Element table = (Element) tables.item(tableIndex);
		if (table.getElementsByTagName("table").getLength() > 0) {
			// we ignore tables which contain <table>s, as they don’t contain conjugations.
 			return;
		}

		if (isImpersonnalTable(table)) {
			handleImpersonnalTableConjugation(table);
			return;
		}

		HashSet<PropertyObjectPair> infos = new HashSet<PropertyObjectPair>();

// 		infos.add(PropertyObjectPair.get(extractedFromConjTable, trueLiteral));

		NodeList lines = table.getElementsByTagName("tr");

		if (getMoodTense(table, infos, lines)) {
			for (int i = 1; i < lines.getLength(); i++) {
				Element line = (Element) lines.item(i);
				NodeList tdList = line.getElementsByTagName("td");

				if (tdList.getLength() < 2) {
					if (!line.getTextContent().trim().equals("—")) {
						log.debug("Missing cells in the conjugation table for '" + delegate.currentLexEntry() + "'");
					}
				} else {
					HashSet<PropertyObjectPair> infl = new HashSet<PropertyObjectPair>(infos);

					String form = tdList.item(1).getTextContent().replace('\u00A0', ' ').trim();
					if (form.charAt(0) == '-') {
						// "dépèche-toi"
						getPerson(infl, form, i, lines.getLength());
						form = tdList.item(0).getTextContent().replace('\u00A0', ' ').trim();
					} else {
						getPerson(infl, tdList.item(0).getTextContent().replace('\u00A0', ' ').trim(), i, lines.getLength());
					}

					if (!"—".equals(form)) {
						delegate.registerInflection(
							"fr",
							"-verb-",
							form,
							delegate.currentLexEntry(),
							0,
							infl,
							null
						);
					}
				}
			}
		}
	}

	public static boolean isImpersonnalTable(Element table) {
		Node modeTH = table.getElementsByTagName("th").item(0);

		if (modeTH != null && modeTH.getTextContent().replace('\u00A0', ' ').trim().equals("Mode")) {
			return true;
		}

		return false;
	}

	public int handleImpersonnalTableConjugation(NodeList tables) {
		for (int i = 0; i < tables.getLength(); i++) {
			Element table = (Element) tables.item(i);
			if (isImpersonnalTable(table)) {
				handleImpersonnalTableConjugation(table);
				return i;
			}
		}

		log.info("Cannot find the impersonal mood table for '" + delegate.currentLexEntry() + "'");
		return -1;
	}

	public void handleImpersonnalTableConjugation(Element impersonalMoodTable) {
		if (impersonalMoodTable == null) {
			log.error("impersonalMoodTable is null for '" + delegate.currentLexEntry() + "'");
		} else {
			NodeList interestingTDs = ((Element) (impersonalMoodTable.getElementsByTagName("tr").item(3)))
			                          .getElementsByTagName("td");

			HashSet<PropertyObjectPair> infos;

			if (interestingTDs.getLength() < 3) {
				log.error("Cannot get present and past participle of '" + delegate.currentLexEntry() + "'");
			} else {
				infos = new HashSet<PropertyObjectPair>();
				String presentParticiple = interestingTDs.item(2).getTextContent().trim();
// 				infos.add(PropertyObjectPair.get(extractedFromConjTable, trueLiteral));
				infos = new HashSet<PropertyObjectPair>();
				infos.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.participle));
				infos.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.present));
				delegate.registerInflection(
					"fr",
					"-verb-",
					presentParticiple,
					delegate.currentLexEntry(),
					0,
					infos,
					null
				);

				if (interestingTDs.getLength() < 6) {
					log.error("Cannot get past participle of '" + delegate.currentLexEntry() + "'");
				} else {
					String pastParticiple = interestingTDs.item(5).getTextContent();
					infos = new HashSet<PropertyObjectPair>();
// 					infos.add(PropertyObjectPair.get(extractedFromConjTable, trueLiteral));
					infos.add(PropertyObjectPair.get(LexinfoOnt.verbFormMood, LexinfoOnt.participle));
					infos.add(PropertyObjectPair.get(LexinfoOnt.tense, LexinfoOnt.past));
					infos.add(PropertyObjectPair.get(LexinfoOnt.gender, LexinfoOnt.masculine));
					infos.add(PropertyObjectPair.get(LexinfoOnt.number, LexinfoOnt.singular));
					delegate.registerInflection(
						"fr",
						"-verb-",
						pastParticiple,
						delegate.currentLexEntry(),
						0,
						infos,
						null
					);
				}
			}
		}
	}

	public void handleConjugationDocument(Element parent) {
		if (parent == null) {
			log.error("Cannot get the element containing the conjugation tables of '" + delegate.currentLexEntry() + "'");
		}

		NodeList tables = parent.getElementsByTagName("table");

		int impersonnalTableIndex = handleImpersonnalTableConjugation(tables);

		for (int i = 1 + impersonnalTableIndex; i < tables.getLength(); i++) {
			handleConjugationTable(tables, i);
		}
	}

	public static boolean notMatchingBrackets(String s) {
		int opened = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '{') {
				opened++;
			} else if (s.charAt(i) == '}') {
				opened--;
			}
		}
		return opened != 0;
	}

	public void parseConjugation(String conjugationTemplateCall) {
		// Render the conjugation to html, while ignoring the example template
		if (conjugationTemplateCall.indexOf("}|") != -1 && notMatchingBrackets(conjugationTemplateCall)) {
			log.warn("Suspicious '}|' in conjugation template call for '" + delegate.currentLexEntry() + "'. Surely a wikicode error. Trying to fix it. Call: '" + conjugationTemplateCall + "'");
			conjugationTemplateCall = conjugationTemplateCall.replace("}|", "|");
		}

		if (conjugationTemplateCall.indexOf("|}") != -1 && notMatchingBrackets(conjugationTemplateCall)) {
			log.warn("Suspicious '|}' in conjugation template call for '" + delegate.currentLexEntry() + "'. Surely a wikicode error. Trying to fix it. Call: '" + conjugationTemplateCall + "'");
			conjugationTemplateCall = conjugationTemplateCall.replace("|}", "|");
		}

		if (!conjugationTemplateCall.startsWith("{{fr-conj-0")) {
			Document doc = wikicodeToHtmlDOM(conjugationTemplateCall);

			if (doc == null) {
				return; // failing silently: error message already given.
			}

			handleConjugationDocument(doc.getDocumentElement());
		}
	}

	public void parseImpersonnalTableConjugation(String conjugationTemplateCall) {
		Document doc = wikicodeToHtmlDOM(conjugationTemplateCall);
		if (doc == null) {
			return; // failing silently: error message already given.
		}

		handleImpersonnalTableConjugation(doc.getDocumentElement());

	}

	private void addAtomicMorphologicalInfo(HashSet<PropertyObjectPair> properties, NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			WiktionaryExtractor.addAtomicMorphologicalInfo(
				properties,
				list.item(i).getTextContent().trim().toLowerCase(WiktionaryExtractor.frLocale)
			);
		}
	}

	private void registerInflectionFromCellChild(Node c, String word) {
		HashSet<PropertyObjectPair> properties = new HashSet<PropertyObjectPair>();
// 		properties.add(PropertyObjectPair.get(extractedFromInflectionTable, trueLiteral));

		Node cell = c;
		while (cell != null && !cell.getNodeName().toLowerCase().equals("td")) {
			cell = cell.getParentNode();
		}

		if (cell == null) {
			if (c.getParentNode().getNodeName().toLowerCase().equals("tr")) {
				// horrible but seen in wiktionary in the last version of Jully 2014
				cell = c;
				log.debug("[HORRIBLE] link is not in a TD, but in a TR element! Page: " + delegate.currentLexEntry() + ", form: " + word);
			} else {
				log.debug("Could not find the parent cell while extracting other form's template. Page: " + delegate.currentLexEntry() + ", form: " + word);
				return;
			}
		}


		Element cellParent = (Element) cell.getParentNode();
		addAtomicMorphologicalInfo(properties, cellParent.getElementsByTagName("th"));
        // TODO [UNDERSTAND] what is extracted from "b" cells ?
		addAtomicMorphologicalInfo(properties, cellParent.getElementsByTagName("b"));

		NodeList tds = cellParent.getElementsByTagName("td");

		int colNumber = -1;

		for (int j = 0; j < tds.getLength(); j++) {
			if (tds.item(j).equals(cell)) {
				colNumber = j;
				break;
			}
		}

		if (colNumber != -1) {
			Element table = (Element) cellParent.getParentNode();
			NodeList trs = table.getElementsByTagName("tr");

			if (trs.getLength() == 0) {
				log.error("BUG: no lines found in the table. Page: " + delegate.currentLexEntry() + ", form: " + word);
				return;
			}

			NodeList ths = ((Element) trs.item(0)).getElementsByTagName("th");

			if (ths.getLength() <= colNumber) {
				 log.error("BUG: not enough cols in the row of the table. Page: " + delegate.currentLexEntry() + ", form: " + word);
				 return;
			}

			Node th = ths.item(colNumber);
			String text = th.getTextContent();
			WiktionaryExtractor.addAtomicMorphologicalInfo(properties, text.trim().toLowerCase(WiktionaryExtractor.frLocale));
		}

		if (word.equals(delegate.currentLexEntry())) {
            // TODO [UNDERSTAND]: what about invariable elements, are all properties registered to the canonicalForm ?
			for (PropertyObjectPair p : properties) {
				delegate.registerPropertyOnCanonicalForm(p.getKey(), p.getValue());
			}
		} else {
			delegate.registerInflection(
				"",
				delegate.currentWiktionaryPos(),
				word,
				delegate.currentLexEntry(),
				delegate.currentDefinitionNumber(),
				properties
			);
		}
	}

	public void parseOtherForm(String templateCall) {
		Document doc = wikicodeToHtmlDOM(templateCall);

        // TODO [URGENT] What is this "fr-accord" meaning ? Supress ?
		Matcher frAccordMatcher = frAccordPattern.matcher(templateCall);

		if (doc == null) {
			return; // failing silently: error message already given.
		}

		NodeList links = doc.getElementsByTagName("a");
		Locale fr = new Locale("fr");

		for (int i = 0; i < links.getLength(); i++) {
			Node a = links.item(i);

			String word = a.getTextContent().trim();
			String wordLower = word.toLowerCase(WiktionaryExtractor.frLocale);
			Node title = a.getAttributes().getNamedItem("title");
			String t = null;

			if (title != null) {
				 t = title.getTextContent().toLowerCase(WiktionaryExtractor.frLocale);
			}

			if (t != null && !word.startsWith("Modèle:") && (t.equals(wordLower) || t.equals(wordLower + " (page inexistante)"))) {
				registerInflectionFromCellChild(a, word);
			}
		}

		links = doc.getElementsByTagName("strong");

		for (int i = 0; i < links.getLength(); i++) {
			Node a = links.item(i);
			Node className = a.getAttributes().getNamedItem("class");
			if (className != null && "selflink".equals(className.getTextContent())) {
				registerInflectionFromCellChild(a, a.getTextContent());
			}
		}
	}
}
