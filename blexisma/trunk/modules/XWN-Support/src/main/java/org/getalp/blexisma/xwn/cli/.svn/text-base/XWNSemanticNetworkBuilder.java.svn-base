package org.getalp.blexisma.xwn.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.getalp.blexisma.semnet.SimpleSemanticNetwork;
import org.getalp.blexisma.utils.OpenFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class XWNSemanticNetworkBuilder {
	private static String ADJPOSNODE = "#pos|adj";
	private static String ADVPOSNODE = "#pos|adv";
	private static String NOUNPOSNODE = "#pos|noun";
	private static String VERBPOSNODE = "#pos|verb";
	
	public static void main(String[] args){
		if (args.length!=5){
			System.err.println("Illegal number of arguments. Usage: java "+XWNSemanticNetworkBuilder.class+
					" adj_file adv_file noun_file verb_file network_file");
			System.exit(0);
		}
		
		SimpleSemanticNetwork<String,String> network = new SimpleSemanticNetwork<String,String>();
		
		network.addNode(ADJPOSNODE);
		network.addNode(ADVPOSNODE);
		network.addNode(NOUNPOSNODE);
		network.addNode(VERBPOSNODE);
		
		System.out.println("Loading "+args[0]);
		network = loadFile(new File(args[0]),ADJPOSNODE,network);
		System.out.println("Loading "+args[1]);
		network = loadFile(new File(args[1]),ADVPOSNODE,network);
		System.out.println("Loading "+args[2]);
		network = loadFile(new File(args[2]),NOUNPOSNODE,network);
		System.out.println("Loading "+args[3]);
		network = loadFile(new File(args[3]),VERBPOSNODE,network);
		
		try {
			network.dumpToWriter(new PrintStream(new FileOutputStream(args[4])));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static SimpleSemanticNetwork<String,String> loadFile(File f, String morpho,
			SimpleSemanticNetwork<String,String> net) {
		String xmlflow = null;
		Namespace ns = null;
		Document doc = null;
		List<Element> flowlist = null;
		
		xmlflow = OpenFile.readFullTextFile(f);
		
		try {
			doc = new SAXBuilder().build(new ByteArrayInputStream(xmlflow.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ns = doc.getRootElement().getNamespace();
		flowlist = ((Element)doc.getRootElement()).getChildren("gloss",ns);
		
		for (Element e : flowlist){
			ArrayList<String> entries = buildEntryNodes(e,ns);
			String def = buildDefNode(e,ns);
			String synt = buildSyntNode(e,ns);
			String desamb = buildDesambNode(e,ns);
			
			net.addNode(def);
			net.addRelation(def, morpho, 1, "pos");
			net.addNode(synt);
			if (desamb.split("\\|").length==2) net.addNode(desamb);
			
			for (String ent : entries){
				net.addNode(ent);
				net.addRelation(ent, def, 1, "def");
				net.addRelation(def, synt, 1, "synt");
				if (desamb.split("\\|").length==2) net.addRelation(def, desamb, 1, "desamb");
			}
		}
		
		return net;
	}
	
	private static ArrayList<String> buildEntryNodes(Element gloss, Namespace ns){
		ArrayList<String> entries = new ArrayList<String>();
		String[] lemmas = gloss.getChildText("synonymSet", ns).split(",");
		
		for (String l : lemmas){
			entries.add("#eng|"+l.trim());
		}
		
		return entries;
	}
	
	private static String buildDefNode(Element gloss, Namespace ns){
		String def = "#def|eng|";
		def = def + gloss.getAttributeValue("synsetID")+"|";
		def = def + gloss.getChildText("text", ns).trim().split(";")[0];
		return def;
	}
	
	private static String buildSyntNode(Element gloss, Namespace ns){
		String ana = "#synt|";
		@SuppressWarnings("unchecked")
		List<Element> list = gloss.getChildren("parse",ns);
		
		for (Element e: list){
			ana = ana + e.getValue().trim();
		}
		
		return ana;
	}
	
	private static String buildDesambNode(Element gloss, Namespace ns){
		String desamb = "#desamb|";
		@SuppressWarnings("unchecked")
		List<Element> list = gloss.getChild("wsd", ns).getChildren("wf", ns);
		
		for (Element e: list){
			String l = e.getAttributeValue("lemma");
			String d = e.getAttributeValue("wnsn");
			if (d!=null&&l!=null)
				desamb = desamb + l + ":" + d + ";";
		}
		
		return desamb;
	}
}
