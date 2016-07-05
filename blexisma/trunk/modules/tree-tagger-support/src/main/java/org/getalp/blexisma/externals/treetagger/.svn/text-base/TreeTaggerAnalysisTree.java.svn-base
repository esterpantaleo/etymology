package org.getalp.blexisma.externals.treetagger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeTaggerAnalysisTree {

	public static class Node {
		protected String occurence;
		protected String pos;
		protected String lemma;
		
		protected List<Node> children;

		public Node(String occurence, String pos, String lemma) {
			this.occurence = occurence;
			this.pos = pos;
			this.lemma = lemma;
		}

		/**
		 * @return the occurence
		 */
		public String getOccurence() {
			return occurence;
		}

		/**
		 * @param occurence the occurence to set
		 */
		public void setOccurence(String occurence) {
			this.occurence = occurence;
		}

		/**
		 * @return the pos
		 */
		public String getPos() {
			return pos;
		}

		/**
		 * @param pos the pos to set
		 */
		public void setPos(String pos) {
			this.pos = pos;
		}

		/**
		 * @return the lemma
		 */
		public String getLemma() {
			return lemma;
		}

		/**
		 * @param lemma the lemma to set
		 */
		public void setLemma(String lemma) {
			this.lemma = lemma;
		}

		/**
		 * @return the children
		 */
		public List<Node> getChildren() {
			return children;
		}
		
		public void addChild(Node child) {
			if (this.children == null) this.children = new ArrayList<Node>();
			this.children.add(child);
		}
	}
	
	public static Node parse(ByteArrayOutputStream os) {
		try {
			String s = os.toString("UTF-8");
			//System.err.print(s);
			return parse(s);
		} catch (UnsupportedEncodingException e) {
			// Should never happen
			e.printStackTrace();
		}
		return null;
	}

	protected static final Pattern linePattern = Pattern.compile("([^\\t]*)\\t([^\\t]*)\\t([^\\t]*)");
	
	public static Node parse(String string) {
		BufferedReader br = new BufferedReader(new StringReader(string)); 
		String ln;
		Node root = new Node("", "ROOT", "");
		Matcher m = linePattern.matcher("");
		try {
			ln = br.readLine();
			while (null != ln) {
				m.reset(ln);
				if (m.matches()) {
					root.addChild(new Node(m.group(1), m.group(2), m.group(3)));
				} else {
					System.err.println("Couldn't match: " + ln);
				}
				ln = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
}
