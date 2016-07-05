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

public class ComputeStatsOnVectorFile {

	private static Options options = null; // Command line options

	private static final String INPUT_FORMAT_OPTION = "f";
	private static final String DEFAULT_INPUT_FORMAT = "xml";

	private static final String COEFVAR_OPTION = "c";
	private static final String MAGNITUDE_OPTION = "M";
	private static final String MEAN_OPTION = "m";
	private static final String STANDARD_DEVIATION_OPTION = "d";
	private static final String VARIANCE_OPTION = "v";
	private static final String DISTANCE_TO_MEDIAN_OPTION = "D";
	private static final String ALL_OPTION = "a";

	private static final int COEFVAR = 2;
	private static final int MAGNITUDE = 4;
	private static final int MEAN = 8;
	private static final int STANDARD_DEVIATION = 16;
	private static final int VARIANCE = 32;
	private static final int DISTANCE_TO_MEDIAN = 64;

	
	private CommandLine cmd = null; // Command Line arguments
	
	private String inputFormat = DEFAULT_INPUT_FORMAT;
	private int measures = 0; 
	private Document doc1;
	
	private static String additionalMessage = "specify - as the vectorfile to read the vectors from STDIN.\n";

	static{
		additionalMessage += "valid measures are: dist, sim, topicdist, topicsim.";
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(COEFVAR_OPTION, false, "compute coefficient of variation of the vectors components.");	
		options.addOption(MAGNITUDE_OPTION, false, "compute magnitude of the vector.");	
		options.addOption(MEAN_OPTION, false, "compute the mean of the vectors components.");	
		options.addOption(STANDARD_DEVIATION_OPTION, false, "compute the standard deviation of the vectors components.");	
		options.addOption(VARIANCE_OPTION, false, "compute variance of the vectors components.");	
		options.addOption(DISTANCE_TO_MEDIAN_OPTION, false, "compute angular distance to median vector.");	
		options.addOption(ALL_OPTION, false, "compute all the measures for the vector. ");	
		// options.addOption(INPUT_FORMAT_OPTION, true, "Specifies the input format (raw, xml). " + DEFAULT_INPUT_FORMAT + " by default.");	
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) {
		ComputeStatsOnVectorFile cliProg = new ComputeStatsOnVectorFile();
		cliProg.loadArgs(args);
		cliProg.computeAndPrintDistance();
	}
		
	public void computeAndPrintDistance() {
		
		ConceptualVector v1 = readCV(doc1);
		
		if ((measures & COEFVAR) == COEFVAR) 
			System.out.println("coefficient of variation=" + v1.coeffVar());
		if ((measures & MAGNITUDE) == MAGNITUDE)
			System.out.println("magnitude=" + v1.getMagnitude());
		if ((measures & MEAN) == MEAN)
			System.out.println("mean=" + v1.mean());
		if ((measures & STANDARD_DEVIATION) == STANDARD_DEVIATION)
			System.out.println("standard deviation=" + v1.standardDeviation());
		if ((measures & VARIANCE) == VARIANCE)
			System.out.println("variance=" + v1.variance());
		if ((measures & DISTANCE_TO_MEDIAN) == DISTANCE_TO_MEDIAN)
			System.out.println("distance to median=" + v1.getAngularDistance(medianVector(v1.getDimension(), v1.getCodeLength())));
	}

	private ConceptualVector medianVector(int dim, int codelength) {
		ConceptualVector cv = new ConceptualVector(dim, codelength);
		for (int i = 0; i < dim; i++) {
			cv.setElementAt(i, (int) Math.round(codelength / Math.sqrt(dim)));
		}
		return cv;
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

		if (cmd.hasOption(INPUT_FORMAT_OPTION)) {
			inputFormat = cmd.getOptionValue(INPUT_FORMAT_OPTION);
		}

		if (cmd.hasOption(COEFVAR_OPTION)) {
			measures = measures | COEFVAR;
		}

		if (cmd.hasOption(MAGNITUDE_OPTION)) {
			measures = measures | MAGNITUDE;
		}
		
		if (cmd.hasOption(MEAN_OPTION)) {
			measures = measures | MEAN;
		}
		
		if (cmd.hasOption(STANDARD_DEVIATION_OPTION)) {
			measures = measures | STANDARD_DEVIATION;
		}
		
		if (cmd.hasOption(VARIANCE_OPTION)) {
			measures = measures | VARIANCE;
		}

		if (cmd.hasOption(DISTANCE_TO_MEDIAN_OPTION)) {
			measures = measures | DISTANCE_TO_MEDIAN;
		}

		if (cmd.hasOption(ALL_OPTION)) {
			measures = measures | COEFVAR | MEAN | MAGNITUDE | STANDARD_DEVIATION | VARIANCE | DISTANCE_TO_MEDIAN;
		}

		if (measures == 0) {
			measures = COEFVAR;
		}
		
		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 1) {
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
	}
	
	 public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + ComputeStatsOnVectorFile.class.getCanonicalName() + " [OPTIONS] vectorfile", 
					"With OPTIONS in:", options, 
					additionalMessage, false);
	 }
}
