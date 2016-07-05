package org.getalp.dbnary.experiment.disambiguation.translations;

import org.getalp.dbnary.experiment.disambiguation.Ambiguity;
import org.getalp.dbnary.experiment.disambiguation.Disambiguable;
import org.getalp.dbnary.experiment.disambiguation.Disambiguator;
import org.getalp.dbnary.experiment.similarity.SimilarityMeasure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TranslationDisambiguator implements Disambiguator {

    private Map<String, SimilarityMeasure> measures;

    {
        measures = new HashMap<>();
    }

    @Override
    public void registerSimilarity(String method, SimilarityMeasure sim) {
        measures.put(method, sim);
    }

    @Override
    public Set<String> getMethods() {
        return measures.keySet();
    }

    @Override
    public void disambiguate(Ambiguity a, final List<Disambiguable> choices) {
        for (String m : measures.keySet()) {
            for (Disambiguable d : choices) {
                double sim = measures.get(m).compute(d.getGloss(), a.getGloss());
                Disambiguable newD = new DisambiguableSense(d.getGloss(), d.getId(),d.getNum());
                newD.setScore(sim);
                a.addDisambiguation(m, newD);
            }
        }
    }
}
