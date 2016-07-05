package org.getalp.dbnary.experiment.preprocessing;

import com.hp.hpl.jena.rdf.model.*;
import org.apache.commons.cli.*;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.ISO639_3.Lang;
import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.DbnaryModel;

import java.lang.reflect.InvocationTargetException;

public class PreprocessTranslations {

    private CommandLine cmd = null; // Command Line arguments
    private static Options options = null; // Command line options

    private static final String LANGUAGE_OPTION = "l";
    private static final String DEFAULT_LANGUAGE = "fr";

    private static final String OUTPUT_FORMAT_OPTION = "f";
    private static final String DEFAULT_OUTPUT_FORMAT = "turtle";

    private static final String CLEANUP_MODEL_OPTION = "c";
    private static final boolean DEFAULT_CLEANUP_MODEL = false;

    int numTrans = 0;

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(OUTPUT_FORMAT_OPTION, true,
                "Output format (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_OUTPUT_FORMAT + " by default.");
        options.addOption(LANGUAGE_OPTION, true,
                "Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
        options.addOption(CLEANUP_MODEL_OPTION, false,
                " output the cleaned up model in standard out. " + DEFAULT_CLEANUP_MODEL + " by default.");
    }

    String[] remainingArgs;
    Model m1;

    String NS;

    private String outputFormat = DEFAULT_OUTPUT_FORMAT;
    private String language = DEFAULT_LANGUAGE;
    private boolean cleanup = DEFAULT_CLEANUP_MODEL;

    private StatsModule stats;
    private String langName;
    private AbstractGlossFilter filter;
    private Property senseNumProperty;
    private Property transNumProperty;

    private void initializeTBox(String lang) {
        NS = DbnaryModel.DBNARY_NS_PREFIX + "/" + lang + "/";
        // TODO: create these adhoc properties another way. They are not part of the DBnary ontology
        senseNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationSenseNumber");
        transNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationNumber");
    }

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

        if (cmd.hasOption(OUTPUT_FORMAT_OPTION)) {
            outputFormat = cmd.getOptionValue(OUTPUT_FORMAT_OPTION);
        }
        outputFormat = outputFormat.toUpperCase();

        language = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
        Lang lg = ISO639_3.sharedInstance.getLang(language);
        language = lg.getId();
        langName = lg.getEn();

        cleanup = cmd.hasOption(CLEANUP_MODEL_OPTION);

        remainingArgs = cmd.getArgs();
        if (remainingArgs.length < 1) {
            printUsage();
            System.exit(1);
        }

        initializeTBox(language);
        //stats = new StatsModule(langName);
        filter = createGlossFilter(language);

        if (null == filter) {
            System.err.println("Could not instanciate Gloss filter for language: " + langName);
            printUsage();
            System.exit(1);
        }

        m1 = ModelFactory.createDefaultModel();


        if (outputFormat.equals("RDF") ||
                outputFormat.equals("TURTLE") ||
                outputFormat.equals("NTRIPLE") ||
                outputFormat.equals("N3") ||
                outputFormat.equals("TTL") ||
                outputFormat.equals("RDFABBREV")) {
            if ("-".equals(remainingArgs[0])) {
                System.err.println("Reading extract from stdin.");
                m1.read(System.in, outputFormat, "file:///dev/stdin");
            } else {
                System.err.println("Reading extract from " + remainingArgs[0]);
                m1.read(remainingArgs[0], outputFormat);
            }
        } else {
            System.err.println("unsupported format :" + outputFormat);
            System.exit(1);
        }
    }

    private AbstractGlossFilter createGlossFilter(String lang) {
        AbstractGlossFilter f = null;
        String cname = AbstractGlossFilter.class.getCanonicalName();
        int dpos = cname.lastIndexOf('.');
        String pack = cname.substring(0, dpos);
        Class<?> wec = null;
        try {
            wec = Class.forName(pack + "." + lang + ".GlossFilter");
            f = (AbstractGlossFilter) wec.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("No gloss filter found for " + lang+" reverting to default "+pack + ".DefaultGlossFilter");
            try {
                wec = Class.forName(pack + ".DefaultGlossFilter");
                f = (AbstractGlossFilter) wec.getConstructor().newInstance();
            } catch (ClassNotFoundException e1) {
                System.err.println("Default gloss filter not found");
            } catch (InvocationTargetException e1) {
                System.err.println("Default gloss filter failed to be instanciated");
            } catch (NoSuchMethodException e1) {
                System.err.println("Default gloss filter failed to be instanciated");
            } catch (InstantiationException e1) {
                System.err.println("Default gloss filter failed to be instanciated");
            } catch (IllegalAccessException e1) {
                System.err.println("Default gloss filter failed to be instanciated");
            }
        } catch (InstantiationException e) {
            System.err.println("Could not instanciate wiktionary extractor for " + lang);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access to wiktionary extractor for " + lang);
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument passed to wiktionary extractor's constructor for " + lang);
            e.printStackTrace(System.err);
        } catch (SecurityException e) {
            System.err.println("Security exception while instanciating wiktionary extractor for " + lang);
            e.printStackTrace(System.err);
        } catch (InvocationTargetException e) {
            System.err.println("InvocationTargetException exception while instanciating wiktionary extractor for " + lang);
            e.printStackTrace(System.err);
        } catch (NoSuchMethodException e) {
            System.err.println("No appropriate constructor when instanciating wiktionary extractor for " + lang);
        }
        return f;
    }

    public static void main(String args[]) {
        PreprocessTranslations cliProg = new PreprocessTranslations();
        cliProg.loadArgs(args);

        cliProg.processTranslations();

        cliProg.displayResults();
    }


    private void displayResults() {
        stats.displayStats(System.err);
        if (cleanup) {
            m1.write(System.out, outputFormat);
        }
    }

    private void processTranslations() {
        // Iterate over all translations

        StmtIterator translations = m1.listStatements((Resource) null, DBnaryOnt.isTranslationOf, (RDFNode) null);

        while (translations.hasNext()) {
            Resource e = translations.next().getSubject();

            if (cleanup) {
                // TODO: remove this cleanup option that was only used to create trec_eval compatible files
                numTrans++;
                m1.add(m1.createLiteralStatement(e, transNumProperty, numTrans));
            }
            Statement g = e.getProperty(DBnaryOnt.gloss);

            if (null == g) {
                stats.registerTranslation(e.getURI(), null);
            } else {
                StructuredGloss sg = filter.extractGlossStructure(g.getString());
                stats.registerTranslation(e.getURI(), sg);
                if (cleanup) {
                    if (null == sg) {
                        // remove gloss from model
                        g.remove();
                    } else {
                        if (null != sg.getSenseNumber()) {
                            g.getModel().add(g.getModel().createLiteralStatement(g.getSubject(), senseNumProperty, sg.getSenseNumber()));
                        }
                        if (null == sg.getGloss()) {
                            // remove gloss from model
                            g.remove();
                        } else {
                            g.changeObject(sg.getGloss());
                        }
                    }
                }
            }

        }
    }


    public static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                "url must point on an RDF model file extracted from wiktionary." +
                        System.getProperty("line.separator", "\n") +
                        "Displays stats on the LMF based RDF dump.";
        formatter.printHelp("java -cp /path/to/wiktionary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url",
                "With OPTIONS in:", options,
                help, false);
    }

}
