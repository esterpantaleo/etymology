/**
 * *
 * SemanticNetwork.java
 * Created on 11 mars 2010 11:03:36
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.api;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Didier SCHWAB
 * @author serasset
 *
 * @param <N>
 * @param <R>
 */
public abstract class SemanticNetwork<N, R> {
    
    public abstract class Edge {
        public abstract N getOrigin();
        public abstract N getDestination();
        public abstract float getConfidence();
        public abstract R getRelation();
        public String toString(){
        	return "Origin: "+getOrigin().toString()+" Destination: "+getDestination().toString()
        	+ " Confidence: "+getConfidence();
        }
    }
    
    
    /**
     * @param node
     */
    public abstract void addNode(N node);
    
    /**
     * @param origin
     * @param destination
     * @param confidence
     * @param relation
     */
    public abstract void addRelation(N origin, N destination, float confidence, R relation);
    
    /**
     * @param node
     * @return
     */
    public abstract Collection<? extends Edge> getEdges(N node);

    public abstract Iterator<N> getNodesIterator();
 
    /**
     * @return an Iterator that will <i>indefinitely</i> loop over all nodes of the semantic network.
     * 
     * The order of iteration is undefined.
     * The first element of the iteration is undefined, and different iterators may start with different elements.
     * Looping <i>indefinitely</i> means that Iterators hasNext() will always return true.
     * It is guaranteed that if you get an element that has already been iterated over, then all elements have been obtained.
     * 
     */
    public abstract Iterator<N> getInfiniteNodesIterator();

    public abstract Iterator<? extends Edge> getEdgesIterator();
    
    /**
     * @return an Iterator that will <i>indefinitely</i> loop over all edges of the semantic network.
     * 
     * The order of iteration is undefined.
     * The first element of the iteration is undefined, and different iterators may start with different elements.
     * Looping <i>indefinitely</i> means that Iterators hasNext() will always return true.
     * It is guaranteed that if you get an element that has already been iterated over, then all elements have be obtained.
     * 
     */
    public abstract Iterator<? extends Edge> getInfiniteEdgesIterator();
    
    /**
     * @return
     */
    public abstract int getNbNodes();
    
    /**
     * @return
     */
    public abstract int getNbEdges();

    /**
     * 
     */
    public abstract void clear() ;

    
}
