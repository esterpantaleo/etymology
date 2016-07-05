package org.getalp.dbnary.cli;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.cli.*;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.getalp.dbnary.*;
import org.getalp.dbnary.IWiktionaryDataHandler;
import static org.getalp.dbnary.IWiktionaryDataHandler.Feature;

public class ExtractWiktionary {

	private static Options options = null; // Command line options

	private static final String LANGUAGE_OPTION = "l";
	private static final String DEFAULT_LANGUAGE = "en";

	private static final String OUTPUT_FORMAT_OPTION = "f";
	private static final String DEFAULT_OUTPUT_FORMAT = "ttl";

	private static final String MODEL_OPTION = "m";
	private static final String DEFAULT_MODEL = "lemon";

	private static final String OUTPUT_FILE_OPTION = "o";
	private static final String DEFAULT_OUTPUT_FILE = "extract";
	
	private static final String SUFFIX_OUTPUT_FILE_OPTION = "s";

	private static final String COMPRESS_OPTION = "z";
	private static final String DEFAULT_COMPRESS = "no";
	
	private static final String FOREIGN_EXTRACTION_OPTION = "x";

    private static final String MORPHOLOGY_OUTPUT_FILE_LONG_OPTION = "morpho";
    private static final String MORPHOLOGY_OUTPUT_FILE_SHORT_OPTION = "M";


    public static final XMLInputFactory2 xmlif;


	private CommandLine cmd = null; // Command Line arguments
	
	private String outputFile = DEFAULT_OUTPUT_FILE;
    private String morphoOutputFile = null;
    private String outputFormat = DEFAULT_OUTPUT_FORMAT;
	private String model = DEFAULT_MODEL;
	private boolean compress;
	private String language = DEFAULT_LANGUAGE;
	private File dumpFile;
	private String outputFileSuffix = "";

	WiktionaryIndex wi;
	IWiktionaryExtractor we;

	private IWiktionaryDataHandler wdh;


	static {
		options = new Options();
		options.addOption("h", "help", false, "Prints usage and exits. ");
		options.addOption(SUFFIX_OUTPUT_FILE_OPTION, false, "Add a unique suffix to output file. ");	
		options.addOption(LANGUAGE_OPTION, true, 
				"Language (fra, eng, deu or por). " + DEFAULT_LANGUAGE + " by default.");
		options.addOption(OUTPUT_FORMAT_OPTION, true, 
				"Output format  (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_OUTPUT_FORMAT + " by default.");
		options.addOption(COMPRESS_OPTION, true, 
				"Compress the output using bzip2 (value: yes/no or true/false). " + DEFAULT_COMPRESS + " by default.");
		options.addOption(MODEL_OPTION, true, 
				"Ontology Model used  (lmf or lemon). Only useful with rdf base formats." + DEFAULT_MODEL + " by default.");
		options.addOption(OUTPUT_FILE_OPTION, true, "Output file. " + DEFAULT_OUTPUT_FILE + " by default ");
        options.addOption(OptionBuilder.withLongOpt(MORPHOLOGY_OUTPUT_FILE_LONG_OPTION)
                .withDescription( "Output file for morphology data. Undefined by default." )
                .hasArg()
                .withArgName("file")
                .create(MORPHOLOGY_OUTPUT_FILE_SHORT_OPTION) );
		options.addOption(FOREIGN_EXTRACTION_OPTION, false, "Extract foreign Languages");
	}
	
	static {
        try {
            xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);
        } catch (Exception ex) {
            System.err.println("Cannot intialize XMLInputFactory while classloading WiktionaryIndexer.");
            throw new RuntimeException("Cannot initialize XMLInputFactory", ex);
        }
    }


    /**
	 * @param args
	 * @throws IOException 
	 * @throws WiktionaryIndexerException 
	 */
	public static void main(String[] args) throws WiktionaryIndexerException, IOException {
		ExtractWiktionary cliProg = new ExtractWiktionary();
		cliProg.loadArgs(args);
		cliProg.extract();
	}
	
	/**
	 * Validate and set command line arguments.
	 * Exit after printing usage if anything is astray
	 * @param args String[] args as featured in public static void main()
	 * @throws WiktionaryIndexerException 
	 */
	private void loadArgs(String[] args) throws WiktionaryIndexerException {
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
		
		if (cmd.hasOption(SUFFIX_OUTPUT_FILE_OPTION)){
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			outputFileSuffix = df.format(new Date());
		}
		
		if (cmd.hasOption(OUTPUT_FORMAT_OPTION)){
			outputFormat = cmd.getOptionValue(OUTPUT_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();

		if (cmd.hasOption(MODEL_OPTION)){
			model = cmd.getOptionValue(MODEL_OPTION);
		}
		model = model.toUpperCase();

		String compress_value = cmd.getOptionValue(COMPRESS_OPTION, DEFAULT_COMPRESS);
		compress = "true".startsWith(compress_value) || "yes".startsWith(compress_value);

		if (cmd.hasOption(OUTPUT_FILE_OPTION)){
			outputFile = cmd.getOptionValue(OUTPUT_FILE_OPTION);
		}

        if (cmd.hasOption(MORPHOLOGY_OUTPUT_FILE_LONG_OPTION)){
            morphoOutputFile = cmd.getOptionValue(MORPHOLOGY_OUTPUT_FILE_LONG_OPTION);
        }

        if (cmd.hasOption(LANGUAGE_OPTION)){
			language = cmd.getOptionValue(LANGUAGE_OPTION);
			language = LangTools.getCode(language);
		}
		
		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 1) {
			printUsage();
			System.exit(1);
		}
		
		we = null;
		if (	outputFormat.equals("RDF") || 
				outputFormat.equals("TURTLE") ||
				outputFormat.equals("NTRIPLE") ||
				outputFormat.equals("N3") ||
				outputFormat.equals("TTL") ||
				outputFormat.equals("RDFABBREV") ) {
			if (model.equals("LEMON")) {
				if (cmd.hasOption(FOREIGN_EXTRACTION_OPTION)){
					wdh = WiktionaryDataHandlerFactory.getForeignDataHandler(language);
				} else {
					wdh = WiktionaryDataHandlerFactory.getDataHandler(language);
				}
			} else {
				System.err.println("LMF format not supported anymore.");
				System.exit(1);
			}
            if (morphoOutputFile != null) wdh.enableFeature(Feature.MORPHOLOGY);
		} else {
			System.err.println("unsupported format :" + outputFormat);
			System.exit(1);
		}

		if (cmd.hasOption(FOREIGN_EXTRACTION_OPTION)){
			we = WiktionaryExtractorFactory.getForeignExtractor(language, wdh);
		} else {
			we = WiktionaryExtractorFactory.getExtractor(language, wdh);
		}

		if (null == we) {
			System.err.println("Wiktionary Extraction not yet available for " + LangTools.inEnglish(language));
			System.exit(1);
		}
		
		wi = new WiktionaryIndex(remainingArgs[0]);
		we.setWiktionaryIndex(wi);
		
		outputFile = outputFile + outputFileSuffix;
		 
		dumpFile = new File(remainingArgs[0]);
	}
	
    public void extract() throws WiktionaryIndexerException, IOException {
        
        // create new XMLStreamReader

        long startTime = System.currentTimeMillis();
        long totalRelevantTime = 0, relevantStartTime = 0, relevantTimeOfLastThousands;
        int nbPages = 0, nbRelevantPages = 0;
        relevantTimeOfLastThousands = System.currentTimeMillis();

        XMLStreamReader2 xmlr = null;
        try {
            // pass the file name. all relative entity references will be
            // resolved against this as base URI.
            xmlr = xmlif.createXMLStreamReader(dumpFile);

            // check if there are more events in the input stream
            String title = "";
            String page = "";
            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && xmlr.getLocalName().equals(WiktionaryIndexer.pageTag)) {
                    title = "";
                    page = "";
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(WiktionaryIndexer.titleTag)) {
                    title = xmlr.getElementText();
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals("text")) {
                	page = xmlr.getElementText();
                } else if (xmlr.isEndElement() && xmlr.getLocalName().equals(WiktionaryIndexer.pageTag)) {
                	if (!title.equals("")) {               	
                        nbPages++;
                        int nbnodes = wdh.nbEntries();
                		we.extractData(title, page);
                		if (nbnodes != wdh.nbEntries()) {
                			totalRelevantTime += (System.currentTimeMillis() - relevantStartTime);
                			nbRelevantPages++;
                			if (nbRelevantPages % 1000 == 0) {
                				System.err.println("Extracted: " + nbRelevantPages + " pages in: " + totalRelevantTime + " / Average = "
                						+ (totalRelevantTime/nbRelevantPages) + " ms/extracted page (" + (System.currentTimeMillis() - relevantTimeOfLastThousands) / 1000 + " ms) (" + nbPages
                						+ " processed Pages in " + (System.currentTimeMillis() - startTime) + " ms / Average = " + (System.currentTimeMillis() - startTime) / nbPages + ")" );
                				// System.err.println("      NbNodes = " + s.getNbNodes());
                				relevantTimeOfLastThousands = System.currentTimeMillis();
                			}
                			// if (nbRelevantPages == 1100) break;
                		}	
                	}
                }
            }

            saveBox(Feature.MAIN, outputFile);
            System.err.println(nbPages + " entries extracted in : " + (System.currentTimeMillis() - startTime));
            System.err.println("Semnet contains: " + wdh.nbEntries() + " nodes.");
            if (null != morphoOutputFile) {
                saveBox(Feature.MORPHOLOGY, morphoOutputFile);
            }

        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());

            if (ex.getNestedException() != null) {
                ex.getNestedException().printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (xmlr != null)
                    xmlr.close();
            } catch (XMLStreamException ex) {
                ex.printStackTrace();
            }
        }
         

    }

    public void saveBox(IWiktionaryDataHandler.Feature f, String of) throws IOException {
        OutputStream ostream;
        if (compress) {
            // outputFile = outputFile + ".bz2";
            ostream = new BZip2CompressorOutputStream(new FileOutputStream(of));
        } else {
            ostream = new FileOutputStream(of);
        }
        try {
            System.err.println("Dumping " + outputFormat + " representation of " + f.name() + ".");
            if (outputFormat.equals("RDF")) {
                wdh.dump(f, new PrintStream(ostream, false, "UTF-8"), null);
            } else {
                wdh.dump(f, new PrintStream(ostream, false, "UTF-8"), outputFormat);
            }
        } catch (IOException e) {
            System.err.println("Caught IOException while printing extracted data: \n" + e.getLocalizedMessage());
            e.printStackTrace(System.err);
            throw e;
        } finally {
            if (null != ostream) {
                ostream.flush();
                ostream.close();
            }
        }
    }
    
    public static void printUsage() {
    	HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -cp /path/to/dbnary.jar org.getalp.dbnary.cli.ExtractWiktionary [OPTIONS] dumpFile", 
				"With OPTIONS in:", options, 
				"dumpFile must be a Wiktionary dump file in UTF-16 encoding. dumpFile directory must be writable to store the index.", false);
    }

}
