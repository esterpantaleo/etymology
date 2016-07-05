package org.getalp.blexisma.morpho;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class WOLFFrenchToWordnet {
	
	private static class WNEntry {
		String wnid;
		String pos;
		String[] ids;
		String id;
		String[] wn30rdfids;
		String wn30rdfid;
		
		WNEntry(String wnline) {
			wnid = wnline.substring(0, 8);
			String nb = wnline.substring(14, 16);
			pos = wnline.substring(12, 13);
			int nbkey = Integer.parseInt(nb, 16);
			String rest = wnline.substring(17);
			String[] elts = rest.split(" ");
			ids = new String[nbkey];
			wn30rdfids = new String[nbkey];
			for (int i = 0; i < nbkey; i++) {
				int n = Integer.parseInt(elts[2*i + 1], 16) + 1;
				ids[i] = "#" + elts[2*i] + "#" + pos + "#" + n;
				wn30rdfids[i] = "synset-" +  elts[2*i] + "-" + pos + "-" + n; 
			}
			id = ids[0];
			wn30rdfid = wn30rdfids[0];
		}
	}
	
	static Hashtable<String, WNEntry> nouns ;
	static Hashtable<String, WNEntry> verbs ;
	static Hashtable<String, WNEntry> adj ;
	static Hashtable<String, WNEntry> adv ;
	static HashSet<String> knownlemmas;
	
	static Model wolf;
	
	public static void main(String args[]) throws IOException {
		
		if (args.length != 3) {
			usage();
			System.exit(1);
		}

		System.err.println("Reading wordnet nouns.");
		nouns = readWN20(args[1] + "/data.noun");
		System.err.println("Reading wordnet verbs.");
		verbs = readWN20(args[1] + "/data.verb");
		System.err.println("Reading wordnet adjectives.");
		adj = readWN20(args[1] + "/data.adj");
		System.err.println("Reading wordnet adverbs.");
		adv = readWN20(args[1] + "/data.adv");

		knownlemmas = readSymbols(args[2]);
		
		wolf = ModelFactory.createDefaultModel();
		
		// read the RDF/XML files
		System.err.println("Reading wolf model.");
		wolf.read(args[0]);
		
		Property wolfid = wolf.getProperty("http://Kaiko.getalp.org/kaiko/volume/Kaiko_wolf-0.1.5-light-skos.owl#initialWolfID");
		Property prefLabel = wolf.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel");
		
		StmtIterator stmts = wolf.listStatements(null, wolfid, (RDFNode)null);
		PrintStream lsfst = new PrintStream("lem2synset.tfst", "ISO-8859-1");
		HashSet<String> lemmasWithTransition = new HashSet<String>();
		HashSet<String> outputSymbols = new HashSet<String>();

		// Generate lemma -> synset transducer
		while (stmts.hasNext()) {
			Statement s = stmts.next();
			String wnid = getWNId(s.getLiteral().getString());
			
			StmtIterator labels = wolf.listStatements(s.getSubject(), prefLabel, (RDFNode)null);
			while (labels.hasNext()) {
				Statement l = labels.next();
				String label = l.getLiteral().getString();
				String lemma = label.replace(' ', '_');
				
				// TODO: remove terms from knownlemmas and generate a id fst with remaining lemmas.
				if (knownlemmas.contains(lemma)) {
					lemmasWithTransition.add(lemma);
					lsfst.println("0 1 " + lemma + " " + wnid);
					outputSymbols.add(wnid);
				} else {
					System.err.println("Ignoring lemma: " + lemma);
				}
			}
		}
		lsfst.println("1");
		lsfst.close();
		
		// should I remove the known lemmas from the final result or just keep it along with its synsets ?
		// knownlemmas.removeAll(lemmasWithTransition);
		
		// Generate lemma -> lemma transducer for unknown lemmas
		PrintStream idfst = new PrintStream("ulem.tfst", "ISO-8859-1");

		for (String l : knownlemmas) {
			idfst.println("0 1 " + l + " " + l);
			outputSymbols.add(l);
		}
		idfst.println("1");
		idfst.close();
		
		// Generate char -> char transducer for unknown words
		PrintStream charfst = new PrintStream("uchars.tfst", "ISO-8859-1");

		for (int i = 33; i < 127; i++) {
			charfst.println("0 1 '" + (char) i + "' '" + (char) i + "' 1.5");
			outputSymbols.add("'" + (char) i + "'");
		}
		
		for (int i = 160; i < 256; i++) {
			charfst.println("0 1 '" + (char) i + "' '" + (char) i + "' 1.5");
			outputSymbols.add("'" + (char) i + "'");
		}
		charfst.println("1");
		charfst.close();
		
			
		// Generate resulting symbol table
		PrintStream synsetsymbs = new PrintStream("synsymbs.txt", "ISO-8859-1");
		synsetsymbs.println("<eps> 0");
		synsetsymbs.println("<space> 1");

		int sn = 2;
		for (String l : outputSymbols) {
			synsetsymbs.println(l + " " + sn++);
		}
		synsetsymbs.close();
		
	}

	private static String getWNId(String wolfid) {
		String elts[] = wolfid.split("-");
		String pos = elts[2];
		String wnid = elts[1];
		Hashtable<String, WNEntry> table = null;
		if (pos.equals("n")) {
			table = nouns;
		} else if (pos.equals("v")) {
			table = verbs;
		} else if (pos.equals("a")) {
			table = adj;
		} else if (pos.equals("b")) {
			table = adv;
		}
		
		WNEntry e = table.get(wnid);
		
		return e.id;
	}

	private static Hashtable<String, WNEntry> readWN20(String fname) throws IOException {
		Hashtable<String, WNEntry> t = new Hashtable<String, WNEntry>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "UTF-8"));
		String line = br.readLine();
		int ln = 1;
		while(line != null) {
			if (! line.startsWith(" ")) {
				if (! Character.isDigit(line.charAt(0))) {
					System.err.println("Ignoring line [" + ln + "]: " + line);
				} else {
					WNEntry e = new WNEntry(line);
					t.put(e.wnid, e);
				}
			}
			line = br.readLine();
			ln++;
		}
		return t;
	}
	
	private static HashSet<String> readSymbols(String fname) throws IOException {
		HashSet<String> t = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "ISO-8859-1"));
		String line = br.readLine();
		int ln = 1;
		while(line != null) {
			String elts[] = line.split(" ");
			t.add(elts[0]);
			
			line = br.readLine();
			ln++;
		}
		return t;
	}
	
	private static void usage() {
		System.out.println("Usage: java -Xmx8G " + WOLFFrenchToWordnet.class.getCanonicalName() + " kaiko_url wndir osyms.txt");
	}
}
