package org.getalp.dbnary.experiment.preprocessing;

public abstract class AbstractGlossFilter {

	public abstract StructuredGloss extractGlossStructure(String rawGloss);
	
	public static String normalize(String rawGloss) {
		String res = rawGloss.trim();
		res = res.replaceAll("\\s{2,}", " ");
		res = res.replaceAll("'''", "");
		res = res.replaceAll("''", "");
		
		return res;
	}

}
