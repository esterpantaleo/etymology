import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.commons.cli.*;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by serasset on 17/06/15.
 */
public class SaldoLink {

    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(SaldoLink.class);
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separator + "ontology.properties";

    private static Options options = null; // Command line options

    private static final String RDF_FORMAT_OPTION = "f";
    private static final String DEFAULT_RDF_FORMAT = "turtle";

    private static final String VERBOSE_FORMAT_OPTION = "v";

    private CommandLine cmd = null; // Command Line arguments

    private String outputFormat = DEFAULT_RDF_FORMAT;
    private boolean verbose = true;

    private HashMap<Resource, TreeMap<Double, LexicalSense>> oupToDBnarySimilarities = new HashMap<>();

    Resource lemonSenseType ;
    Property lemonSenseProperty;
    Property lemonDefinitionProperty;
    Property lemonCanonicalForm;
    Property lemonWrittenRep;
    Property lemonValue;
    Property lemonReference;
    Property lexinfoPOS;
    Property wnsaldoFrequency;
    Property wnGloss;

    SimilarityMeasure similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
            .alpha(1d).beta(0).gamma(0).computeRatio(false).fuzzyMatching(false).normalize(true).regularizeOverlapInput(true).build();

    static{
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(RDF_FORMAT_OPTION, true,
                "RDF format of the input file (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_RDF_FORMAT + " by default.");
        options.addOption(VERBOSE_FORMAT_OPTION, false,
                "print reconcile in verbose mode (i.e. with headers).");
    }

    String[] remainingArgs;

    Model saldo;
    Model links;

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

            lemonSenseType = saldo.getResource("http://lemon-model.net/lemon#LexicalSense");
            lemonSenseProperty = saldo.getProperty("http://lemon-model.net/lemon#sense");
            lemonCanonicalForm = saldo.getProperty("http://lemon-model.net/lemon#canonicalForm");
            lemonWrittenRep = saldo.getProperty("http://lemon-model.net/lemon#writtenRep");
            lemonValue = saldo.getProperty("http://lemon-model.net/lemon#value");
            lemonReference = saldo.getProperty("http://lemon-model.net/lemon#reference");
            lemonDefinitionProperty = saldo.getProperty("http://lemon-model.net/lemon#definition");
            lexinfoPOS = saldo.getProperty("http://www.lexinfo.net/ontology/2.0/lexinfo#partOfSpeech");
            wnsaldoFrequency = saldo.getProperty("http://spraakbanken.gu.se/rdf/saldo/frequency");
            wnGloss = saldo.getProperty("http://wordnet-rdf.princeton.edu/ontology#gloss");

            links = ModelFactory.createDefaultModel();

            // Load wordnet model

            System.err.println("Reading Saldo+Wordnet model from " + remainingArgs[0]);
            Dataset tdbDataset = TDBFactory.createDataset(remainingArgs[0]);
            tdbDataset.begin(ReadWrite.READ);
            saldo = tdbDataset.getDefaultModel();

        }
    }

    private void computeSimilarities() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.err.println("Connecting to DBnary store");
        Store store = new JenaTDBStore("/Users/serasset/dev/wiktionary/dbnaryen.tdb");
        StoreHandler.registerStoreInstance(store);

        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        DBNary dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, new Language[]{Language.ENGLISH});

        System.err.println("Iterating over resource.");
        ResIterator resit = saldo.listResourcesWithProperty(wnsaldoFrequency);
        while(resit.hasNext()) {
            Resource sense = resit.next();

            computeSimilarity(sense, dbnary);

        }
        resit.close();

    }

    private void computeSimilarity(Resource sense, DBNary dbnary) {
        String oupDef = getDefinition(sense);
        Resource pos = getPOS(sense);

        if (oupDef == null) return;
        StringSemanticSignature wnSignature = new StringSemanticSignatureImpl(oupDef);

        String oupCanonicalForm = getCanonicalForm(sense);
        Vocable v = null;
        TreeMap<Double, LexicalSense> weightedDefs = new TreeMap<>();
        try {
            System.err.println("Querying DBnary for: " + oupCanonicalForm);
            v = dbnary.getVocable(oupCanonicalForm);
            List<LexicalEntry> entries = dbnary.getLexicalEntries(v);
            for (LexicalEntry le : entries) {
                System.err.println("LexEntry" + le);

                if (shouldIgnore(le, pos)) continue;
                List<LexicalSense> senses = dbnary.getLexicalSenses(le);
                for (LexicalSense s : senses) {
                    StringSemanticSignature dbnSignature = new StringSemanticSignatureImpl(s.getDefinition());

                    double sim = similarityMeasure.compute(wnSignature, dbnSignature);
                    weightedDefs.put(new Double(sim), s);
                }
            }
        } catch (NoSuchVocableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println(weightedDefs);
        oupToDBnarySimilarities.put(sense, weightedDefs);
    }

    private boolean shouldIgnore(LexicalEntry le, Resource pos) {
        String dbnaryPOS = le.getPartOfSpeech();

        if (dbnaryPOS != null && pos != null) return ! dbnaryPOS.equals(pos.getURI());

        return false;
    }

    private Resource getPOS(Resource sense) {
        StmtIterator statements = saldo.listStatements(null, lemonSenseProperty, sense);
        Resource pos = null;
        if (statements.hasNext()) {
            Statement stmt = statements.next();
            pos = stmt.getSubject().getPropertyResourceValue(lexinfoPOS);
        }
        if (null == pos) System.err.println("No POS for " + sense);
        if (statements.hasNext()) System.err.println("Multiple POS in " + sense);
        return pos;
    }

    private String getDefinition(Resource sense) {
        StmtIterator statements = saldo.listStatements(sense, lemonReference, (RDFNode) null);
        String definition = null;
        if (statements.hasNext()) {
            Statement stmt = statements.next();
            try {
                definition = stmt.getObject().asResource().getRequiredProperty(wnGloss).getLiteral().getString();
            } catch (ResourceRequiredException e) {
                e.printStackTrace();
            }
        }
        if (null == definition) System.err.println("No definition for " + sense);
        if (statements.hasNext()) System.err.println("Multiple definition in " + sense);
        return definition;
    }

    private Resource getReference(Resource sense) {
        StmtIterator statements = saldo.listStatements(sense, lemonReference, (RDFNode) null);
        Resource ref = null;
        if (statements.hasNext()) {
            Statement stmt = statements.next();
            try {
                ref = stmt.getObject().asResource();
            } catch (ResourceRequiredException e) {
                e.printStackTrace();
            }
        }
        if (null == ref) System.err.println("No definition for " + sense);
        if (statements.hasNext()) System.err.println("Multiple definition in " + sense);
        return ref;
    }

    private String getCanonicalForm(Resource saldoSense) {

        // Get wordnet synset
        StmtIterator statements = saldo.listStatements(saldoSense, lemonReference, (RDFNode) null);
        Resource wnSynset = null;
        if (statements.hasNext()) {
            Statement stmt = statements.next();
            wnSynset = stmt.getObject().asResource();
        }
        if (null == wnSynset) System.err.println("No definition for " + saldoSense);

        // get back a wordet sense.
        StmtIterator wnSenses = saldo.listStatements(null, lemonReference, wnSynset);
        Statement stmt = null;
        while (wnSenses.hasNext()) {
            stmt = wnSenses.next();
            if (stmt.getSubject().getURI().contains("wordnet")) break;
        }
        if (null == stmt) System.err.println("No wordnet sense for " + saldoSense);

        // Get back the wordnet lexical entries canonical form.
        Resource wnSense = stmt.getSubject();
        String form = null;
        StmtIterator wnEntries = saldo.listStatements(null, lemonSenseProperty, wnSense);
        stmt = null;
        if (wnEntries.hasNext()) {
            stmt = wnEntries.next();
            form = stmt.getSubject().asResource().getPropertyResourceValue(lemonCanonicalForm).getRequiredProperty(lemonWrittenRep).getLiteral().getString();
        }
        return form;
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SaldoLink cliProg = new SaldoLink();
        cliProg.loadArgs(args);
        cliProg.computeSimilarities();

        // put similarities in links model
        cliProg.dumpResults();
    }

    private void dumpResults() {
        for (Map.Entry<Resource, TreeMap<Double, LexicalSense>> resourceTreeMapEntry : oupToDBnarySimilarities.entrySet()) {
            for (Map.Entry<Double, LexicalSense> doubleNodeEntry : resourceTreeMapEntry.getValue().entrySet()) {
                System.out.print(getReference(resourceTreeMapEntry.getKey()) + "\t\"" + getDefinition(resourceTreeMapEntry.getKey()) + "\"\t");
                System.out.println(doubleNodeEntry.getKey() + "\t\"" + doubleNodeEntry.getValue().getDefinition() + "\"\t" + doubleNodeEntry.getValue().getNode());
            }
        }
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
