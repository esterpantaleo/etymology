package org.getalp.dbnary.experiment.preprocessing.ell;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {
	
	private static String sensenum = "(?:\\d+)";
	private static String simpleSenseNumberingRegExp = "^(" + sensenum + ")[\\.](.*)$";
	private static Pattern simpleSenseNumberingPattern = Pattern.compile(simpleSenseNumberingRegExp);
	private static Matcher simpleSenseNumberingMatcher = simpleSenseNumberingPattern.matcher("");
	
	private static String senseNumOnlyRegExp = "^(" + sensenum + ")$";
	private static Pattern senseNumOnlyPattern = Pattern.compile(senseNumOnlyRegExp);
	private static Matcher senseNumOnlyMatcher = senseNumOnlyPattern.matcher("");

	public StructuredGloss extractGlossStructure(String rawGloss) {

		rawGloss = normalize(rawGloss);
		if (rawGloss.length() == 0) return null;
		simpleSenseNumberingMatcher.reset(rawGloss);
		if (simpleSenseNumberingMatcher.matches()) {
			String g = simpleSenseNumberingMatcher.group(2);
			if (g.trim().length() == 0) {
				g = null;
			}
			String n = simpleSenseNumberingMatcher.group(1);
			return new StructuredGloss(n, g);
		}
	
		senseNumOnlyMatcher.reset(rawGloss);
		if (senseNumOnlyMatcher.matches()) {
			String n = senseNumOnlyMatcher.group(1);
			return new StructuredGloss(n, null);
		}
	
		return new StructuredGloss(null, rawGloss);
	}
}
