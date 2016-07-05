package org.getalp.dilaf;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import com.hp.hpl.jena.rdf.model.Resource;

public class DilafZarmaExtractor {

	private XMLInputFactory2 xmlif;
	
	public DilafZarmaExtractor() {
	     try {
	            xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
	            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
	            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
	            xmlif.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);
	        } catch (Exception ex) {
	            System.err.println("Cannot intialize XMLInputFactory while creating DilafZarmaExtractor.");
	            throw new RuntimeException("Cannot initialize XMLInputFactory", ex);
	        }
	}
	
	public void importDilafZarmaXmlFile(File dilafFile, DilafLemonDataHandler wdh) throws DilafExtractorException {

	        // create new XMLStreamReader

	        long starttime = System.currentTimeMillis();
	        int nbArticles = 0;

	        XMLStreamReader2 xmlr = null;
	        try {
	            // pass the file name. all relative entity references will be
	            // resolved against this as base URI.
	            xmlr = xmlif.createXMLStreamReader(dilafFile);

	            // check if there are more events in the input stream
	            while (xmlr.hasNext()) {
	                xmlr.next();
	                if (xmlr.isStartElement() && xmlr.getLocalName().equals("dilaf")) {
	                    // create lemon lexicon instance and link all entries to it.
	                	// Get source, target, creation date and version number ?
	                    
	                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("article")) {
	                    importArticle(xmlr, wdh);
	                    nbArticles++;
	                }
	            }
	        } catch (XMLStreamException ex) {
	            System.out.println(ex.getMessage());

	            if (ex.getNestedException() != null) {
	                ex.getNestedException().printStackTrace();
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (xmlr != null)
	                    xmlr.close();
	            } catch (XMLStreamException ex) {
	                ex.printStackTrace();
	            }
	        }

	        long endtime = System.currentTimeMillis();
	        System.out.println(" Parsing Time = " + (endtime - starttime) + "; " + nbArticles + " pages parsed.");
	    }

	private void importArticle(XMLStreamReader2 xmlr, DilafLemonDataHandler wdh) throws XMLStreamException {
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
            	wdh.registerDefinition(lexicalSense, def);
            } else if (xmlr.isEndElement() && xmlr.getLocalName().equals("article")) {
            	return;
            }
		}
    	
    	
	}
	
	public static void main(String args[]) throws DilafExtractorException {
		DilafZarmaExtractor e = new DilafZarmaExtractor();
		DilafLemonDataHandler wdh = new DilafLemonDataHandler("dje");
		e.importDilafZarmaXmlFile(new File(args[0]), wdh);
		wdh.dump(System.out, "TURTLE");
	}
}
