package org.getalp.dbnary.experiment.encoding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An iterable wrapper for the code point iterator that allows to iterate over codepoints of a string using
 * the foreach syntax
 */
public class CodePointWrapper implements Iterable<Integer> {
    CodePointIterator it;

    public CodePointWrapper(String str) {
        this.it = new CodePointIterator(str);
    }

    @Override
    public Iterator<Integer> iterator() {
        return it;
    }

    public List<Integer> asList() {
        List<Integer> l = new ArrayList<Integer>();
        for (int v : this) {
            l.add(v);
        }
        return l;
    }
}
