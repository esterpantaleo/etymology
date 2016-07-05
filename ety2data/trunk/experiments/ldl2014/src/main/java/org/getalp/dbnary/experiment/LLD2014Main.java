package org.getalp.dbnary.experiment;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.wcohen.ss.Level2Levenstein;
import org.apache.commons.cli.*;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.ISO639_3.Lang;
import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.experiment.disambiguation.Ambiguity;
import org.getalp.dbnary.experiment.disambiguation.Disambiguable;
import org.getalp.dbnary.experiment.disambiguation.Disambiguator;
import org.getalp.dbnary.experiment.disambiguation.translations.MFSTranslationDisambiguator;
import org.getalp.dbnary.experiment.disambiguation.translations.TranslationAmbiguity;
import org.getalp.dbnary.experiment.disambiguation.translations.TranslationDisambiguator;
import org.getalp.dbnary.experiment.similarity.string.Level2Sim;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class LLD2014Main {

    private static final String LANGUAGE_OPTION = "l";
    private static final String MODEL_FILE_OPTION = "m";
    private static final String DEFAULT_LANGUAGE = "fr";
    private static Options options = null; // Command line op

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(LANGUAGE_OPTION, true,
                "Language (fra, eng, deu, por). " + DEFAULT_LANGUAGE + " by default.");
        options.addOption(MODEL_FILE_OPTION, true, "Input file in (xmlrdf, turtle, n3, etc.)");
    }

    private static Model model;
    private static Model outputModel;
    private CommandLine cmd = null; // Command Line arguments
    private Property senseNumProperty;
    private Property transNumProperty;

    {
        senseNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationSenseNumber");
        transNumProperty = DbnaryModel.tBox.getProperty(DBnaryOnt.getURI() + "translationNumber");
    }

    private Disambiguator disambiguator;
    private double deltaThreshold;
    // private Locale language;
    private String lang;
	private String NS;


    private LLD2014Main() {

        disambiguator = new TranslationDisambiguator();
        //for (double w1 = 0.1; w1 < 0.9; w1 += 0.1) {
        double w1 = 0.1;
        double w2 = 1d - w1;
        String mstr = String.format("_%f_%f", w1, w2);

        //disambiguator.registerSimilarity("FTiJW" + mstr, new TverskiIndex(w1, w2, true,false, new JaroWinklerUnicode()));
        //        disambiguator.registerSimilarity("FTiLs" + mstr, new TverskiIndex(w1, w2, true, false, new ScaledLevenstein()));
        //disambiguator.registerSimilarity("FTiME" + mstr, new TverskiIndex(w1, w2, true,false, new MongeElkan()));
        //disambiguator.registerSimilarity("FTiLcss" + mstr, new TverskiIndex(w1, w2, true,false));
        //disambiguator.registerSimilarity("FTi" + mstr, new TverskiIndex(w1, w2, false,false));
        //   disambiguator.registerSimilarity("L2Me" + mstr, new Level2Sim(new Level2MongeElkan()));
        disambiguator.registerSimilarity("L2Ls" + mstr, new Level2Sim(new Level2Levenstein()));
        // disambiguator.registerSimilarity("L2Jw" + mstr, new Level2Sim(new Level2JaroWinkler()));
        //}
    }

    public static void main(String[] args) throws IOException {

        LLD2014Main lld = new LLD2014Main();
        lld.loadArgs(args);

        //Store vts = new JenaMemoryStore(args[0]);
        //StoreHandler.registerStoreInstance(vts);

        for (double deltaT = 0.05; deltaT < .065 + .001d; deltaT += 0.05) {
            String message = String.format("Processing translation (Δt=%.2f)", deltaT);
            System.out.println(message);
            System.err.println();
            lld.setDeltaThreshold(deltaT);
            lld.processTranslations(model);
        }
    }

    public static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                "url must point on an RDF model file extracted from wiktionary and cleaned up (with sense numbers and translation numbers." +
                        System.getProperty("line.separator", "\n") +
                        "Disambiguates translation relations";
        formatter.printHelp("java -cp /path/to/wiktionary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url",
                "With OPTIONS in:", options,
                help, false);
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

        if (cmd.hasOption("h")) {
            printUsage();
            System.exit(0);
        }
        String modelFile = "";
        if (!cmd.hasOption(MODEL_FILE_OPTION)) {
            System.err.println("You must supply an input file (-m option)");
            printUsage();
            System.exit(1);
        } else {
            modelFile = cmd.getOptionValue(MODEL_FILE_OPTION);
        }

        lang = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
        Lang l = (ISO639_3.sharedInstance.getLang(lang));
        lang = (l.getPart1() != null) ? l.getPart1() : l.getId();
        String lang3 = ISO639_3.sharedInstance.getIdCode(lang);
        // language = Locale.forLanguageTag(lang);

        model = ModelFactory.createOntologyModel();
        model.read(modelFile);

		NS = DbnaryModel.DBNARY_NS_PREFIX + "/" + lang3 + "/";
		
        outputModel = ModelFactory.createOntologyModel();
        outputModel.setNsPrefixes(model.getNsPrefixMap());

    }

    private void processTranslations(Model m1) throws FileNotFoundException {


        MFSTranslationDisambiguator mfs = new MFSTranslationDisambiguator();

        String mfsFileName = String.format("%s_results_MFS.res", lang);
        String voteFileName = String.format("%s_results_Vote.res", lang);

        FileOutputStream mfsfos = new FileOutputStream(mfsFileName);
        PrintStream psmfs = new PrintStream(mfsfos, true);

        FileOutputStream votefos = new FileOutputStream(voteFileName);
        PrintStream psvote = new PrintStream(votefos, true);


        Map<String, PrintStream> streams = new HashMap<>();
        for (String m : disambiguator.getMethods()) {
            String fileName = String.format("%s_results_%s_Dl_%.2f.res", lang, m, deltaThreshold);
            FileOutputStream fos = new FileOutputStream(fileName);
            streams.put(m, new PrintStream(fos, true));
        }


        StmtIterator translations = m1.listStatements(null, DBnaryOnt.isTranslationOf, (RDFNode) null);


        while (translations.hasNext()) {
            Statement next = translations.next();

            Resource e = next.getSubject();

            Statement n = e.getProperty(transNumProperty);
            Statement s = e.getProperty(senseNumProperty);
            Statement g = e.getProperty(DBnaryOnt.gloss);

            boolean connected = false;
            if (null != s) {
            	// Process sense number
            	// System.out.println("Avoiding treating " + s.toString());
            	connected = connectNumberedSenses(s, outputModel);
            }
            if (!connected && null != n && null != g) {
                String gloss = g.getObject().toString();
                Ambiguity ambiguity = new TranslationAmbiguity(gloss, n.getObject().toString().split("\\^\\^")[0], deltaThreshold);
                String uri = g.getSubject().toString();
                Ambiguity mfcAmbiguity = new TranslationAmbiguity(gloss, n.getObject().toString().split("\\^\\^")[0]);
                Resource lexicalEntry = next.getObject().asResource();
                StmtIterator senses = m1.listStatements(lexicalEntry, LemonOnt.sense, (RDFNode) null);
                List<Disambiguable> choices = new ArrayList<>();
                int senseCounter = 1;
                while (senses.hasNext()) {
                    Statement nextSense = senses.next();
                    String sstr = nextSense.getObject().toString();
                    sstr = sstr.substring(sstr.indexOf("__ws_"));
                    Statement dRef = nextSense.getProperty(LemonOnt.definition);
                    Statement dVal = dRef.getProperty(LemonOnt.value);
                    String deftext = dVal.getObject().toString();
                    //choices.add(new DisambiguableSense(deftext, sstr,senseCounter));
                    senseCounter++;
                }
                disambiguator.disambiguate(ambiguity, choices);
                mfs.disambiguate(mfcAmbiguity, choices);
                for (String m : ambiguity.getMethods()) {
                    streams.get(m).println(ambiguity.toString(m));

                    Resource sense = outputModel.createResource(uri);
                    outputModel.add(outputModel.createStatement(sense, DBnaryOnt.isTranslationOf, outputModel.createResource(NS + ambiguity.getBestSolution(m).getId())));

                }
                psmfs.println(mfcAmbiguity.toString("MFS"));
                psvote.println(ambiguity.toStringVote());
            }

            //System.out.println(n.getObject().toString().split("\\^\\^")[0] + " 0 " + senseIds.get(num) + " " + rank);
        }
        System.out.println(mfs);
        psmfs.close();
        psvote.close();
        for (String m : disambiguator.getMethods()) {
            streams.get(m).close();
        }

        outputModel.write(new FileOutputStream(lang + "_disambiguated_translations.ttl"), "TTL");
    }


    private boolean connectNumberedSenses(Statement s, Model outModel) {
    	boolean connected = false;
		Resource translation = s.getSubject();
		Resource lexEntry = translation.getPropertyResourceValue(DBnaryOnt.isTranslationOf);
		String nums = s.getString();
		
		if (lexEntry.hasProperty(RDF.type, LemonOnt.LexicalEntry)) {
			ArrayList<String> ns = getSenseNumbers(nums);
			for (String n : ns) {
				connected = connected || attachTranslationToNumberedSense(translation, lexEntry, n, outModel);
			}
		}
		return connected;
	}

	private boolean attachTranslationToNumberedSense(Resource translation, Resource lexEntry, String n,
			Model outModel) {
		boolean connected = false;
		StmtIterator senses = lexEntry.listProperties(LemonOnt.sense);
		while (senses.hasNext()) {
			Resource sense = senses.next().getResource();
			Statement senseNumStatement = sense.getProperty(DBnaryOnt.senseNumber);
			if (n.equalsIgnoreCase(senseNumStatement.getString())) {
				connected = true;
				outModel.add(outModel.createStatement(translation, DBnaryOnt.isTranslationOf, sense));
			}
		}
		return connected;
	}

	public ArrayList<String> getSenseNumbers(String nums) {
		ArrayList<String> ns = new ArrayList<String>();
		
		if (nums.contains(",")) {
			String[] ni = nums.split(",");
			for (int i = 0; i < ni.length; i++) {
				ns.addAll(getSenseNumbers(ni[i]));
			}
		} else if (nums.contains("-") || nums.contains("—") || nums.contains("–")) {
			String[] ni = nums.split("[-—–]");
			if (ni.length != 2) {
				System.err.append("Strange split on dash: " + nums);
			} else {
				try {
					int s = Integer.parseInt(ni[0].trim());
					int e = Integer.parseInt(ni[1].trim());
					
					if (e <= s) {
						System.out.println("end of range is lower than beginning in: " + nums);
					} else {
						for (int i = s; i <= e ; i++) {
							ns.add(Integer.toString(i));
						}
					}
				} catch (NumberFormatException e) {
					System.err.println(e.getLocalizedMessage());
				}
			}
		} else {
			try {
				ns.add(nums.trim());
			}  catch (NumberFormatException e) {
				System.err.println(e.getLocalizedMessage() + ": " + nums);
			}
		}
		return ns;
	}

	public void setDeltaThreshold(double deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
    }
    
    
}


    /* select distinct count(?a),?a,?s where {?a ?r ?s. FILTER (regex(?r, "^.*nym$"))}
         */
