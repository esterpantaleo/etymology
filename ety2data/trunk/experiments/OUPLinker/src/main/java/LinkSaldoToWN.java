import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LinkSaldoToWN {

	private static Options options = null; // Command line options

	private static final String RDF_FORMAT_OPTION = "f";
	private static final String DEFAULT_RDF_FORMAT = "turtle";

	private static final String VERBOSE_FORMAT_OPTION = "v";

	private CommandLine cmd = null; // Command Line arguments

	private String outputFormat = DEFAULT_RDF_FORMAT;
	private boolean verbose = true;

	private Map<String, Resource> senseIdsMap = new HashMap<String, Resource>();
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(RDF_FORMAT_OPTION, true,
				"RDF format of the input file (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_RDF_FORMAT + " by default.");
		options.addOption(VERBOSE_FORMAT_OPTION, false,
				"print reconcile in verbose mode (i.e. with headers).");
	}	

	String[] remainingArgs;

	Model wn;
	Model saldo;

	String NS;

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
		if (cmd.hasOption("h")) {
			printUsage();
			System.exit(0);
		}

		if (cmd.hasOption(RDF_FORMAT_OPTION)) {
			outputFormat = cmd.getOptionValue(RDF_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();

        if (	outputFormat.equals("RDF") ||
                outputFormat.equals("TURTLE") ||
                outputFormat.equals("NTRIPLE") ||
                outputFormat.equals("N3") ||
                outputFormat.equals("TTL") ||
                outputFormat.equals("RDFABBREV") ) {

            verbose = cmd.hasOption(VERBOSE_FORMAT_OPTION);

            remainingArgs = cmd.getArgs();
            if (remainingArgs.length < 1) {
                printUsage();
                System.exit(1);
            }

            saldo = ModelFactory.createDefaultModel();

			System.err.println("Reading saldo model from " + remainingArgs[1]);
			saldo.read(remainingArgs[1]);
            saldo.setNsPrefix("wn31", "http://wordnet-rdf.princeton.edu/wn31/");
			// Load wordnet model
            System.err.println("Reading wordnet model from " + remainingArgs[0]);
            Dataset tdbDataset = TDBFactory.createDataset(remainingArgs[0]);
			tdbDataset.begin(ReadWrite.READ);
            wn = tdbDataset.getDefaultModel();

            // wn.read(remainingArgs[0]);



            System.err.println("Creating mappings...");
            createMappings(senseIdsMap);

            tdbDataset.close();

            mapToWN();

            saldo.write(System.out, outputFormat);

        }
    }

    private void mapToWN() {
        Resource lemonSenseType = saldo.getResource("http://lemon-model.net/lemon#LexicalSense");
        Property lemonRef = saldo.getProperty("http://lemon-model.net/lemon#", "reference");

        ResIterator resit = saldo.listResourcesWithProperty(RDF.type, lemonSenseType);
        int nbLinked =0 , nbUnlinked = 0;

        ArrayList<Statement> toBeRemoved = new ArrayList<>();
        ArrayList<Statement> toBeAdded = new ArrayList<>();

        while(resit.hasNext()) {
            RDFNode object = resit.next();

            if (object.isResource()) {
                StmtIterator referenceStmt = object.asResource().listProperties(lemonRef);
                while (referenceStmt.hasNext()) {
                    Statement stmt = referenceStmt.next();
                    if (stmt != null) {
                        String si = stmt.getLiteral().getString();
                        Resource synset = senseIdsMap.get(si);
                        if (synset != null) {
                            toBeRemoved.add(stmt);
                            toBeAdded.add(saldo.createStatement(object.asResource(), lemonRef, synset));
                            nbLinked++;
                        } else {
                            System.err.println("No wnid for " + object + "(" + si + ")");
                            nbUnlinked++;
                        }
                    } else {
                        System.err.println("No reference in LexicalSense for " + object);
                    }
                }
            }

        }
        saldo.remove(toBeRemoved);
        saldo.add(toBeAdded);
        System.err.println("Linked/Unlinked/Total");
        System.err.println(nbLinked + "/" + nbUnlinked + "/" + (nbLinked + nbUnlinked));

    }

    private void createMappings(Map<String, Resource> senseIdsMap) {
        Property oldSenseId = wn.getProperty("http://wordnet-rdf.princeton.edu/ontology#", "old_sense_key");
        Property lemonSense = wn.getProperty("http://lemon-model.net/lemon#", "sense");
        Property lemonRef = wn.getProperty("http://lemon-model.net/lemon#", "reference");
        NodeIterator resit = wn.listObjectsOfProperty(lemonSense);

        while(resit.hasNext()) {
            RDFNode object = resit.next();

            if (object.isResource()) {
                Statement senseIdStmt = object.asResource().getProperty(oldSenseId);
                if (senseIdStmt != null) {
                    String si = senseIdStmt.getLiteral().getString();
                    Resource synset = object.asResource().getPropertyResourceValue(lemonRef);
                    senseIdsMap.put(si, synset);
                } else {
                    System.err.println("No old sense key for " + object);
                }
            } else {
                System.err.println("Wornet lemon sense is not a resource for " + object);
            }

        }

    }


    private String getCode(Resource resource) {
		// TODO Auto-generated method stub
		return resource.getLocalName();
	}

	public static void main(String args[]) {
		LinkSaldoToWN cliProg = new LinkSaldoToWN();
		cliProg.loadArgs(args);
		cliProg.reconcile();
		
	}

	private void reconcile() {
        System.out.println(senseIdsMap.get(remainingArgs[1]));
	}
	
	private int countResourcesOfType(Resource type) {
		ResIterator resit = wn.listResourcesWithProperty(RDF.type, type);
		int nb = 0;
		while(resit.hasNext()) {
			nb++;
			resit.next();
		}
		resit.close();
		return nb;
	}

	private int countRelations(Property prop) {
		ResIterator resit = wn.listResourcesWithProperty(prop);
		int nb = 0;

		while(resit.hasNext()) {
			Resource rel = resit.next();

			nb++;
		}
		resit.close();

		return nb;
	}


	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help =
			"" +
			System.getProperty("line.separator", "\n") +
			"Attach proper synset ids to links with old sense keys in Saldo rdf.";
		formatter.printHelp("java -cp /path/to/dbnary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] wnfile saldofile",
				"With OPTIONS in:", options, 
				help, false);
	}

}
