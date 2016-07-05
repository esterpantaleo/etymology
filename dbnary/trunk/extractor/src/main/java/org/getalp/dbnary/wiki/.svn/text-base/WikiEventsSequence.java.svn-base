package org.getalp.dbnary.wiki;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by serasset on 28/01/16.
 */
public class WikiEventsSequence implements Iterable<WikiText.Token> {

    private WikiText txt;
    private WikiEventFilter filter;

    public WikiEventsSequence(WikiText txt, WikiEventFilter filter) {
        this.txt = txt;
        this.filter = filter;
    }

    @Override
    public Iterator<WikiText.Token> iterator() {
        return new WikiEventIterator(txt, filter);
    }

}
