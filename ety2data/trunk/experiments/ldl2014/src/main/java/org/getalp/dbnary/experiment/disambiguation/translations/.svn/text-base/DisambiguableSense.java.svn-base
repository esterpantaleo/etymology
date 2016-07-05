package org.getalp.dbnary.experiment.disambiguation.translations;

import org.getalp.dbnary.experiment.disambiguation.Disambiguable;

public class DisambiguableSense implements Disambiguable {

    private String gloss;

    private double score;

    private boolean processed;

    private String id;

    {
        processed = false;
    }

    private String number;


    public DisambiguableSense(final String gloss, final String id, final String number) {
        this.gloss = gloss;
        this.id = id;
        this.number = number;
    }

    /*public DisambiguableSense(final LexicalSense ls) {
        this.gloss = ls.getDefinition();
        this.id = ls.getURI();
    }*/

    @Override
    public String getGloss() {
        return gloss;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void setScore(final double score) {
        processed = true;
        this.score = score;
    }

    @Override
    public boolean hasBeenProcessed() {
        return processed;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getNum() {
        return number;
    }

    @Override
    public String toString() {
        return getId() + "\t" + getScore();
    }
}
