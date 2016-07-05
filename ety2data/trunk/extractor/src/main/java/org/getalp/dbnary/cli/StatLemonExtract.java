package org.getalp.dbnary.cli;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.DbnaryModel;

import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.LemonOnt;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class StatLemonExtract extends DbnaryModel {

	protected class IncrementableInt {
		int val;
		
		public IncrementableInt() {
			val = 0;
		}
		
		public IncrementableInt(int val) {
			this.val = val;
		}
		
		public void incr() {
			this.val++;
		}
		
		public void incr(int step) {
			this.val = this.val + step;
		}
		
		public String toString() {
			return Integer.toString(this.val);
		}
	}
	
	private static Options options = null; // Command line options

	private static final String LANGUAGE_OPTION = "l";
	private static final String DEFAULT_LANGUAGE = "fra";

	private static final String RDF_FORMAT_OPTION = "f";
	private static final String DEFAULT_RDF_FORMAT = "turtle";	

	private static final String COUNT_LANGUAGE_OPTION = "c";
	private static final String DEFAULT_COUNT_LANGUAGE = "eng,fra,deu,por";	

	private static final String STATS_FORMAT_OPTION = "s";
	private static final String DEFAULT_STATS_FORMAT = "latex";	
	
	private static final String VERBOSE_FORMAT_OPTION = "v";

	private CommandLine cmd = null; // Command Line arguments

	private String outputFormat = DEFAULT_RDF_FORMAT;
	private String statsFormat = DEFAULT_STATS_FORMAT;
	private String language = DEFAULT_LANGUAGE;
	private String countLanguages = DEFAULT_COUNT_LANGUAGE;
	private boolean verbose = true;

	// TODO: extract iso code from lexvo entity.
	private SortedMap<String, IncrementableInt> counts = new TreeMap<String,IncrementableInt>();
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(LANGUAGE_OPTION, true, 
				"Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
		options.addOption(RDF_FORMAT_OPTION, true, 
				"RDF format of the input file (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_RDF_FORMAT + " by default.");
		options.addOption(STATS_FORMAT_OPTION, true, 
				"Output format of the stats (latex or csv). " + DEFAULT_STATS_FORMAT + " by default.");
		options.addOption(COUNT_LANGUAGE_OPTION, true, 
				"Languages to count (as a comma separated list). " + DEFAULT_COUNT_LANGUAGE + " by default.");
		options.addOption(VERBOSE_FORMAT_OPTION, false, 
				"print stats in verbose mode (i.e. with headers).");
	}	

	String[] remainingArgs;

	Model m1;
	
	String NS;

	private String comma;
	private String nl;

	
	private void initializeTBox(String lang) {
		NS = DBNARY_NS_PREFIX + "/" + lang + "/";
	}
	
	private void loadArgs(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing arguments: " + e.getLocalizedMessage());
			printUsage();
			System.exit(1);
		}

		// Check for args
		if (cmd.hasOption("h")){
			printUsage();
			System.exit(0);
		}

		if (cmd.hasOption(RDF_FORMAT_OPTION)){
			outputFormat = cmd.getOptionValue(RDF_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();

		if (cmd.hasOption(STATS_FORMAT_OPTION)){
			statsFormat = cmd.getOptionValue(STATS_FORMAT_OPTION);
		}
		statsFormat = statsFormat.toUpperCase();

		if (cmd.hasOption(LANGUAGE_OPTION)) {
			language = cmd.getOptionValue(LANGUAGE_OPTION);
			language = LangTools.getCode(language);
		}

		if (cmd.hasOption(COUNT_LANGUAGE_OPTION)){
			countLanguages = cmd.getOptionValue(COUNT_LANGUAGE_OPTION);
		}
		String clangs[] = countLanguages.split(",");
		int i = 0;
		while(i != clangs.length) {
			counts.put(LangTools.getCode(clangs[i]), new IncrementableInt());
			i = i + 1;
		}

		verbose = cmd.hasOption(VERBOSE_FORMAT_OPTION);
		
		remainingArgs = cmd.getArgs();
		if (remainingArgs.length < 1) {
			printUsage();
			System.exit(1);
		}
		
		initializeTBox(language);
		m1 = ModelFactory.createDefaultModel();
		
		if (	outputFormat.equals("RDF") || 
				outputFormat.equals("TURTLE") ||
				outputFormat.equals("NTRIPLE") ||
				outputFormat.equals("N3") ||
				outputFormat.equals("TTL") ||
				outputFormat.equals("RDFABBREV") ) {
			if ("-".equals(remainingArgs[0])) {
				System.err.println("Reading extract from stdin.");
				m1.read(System.in, "file:///dev/stdin", outputFormat);
			} else {
				System.err.println("Reading extract from " + remainingArgs[0]);
				m1.read(remainingArgs[0], outputFormat);
			}
		} else {
			System.err.println("unsupported format :" + outputFormat);
			System.exit(1);
		}
	}


	private String getCode(Resource resource) {
		// TODO Auto-generated method stub
		return resource.getLocalName();
	}

	public static void main(String args[]) {
		StatLemonExtract cliProg = new StatLemonExtract();
		cliProg.loadArgs(args);
		cliProg.stats();
		
	}

	private void stats() {
		
		if ("LATEX".equals(statsFormat)) System.out.println("Stats on RDF file: " + remainingArgs[0]);
		
		// Number of Lexical Entries

		int nble = countResourcesOfType(LemonOnt.LexicalEntry);
		int nblv = countResourcesOfType(DBnaryOnt.Vocable);
		int nblw = countResourcesOfType(LemonOnt.Word);
		int nblp = countResourcesOfType(LemonOnt.Phrase);
		
				
		int nbEquiv = countResourcesOfType(DBnaryOnt.Translation);
		int nbsense = countResourcesOfType(LemonOnt.LexicalSense);
		comma = ("LATEX".equals(statsFormat)) ?  " & " : ",";
		nl = ("LATEX".equals(statsFormat)) ?  "\\\\" : "";
		
		if (verbose) {
			System.out.print("Language Edition" + comma + "Entries" + comma + "Vocables" + comma + "Senses" + comma + "Translations");
			System.out.println(nl);
		}

		
		if ("LATEX".equals(statsFormat)) 
			System.out.print("\\textbf{" + language  + "}");
		else 
			System.out.print(LangTools.inEnglish(language));
		System.out.print(comma);
		System.out.print("" + (nble + nblw + nblp));
		System.out.print(comma);
		System.out.print(nblv);
		System.out.print(comma);
		System.out.print(nbsense);
		System.out.print(comma);
		System.out.print(nbEquiv);
		System.out.println(nl);

		System.out.println("");
		
		if (verbose) {
			System.out.print("Language Edition" + comma + "syn" + comma + "qsyn" + comma + "ant" + comma + "hyper" + comma + "hypo" + comma + "mero" + comma + "holo");
			System.out.println(nl);
		}
		
		if ("LATEX".equals(statsFormat)) 
			System.out.print("\\textbf{" + language  + "}");
		else 
			System.out.print(LangTools.inEnglish(language));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.synonym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.approximateSynonym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.antonym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.hypernym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.hyponym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.meronym));
		System.out.print(comma);
		System.out.print(countRelations(DBnaryOnt.holonym));
		System.out.println(nl);
		System.out.println("");
		System.out.println("");

		printTranslationsStats();
	}
	
	private int countResourcesOfType(Resource type) {
		ResIterator resit = m1.listResourcesWithProperty(RDF.type, type);
		int nb = 0;
		while(resit.hasNext()) {
			nb++;
			resit.next();
		}
		resit.close();
		return nb;
	}

	private int countRelations(Property prop) {
		ResIterator resit = m1.listResourcesWithProperty(prop);
		int nb = 0;

		while(resit.hasNext()) {
			Resource rel = resit.next();

			nb++;
		}
		resit.close();

		return nb;
	}

	private void printTranslationsStats() {
		// Number of relations
		ResIterator relations = m1.listResourcesWithProperty(RDF.type, DBnaryOnt.Translation);
		HashSet<String> langs = new HashSet<String>();
		int others = 0;
		while(relations.hasNext()) {
			Resource r = relations.next();
            // TODO: Also count targetLanguageCode properties
			Statement t = r.getProperty(DBnaryOnt.targetLanguage);
			if (null != t) {
				RDFNode lang = t.getObject();
				langs.add(getCode(lang.asResource()));
				if (counts.containsKey(getCode(lang.asResource()))) {
					counts.get(getCode(lang.asResource())).incr();
				} else {
					others = others + 1;
				}
			}
		}
		relations.close();

		int total = 0;

		
		if (verbose) {
			for (Entry<String, IncrementableInt> i : counts.entrySet()) {
				total = total + i.getValue().val;
				System.out.print(comma + i.getKey());
			}
			System.out.print(comma + "others" + comma + "Total");
			System.out.println(nl);
		}
		
		total = total + others;
		
		if ("LATEX".equals(statsFormat)) 
			System.out.print("\\textbf{" + language  + "}");
		else 
			System.out.print(LangTools.inEnglish(language));
		for (Entry<String, IncrementableInt> i : counts.entrySet()) {
			System.out.print(comma + i.getValue().val);
		}
		System.out.print(comma + others + comma + total);
		System.out.println(nl);

		System.out.println("-------------------------");
		System.out.println(langs.size() + " different target languages.");
		for (String l : langs) {
			System.out.print(l + " ");
		}
		
		
	}

	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help = 
			"url must point on an RDF model file extracted from wiktionary." +
			System.getProperty("line.separator", "\n") +
			"Displays stats on the LMF based RDF dump.";
		formatter.printHelp("java -cp /path/to/dbnary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url", 
				"With OPTIONS in:", options, 
				help, false);
	}

}
