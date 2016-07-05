package org.getalp.dbnary.experiment.preprocessing;

import com.hp.hpl.jena.rdf.model.*;
import org.apache.commons.cli.*;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.ISO639_3.Lang;
import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.LemonOnt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateGoldStandard {
	
	private CommandLine cmd = null; // Command Line arguments
	private static Options options = null; // Command line options
	
	private static final String LANGUAGE_OPTION = "l";
	private static final String DEFAULT_LANGUAGE = "fr";

	private static final String OUTPUT_FORMAT_OPTION = "f";
	private static final String DEFAULT_OUTPUT_FORMAT = "turtle";	

	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(OUTPUT_FORMAT_OPTION, true, 
				"Output format (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_OUTPUT_FORMAT + " by default.");
		options.addOption(LANGUAGE_OPTION, true, 
				"Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
	}	

	String[] remainingArgs;
	Model m1;
	
	String NS;
	
	private String outputFormat = DEFAULT_OUTPUT_FORMAT;
	private String language = DEFAULT_LANGUAGE;

	private String langName;
	private Property senseNumProperty;
	private Property transNumProperty;

	private void initializeTBox(String lang) {
		NS = DbnaryModel.DBNARY_NS_PREFIX + "/" + lang + "/";
		senseNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationSenseNumber");
		transNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationNumber");
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

		if (cmd.hasOption(OUTPUT_FORMAT_OPTION)){
			outputFormat = cmd.getOptionValue(OUTPUT_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();		

		language = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
		Lang lg = ISO639_3.sharedInstance.getLang(language);
		language = lg.getId();
		langName = lg.getEn();

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
				m1.read(System.in, outputFormat, "file:///dev/stdin");
			} else {
				System.err.println("Reading extract from " + remainingArgs[0]);
				m1.read(remainingArgs[0], outputFormat);
			}
		} else {
			System.err.println("unsupported format :" + outputFormat);
			System.exit(1);
		}
	}

	public static void main(String args[]) {
		CreateGoldStandard cliProg = new CreateGoldStandard();
		cliProg.loadArgs(args);
		
		cliProg.processTranslations();
		
		cliProg.displayResults();
	}
	
	
	private void displayResults() {

	}

	
	private void processTranslations() {
		// Iterate over all translations
		
		StmtIterator translations = m1.listStatements((Resource) null, DBnaryOnt.isTranslationOf, (RDFNode) null);
		
		while (translations.hasNext()) {
			Statement isTransOf = translations.next();
			Resource e = isTransOf.getSubject();
			
			Statement n = e.getProperty(transNumProperty);
			Statement s = e.getProperty(senseNumProperty);
			Statement g = e.getProperty(DBnaryOnt.gloss);
			
			if (null != s && null != g) {
				String sn = s.getString();
				
				List<String> nums = parseSenseNumbers(sn);
				
				if (! nums.isEmpty()) {
					// Fetch all entries senses and select the correct ones.
					Resource entry = isTransOf.getResource();
					StmtIterator senses = entry.listProperties(LemonOnt.sense);
					
					while (senses.hasNext()) {
						Resource sens = senses.next().getResource();
						String sensenum = sens.getProperty(DBnaryOnt.senseNumber).getString();
						if (nums.contains(sensenum)) {
							String localName = sens.getURI();
							int k = localName.indexOf("__ws_");
							localName = localName.substring(k);
							System.out.format("%d 0 %s 1\n", n.getInt(), localName);
						}
					}
				}
			}
		}
	}
	

	Pattern onlyDigitsAndComma = Pattern.compile("[\\d,]*");
	Matcher matchDigitAndComma = onlyDigitsAndComma.matcher("");
	private List<String> parseSenseNumbers(String sn) {
		ArrayList<String> res = new ArrayList<>();
		sn = sn.trim();
		if (sn.length() == 0) return res;
		String senses = sn.replaceAll(" ","");
		matchDigitAndComma.reset(senses);
		if (matchDigitAndComma.matches()) {
			String[] senseNums = senses.split(",");
			for (int i = 0; i < senseNums.length; i++) {
				res.add(senseNums[i]);
			}
		} else {
			System.err.println("Unsupported format: " + sn);
		}
		return res;
	}

	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help = 
			"url must point on an RDF model file extracted from wiktionary and cleaned up (with sense numbers and translation numbers." +
			System.getProperty("line.separator", "\n") +
			"Displays stats on the LMF based RDF dump.";
		formatter.printHelp("java -cp /path/to/wiktionary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url", 
				"With OPTIONS in:", options, 
				help, false);
	}

}
