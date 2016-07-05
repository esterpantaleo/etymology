package org.getalp.dilaf;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;

/**
 * Created by serasset on 06/04/16.
 */
public abstract class DilafExtractor {
	protected XMLInputFactory2 xmlif;
    protected DilafLemonDataHandler wdh;

    public DilafExtractor(DilafLemonDataHandler wdh) {
        this.wdh = wdh;
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

	public static void main(String args[]) throws DilafExtractorException {
		String lang = args[0];
        DilafLemonDataHandler wdh = new DilafLemonDataHandler(lang);
        DilafExtractor e = createExtractorForLanguage(lang, wdh);
		e.importDilafXmlFile(new File(args[1]));
		wdh.dump(System.out, "TURTLE");
	}

	private static DilafExtractor createExtractorForLanguage(String lang, DilafLemonDataHandler wdh) {
        if ("bam".equals(lang))
            return new DilafBambaraExtractor(wdh);
        else if ("dje".equals(lang))
            return new DilafZarmaExtractor(wdh);
        else {
            System.err.format("No extractor for language %s", lang);
            System.exit(-1);
        }
        return null;
    }

	public void importDilafXmlFile(File dilafFile) throws DilafExtractorException {

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
	                if (xmlr.isStartElement() && rootElement().equals(xmlr.getLocalName())) {
	                    // create lemon lexicon instance and link all entries to it.
	                	// Get source, target, creation date and version number ?

	                } else if (xmlr.isStartElement() && articleElement().equals(xmlr.getLocalName())) {
	                    importArticle(xmlr);
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
	        System.err.println(" Parsing Time = " + (endtime - starttime) + "; " + nbArticles + " pages parsed.");
	    }

	protected abstract void importArticle(XMLStreamReader2 xmlr) throws XMLStreamException;

    protected abstract String rootElement();
    protected abstract String articleElement();

}
