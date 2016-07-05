package org.getalp.blexisma.morpho;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;


public class WordNetDistancesOnNouns {


	static IDictionary idict;

	static SortedMap<String, ISynset> nouns ;
	static SortedMap<String, ISynset> verbs ;
	static SortedMap<String, ISynset> adj ;
	static SortedMap<String, ISynset> adv ;

	public static void main(String args[]) throws IOException {

		if (args.length != 1) {
			usage();
			System.exit(1);
		}

		System.err.println("Reading wordnet data");
		long t = System.currentTimeMillis();

		String u = null;
		if (args[0].contains("://")) {
			u = args[0];
		} else {
			u = "file://" + args[0];
		}
		URL url = new URL(u);
		idict = new Dictionary(url);
		try {
			idict.open();
		} catch (IOException e) {
			System.err.println("Could not load wordnet dictionary at: " + url.toString());
			System.exit(-1);
		}

		System.err.println("(" + (System.currentTimeMillis() - t) + ")");
		t = System.currentTimeMillis();
		System.err.println("Creating graph");

		// Create graph structure
		HashMap<ISynset, Integer> syn2num = new HashMap<ISynset,Integer>();
		HashMap<Integer, ISynset> num2syn = new HashMap<Integer, ISynset>();
		
		Iterator<ISynset> it = idict.getSynsetIterator(POS.NOUN);
		int nbnodes = 0;
		while (it.hasNext()) {
			ISynset syns = it.next();
			syn2num.put(syns, nbnodes);
			num2syn.put(nbnodes, syns);
			nbnodes++;
		}
		System.err.println(nbnodes + " nodes.");
		System.err.println("Allocating matrix.");
		
		int [][] adj= new int[nbnodes][nbnodes];
		
		System.err.println("Initializing matrix.");
		
		for (int[] l: adj)
			Arrays.fill(l, Integer.MAX_VALUE);

		it = idict.getSynsetIterator(POS.NOUN);
		int n = 0;
		while (it.hasNext()) {
			ISynset syns = it.next();
			Integer i = syn2num.get(syns);
			if (i.intValue() != n) System.err.println("Iterator did not give the same order.");
			for (ISynsetID relatedSynsetId: syns.getRelatedSynsets()) {
				ISynset r = idict.getSynset(relatedSynsetId);
				Integer j = syn2num.get(syns);
				if (null != j) {
					adj[i][j] = 1;
					adj[j][i] = 1;
				}
			}
			n++;
		}
		
		System.err.println("(" + (System.currentTimeMillis() - t) + ")");
		t = System.currentTimeMillis();
		System.err.println("Computing pathes");


		int [][] D = adj;

		for (int k=0; k<nbnodes; k++){
			for (int i=0; i<nbnodes; i++){
				for (int j=0; j<nbnodes; j++){
					if(D[i][k] != Integer.MAX_VALUE && D[k][j] != Integer.MAX_VALUE && D[i][k]+D[k][j] < D[i][j]){
						D[i][j] = D[i][k]+D[k][j];                    
					}
				}
			}
		}
		System.err.println("(" + (System.currentTimeMillis() - t) + ")");
		t = System.currentTimeMillis();
		System.err.println("Results:");


		System.err.println("(" + (System.currentTimeMillis() - t) + ")");

	}

	private static void usage() {
		System.out.println("Usage: java -Xmx8G " + WordNetDistancesOnNouns.class.getCanonicalName() + " wndir_url osyms.txt");
	}
}
