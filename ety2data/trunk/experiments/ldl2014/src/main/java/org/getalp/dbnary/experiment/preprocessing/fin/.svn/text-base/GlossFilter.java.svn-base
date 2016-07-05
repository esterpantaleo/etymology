package org.getalp.dbnary.experiment.preprocessing.fin;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {
	
	private static String sensenum = "(?:\\d+\\.?|\\s|,|/)+";
	private static String simpleSenseNumberingRegExp = "^((?:" + sensenum + "\\|?)+)(.*)$";
	private static Pattern simpleSenseNumberingPattern = Pattern.compile(simpleSenseNumberingRegExp);
	private static Matcher simpleSenseNumberingMatcher = simpleSenseNumberingPattern.matcher("");
		
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
			n = n.trim().replace('|', ',');
			if (n.endsWith(",")) {
				n = n.substring(0, n.length()-1);
			}
			return new StructuredGloss(n, g);
		}
		
		return new StructuredGloss(null, rawGloss);
	}
}
