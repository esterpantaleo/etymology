package org.getalp.dbnary;

import java.io.File;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

public class WiktionaryGrep {

    public static final String titleTag = "title";
    public static final String textTag = "text";

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

    public static void grep(File dumpFile, Pattern pat, Writer out) throws WiktionaryIndexerException {

        // create new XMLStreamReader

        XMLStreamReader2 xmlr = null;
        try {
            // pass the file name. all relative entity references will be
            // resolved against this as base URI.
            xmlr = xmlif.createXMLStreamReader(dumpFile);

            // check if there are more events in the input stream
            String title = "";
        	Matcher match = pat.matcher("");

            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && xmlr.getLocalName().equals(titleTag)) {
                    title = xmlr.getElementText();
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(textTag)) {
                	String text = xmlr.getElementText();
                	match.reset(text);
                	if (match.find()) {
                		out.write(title+"\n");
                		out.flush();
                	}
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
    }

}
