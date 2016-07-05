package org.getalp.dbnary.experiment.preprocessing.eng;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {
	
	private static String aTrierRegExp = "to be checked";
	private static Pattern aTrierPattern = Pattern.compile(aTrierRegExp);
	private static Matcher aTrierMatcher = aTrierPattern.matcher("");

    private static String simpleSenseNumberingRegExp = "^([^\\|]*)\\|(\\d+)$";
    private static Pattern simpleSenseNumberingPattern = Pattern.compile(simpleSenseNumberingRegExp);
    private static Matcher simpleSenseNumberingMatcher = simpleSenseNumberingPattern.matcher("");

    private static String glossNumSenseNumberingRegExp = "^(.*)\\s*\\((\\d+)\\)(?:[\\p{Punct}\\s])*$";
    private static Pattern glossNumSenseNumberingPattern = Pattern.compile(glossNumSenseNumberingRegExp);
    private static Matcher glossNumSenseNumberingMatcher = glossNumSenseNumberingPattern.matcher("");

    private static String senseDashGlossRegExp = "^\\s*(\\d+)(?:[\\.])\\s*(.*)\\s*$";
    private static Pattern senseDashGlossPattern = Pattern.compile(senseDashGlossRegExp);
    private static Matcher senseDashGlossMatcher = senseDashGlossPattern.matcher("");

    public StructuredGloss extractGlossStructure(String rawGloss) {
   
		aTrierMatcher.reset(rawGloss);
		if (aTrierMatcher.find()) return null; // non relevant gloss should be discarded
		
		rawGloss = normalize(rawGloss);
        if (rawGloss.length() == 0) return null;
        
        
        
        simpleSenseNumberingMatcher.reset(rawGloss);
        if (simpleSenseNumberingMatcher.matches()) {
            return new StructuredGloss(simpleSenseNumberingMatcher.group(2), simpleSenseNumberingMatcher.group(1));
        }
		glossNumSenseNumberingMatcher.reset(rawGloss);
		if (glossNumSenseNumberingMatcher.matches()) {
			String g = glossNumSenseNumberingMatcher.group(1);
			if (null != g && g.trim().length() == 0) {
				g = null;
			}
			return new StructuredGloss(glossNumSenseNumberingMatcher.group(2), g);
		}
        senseDashGlossMatcher.reset(rawGloss);
        if (senseDashGlossMatcher.matches()) {
            return new StructuredGloss(senseDashGlossMatcher.group(1), senseDashGlossMatcher.group(2));
        }

        // if (rawGloss.matches(".*\\d.*")) System.err.println("Digit in gloss: " + rawGloss );

        return new StructuredGloss(null, rawGloss);
    }
}
