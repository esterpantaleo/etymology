package org.getalp.blexisma.morpho;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;

import org.getalp.blexisma.morpho.WordNetDistance.Vertex;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Synset;


public class WordNetDistance {
	static class Vertex implements Comparable<Vertex>
	{
		public final String name;
		public Edge[] adjacencies;
		public double minDistance = Double.POSITIVE_INFINITY;
		public Vertex previous;
		public Vertex(String argName) { name = argName; }
		public String toString() { return name; }
		public int compareTo(Vertex other)
		{
			return Double.compare(minDistance, other.minDistance);
		}

	}


	static class Edge
	{
		public final Vertex target;
		public final double weight;
		public Edge(Vertex argTarget, double argWeight)
		{ target = argTarget; weight = argWeight; }
	}	

	static IDictionary idict;

	static SortedMap<String, ISynset> nouns ;
	static SortedMap<String, ISynset> verbs ;
	static SortedMap<String, ISynset> adj ;
	static SortedMap<String, ISynset> adv ;

	public static void computePaths(Vertex source)
	{
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge exiting u
			for (Edge e : u.adjacencies)
			{
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU ;
					v.previous = u;
					vertexQueue.add(v);

				}

			}
		}
	}


	public static List<Vertex> getShortestPathTo(Vertex target)
	{
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
			path.add(vertex);

		Collections.reverse(path);
		return path;
	}

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
		HashMap<ISynset, Vertex> map = new HashMap<ISynset,Vertex>();
		Iterator<ISynset> it = idict.getSynsetIterator(POS.NOUN);

		while (it.hasNext()) {
			ISynset syns = it.next();
			map.put(syns, new Vertex(Synset.zeroFillOffset(syns.getOffset())));
		}

		it = idict.getSynsetIterator(POS.NOUN);
		Edge[] edgePrototype = new Edge[0];
		while (it.hasNext()) {
			ISynset syns = it.next();
			Vertex from = map.get(syns);

			ArrayList<Edge> tos = new ArrayList<Edge>();

			for (ISynsetID relatedSynsetId: syns.getRelatedSynsets()) {
				ISynset r = idict.getSynset(relatedSynsetId);
				Vertex target = map.get(r);
				
				if (null != target) 
					tos.add(new Edge(target, 1.0));
			}
			from.adjacencies = tos.toArray(edgePrototype);
		}

		System.err.println("(" + (System.currentTimeMillis() - t) + ")");
		t = System.currentTimeMillis();
		System.err.println("Computing pathes");
		
		IIndexWord w1 = idict.getIndexWord("shoe", POS.NOUN);
		IIndexWord w2 = idict.getIndexWord("foot", POS.NOUN);

		List<IWordID> senseIds = w1.getWordIDs();
		ISynset s1 = idict.getSynset(senseIds.get(0).getSynsetID());
		senseIds = w2.getWordIDs();
		ISynset s2 = idict.getSynset(senseIds.get(0).getSynsetID());

		computePaths(map.get(s1));
		
		System.err.println("(" + (System.currentTimeMillis() - t) + ")");
		t = System.currentTimeMillis();
		System.err.println("Results:");

		for (Vertex v : map.values()) {
			System.out.println("Distance to " + v + ": " + v.minDistance);
			List<Vertex> path = getShortestPathTo(v);
			System.out.println("Path: " + path);
		}

		System.err.println("(" + (System.currentTimeMillis() - t) + ")");

	}

	private static void usage() {
		System.out.println("Usage: java -Xmx8G " + WordNetDistance.class.getCanonicalName() + " wndir_url osyms.txt");
	}
}
