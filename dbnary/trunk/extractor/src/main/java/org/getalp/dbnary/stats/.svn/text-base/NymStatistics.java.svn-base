package org.getalp.dbnary.stats;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.DBnaryOnt;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class NymStatistics {

	private static int countRelations(Property prop, Model m1) {
		ResIterator resit = m1.listResourcesWithProperty(prop);
		int nb = 0;

		while(resit.hasNext()) {
			resit.next();
			nb++;
		}
		resit.close();

		return nb;
	}
	
	public static void printStats(Model m1, String language, PrintWriter out, boolean verbose) {
		
		if (verbose) {
			out.println(getHeaders());
		}
		
		//out.print(ISO639_3.sharedInstance.inEnglish(language));
		out.print(countRelations(DBnaryOnt.synonym, m1));
		out.print("," + countRelations(DBnaryOnt.approximateSynonym, m1));
		out.print("," + countRelations(DBnaryOnt.antonym, m1));
		out.print("," + countRelations(DBnaryOnt.hypernym, m1));
		out.print("," + countRelations(DBnaryOnt.hyponym, m1));
		out.print("," + countRelations(DBnaryOnt.meronym, m1));
		out.print("," + countRelations(DBnaryOnt.holonym, m1));
        out.print("," + countRelations(DBnaryOnt.troponym, m1));

		out.flush();

	}

	public static String getHeaders() {
		return "syn,qsyn,ant,hyper,hypo,mero,holo,tropo";
	}

	public static void printStats(Model m1, String language, PrintWriter printWriter) {
		printStats(m1, language, printWriter, false);
	}
}
