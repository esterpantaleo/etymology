package org.getalp.blexisma.morpho;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Synset;

public class WOLFToWordnetSenseIds {
	

	static Hashtable<String, ISynset> nouns ;
	static Hashtable<String, ISynset> verbs ;
	static Hashtable<String, ISynset> adj ;
	static Hashtable<String, ISynset> adv ;
	static HashSet<String> knownlemmas;
	
	static Model wolf;
	static IDictionary wndict;

	public static void main(String args[]) throws IOException {
		
		if (args.length != 3) {
			usage();
			System.exit(1);
		}

		knownlemmas = readSymbols(args[2]);

		String u = null;
		if (args[1].contains("://")) {
			u = args[1];
		} else {
			u = "file://" + args[1];
		}
		URL url = new URL(u);
		wndict = new Dictionary(url);
		try {
			wndict.open();
		} catch (IOException e) {
			System.err.println("Could not load wordnet dictionary at: " + url.toString());
			System.exit(-1);
		}
		
		nouns = createOffsetIndex(POS.NOUN);
		verbs = createOffsetIndex(POS.VERB);
		adj = createOffsetIndex(POS.ADJECTIVE);
		adv = createOffsetIndex(POS.ADVERB);
		
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
			ISynset synset = getSynsetFromWolfId(s.getLiteral().getString());
			String pos = getLefffPosFromWNPos(synset.getPOS());
			String wnid = synset.getWord(1).getSenseKey().toString();
			
			StmtIterator labels = wolf.listStatements(s.getSubject(), prefLabel, (RDFNode)null);
			while (labels.hasNext()) {
				Statement l = labels.next();
				String label = l.getLiteral().getString();
				String lemma = "#" + label.replace(' ', '_');
				lemma = lemma.replace("\"", "").trim() + "#" + pos;
				
				// TODO: remove terms from knownlemmas and generate a id fst with remaining lemmas.
				if (knownlemmas.contains(lemma)) {
					lemmasWithTransition.add(lemma);
					lsfst.println("0 1 " + lemma + " " + wnid);
					outputSymbols.add(wnid);
				} else {
					// TODO: Maybe add these lemmas to the char -> lemma converter
					// System.err.println("Ignoring lemma: " + lemma);
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


	
	private static Hashtable<String, ISynset> createOffsetIndex(POS pos) {
		Hashtable<String, ISynset> index = new Hashtable<String, ISynset>(50000);
		
		Iterator<ISynset> it = wndict.getSynsetIterator(pos);
		
		while (it.hasNext()) {
			ISynset synset = it.next();
			
			String offset = Synset.zeroFillOffset(synset.getOffset());
			index.put(offset, synset);
		}
		
		return index;
	}



	private static String getLefffPosFromWNPos(POS pos) {
		if (null == pos)
			return "";
		else if (pos == POS.NOUN)
			return "nc";
		else if (pos == POS.VERB)
			return "v";
		else if (pos == POS.ADJECTIVE)
			return "a";
		else if (pos == POS.ADVERB)
			return "j";
		else
			return "";
	}

	private static ISynset getSynsetFromWolfId(String wolfid) {
		String elts[] = wolfid.split("-");
		String pos = elts[2];
		String wnid = elts[1];
		Hashtable<String, ISynset> table = null;
		if (pos.equals("n")) {
			table = nouns;
		} else if (pos.equals("v")) {
			table = verbs;
		} else if (pos.equals("a")) {
			table = adj;
		} else if (pos.equals("b")) {
			table = adv;
		}
		
		ISynset synset = table.get(wnid);
		
		return synset;
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
		System.out.println("Usage: java -Xmx8G " + WOLFToWordnetSenseIds.class.getCanonicalName() + " kaiko_url wndir_url osyms.txt");
	}
}
