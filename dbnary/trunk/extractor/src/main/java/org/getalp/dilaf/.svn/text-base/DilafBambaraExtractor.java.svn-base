package org.getalp.dilaf;

import com.hp.hpl.jena.rdf.model.Resource;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamException;

import static org.getalp.dbnary.DBnaryOnt.usage;

public class DilafBambaraExtractor extends DilafExtractor {

	public DilafBambaraExtractor(DilafLemonDataHandler wdh) {
		super(wdh);
	}

	@Override
	protected void importArticle(XMLStreamReader2 xmlr) throws XMLStreamException {
        String lexId = xmlr.getAttributeValue(null, "id");
		String lemma = null, senseNumber = null, pronounciation = null, partOfSpeech = null;
		Resource lexicalEntry = null;
        // <!ELEMENT item (forme, forme_tons*, phon*, morphologie?, (cf|(variante, variante_tons*))*, compo_ba?,compo_fr*, cat?, syntaxe?, bloc*)>
		while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement() && xmlr.getLocalName().equals("forme")) { // entrée
                // attributs: usage, non_usage, orthographe, emplois
            	lemma = xmlr.getElementText();
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("forme_tons")) { // transcription phonétique
                // ignore for now
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("phon")) { // transcription phonétique
            	// Sometimes a pronunciation, sometime a french explanation on the pronunciation, sometimes a codde ("gw", "fl", etc...)
                // Ignore for now
                // pronounciation = xmlr.getElementText();
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("cat")) { // part of speech
            	partOfSpeech = xmlr.getElementText();
                lexicalEntry = wdh.registerLexicalEntry(lexId, partOfSpeech);
                wdh.setCanonicalForm(lexicalEntry, lemma);
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("bloc")) { // French translation
            	if (null == lexicalEntry) {
                    System.err.format("Null lexical Entry while processing bloc in %s\n", lexId);
                    lexicalEntry = wdh.registerLexicalEntry(lexId, "");
                }
            	extractBloc(xmlr, lexicalEntry);
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("feeriji")) { // Definition
            	String def = xmlr.getElementText();
            	// wdh.registerDefinition(lexicalSense, def);
            } else if (xmlr.isEndElement() && articleElement().equals(xmlr.getLocalName())) {
            	return;
            }
		}
    	
    	
	}

    private void extractBloc(XMLStreamReader2 xmlr, Resource lexicalEntry) throws XMLStreamException {
        while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement() && xmlr.getLocalName().equals("sens")) {
                extractMeaning(xmlr, lexicalEntry);
            } else if (xmlr.isEndElement() && "bloc".equals(xmlr.getLocalName())) {
                return;
            }
        }
    }

    private void extractMeaning(XMLStreamReader2 xmlr, Resource lexicalEntry) throws XMLStreamException {
        String senseId = xmlr.getAttributeValue(null, "id");
        String terme = xmlr.getAttributeValue(null, "terme");
        String usage = xmlr.getAttributeValue(null, "usage");
        String nonUsage = xmlr.getAttributeValue(null, "non_usage");
        String status = xmlr.getAttributeValue(null, "status");
        String emploi = xmlr.getAttributeValue(null, "emploi");
        Resource sense = wdh.registerLexicalSense(lexicalEntry, senseId, terme, usage, nonUsage,status, emploi);
        while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement()) {
                if (xmlr.getLocalName().equals("francais")) {
                    wdh.registerDefinition(sense, xmlr.getElementText(), "fr");
                } else if (xmlr.getLocalName().equals("cf")) {
                    // TODO: handle cf
                } else if (xmlr.getLocalName().equals("exemple")) {
                    processExample(xmlr, sense);
                } else if (xmlr.getLocalName().equals("expression")) {
                    processExpression(xmlr, sense);
                } else if (xmlr.getLocalName().equals("proverbe")) {
                    processExample(xmlr, sense);
                } else if (xmlr.getLocalName().equals("dicton")) {
                    processExample(xmlr, sense);
                } else if (xmlr.getLocalName().equals("syn")) {
                    // TODO: handle cf
                } else if (xmlr.getLocalName().equals("ant")) {
                    // TODO: handle cf
                }
            } else if (xmlr.isEndElement() && "sens".equals(xmlr.getLocalName())) {
                return;
            }
        }
    }

    private void processExpression(XMLStreamReader2 xmlr, Resource sense) throws XMLStreamException {
        String elementType = xmlr.getLocalName();
        String forme = null, formeTons = null, francais = null;
        String expId = xmlr.getAttributeValue(null, "id");
        //     <!ELEMENT expression (forme, forme_tons*, francais?, exemple*)>
        while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement()) {
                if (xmlr.getLocalName().equals("forme")) {
                    forme = xmlr.getElementText();
                } else if (xmlr.getLocalName().equals("forme_tons")) {
                    formeTons = xmlr.getElementText();
                } else if (xmlr.getLocalName().equals("francais")) {
                    francais = xmlr.getElementText();
                } else if (xmlr.getLocalName().equals("exemple")) {
                    // Create the new lexical entry + its sense and attach the example to the sense
                    Resource lexicalEntry = wdh.registerLexicalEntry(expId, elementType);
                    Resource expSense = wdh.registerLexicalSense(lexicalEntry, expId + "__ws", forme, null, null, null, null);
                    processExample(xmlr, expSense);
                }
            } else if (xmlr.isEndElement() && elementType.equals(xmlr.getLocalName())) {
                // TODO: link expression lexical entry to the correct root word sense
                return;
            }
        }
    }

    private void processExample(XMLStreamReader2 xmlr, Resource sense) throws XMLStreamException {
        String exampleType = xmlr.getLocalName();
        String ba = null, baTons = null, fr = null;
        String usage = xmlr.getAttributeValue(null, "usage");
        while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement()) {
                if (xmlr.getLocalName().equals("ba")) {
                    ba = xmlr.getElementText();
                } else if (xmlr.getLocalName().equals("ba_tons")) {
                    baTons = xmlr.getElementText();
                } else if (xmlr.getLocalName().equals("fr")) {
                    fr = xmlr.getElementText();
                }
            } else if (xmlr.isEndElement() && exampleType.equals(xmlr.getLocalName())) {
                usage = (null == usage) ? ("(" + exampleType + ")") : "(" + exampleType + ") " + usage;
                wdh.registerExample(sense, ba, baTons, fr, usage);
                return;
            }
        }
    }

    @Override
    protected String rootElement() {
        return "dilaf";
    }

    @Override
    protected String articleElement() {
        return "item";
    }

}
