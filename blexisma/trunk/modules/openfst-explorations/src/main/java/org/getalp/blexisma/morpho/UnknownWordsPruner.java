package org.getalp.blexisma.morpho;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UnknownWordsPruner {

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
			for(int i=4; i >= trans.length; i++) {
				weight = weight + trans[i] + "\t";
			}
			if (weight.length() > 0) {
				weight = weight.substring(0, weight.length()-1);
			}
		}
	}
	
	public void readFst(String fstfile) throws IOException {
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(fstfile), "UTF-8"));
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		int maxIndex = 0;
		
		// Read transitions
		String l = b.readLine();
		while (null != l) {
			if (l.contains("\t")) {
				Transition t = new Transition(l);
				transitions.add(t);
				if (maxIndex < t.to) maxIndex = t.to;
				if (maxIndex < t.from) maxIndex = t.from;
			}
			l = b.readLine();
		}
		
		// fill array of nodes
		Set<Transition>[] delta = new Set[maxIndex+1];
		for (int i = 0; i < delta.length; i++) {
			delta[i] = new HashSet<Transition>();
		}
		
		for (Transition t : transitions) {
			delta[t.from].add(t);
		}
		
		
	}
	
	
}
