package org.getalp.dbnary.experiment.similarity;


public interface SimilarityMeasure {
    public double compute(String a, String b);
}
