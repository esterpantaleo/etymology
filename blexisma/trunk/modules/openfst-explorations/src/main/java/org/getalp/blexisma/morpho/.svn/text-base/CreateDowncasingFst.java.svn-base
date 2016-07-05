package org.getalp.blexisma.morpho;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class CreateDowncasingFst {

	protected static class Range {
		protected int d;
		protected int f;
		
		protected Range(int d, int f) {
			this.d = d;
			this.f = f;
		}
	}
	
	public static final ArrayList<Range> ranges = new ArrayList<Range>();
	
	static {
		ranges.add(new Range(0x0001, 0x007F)); // ASCII
		ranges.add(new Range(0x0080, 0x00FF)); // Latin 1 supplement
		// ranges.add(new Range(0x0100, 0x017F)); // Latin Extended A
		// ranges.add(new Range(0xFB00, 0xFB06)); // Latin Extended A
		// ranges.add(new Range(0x0300, 0x036F)); // Latin Extended A
		// ranges.add(new Range(0x0370, 0x03FF)); // Latin Extended A
	}
	
	private static void createDowncasingTransducer(SortedSet<String> insymbs, SortedSet<String> outsymbs, PrintStream out) {
		for (Range r : ranges) {
			for (char i = (char)r.d; i <= (char)r.f; i++) {
				Character c = Character.valueOf(i);
				if (Symbols.controls.containsKey(c)) {
					String s = Symbols.controls.get(c);
					out.println("0 0 " + s + " " + s );
					insymbs.add(s);
				} else if (Character.isUpperCase(i)) {
					out.println("0 0 " + ((char) i) + " " + ((char) i));
					out.println("0 0 " + ((char) i) + " " + (char) Character.toLowerCase(i));
					insymbs.add("" + (char) i);
					outsymbs.add("" + (char) i);
					outsymbs.add("" + Character.toLowerCase(i));
				} else if (! Character.isISOControl(i)) {
					out.println("0 0 " + ((char) i) + " " + ((char) i));
					insymbs.add("" + (char) i);
					outsymbs.add("" + (char) i);
				}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO: determine canonical representation of characters.
		// TODO: create an fst to canonicalize the char representations.
		// TODO: apply canonicalization on dictionary entries when creating lexicon FST.
		// TODO: canonicalize text when creating the input FSA.
		// TODO: should greek character be lower cased ?
		SortedSet<String> insymbs = new TreeSet<String>();
		SortedSet<String> outsymbs = new TreeSet<String>();
		
		createDowncasingTransducer(insymbs, outsymbs, System.out);
		
		dumpCharacterTable(System.out);
		
		
		
	}

	private static void dumpCharacterTable(PrintStream out) {
		out.println("<eps> 0");
		for (Range r : ranges) {
			for (char i = (char)r.d; i <= (char)r.f; i++) {
				Character c = Character.valueOf(i);
				if (Symbols.controls.containsKey(c)) {
					String s = Symbols.controls.get(c);
					out.println(s + " " + ((int) i));
				} else if (! Character.isISOControl(i)){
					out.println(((char) i) + " " + ((int) i));
				}
			}
		}
	}
	
	private static void createSeparatorsTransducer(PrintStream out) {
		for (Range r : ranges) {
			for (char i = (char)r.d; i <= (char)r.f; i++) {
				Character c = Character.valueOf(i);
				if (Character.isWhitespace(c)) {
					String s = Symbols.controls.containsKey(c) ? Symbols.controls.get(c) : "" + c;
					out.println("0 1 " + s + " <eps>");
				}
				if (Symbols.controls.containsKey(c)) {
					String s = Symbols.controls.get(c);
					out.println(s + " " + ((int) i));
				} else if (! Character.isISOControl(i)){
					out.println(((char) i) + " " + ((int) i));
				}
			}
		}
	}

}
