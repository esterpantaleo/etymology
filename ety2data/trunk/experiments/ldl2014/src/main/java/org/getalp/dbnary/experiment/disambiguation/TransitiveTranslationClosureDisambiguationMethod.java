package org.getalp.dbnary.experiment.disambiguation;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.LemonOnt;
import org.getalp.dbnary.LexinfoOnt;
import org.getalp.dbnary.experiment.similarity.string.TverskiIndex;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransitiveTranslationClosureDisambiguationMethod implements
        DisambiguationMethod {

    private final double delta;
    int degree = 0;

    {
        wordDistribution = new HashMap<>();
    }

    private TverskiIndex tversky = new TverskiIndex(.5, .5, true, false, new ScaledLevenstein());
    private Map<String, Double> wordDistribution;
    private Map<String, Model> models;
    private String lang;

    public TransitiveTranslationClosureDisambiguationMethod(int degree, String lang, Map<String, Model> models, double delta) {
        this.degree = degree;
        this.models = models;
        this.lang = lang;
        this.delta = delta;
    }

    private String getTranslationLanguage(String uri) {
        String lang = "";
        Pattern lp = Pattern.compile(".*__tr_(...)_[0-9].*");
        Matcher lm = lp.matcher(uri);
        if (lm.find()) {
            lang = lm.group(1);
        }
        return lang;
    }

    StmtIterator getTranslationLexicalEntryStmtIterator(Resource translation, String currentLang) {
        String writtenForm = translation.getProperty(DBnaryOnt.writtenForm).getObject().toString();
        String uri = DbnaryModel.DBNARY_NS_PREFIX + "/" + currentLang + "/" + DbnaryModel.uriEncode(writtenForm).split("@")[0];
        Resource r = models.get(currentLang).getResource(uri);
        return models.get(currentLang).listStatements(r, DBnaryOnt.refersTo, (RDFNode) null);
    }

    private List<String> computeTranslationClosure(Resource translation, String pos, int degree) {
        String topLevelLang = lang;
        String currentLang = getTranslationLanguage(translation.getURI());
        List<String> output = new ArrayList<>();
        List<List<String>> lowerLevelOutput = new ArrayList<>();
        if (degree != 0 && models.containsKey(currentLang)) {
            StmtIterator lexEntries = getTranslationLexicalEntryStmtIterator(translation, currentLang);
            while (lexEntries.hasNext()) {
                Statement lnext = lexEntries.next();
                Statement stmtPos = lnext.getObject().asResource().getProperty(LexinfoOnt.partOfSpeech);
                String foreignpos = null;
                if (stmtPos != null) {
                    foreignpos = stmtPos.getObject().toString();
                }
                if (pos == null || (pos != null && foreignpos != null && pos.equals(foreignpos))) {
                    RDFNode lexEntryNode = lnext.getObject();
                    //Find translations pointing back to top level lang
                    StmtIterator trans = models.get(currentLang).listStatements(null, DBnaryOnt.isTranslationOf, lexEntryNode);
                    while (trans.hasNext()) {
                        Statement ctransstmt = trans.next();
                        Resource ctrans = ctransstmt.getSubject();
                        String l = getTranslationLanguage(ctrans.getURI());
                        if (l.equalsIgnoreCase(topLevelLang)) { // Back to topLevel
                            StmtIterator backLex = getTranslationLexicalEntryStmtIterator(ctrans, l);
                            while (backLex.hasNext()) {
                                Statement backLexnext = backLex.next();
                                Statement lexcfp = backLexnext.getProperty(LemonOnt.canonicalForm);
                                String writtenRep = "";
                                if (lexcfp != null) {
                                    Statement wrepstmt = lexcfp.getProperty(LemonOnt.writtenRep);
                                    writtenRep = wrepstmt.getObject().toString();
                                }
                                if (writtenRep.contains("@")) {
                                    output.add(writtenRep.split("@")[0]);
                                } else {
                                    output.add(writtenRep);
                                }
                                //Iterating senses
                                StmtIterator senses = backLexnext.getObject().asResource().listProperties(LemonOnt.sense);
                                while (senses.hasNext()) {
                                    Statement nextSense = senses.next();
                                    Resource wordsense = nextSense.getResource();
                                    Statement dRef = wordsense.getProperty(LemonOnt.definition);
                                    Statement dVal = dRef.getProperty(LemonOnt.value);
                                    String deftext = dVal.getObject().toString();
                                    if (deftext.contains("@")) {
                                        output.add(deftext.split("@")[0]);
                                    } else {
                                        output.add(deftext);
                                    }
                                }
                            }
                        } else { //Recurse away!
                            List<String> recSol = computeTranslationClosure(ctrans, pos, degree - 1);
                            //lowerLevelOutput.add(recSol);
                            output.addAll(recSol);
                        }
                    }
                }
            }
        }
        ArrayList<WeigthedDef> scores = new ArrayList<>();
        for(int i=0;i<lowerLevelOutput.size();i++){
            scores.add(new WeigthedDef(tversky.compute(output,lowerLevelOutput.get(i)),lowerLevelOutput.get(i)));
        }
        //Collections.sort(scores);
        //List<WeigthedDef> scoresSelected = scores.subList(0,(int)Math.ceil(scores.size()*0.1));
        List<WeigthedDef> scoresSelected = scores;
        for(WeigthedDef wd: scoresSelected){
            output.addAll(wd.def);
        }
        return output;
    }

    @Override
    public Set<Resource> selectWordSenses(Resource lexicalEntry,
                                          Object context) throws InvalidContextException,
            InvalidEntryException {
        HashSet<Resource> res = new HashSet<Resource>();

        if (!lexicalEntry.hasProperty(RDF.type, LemonOnt.LexicalEntry) &&
                !lexicalEntry.hasProperty(RDF.type, LemonOnt.Word) &&
                !lexicalEntry.hasProperty(RDF.type, LemonOnt.Phrase))
            throw new InvalidEntryException("Expecting a LEMON Lexical Entry.");
        if (context instanceof Resource) {
            Resource trans = (Resource) context;
            if (!trans.hasProperty(RDF.type, DBnaryOnt.Translation))
                throw new InvalidContextException("Expecting a DBnary Translation Resource.");

            List<String> closure = computeTranslationClosure(trans, null, this.degree);

            // If the definition is empty we don't need to compute any similarities
            if (closure.isEmpty()) return res;

            StringBuilder concatAll = new StringBuilder();
            for (String item : closure) {
                concatAll.append(item + " ");
            }


            ArrayList<WeigthedSense> weightedList = new ArrayList<WeigthedSense>();

            StmtIterator senses = lexicalEntry.listProperties(LemonOnt.sense);
            while (senses.hasNext()) {
                Statement nextSense = senses.next();
                Resource wordsense = nextSense.getResource();
                Statement dRef = wordsense.getProperty(LemonOnt.definition);
                Statement dVal = dRef.getProperty(LemonOnt.value);
                String deftext = dVal.getObject().toString();
                double sim = tversky.compute(deftext, concatAll.toString());
                insert(weightedList, wordsense, sim);
            }

            if (weightedList.size() == 0) return res;

            int i = 0;
            double worstScore = weightedList.get(0).weight - delta;
            while (i != weightedList.size() && weightedList.get(i).weight >= worstScore) {
                System.err.println(weightedList.get(i).sense.getURI());
                res.add(weightedList.get(i).sense);
                i++;
            }
        } else {
            throw new InvalidContextException("Expecting a JENA Resource.");
        }

        return res;
    }

    private void insert(ArrayList<WeigthedSense> weightedList,
                        Resource wordsense, double sim) {
        weightedList.add(null);
        int i = weightedList.size() - 1;
        while (i != 0 && weightedList.get(i - 1).weight < sim) {
            weightedList.set(i, weightedList.get(i - 1));
            i--;
        }
        weightedList.set(i, new WeigthedSense(sim, wordsense));
    }

    private class WeigthedSense {
        protected double weight;
        protected Resource sense;

        public WeigthedSense(double weight, Resource sense) {
            super();
            this.weight = weight;
            this.sense = sense;
        }
    }

    private class WeigthedDef implements Comparable<WeigthedDef>{
        protected double weight;
        protected List<String> def;

        public WeigthedDef(double weight, List<String> def) {
            super();
            this.weight = weight;
            this.def = def;
        }

        @Override
        public int compareTo(WeigthedDef o) {
            return 1-Double.valueOf(weight).compareTo(o.weight);
        }
    }

}
