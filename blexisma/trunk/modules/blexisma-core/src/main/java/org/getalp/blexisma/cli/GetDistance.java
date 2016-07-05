package org.getalp.blexisma.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetDistance {

	private static Options options = null; // Command line options

	private static final String VECTOR_BASE_FILE_OPTION = "v";
	private static final String DEFAULT_VECTOR_BASE_FILE = "vb";

	private static final String DISTANCE_MEASURE_OPTION = "m";
	private static final String DEFAULT_DISTANCE_MEASURE = "dist";

	private CommandLine cmd = null; // Command Line arguments
	
	private String vbfile = DEFAULT_VECTOR_BASE_FILE;
	private String measure = DEFAULT_DISTANCE_MEASURE;
	private String id1, id2;

	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(VECTOR_BASE_FILE_OPTION, true, "Specifies the name of the vector base file. " + DEFAULT_VECTOR_BASE_FILE + " by default.");	
		options.addOption(DISTANCE_MEASURE_OPTION, true, "Specifies the requested measure. " + DEFAULT_DISTANCE_MEASURE + " by default.");	
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) {
		GetDistance cliProg = new GetDistance();
		cliProg.loadArgs(args);
		cliProg.computeAndPrintDistance();
	}
		
	public void computeAndPrintDistance() {
		
		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(vbfile);
		ConceptualVector v1 = vb.getVector(id1);
		ConceptualVector v2 = vb.getVector(id2);
		
		if ("dist".equals(measure)) 
			System.out.println("angular distance=" + v1.getAngularDistance(v2));
		else if ("sim".equals(measure))
			System.out.println("cosine similarity=" + v1.getCosineSimilarity(v2));
		else if ("topicdist".equals(measure))
			System.out.println("topic distance=" + v1.getTopicDistance(v2, 0.5));
		else if ("topicsim".equals(measure))
			System.out.println("topic similarity=" + v1.getTopicSimilarity(v2, 0.5));
		else { 
			System.err.println("Unrecognized measure: " + measure);
			printUsage();
			System.exit(1);
		}
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

		if (cmd.hasOption(VECTOR_BASE_FILE_OPTION)){
			vbfile = cmd.getOptionValue(VECTOR_BASE_FILE_OPTION);
		}

		if (cmd.hasOption(DISTANCE_MEASURE_OPTION)){
			measure = cmd.getOptionValue(DISTANCE_MEASURE_OPTION);
		}

		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 2) {
			printUsage();
			System.exit(1);
		}

		id1 = remainingArgs[0];
		id2 = remainingArgs[1];

	}
	
	 public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + GetDistance.class.getCanonicalName() + " [OPTIONS] vectorfile1 vectorfile2", 
					"With OPTIONS in:", options, 
					null, false);
	 }
}
