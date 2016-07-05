package org.getalp.blexisma.xwn.cli;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class XWN20to21Adapter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length!=2) {
			System.err.println("bad argument number");
			System.exit(1);
		}
		
		HashMap<String,String> map = new HashMap<String,String>();
		String xmlflow = readFullTextFile(new File(args[1]));
		Document doc = null;
		Iterator<?> flowlist = null;
		Namespace namespace = null;
		Element currentGloss = null;
		
		BufferedReader bfrd = readTextFileLineByLine(new File(args[0]));
		
		try {
			while (bfrd.ready()) {
				String ligne = bfrd.readLine();
				String def = ligne.split("\\|")[0];
				String synid = ligne.split("\\|")[1].split(" ")[0];
				map.put(def.trim(), synid.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			doc = new SAXBuilder().build(new ByteArrayInputStream(xmlflow.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		namespace = doc.getRootElement().getNamespace();
		
		flowlist = doc.getRootElement().getChildren("gloss",namespace).iterator();
		
		while (flowlist.hasNext()) {
			currentGloss = (Element)flowlist.next();
			String localdef = currentGloss.getChildText("text",namespace).trim();
			System.out.println("localdef: "+localdef);
			if (map.containsKey(localdef)) {
				System.out.println("puting as new synsetID: "+map.get(localdef));
				currentGloss.setAttribute("synsetID", map.get(localdef));
			}
		}
		
		try {
			XMLOutputter out = new XMLOutputter();
			out.setFormat(Format.getPrettyFormat());
			java.io.FileWriter writer = new java.io.FileWriter(args[0]+".xml");
			out.output(doc, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final static BufferedReader readTextFileLineByLine(File file){
		BufferedReader bfrd = null;
		
			try {
				bfrd = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.defaultCharset().name()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		return bfrd;
	}
	
	private final static String readFullTextFile(File file){
		BufferedReader bfrd = null;
		StringBuffer tmp = new StringBuffer();
		
		try {
			bfrd = readTextFileLineByLine(file);
			
			while (bfrd.ready())
			{
				tmp.append(bfrd.readLine());
				
			}
			bfrd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IO error, corrupted or in use file");
			e1.printStackTrace();
		}
		
		return tmp.toString();
	}
}
