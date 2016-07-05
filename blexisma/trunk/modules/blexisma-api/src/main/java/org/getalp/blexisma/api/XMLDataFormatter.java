package org.getalp.blexisma.api;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.getalp.blexisma.api.syntaxanalysis.AnaTreeInfos;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;

public class XMLDataFormatter {
	
	public static String xmlFormat(AnalysisTree analysisTree) {
		// Create an output factory
	      XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
	      // Set namespace prefix defaulting for all created writers
	      ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
	      // Create an XML stream writer
	      try {
			XMLStreamWriter xmlw =
			     xmlof.createXMLStreamWriter(os, "UTF-8");
		    xmlw.writeStartDocument();
		    writeTree(analysisTree, xmlw);
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
	
	public static String xmlFormat(SemanticDefinition def) {
		// Create an output factory
	      XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
	      // Set namespace prefix defaulting for all created writers
	      ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
	      // Create an XML stream writer
	      try {
			XMLStreamWriter xmlw =
			     xmlof.createXMLStreamWriter(os, "UTF-8");
		    xmlw.writeStartDocument();
		    writeDefinition(def, xmlw);
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
	
	public static String xmlFormat(ConceptualVector cv) {
		return xmlFormat(cv, null);
	}
	
	public static String xmlFormat(ConceptualVector cv, String id) {
		// Create an output factory
	      XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
	      // Set namespace prefix defaulting for all created writers
	      ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
	      // Create an XML stream writer
	      try {
			XMLStreamWriter xmlw =
			     xmlof.createXMLStreamWriter(os, "UTF-8");
		    xmlw.writeStartDocument();
		    writeVector(cv, xmlw, id);
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
	
	
	public static void writeTree(AnalysisTree analysisTree, XMLStreamWriter xmlw) throws XMLStreamException {
		if (analysisTree.isError()) {
			xmlw.writeEmptyElement("error_node");
		} else {
			xmlw.writeStartElement("node");
			AnaTreeInfos infos = analysisTree.getInfos();
			if (null != infos) {
				checkedWriteAttribute("funct", infos.getFct(), xmlw);
				checkedWriteAttribute("lemma", infos.getLem(), xmlw);
				checkedWriteAttribute("word", infos.getWord(), xmlw);
				checkedWriteAttribute("pos", formatMorpho(analysisTree.getInfos().getMorphoProperties()), xmlw);
				writeDefinition(analysisTree.getInfos().getDef(), xmlw);
			}
			if (null != analysisTree.getChildren()) {
				for (AnalysisTree child : analysisTree.getChildren()) {
					writeTree(child, xmlw);
				}
			}
			xmlw.writeEndElement();
		}
	}

	
	public static void writeDefinition(SemanticDefinition def, XMLStreamWriter xmlw) throws XMLStreamException {
		if (null == def) return;
		xmlw.writeStartElement("def");
		checkedWriteAttribute("lemma", def.getId(), xmlw);
		writeVector(def.getMainVector(), xmlw);
		xmlw.writeStartElement("senses");
		int sn = 0;
		if (null != def.getSenseList()) {
			for (Sense sense : def.getSenseList()) {
				xmlw.writeStartElement("sense");
				xmlw.writeAttribute("snum", Integer.toString(sn));
				xmlw.writeAttribute("pos", formatMorpho(sense.getMorpho()));
				xmlw.writeStartElement("sid");
				xmlw.writeCharacters(sense.toSimpleString());
				xmlw.writeEndElement(); // sid
				writeVector(sense.getVector(), xmlw);
				xmlw.writeEndElement(); // sense
				sn++;
			}
		}
		xmlw.writeEndElement(); // senses
		xmlw.writeEndElement(); // def
	}
	
	public static void writeVector(ConceptualVector mainVector, XMLStreamWriter xmlw) throws XMLStreamException {
		writeVector(mainVector, xmlw, null);
	}

	public static void writeVector(ConceptualVector mainVector, XMLStreamWriter xmlw, String id) throws XMLStreamException {
		xmlw.writeStartElement("conceptual_vector");
		if (null != id) {
			xmlw.writeStartElement("id");
			xmlw.writeCharacters(id);
			xmlw.writeEndElement();
		}
		if (null != mainVector) {
			xmlw.writeStartElement("dim");
			xmlw.writeCharacters(Integer.toString(mainVector.getDimension()));
			xmlw.writeEndElement();
			xmlw.writeStartElement("norm");
			xmlw.writeCharacters(Integer.toString(mainVector.getCodeLength()));
			xmlw.writeEndElement();
			xmlw.writeStartElement("vect");
			xmlw.writeCharacters(mainVector.toStringHexa());
			xmlw.writeEndElement();
		}
		xmlw.writeEndElement(); // conceptual_vector
	}

	private static String formatMorpho(Collection<MorphoProperties> morphoProperties) {
		StringBuffer b = new StringBuffer();
		if (null != morphoProperties) {
			for (MorphoProperties pos : morphoProperties) {
				b.append(",");
				b.append(pos.toString());
			}
		}
		return (b.length() == 0) ? "" : b.substring(1);
	}

	private static void checkedWriteAttribute(String name, Object o, XMLStreamWriter xmlw) throws XMLStreamException {
		if (null != o) xmlw.writeAttribute(name, o.toString());
	}
	
}
