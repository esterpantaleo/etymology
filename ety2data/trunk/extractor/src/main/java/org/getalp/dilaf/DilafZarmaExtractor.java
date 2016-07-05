package org.getalp.dilaf;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import com.hp.hpl.jena.rdf.model.Resource;

public class DilafZarmaExtractor extends DilafExtractor {

	public DilafZarmaExtractor(DilafLemonDataHandler wdh) {
		super(wdh);
	}

	protected void importArticle(XMLStreamReader2 xmlr) throws XMLStreamException {
		String lemma = null, senseNumber = null, pronounciation = null, partOfSpeech = null;
		Resource lexicalSense = null;
		while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.isStartElement() && xmlr.getLocalName().equals("sanniize")) { // entrée
            	int sni = xmlr.getAttributeIndex(null, "lamba");
            	senseNumber = (sni == -1) ? "1" : xmlr.getAttributeValue(sni);
            	lemma = xmlr.getElementText();
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("ciiyaŋ")) { // transcription phonétique
            	pronounciation = xmlr.getElementText();
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("kanandi")) { // part of speech
            	partOfSpeech = xmlr.getElementText();
            	lexicalSense = wdh.registerNewLexicalSense(lemma, partOfSpeech, pronounciation, senseNumber);
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("bareyaŋ")) { // French translation
            	String translations = xmlr.getElementText();
            	wdh.registerTranslations(lexicalSense, translations);
            } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("feeriji")) { // Definition
            	String def = xmlr.getElementText();
            	wdh.registerDefinition(lexicalSense, def, "dje");
            } else if (xmlr.isEndElement() && xmlr.getLocalName().equals("article")) {
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
		return "article";
	}


}
