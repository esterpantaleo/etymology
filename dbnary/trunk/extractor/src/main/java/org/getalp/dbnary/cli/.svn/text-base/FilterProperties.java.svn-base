package org.getalp.dbnary.cli;

import com.hp.hpl.jena.rdf.model.*;
import org.apache.commons.cli.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FilterProperties {
	
	private static Options options = null; // Command line options

	private static final String KEEP_PROPERTIES_OPTION = "i";

	private static final String RDF_FORMAT_OPTION = "f";
	private static final String DEFAULT_RDF_FORMAT = "turtle";	

	private static final String REMOVE_PROPERTIES_OPTION = "o";

	private static final String VERBOSE_OPTION = "v";
    private static final String UNREACHABLE_ANONS_OPTION = "a";

	private CommandLine cmd = null; // Command Line arguments

	private String outputFormat = DEFAULT_RDF_FORMAT;
	private Set<Property> keepProperties = null;
    private Set<Property> removeProperties = null;
	private boolean verbose;
    private boolean removeAnons;

    private int nbr = 0, nbt = 0;

	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(KEEP_PROPERTIES_OPTION, true,
				"Comma separated list of properties to keep.");
		options.addOption(REMOVE_PROPERTIES_OPTION, true,
				"Comma separated list of properties to remove.");
        options.addOption(RDF_FORMAT_OPTION, true,
                "RDF format of the input file (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_RDF_FORMAT + " by default.");
        options.addOption(UNREACHABLE_ANONS_OPTION, false,
                "Remove anon objects of filtered out properties.");
		options.addOption(VERBOSE_OPTION, false,
				"print stats on the number of properties filtered in/out.");
	}	

	String[] remainingArgs;

	Model m;

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

		if (cmd.hasOption(RDF_FORMAT_OPTION)){
			outputFormat = cmd.getOptionValue(RDF_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();

        String[] keepArgs = null;
        if (cmd.hasOption(KEEP_PROPERTIES_OPTION)) {
            keepArgs = cmd.getOptionValue(KEEP_PROPERTIES_OPTION).split("[,;]");
        }

        String[] removeArgs = null;
        if (cmd.hasOption(REMOVE_PROPERTIES_OPTION)) {
            removeArgs = cmd.getOptionValue(REMOVE_PROPERTIES_OPTION).split("[,;]");
        }

        if (keepArgs == null && removeArgs == null) {
            System.err.println("Either -i or -o option should be specified.");
            printUsage();
            System.exit(1);
        }

        if (keepArgs != null && removeArgs != null) {
            System.err.println("-i or -o options should be specified together.");
            printUsage();
            System.exit(1);
        }

        verbose = cmd.hasOption(VERBOSE_OPTION);
        removeAnons = cmd.hasOption(UNREACHABLE_ANONS_OPTION);

		remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 1) {
            System.err.println("Missing url.");
			printUsage();
			System.exit(1);
		}
		
		m = ModelFactory.createDefaultModel();
		
		if (	outputFormat.equals("RDF") || 
				outputFormat.equals("TURTLE") ||
				outputFormat.equals("NTRIPLE") ||
				outputFormat.equals("N3") ||
				outputFormat.equals("TTL") ||
				outputFormat.equals("RDFABBREV") ) {
			if ("-".equals(remainingArgs[0])) {
				if (verbose) System.err.println("Reading extract from stdin.");
				m.read(System.in, "file:///dev/stdin", outputFormat);
			} else {
				if (verbose) System.err.println("Reading extract from " + remainingArgs[0]);
				m.read(remainingArgs[0], outputFormat);
			}
		} else {
			System.err.println("unsupported format :" + outputFormat);
			System.exit(1);
		}

        // Initialize the set of properties to keep or remove
        keepProperties = parsePropertiesArg(keepArgs);
        removeProperties = parsePropertiesArg(removeArgs);

    }

    private Set<Property> parsePropertiesArg(String[] args) {
        Set<Property> props = new HashSet<Property>();
        if (args != null) {
            for (String keepArg : args) {
                if (keepArg.startsWith("http://")) {
                    props.add(m.getProperty(keepArg));
                } else {
                    // Assume it's a name with a prefix defined in the model
                    String[] prefixAndName = keepArg.split(":");
                    if (prefixAndName.length > 2) {
                        System.err.println("Malformed property: " + keepArg);
                    } else if (prefixAndName.length == 2) {
                        String namespace = m.getNsPrefixURI(prefixAndName[0]);
                        props.add(m.getProperty(namespace, prefixAndName[1]));
                    } else {
                        props.add(m.getProperty(prefixAndName[0]));
                    }
                }
            }
        }
        return props;
    }

	public static void main(String args[]) {
		FilterProperties cliProg = new FilterProperties();
		cliProg.loadArgs(args);
		cliProg.filter();
	}

	private void filter() {
        StmtIterator resit = m.listStatements();
        List<Statement> toBeRemoved = new LinkedList<Statement>();
        while(resit.hasNext()) {
            Statement s = resit.nextStatement();
            if ((! removeProperties.isEmpty() && removeProperties.contains(s.getPredicate())) ||
                    (! keepProperties.isEmpty() && ! keepProperties.contains(s.getPredicate()))) {
                toBeRemoved.add(s);
                Resource o = s.getResource();
                if (removeAnons && o.isAnon()) {
                    StmtIterator os = m.listStatements(o, (Property)null, (RDFNode)null);
                    while(os.hasNext()) {
                        Statement oss = os.nextStatement();
                        toBeRemoved.add(oss);
                        nbr++;
                    }
                }
                nbr++;
            }
            nbt++;
        }
        resit.close();
        m.remove(toBeRemoved);
        if (verbose)
            System.err.println("Filtered: " + (nbt - nbr) + " kept/" + nbr + " removed");
        m.write(System.out, outputFormat);
    }
	

	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help = 
			"url must point on an RDF model file (use - to read from stdin)." +
			System.getProperty("line.separator", "\n") +
			"Filter properties from the model and dumps the filtered model to stdin." +
            System.getProperty("line.separator", "\n") +
            "either -i or -o option should be specified, but not both.";
		formatter.printHelp("java -cp /path/to/wiktionary.jar org.getalp.dbnary.cli.FilterProperties [OPTIONS] url",
				"With OPTIONS in:", options, 
				help, false);
	}

}
