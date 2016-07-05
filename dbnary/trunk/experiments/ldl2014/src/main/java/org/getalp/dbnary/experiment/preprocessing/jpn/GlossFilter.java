package org.getalp.dbnary.experiment.preprocessing.jpn;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {		
	
	private static String sensenum = "(?:(?:[\\d０-９]+(?:\\.\\d+)?)|、|\\s)+";

	// 語義1及び語義2
	private static String numSenseListGlossRegExp = "^((?:語義)?\\s*" + sensenum + "(?:及び(?:語義)?\\s*" + sensenum + ")*)(.*)$";
	private static Pattern numSenseListGlossPattern = Pattern.compile(numSenseListGlossRegExp);
	private static Matcher numSenseListGlossMatcher = numSenseListGlossPattern.matcher("");
	
	
	private static String numSenseGlossRegExp = "^(?:語義)?\\s*(" + sensenum + ")(.*)$";
	private static Pattern numSenseGlossPattern = Pattern.compile(numSenseGlossRegExp);
	private static Matcher numSenseGlossMatcher = numSenseGlossPattern.matcher("");
		
	// (1)の意味
	private static String numSense2GlossRegExp = "^[\\(（](" + sensenum + ")[\\)）]の意味(.*)$";
	private static Pattern numSense2GlossPattern = Pattern.compile(numSense2GlossRegExp);
	private static Matcher numSense2GlossMatcher = numSense2GlossPattern.matcher("");

	private static String glossNumSenseRegExp = "^(.*)語義\\s*(" + sensenum + ")[\\)）\\s\\p{Punct}]*$";
	private static Pattern glossNumSensePattern = Pattern.compile(glossNumSenseRegExp);
	private static Matcher glossNumSenseMatcher = glossNumSensePattern.matcher("");

	public StructuredGloss extractGlossStructure(String rawGloss) {
		
		rawGloss = normalize(rawGloss);
		if (rawGloss.length() == 0) return null;

		numSenseListGlossMatcher.reset(rawGloss);
		if (numSenseListGlossMatcher.matches()) {
			String g = numSenseListGlossMatcher.group(2);
			if (null != g && g.trim().length() == 0) {
				g = null;
			}
			String n = normalizeNumbers(numSenseListGlossMatcher.group(1));
			return new StructuredGloss(n, g);
		}
		
		numSenseGlossMatcher.reset(rawGloss);
		if (numSenseGlossMatcher.matches()) {
			String g = numSenseGlossMatcher.group(2);
			if (null != g && g.trim().length() == 0) {
				g = null;
			}
			String n = normalizeNumbers(numSenseGlossMatcher.group(1));
			return new StructuredGloss(n, g);
		}
		
		numSense2GlossMatcher.reset(rawGloss);
		if (numSense2GlossMatcher.matches()) {
			String g = numSense2GlossMatcher.group(2);
			if (null != g && g.trim().length() == 0) {
				g = null;
			}
			String n = normalizeNumbers(numSense2GlossMatcher.group(1));
			return new StructuredGloss(n, g);
		}
		
		glossNumSenseMatcher.reset(rawGloss);
		if (glossNumSenseMatcher.matches()) {
			String g = glossNumSenseMatcher.group(1);
			if (null != g && g.trim().length() == 0) {
				g = null;
			}
			String n = normalizeNumbers(glossNumSenseMatcher.group(2));
			return new StructuredGloss(n, g);
		}
		return new StructuredGloss(null, rawGloss);
	}

	private String normalizeNumbers(String val) {
		val = val.replaceAll("語義", "");
		val = val.replaceAll("及び", ",");
		StringBuffer s = new StringBuffer(val);
		int i = 0;
		while (i != s.length()) {
			char c = s.charAt(i);
			if (c >= '０' && c <= '９') {
				s.setCharAt(i, (char) ('0' + (c - '０')));
			}
			i++;
		}
		
		return s.toString();
	}
}
