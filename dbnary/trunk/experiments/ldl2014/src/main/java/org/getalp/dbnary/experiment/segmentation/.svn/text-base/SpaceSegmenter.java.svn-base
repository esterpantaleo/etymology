package org.getalp.dbnary.experiment.segmentation;

import java.util.ArrayList;
import java.util.List;

public class SpaceSegmenter implements Segmenter {

    @Override
    public List<String> segment(String value) {
        value = value.replaceAll("\\p{Punct}+", "");
        List<String> ret = new ArrayList<String>();
        for (String token : value.split("\\p{Z}")) {
            ret.add(token);
        }
        return ret;
    }
}
