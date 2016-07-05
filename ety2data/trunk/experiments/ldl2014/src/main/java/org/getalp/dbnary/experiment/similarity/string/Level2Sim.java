package org.getalp.dbnary.experiment.similarity.string;

import com.wcohen.ss.AbstractTokenizedStringDistance;
import org.getalp.dbnary.experiment.similarity.SimilarityMeasure;

import java.util.List;


public class Level2Sim implements SimilarityMeasure {

    AbstractTokenizedStringDistance distance;

    public Level2Sim(AbstractTokenizedStringDistance distance) {
        this.distance = distance;
    }

    @Override
    public double compute(String a, String b) {

        //Level2Levenstein me = new Level2Levenstein();
        //com.wcohen.ss.Level2Sim me = new com.wcohen.ss.Level2Sim();
        return distance.score(a, b);
    }

    public double compute(List<String> a, List<String> b) {
        String sa="";
        String sb="";
        for(String s:a){
            sa+=a+" ";
        }
        sa = sa.trim();
        for(String s:b){
            sb+=b+" ";
        }
        sb = sb.trim();

        return compute(sa,sb);
    }
}
