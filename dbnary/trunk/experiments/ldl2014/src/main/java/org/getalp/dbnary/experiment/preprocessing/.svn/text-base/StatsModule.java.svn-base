package org.getalp.dbnary.experiment.preprocessing;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StatsModule {
	private class Stat {
		private int nbTranslations = 0;
		private int nbGlosses = 0;
		private Map<String,String> glossesWithSenseNumber;
		private int nbGlossesWithText = 0;
		private int nbGlossesWithSensNumberAndDescription = 0;

        {
            glossesWithSenseNumber = new HashMap<String,String>();
        }

		public void registerTranslation(String translationUri, StructuredGloss sg) {
			nbTranslations++;

			if (null != sg) {
				String senseNumbers = sg.getSenseNumber();
				String descr = sg.getGloss();
				if (null != senseNumbers || null != descr) {
					nbGlosses++;
					if (null != senseNumbers) {
						String sn = glossesWithSenseNumber.get(translationUri);
						if (null == sn) {
							glossesWithSenseNumber.put(translationUri, senseNumbers);
						} else {
							glossesWithSenseNumber.put(translationUri, sn + "," + senseNumbers);
						}
					}

					if (null != descr) {
						nbGlossesWithText++;
					}

					if (null != senseNumbers && null != descr) {
						nbGlossesWithSensNumberAndDescription++;
					}
				}
			}
		}

		public void displayStats(String lang, PrintStream w) {
				w.format("%s & $%d$ & $%d$ & $%d$ & $%d$ & $%d$ \\\\\n", lang, nbTranslations, nbGlosses, nbGlossesWithText, glossesWithSenseNumber.size(), nbGlossesWithSensNumberAndDescription);
		}
	}
	
	HashMap<String,Stat> stats = new HashMap<String,Stat>();
	Stat currentStat;
	
	public StatsModule() {
		super();
	}
	
	public void reset(String lang) {
		currentStat = new Stat();
		stats.put(lang, currentStat);
	}
	
	public void registerTranslation(String translationUri, StructuredGloss sg) {
		currentStat.registerTranslation(translationUri, sg);
	}

	public void displayStats(PrintStream w) {
		for (Entry<String, Stat> e : stats.entrySet()) {
			e.getValue().displayStats(e.getKey(), w);
		}
	}

}
