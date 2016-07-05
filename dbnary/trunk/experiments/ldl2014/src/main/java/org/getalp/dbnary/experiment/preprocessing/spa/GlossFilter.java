package org.getalp.dbnary.experiment.preprocessing.spa;

import org.getalp.dbnary.experiment.preprocessing.AbstractGlossFilter;
import org.getalp.dbnary.experiment.preprocessing.StructuredGloss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossFilter extends AbstractGlossFilter {

	public static final String senseNumRegExp = "\\d+(?:[abcdefghijklmn][iv]*)?";
		
	public static final String simpleNumListFilter = "^\\s*(" + senseNumRegExp +"(?:\\s*[\\,\\-–]\\s*" + senseNumRegExp +")*)\\s*$";
	public static final Pattern simpleNumListPattern = Pattern.compile(simpleNumListFilter);
	public static final Matcher simpleNumListMatcher = simpleNumListPattern.matcher("");
	
	public StructuredGloss extractGlossStructure(String rawGloss) {
		
		simpleNumListMatcher.reset(rawGloss);
		if (simpleNumListMatcher.matches()) {
			return new StructuredGloss(simpleNumListMatcher.group(1), null);
		}
		return new StructuredGloss(null, rawGloss);
	}
}
