package org.getalp.dbnary.wiki;

/**
 * Created by serasset on 01/02/16.
 */
public interface WikiEventFilter {
    /**
     * returns true if tok should be kept in the event sequence.
     * @param tok the token to be considered
     * @return a boolean
     */
    boolean apply(WikiText.Token tok);
}
