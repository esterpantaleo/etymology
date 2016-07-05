package org.getalp.blexisma.wca;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import Dictionnary.Dictionary;
import Dictionnary.H2Dictionary;
import Dictionnary.RAMDict;

public class ExpandAndDisambiguate {
	
	private static Options options = null; // Command line options

	private static final String CONFIG_FILE_OPTION = "c";
	private static final String DEFAULT_CONFIG_FILE = "./config.xml";

	private CommandLine cmd = null; // Command Line arguments
	
	private String configFile = DEFAULT_CONFIG_FILE;

	private Dictionary dict;
	
	private VideoSenseContext ctxt;
	private String fname;
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(CONFIG_FILE_OPTION, true, 
				"Specify the configuration file path. " + DEFAULT_CONFIG_FILE + " by default.");
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) throws Exception {
		ExpandAndDisambiguate cliProg = new ExpandAndDisambiguate();
		cliProg.loadArgs(args);
		cliProg.process();
	}
	
	/**
	 * Validate and set command line arguments.
	 * Exit after printing usage if anything is astray
	 * @param args String[] args as featured in public static void main()
	 */
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
		
		if (cmd.hasOption(CONFIG_FILE_OPTION)){
			configFile = cmd.getOptionValue(CONFIG_FILE_OPTION);
		}
		
		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 2) {
			printUsage();
			System.exit(1);
		}

		if (remainingArgs[0].startsWith("jdbc:h2:")) {
    		dict = new H2Dictionary(remainingArgs[0]);
    	} else {
    		dict = new RAMDict(remainingArgs[0]);
    	}

		ctxt = new VideoSenseContext(configFile);
		fname = remainingArgs[1];
	}
	
	public void process() throws Exception {
		OpenFstTransducer g;

    	double time = System.currentTimeMillis();
		g = ctxt.desamb(dict, fname);
    	System.out.println(g.toString());
    	System.err.println(System.currentTimeMillis()-time);
	}
	
	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -cp /path/to/wiktionary.jar "+ ExpandAndDisambiguate.class.getCanonicalName() + " [OPTIONS] dictionary automatonFile", 
				"With OPTIONS in:", options, 
				"automatonFile is the result of an openfst morphological analysis.", false);
	}
}
