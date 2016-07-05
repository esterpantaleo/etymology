package org.getalp.blexisma.externals.treetagger;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.getalp.blexisma.externals.treetagger.TreeTaggerAnalysisTree.Node;

public class XMLTreeTaggerOutputFormatter {
	public static String xmlFormat(Node root) {
		// Create an output factory
	      XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
	      // Set namespace prefix defaulting for all created writers
	      ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
	      // Create an XML stream writer
	      try {
			XMLStreamWriter xmlw =
			     xmlof.createXMLStreamWriter(os, "UTF-8");
		    xmlw.writeStartDocument();
		    writeTree(root, xmlw);
		    xmlw.writeEndDocument();
		    xmlw.close();
			return os.toString("UTF-8");

		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return os.toString();
		}
	}
	
	public static void writeTree(Node node, XMLStreamWriter xmlw) throws XMLStreamException {
		if (null != node) {
			xmlw.writeStartElement("node");
				checkedWriteAttribute("occ", node.getOccurence(), xmlw);
				checkedWriteAttribute("pos", node.getPos(), xmlw);
				checkedWriteAttribute("lemma", node.getLemma(), xmlw);
			if (null != node.getChildren()) {
				for (Node child : node.getChildren()) {
					writeTree(child, xmlw);
				}
			}
			xmlw.writeEndElement();
		}
	}
	
	private static void checkedWriteAttribute(String name, Object o, XMLStreamWriter xmlw) throws XMLStreamException {
		if (null != o) xmlw.writeAttribute(name, o.toString());
	}
	
}
