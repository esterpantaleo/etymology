package org.getalp.dbnary;

import java.io.File;
import java.io.StringReader;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

public class WiktionaryIndexer {

    public static final String pageTag = "page";
    public static final String titleTag = "title";
    public static final int tagSize = pageTag.length() + 3;

    public static final XMLInputFactory2 xmlif;

    static {
        try {
            xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);
        } catch (Exception ex) {
            System.err.println("Cannot intialize XMLInputFactory while classloading WiktionaryIndexer.");
            throw new RuntimeException("Cannot initialize XMLInputFactory", ex);
        }
    }

    public static void createIndex(File dumpFile, Map<String, OffsetValue> map) throws WiktionaryIndexerException {

        // create new XMLStreamReader

        long starttime = System.currentTimeMillis();
        int nbPages = 0;

        XMLStreamReader2 xmlr = null;
        try {
            // pass the file name. all relative entity references will be
            // resolved against this as base URI.
            xmlr = xmlif.createXMLStreamReader(dumpFile);

            // check if there are more events in the input stream
            long boffset = 0, eoffset = 0;
            String title = "";
            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && xmlr.getLocalName().equals(pageTag)) {
                    boffset = xmlr.getLocationInfo().getStartingCharOffset();
                    title = "";
                    eoffset = 0;
                    nbPages++;
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(titleTag)) {
                    title = xmlr.getElementText();
                } else if (xmlr.isEndElement() && xmlr.getLocalName().equals(pageTag)) {
                    eoffset = xmlr.getLocationInfo().getEndingCharOffset();
                    if (!title.equals(""))
                        map.put(title, new OffsetValue(boffset, (int)(eoffset - boffset) ));
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
        System.out.println(" Parsing Time = " + (endtime - starttime) + "; " + nbPages + " pages parsed.");
    }

    public static String getTextElementContent(String wiktionaryPageContent) {
        if (wiktionaryPageContent == null) return null;
        StringReader sr = new StringReader(wiktionaryPageContent);
        XMLStreamReader xmlr = null;
        try {
            xmlr = xmlif.createXMLStreamReader(sr);

            // check if there are more events in the input stream
            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && xmlr.getLocalName().equals("text")) {
                    return xmlr.getElementText(); 
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("redirect")) {
                    String target = xmlr.getAttributeValue("", "title");
                    return "#REDIRECT [[" + target + "]]";
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
        // This happens only when no text element is found in the page.
        return null;
    }

}
