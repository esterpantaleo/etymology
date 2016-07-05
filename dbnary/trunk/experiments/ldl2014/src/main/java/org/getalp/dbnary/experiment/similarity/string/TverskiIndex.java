package org.getalp.dbnary.experiment.similarity.string;

import com.wcohen.ss.AbstractStringDistance;
import org.getalp.dbnary.experiment.encoding.CodePointWrapper;
import org.getalp.dbnary.experiment.segmentation.Segmenter;
import org.getalp.dbnary.experiment.segmentation.SpaceSegmenter;
import org.getalp.dbnary.experiment.similarity.SimilarityMeasure;

import java.util.List;

public class TverskiIndex implements SimilarityMeasure {
    private Segmenter segmenter;

    private double alpha;
    private double beta;
    private boolean fuzzyMatching;
    private boolean symmetric = false;
    private AbstractStringDistance distance;
    private boolean lcss;
    private boolean lcssConstraint = true;


    public TverskiIndex(double alpha, double beta) {
        segmenter = new SpaceSegmenter();
        lcss = false;
        this.alpha = alpha;
        this.beta = beta;
        fuzzyMatching = false;
    }

    public TverskiIndex(double alpha, double beta, boolean fuzzyMatching, boolean symmetric, AbstractStringDistance distance) {
        this.distance = distance;
        lcss = false;
        segmenter = new SpaceSegmenter();
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        this.symmetric = symmetric;
    }

    public TverskiIndex(double alpha, double beta, boolean fuzzyMatching, boolean symmetric) {
        lcss = true;
        segmenter = new SpaceSegmenter();
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        this.symmetric = symmetric;
    }

    public TverskiIndex(Segmenter segmenter, double alpha, double beta) {
        this.segmenter = segmenter;
        this.alpha = alpha;
        this.beta = beta;
        fuzzyMatching = false;
    }

    public TverskiIndex(Segmenter segmenter, double alpha, double beta, boolean fuzzyMatching, boolean symmetric) {
        this.segmenter = segmenter;
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        if (!fuzzyMatching) {
            lcss = false;
        }
        this.symmetric = symmetric;
    }

    public static int longestSubString(String first, String second) {
        if (first == null || second == null || first.length() == 0 || second.length() == 0) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];
        CodePointWrapper cpFirst = new CodePointWrapper(first);
        int i = 0;
        for (int cpi : cpFirst) {
            CodePointWrapper cpSecond = new CodePointWrapper(second);
            int j = 0;
            for (int cpj : cpSecond) {
                if (cpi == cpj) {
                    if (i == 0 || j == 0) {
                        table[i][j] = 1;
                    } else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
                j++;
            }
            i++;
        }
        return maxLen;
    }

    @Override
    public double compute(String a, String b) {
        return compute(segmenter.segment(a), segmenter.segment(b));
    }

    public double compute(List<String> a, List<String> b) {
        double overlap;
        if (!fuzzyMatching) {
            overlap = computeOverlap(a, b);
        } else {
            overlap = computeFuzzyOverlap(a, b);
        }
        double diffA = a.size() - overlap;
        double diffB = b.size() - overlap;
        if (symmetric) {
            double factA = Math.min(diffA, diffB);
            double factB = Math.max(diffA, diffB);
            return overlap / (beta * (alpha * factA + (1 - alpha) * factB) + overlap);
        } else {
            return overlap / (overlap + diffA * alpha + diffB * beta);
        }

    }

    private double computeOverlap(List<String> a, List<String> b) {
        int size = Math.min(a.size(), b.size());
        double overlap = 0;
        for (int i = 0; i < size && a.get(i).contains(b.get(i)); i++) {
            overlap += 1;
        }
        return overlap;
    }

    private double computeFuzzyOverlap(List<String> la, List<String> lb) {
        double overlap = 0;
        for (String a : la) {
            for (String b : lb) {
                double score = 0;
                double lcss = longestSubString(a, b);
                double md = Math.max(Math.abs(lcss / a.length()), Math.abs(lcss / b.length()));
                if (!this.lcss) {
                    score = distance.score(distance.prepare(a), distance.prepare(b));
                } else {
                    score = md;
                }
                if (score > 0.999 || score < 1.0 && ((lcssConstraint && lcss >= 3) || !lcssConstraint)) {

                    if (!this.lcss) {
                        if (lcssConstraint) {
                            overlap += score + (1 - score) * (md - 0.5);
                        } else {
                            overlap += score;
                        }
                    } else {
                        overlap += md;
                    }
                }
            }
        }

        return overlap;
    }

    public boolean isLcssConstraint() {
        return lcssConstraint;
    }

    public void setLcssConstraint(boolean lcssConstraint) {
        this.lcssConstraint = lcssConstraint;
    }
}
