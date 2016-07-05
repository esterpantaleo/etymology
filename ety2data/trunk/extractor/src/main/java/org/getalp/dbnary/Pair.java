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

    public boolean containedIn(Pair p){
        return (p.start<=start && p.end>=end);
    }

    public boolean containedIn(ArrayList<Pair> a){
        for (int i=0; i<a.size(); i++){
            if (this.containedIn(a.get(i))){
		return true;
	    }
	}
	return false;
    }
}
