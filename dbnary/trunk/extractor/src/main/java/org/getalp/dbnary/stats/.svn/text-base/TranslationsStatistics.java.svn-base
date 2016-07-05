package org.getalp.dbnary.stats;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.DBnaryOnt;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class TranslationsStatistics {
	protected static class IncrementableInt {
		int val;
		
		public IncrementableInt() {
			val = 0;
		}
		
		public IncrementableInt(int val) {
			this.val = val;
		}
		
		public void incr() {
			this.val++;
		}
		
		public void incr(int step) {
			this.val = this.val + step;
		}
		
		public String toString() {
			return Integer.toString(this.val);
		}
	}
	
	private static String getCode(Resource resource) {
		return resource.getLocalName();
	}
	

	public static void printStats(Model m1, String language, String targets, PrintWriter out, boolean verbose) {
		// TODO: extract iso code from lexvo entity.
		SortedMap<String, IncrementableInt> counts = initCounts(targets);
		
		ResIterator relations = m1.listResourcesWithProperty(RDF.type, DBnaryOnt.Translation);
		HashSet<String> langs = new HashSet<String>();
		int others = 0;
		while(relations.hasNext()) {
			Resource r = relations.next();
			Statement t = r.getProperty(DBnaryOnt.targetLanguage);
			if (null != t) {
				RDFNode lang = t.getObject();
				langs.add(getCode(lang.asResource()));
				if (counts.containsKey(getCode(lang.asResource()))) {
					counts.get(getCode(lang.asResource())).incr();
				} else {
					others = others + 1;
				}
			}
		}
		relations.close();

		int total = 0;

		if (verbose) {
			out.println(getHeaders(counts));
		}
		
		for (Entry<String, IncrementableInt> j : counts.entrySet()) {
			total = total + j.getValue().val;
		}		
		total = total + others;
		
		
		//out.print(ISO639_3.sharedInstance.inEnglish(language));
		for (Entry<String, IncrementableInt> j : counts.entrySet()) {
			out.print(j.getValue().val + ",");
		}
		out.print(others + "," + total + "," + langs.size());
		
		out.flush();

	}

	private static SortedMap<String, IncrementableInt> initCounts(String targets) {
		SortedMap<String, IncrementableInt> counts = new TreeMap<String,IncrementableInt>();

		String clangs[] = targets.split(",");
		int i = 0;
		while(i != clangs.length) {
			counts.put(LangTools.getCode(clangs[i]), new IncrementableInt());
			i = i + 1;
		}
		return counts;
	}


	public static String getHeaders(String targets) {
		SortedMap<String, IncrementableInt> counts = initCounts(targets);

		return getHeaders(counts);		
	}
	
	private static String getHeaders(SortedMap<String, IncrementableInt> counts) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, IncrementableInt> j : counts.entrySet()) {
			sb.append(j.getKey()).append(",");
		}
		sb.append("others,Total,# of lang");
		// remove extra leading comma.
		return sb.toString();
	}


	public static void printStats(Model m1, String language,
			String targets, PrintWriter out) {
		printStats(m1, language, targets, out, false);
		
	}
}
