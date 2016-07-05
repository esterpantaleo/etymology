package org.getalp.blexisma.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.knallgrau.utils.textcat.TextCategorizer;

public class DetectLanguage {

	private static Options options = null; // Command line options

	private static final String INPUT_ENCODING_OPTION = "e";
	private static final String DEFAULT_INPUT_ENCODING = "utf-8";

	private CommandLine cmd = null; // Command Line arguments
	
	private String inputEncoding = DEFAULT_INPUT_ENCODING;

	private InputStreamReader ir;
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(INPUT_ENCODING_OPTION, true, "specify the encoding of the input");	
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DetectLanguage cliProg = new DetectLanguage();
		cliProg.loadArgs(args);
		cliProg.detectLanguage();
		
	}

	private void detectLanguage() throws IOException {
		String category = "please enter a command line argument.";
		String content = read(1000);
        TextCategorizer guesser = new TextCategorizer();
        category = guesser.categorize(content);
        System.out.println(category);

	}

	private String read(int limit) throws IOException {
		StringBuffer bf = new StringBuffer();
		BufferedReader br = new BufferedReader(ir);

		String l = br.readLine();
		int cpt = 0;
		while (l != null && cpt < limit) {
			bf.append(l); bf.append("\n");
			cpt += l.length() + 1;
			l = br.readLine();
		} 
		return bf.toString();
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

		if (cmd.hasOption("h")){
			printUsage();
			System.exit(0);
		}

		if (cmd.hasOption(INPUT_ENCODING_OPTION)) {
			inputEncoding = cmd.getOptionValue(INPUT_ENCODING_OPTION);
		}

		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 1) {
			printUsage();
			System.exit(1);
		}

		String infn = remainingArgs[0];
		try {
			if ("-".equals(infn)) {
				 ir = new InputStreamReader(System.in, inputEncoding);
			} else {
				ir = new InputStreamReader(new FileInputStream(infn), inputEncoding);
			}
		} catch (FileNotFoundException e) {
			System.err.println(infn + ": file not found.");
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported encoding \""+ inputEncoding + "\".");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	 public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + DetectLanguage.class.getCanonicalName() + " [OPTIONS] textfile", 
					"With OPTIONS in:", options, 
					"specify - as textfile to read data from stdin.", false);
	 }
}
