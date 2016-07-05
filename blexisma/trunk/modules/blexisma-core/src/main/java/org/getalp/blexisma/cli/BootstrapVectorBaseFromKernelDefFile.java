package org.getalp.blexisma.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.SemanticNetwork.Edge;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.SimpleSemanticNetwork;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;

public class BootstrapVectorBaseFromKernelDefFile {
	private static Options options = null; // Command line options

	private static final String INPUT_FILE_OPTION = "f";
	private static final String DEFAULT_INPUT_FILE = "-";

	private static final String NETWORK_FILE_OPTION = "n";
	private static final String DEFAULT_NETWORK_FILE = "network";

	private static final String OUTPUT_FILE_OPTION = "o";
	private static final String DEFAULT_OUTPUT_FILE = "bootstrapped.vb";

	private static final String ENCODING_SIZE_OPTION = "s";
	private static final int DEFAULT_ENCODING_SIZE = 32768;

	private CommandLine cmd = null; // Command Line arguments
	
	private static String additionalMessage = "specify - as the input file to read from STDIN.\n";

	static {
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		// options.addOption(INPUT_FORMAT_OPTION, true, "Specifies the input format (raw, xml). " + DEFAULT_INPUT_FORMAT + " by default.");	
		options.addOption(INPUT_FILE_OPTION, true, "Specifies the file containing the list of definitions (1 per line). " + DEFAULT_INPUT_FILE + " by default.");	
		options.addOption(NETWORK_FILE_OPTION, true, "Specifies the file containing the list of definitions (1 per line). " + DEFAULT_INPUT_FILE + " by default.");	
		options.addOption(OUTPUT_FILE_OPTION, true, "Specifies the name of the resulting vectorial base file. " + DEFAULT_OUTPUT_FILE + " by default.");	
		options.addOption(ENCODING_SIZE_OPTION, true, "Specifies the encoding size of the vectorial base. " + DEFAULT_ENCODING_SIZE + " by default.");	
	}
	
	private ArrayList<String> defs;
	
	private String inputFile = DEFAULT_INPUT_FILE;
	private String outputFile = DEFAULT_OUTPUT_FILE;
	private int encodingSize = DEFAULT_ENCODING_SIZE;
	private SemanticNetwork<String, String> network;

	private String networkFile = DEFAULT_NETWORK_FILE;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) {
		BootstrapVectorBaseFromKernelDefFile cliProg = new BootstrapVectorBaseFromKernelDefFile();
		cliProg.loadArgs(args);
		cliProg.bootstrapBase();
	}
	
	private void bootstrapBase() {
		// TODO: extract a set of lemma in a file, get the defs from the wiktionary network...
		String_RAM_VectorialBase vb = new String_RAM_VectorialBase(encodingSize, defs.size());
		for (int i = 0; i<defs.size(); i++) {
			ConceptualVector cv = new ConceptualVector(defs.size(), encodingSize);
			cv.setElementAt(i, encodingSize);
			cv.normalise();

			Collection<? extends SemanticNetwork<String, String>.Edge> edges = network.getEdges(defs.get(i));
			if (null != edges) {
				for (SemanticNetwork<String, String>.Edge edge : edges) {
					if (edge.getRelation().equals("def")) {
						String def = edge.getDestination();
						vb.addVector(def, cv);
					}
				}
			}
		}
		vb.save(outputFile);
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

		if (cmd.hasOption(INPUT_FILE_OPTION)){
			inputFile = cmd.getOptionValue(INPUT_FILE_OPTION);
		}

		if (cmd.hasOption(NETWORK_FILE_OPTION)){
			networkFile = cmd.getOptionValue(NETWORK_FILE_OPTION);
		}

		if (cmd.hasOption(OUTPUT_FILE_OPTION)){
			outputFile = cmd.getOptionValue(OUTPUT_FILE_OPTION);
		}

		if (cmd.hasOption(ENCODING_SIZE_OPTION)){
			encodingSize = Integer.parseInt(cmd.getOptionValue(ENCODING_SIZE_OPTION));
		}

		try {
			if ("-".equals(inputFile)) {
				readDefinitionList(System.in);
			} else {
				readDefinitionList(new FileInputStream(inputFile));
			}
		} catch (IOException e) {
			System.err.println("Lemma list could not be read due to an IO Error.");
			e.printStackTrace(System.err);
			System.exit(1);
		} 
		
		
		try {
			this.network = TextOnlySemnetReader.loadNetwork(networkFile);
			System.err.println("Loaded network with " + network.getNbEdges() + " edges.");
		} catch (IOException e) {
			System.err.println("Network could not be read due to an IO Error.");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	 private void readDefinitionList(InputStream in) throws IOException {
		BufferedReader brdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		defs = new ArrayList<String>();
		String curLine = null;
		while ((curLine = brdr.readLine()) != null) {
			defs.add("#fra|" + curLine);
		}
	}


	public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + BootstrapVectorBaseFromKernelDefFile.class.getCanonicalName() + " [OPTIONS] ", 
					"With OPTIONS in:", options, 
					additionalMessage, false);
	 }
}
