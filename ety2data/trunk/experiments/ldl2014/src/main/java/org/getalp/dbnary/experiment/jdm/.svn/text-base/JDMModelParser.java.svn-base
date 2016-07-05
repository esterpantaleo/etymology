package org.getalp.dbnary.experiment.jdm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.commons.cli.*;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.dbnary.experiment.jdm.model.JDMModel;

import java.io.*;

public class JDMModelParser {
    private static final String LANGUAGE_OPTION = "l";
    private static final String DEFAULT_LANGUAGE = "fr";
    private String language = DEFAULT_LANGUAGE;
    private static final String OUTPUT_FORMAT_OPTION = "f";
    private static final String DEFAULT_OUTPUT_FORMAT = "turtle";
    private String outputFormat = DEFAULT_OUTPUT_FORMAT;
    private static Options options = null; // Command line options
    String[] remainingArgs;

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(OUTPUT_FORMAT_OPTION, true,
                "Output format (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_OUTPUT_FORMAT + " by default.");
        options.addOption(LANGUAGE_OPTION, true,
                "Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
    }

    private BufferedReader jdmReader;
    private CommandLine cmd = null; // Command Line arguments
    private String langName;
    private Model m1;

    public static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                "url must point on an RDF model file extracted from wiktionary and cleaned up (with sense numbers and translation numbers." +
                        System.getProperty("line.separator", "\n") +
                        "Displays stats on the LMF based RDF dump.";
        formatter.printHelp("java -cp /path/to/wiktionary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url",
                "With OPTIONS in:", options,
                help, false);
    }

    public static void main(String[] args) throws IOException {
        JDMModelParser jdmmp = new JDMModelParser();
        jdmmp.loadArgs(args);
        jdmmp.extractJDM();
    }

    private void loadArgs(String[] args) throws FileNotFoundException {
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

        if (cmd.hasOption(OUTPUT_FORMAT_OPTION)) {
            outputFormat = cmd.getOptionValue(OUTPUT_FORMAT_OPTION);
        }
        outputFormat = outputFormat.toUpperCase();

        language = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
        ISO639_3.Lang lg = ISO639_3.sharedInstance.getLang(language);
        language = lg.getId();
        langName = lg.getEn();

        remainingArgs = cmd.getArgs();
        if (remainingArgs.length < 1) {
            printUsage();
            System.exit(1);
        }

        //initializeTBox();

        m1 = ModelFactory.createDefaultModel();


        if (outputFormat.equals("RDF") ||
                outputFormat.equals("TURTLE") ||
                outputFormat.equals("NTRIPLE") ||
                outputFormat.equals("N3") ||
                outputFormat.equals("TTL") ||
                outputFormat.equals("RDFABBREV")) {
            if ("-".equals(remainingArgs[0])) {
                System.err.println("Reading extract from stdin.");
                jdmReader = new BufferedReader(new InputStreamReader(System.in));
                //m1.read(System.in, outputFormat, "file:///dev/stdin");
            } else {
                System.err.println("Reading extract from " + remainingArgs[0]);
                jdmReader = new BufferedReader(new FileReader(remainingArgs[0]));
            }
        } else {
            System.err.println("unsupported format :" + outputFormat);
            System.exit(1);
        }
    }

    private void extractJDM() throws IOException {
        String line = "";
        JDMDumpSectionType currentSection = JDMDumpSectionType.NONE;
        while (null != (line = jdmReader.readLine())) {
            currentSection = JDMDumpSectionType.fromDumpLine(line, currentSection);
            switch(currentSection){
                case REL_TYPES:
                    parseRelTypeLine(line);
                    break;
                case NODES:
                    parseNodeLine(line);
                    break;
                case RELS:
                    parseRelationLine(line);
                    break;
            }
        }
    }

    public void parseRelTypeLine(String line){
        String id;
        String name;
        String extName;
        String info;

        String[] fields = line.split("|");
        id = fields[0].split("=")[1];
        name=fields[1].split("=")[1].replace("\"","");
        extName=fields[2].split("=")[1].replace("\"","");
        info = fields[3].split("=")[1].replace("\"","");

        Property currentProperty = JDMModel.tBox.getProperty(JDMModel.JDM_NS_PREFIX+"relation");
        JDMModel.jdmProperties.add(currentProperty);

        Resource r = m1.createResource(JDMModel.JDM_NS_PREFIX + "__relation_" + id + "_" + name + "__");

        m1.add(m1.createStatement(r, JDMModel.relationProperty, JDMModel.relation));
        m1.add(m1.createLiteralStatement(r,JDMModel.relationNameProperty,name));
        m1.add(m1.createLiteralStatement(r,JDMModel.relationExtNameProperty,extName));
        m1.add(m1.createLiteralStatement(r,JDMModel.relationInfoProperty,info));
    }

    public void parseNodeLine(String line){

    }

    public void parseRelationLine(String line){

    }


}
