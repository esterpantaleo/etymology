package org.getalp.blexisma.wca;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import Dictionnary.Dictionary;
import Dictionnary.H2Dictionary;
import Dictionnary.RAMDict;

public class ExpandAndDisambiguateCorpus {
	
	private static Options options = null; // Command line options

	private static final String CONFIG_FILE_OPTION = "c";
	private static final String DEFAULT_CONFIG_FILE = "./config.xml";

	private static final String NBTHREAD_OPTION = "t";
	private static final int DEFAULT_NBTHREAD = 4;

	private static final String OUTPUT_DIR_OPTION = "o";
	private static final String DEFAULT_OUTPUT_DIR = ".";

	private CommandLine cmd = null; // Command Line arguments
	
	private String configFile = DEFAULT_CONFIG_FILE;
	private int nbthread = DEFAULT_NBTHREAD;
	private String outputDir = DEFAULT_OUTPUT_DIR;

	private Dictionary dict;
	
	private String dir;
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(CONFIG_FILE_OPTION, true, 
				"Specify the configuration file path. " + DEFAULT_CONFIG_FILE + " by default.");
		options.addOption(NBTHREAD_OPTION, true, 
				"Specify the number of parallel thread to process the corpus. " + DEFAULT_NBTHREAD + " by default.");
		options.addOption(OUTPUT_DIR_OPTION, true, 
				"Specify the directory where disambiguated files will be written. " + DEFAULT_OUTPUT_DIR + " by default.");
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) throws Exception {
		ExpandAndDisambiguateCorpus cliProg = new ExpandAndDisambiguateCorpus();
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
		
		if (cmd.hasOption(CONFIG_FILE_OPTION)) {
			configFile = cmd.getOptionValue(CONFIG_FILE_OPTION);
		}

		if (cmd.hasOption(NBTHREAD_OPTION)) {
			String n = cmd.getOptionValue(NBTHREAD_OPTION);
			nbthread = Integer.parseInt(n);
		}

		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 2) {
			printUsage();
			System.exit(1);
		}

    	dict = new H2Dictionary(remainingArgs[0]);

		dir = remainingArgs[1];
	}
	
	public void process() throws Exception {
		ExecutorService execSvc = Executors.newFixedThreadPool( 4 );
		
		File d = new File(dir);
		
		File[] files = d.listFiles();
		for (File f : files) {
			String r = outputDir + File.separator + f.getName();
			execSvc.execute(new SingleTextProcessor(configFile, dict, f.getPath(), r));
		}
        execSvc.shutdown();
	}
	
	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -cp /path/to/wiktionary.jar "+ ExpandAndDisambiguateCorpus.class.getCanonicalName() + " [OPTIONS] dictionary corpusDir", 
				"With OPTIONS in:", options, 
				"corpusDir contains the results of an openfst morphological analysis.", false);
	}
}
