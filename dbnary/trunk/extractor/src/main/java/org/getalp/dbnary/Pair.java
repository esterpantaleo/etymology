package org.getalp.dbnary;

import java.util.*;

/** 
 * @author pantaleo
 * 
 */
public class Pair{
    public int start, end;

    public Pair(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /*
     * Check if a given Pair is contained in the input Pair p.
     *
     * @param p a Pair
     * @return true if the given Pair is contained in the input Pair p, false otherwise
     */
    public boolean containedIn(Pair p){
        return (p.start<=start && p.end>=end);
    }

    /*
     * Check if the given Pair is contained in any of the input ArrayList of Pair-s.
     *
     * @param a an ArrayList of Pair objects
     * @return true if the given Pair is contained in any of the input Pair-s, false otherwise
     */
    public boolean containedIn(ArrayList<Pair> a){
        for (int i=0; i<a.size(); i++){
            if (this.containedIn(a.get(i))){
		return true;
	    }
	}
	return false;
    }
}
