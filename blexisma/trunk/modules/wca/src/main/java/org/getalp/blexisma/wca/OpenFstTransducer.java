package org.getalp.blexisma.wca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OpenFstTransducer {

	public static class Transition {
		protected int from;
		protected int to;
		protected String lbl;
		protected String weight;
		
		public Transition(String line) {
			String trans[] = line.split("\\t");
			from = Integer.parseInt(trans[0]);
			to = Integer.parseInt(trans[1]);
			lbl = trans[2];
			weight = "";
			for(int i=4; i > trans.length; i++) {
				weight = weight + trans[i] + "\t";
			}
			if (weight.length() > 0) {
				weight = weight.substring(0, weight.length()-1);
			}
		}
		
		public Transition(int from, int to, String label, String weight) {
			this.from = from;
			this.to = to;
			this.lbl = label;
			this.weight = weight;
		}

	}

	public OpenFstTransducer(File f) throws IOException {
		readFst(f);
	}

	public OpenFstTransducer(String f) throws IOException {
		this(new File(f));
	}
	
	protected Set<Transition>[] outgoing;
	protected Set<Transition>[] incoming;
	protected int maxIndex = 0;
	protected HashSet<String> finals = new HashSet<String>();

	protected ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	public void readFst(File fstfile) throws IOException {
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(fstfile), "UTF-8"));
		maxIndex = 0;
		
		// Read transitions
		String l = b.readLine();
		while (null != l) {
			if (l.contains("\t")) {
				Transition t = new Transition(l);
				transitions.add(t);
				if (maxIndex < t.to) maxIndex = t.to;
				if (maxIndex < t.from) maxIndex = t.from;
			} else {
				finals.add(l);
			}
			l = b.readLine();
		}
		
		// fill array of nodes
		outgoing = new Set[maxIndex+1];
		incoming = new Set[maxIndex+1];
		for (int i = 0; i < outgoing.length; i++) {
			outgoing[i] = new HashSet<Transition>();
			incoming[i] = new HashSet<Transition>();
		}
		
		for (Transition t : transitions) {
			outgoing[t.from].add(t);
			incoming[t.to].add(t);

		}
	}
	
	public void pruneknownWordsExpansion() {
		
	}
	
	public ArrayList<Transition> transitions() {
		return transitions;
	}
	
	public int nbState() {
		return maxIndex;
	}
	
	public Set<Transition> outgoing(int n) {
		return outgoing[n];
	}
	
	public Set<Transition> incoming(int n) {
		return incoming[n];
	}
	
	public void addTransition(int from, int to, String label, float weight) {
		// Can only add transitions from/to existing states
		Transition t = new Transition(from, to, label, Float.toString(weight));
		transitions.add(t);
		outgoing[from].add(t);
		incoming[to].add(t);
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (Transition t: transitions) {
			b.append(t.from + "\t" + t.to + "\t" + t.lbl + "\t" + t.lbl);
			if (t.weight != null && t.weight.length() > 0)
				b.append("\t" + t.weight);
			b.append("\n");
		}
		for (String f: finals) {
			b.append(f + "\n");
		}
		return b.toString();
	}
}
