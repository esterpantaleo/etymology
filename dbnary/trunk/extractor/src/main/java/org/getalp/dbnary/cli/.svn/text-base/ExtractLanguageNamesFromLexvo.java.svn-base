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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.getalp.dbnary.LangTools;

public class ExtractLanguageNamesFromLexvo {

	
	private static Options options = null; // Command line options

	private static final String LANGUAGE_OPTION = "l";
	private static final String DEFAULT_LANGUAGE = "pol";
	

	private CommandLine cmd = null; // Command Line arguments

	private String language = DEFAULT_LANGUAGE;
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(LANGUAGE_OPTION, true, 
				"Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
	}	

	String[] remainingArgs;

	Model lexvo;
	
	private String comma;
	private String nl;

		
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

		language = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
		language = LangTools.getPart1(language);
		if (language == null || language.equals("")) {
			printUsage();
			System.exit(0);
		}

		remainingArgs = cmd.getArgs();
		if (remainingArgs.length < 1) {
			printUsage();
			System.exit(1);
		}

		lexvo = ModelFactory.createDefaultModel();

		if ("-".equals(remainingArgs[0])) {
			System.err.println("Reading lexvo dump from stdin.");
			lexvo.read(System.in, "file:///dev/stdin", "RDF/XML");
		} else {
			System.err.println("Reading lexvo dump from " + remainingArgs[0]);
			lexvo.read(remainingArgs[0], "RDF/XML");
		}
	}

	public static void main(String args[]) {
		ExtractLanguageNamesFromLexvo cliProg = new ExtractLanguageNamesFromLexvo();
		cliProg.loadArgs(args);
		cliProg.extract();
		
	}

	static final String lvont = "http://lexvo.org/ontology#";
	
	private void extract() {
		Resource languageType = lexvo.getResource(lvont + "Language");
		ResIterator resit = lexvo.listResourcesWithProperty(RDF.type, null);
		
		while(resit.hasNext()) {
			Resource lang = resit.next();
			if (! lang.getNameSpace().equals("http://lexvo.org/id/iso639-3/")) continue;
			StmtIterator labels = lang.listProperties(RDFS.label);
			while(labels.hasNext()) {
				Statement label = labels.next();
				if (label.getLanguage().equals(language)) {
					System.out.println(label.getString().toLowerCase() + "\t" + lang.getLocalName());
				}
				
			}
			
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
