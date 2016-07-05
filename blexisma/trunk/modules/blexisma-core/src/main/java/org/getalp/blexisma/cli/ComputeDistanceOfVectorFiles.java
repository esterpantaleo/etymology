package org.getalp.blexisma.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.getalp.blexisma.api.ConceptualVector;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ComputeDistanceOfVectorFiles {

	private static Options options = null; // Command line options

	private static final String INPUT_FORMAT_OPTION = "f";
	private static final String DEFAULT_INPUT_FORMAT = "xml";

	private static final String DISTANCE_MEASURE_OPTION = "m";
	private static final String DEFAULT_DISTANCE_MEASURE = "dist";

	private CommandLine cmd = null; // Command Line arguments
	
	private String inputFormat = DEFAULT_INPUT_FORMAT;
	private String measure = DEFAULT_DISTANCE_MEASURE;
	private Document doc1;
	private Document doc2;
	
	private static String additionalMessage = "specify - as the vectorfile1/vectorfile2 to read one of the vectors from STDIN.\n";

	static{
		additionalMessage += "valid measures are: dist, sim, topicdist, topicsim.";
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		// options.addOption(INPUT_FORMAT_OPTION, true, "Specifies the input format (raw, xml). " + DEFAULT_INPUT_FORMAT + " by default.");	
		options.addOption(DISTANCE_MEASURE_OPTION, true, "Specifies the requested measure. " + DEFAULT_DISTANCE_MEASURE + " by default.");	
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) {
		ComputeDistanceOfVectorFiles cliProg = new ComputeDistanceOfVectorFiles();
		cliProg.loadArgs(args);
		cliProg.computeAndPrintDistance();
	}
		
	public void computeAndPrintDistance() {
		
		ConceptualVector v1 = readCV(doc1);
		ConceptualVector v2 = readCV(doc2);
		
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

	private static ConceptualVector readCV(Document doc) {
		int dim = Integer.parseInt(doc.getRootElement().getChild("dim").getTextTrim());
		int norm = Integer.parseInt(doc.getRootElement().getChild("norm").getTextTrim());
		String vect = doc.getRootElement().getChild("vect").getTextTrim();
		return new ConceptualVector(vect, dim, norm);
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

		if (cmd.hasOption(INPUT_FORMAT_OPTION)){
			inputFormat = cmd.getOptionValue(INPUT_FORMAT_OPTION);
		}

		if (cmd.hasOption(DISTANCE_MEASURE_OPTION)){
			measure = cmd.getOptionValue(DISTANCE_MEASURE_OPTION);
		}

		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 2) {
			printUsage();
			System.exit(1);
		}

		String infn = remainingArgs[0];
		try {
			if ("-".equals(infn)) {
				doc1 = new SAXBuilder().build(System.in);				
			} else {
				doc1 = new SAXBuilder().build(infn);
			}
		} catch (IOException e) {
			System.err.println("First vector could not be read due to an IO Error.");
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (JDOMException e) {
			System.err.println("First vector could not be read due to an XML parsing error.");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		String outfn = remainingArgs[1];
		try {
			if ("-".equals(outfn)) {
				if ("-".equals(infn)) {
					System.err.println("Only one vector can be read from stdin.");
					System.exit(1);
				}
				doc2 = new SAXBuilder().build(System.in);
			} else {
				doc2 = new SAXBuilder().build(outfn);
			}
		} catch (IOException e) {
			System.err.println("Second vector could not be read due to an IO Error.");
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (JDOMException e) {
			System.err.println("Second vector could not be read due to an XML parsing error.");
			e.printStackTrace(System.err);
			System.exit(1);
		}

	}
	
	 public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + ComputeDistanceOfVectorFiles.class.getCanonicalName() + " [OPTIONS] vectorfile1 vectorfile2", 
					"With OPTIONS in:", options, 
					additionalMessage, false);
	 }
}
