package org.getalp.dbnary.experiment.preprocessing.rus;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {
	
	private static String sensenum = "(?:\\d+)";
	
	private static String senseNumOnlyRegExp = "^\\s*(" + sensenum + ")\\s*$";
	private static Pattern senseNumOnlyPattern = Pattern.compile(senseNumOnlyRegExp);
	private static Matcher senseNumOnlyMatcher = senseNumOnlyPattern.matcher("");

	public StructuredGloss extractGlossStructure(String rawGloss) {

		rawGloss = normalize(rawGloss);
		if (rawGloss.length() == 0) return null;
	
		senseNumOnlyMatcher.reset(rawGloss);
		if (senseNumOnlyMatcher.matches()) {
			String n = senseNumOnlyMatcher.group(1);
			return new StructuredGloss(n, null);
		}
	
		return new StructuredGloss(null, rawGloss);
	}
}
