/**
 * *
 * SemanticNetworkHashTable.java
 * Created on 11 mars 2010 11:32:01
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.semnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.utils.StrongHashTable;
import org.getalp.blexisma.utils.TimeUtils;

/**
 * @author Didier SCHWAB
 *
 */
public class RAM_SemanticNetwork extends SemanticNetwork<String, String> {

    // TODO: Such optimization may be done by the semnet user
//    public static int ICL_RELATION = 0;
//    public static int ONTO_RELATION = 1;
//    public static int LINK = -1;
//    
//    public int getRelation(String relation){
//
//        if(relation.equals("icl"))
//            return ICL_RELATION;
//        else
//            return -1;
//        }
    
    public class Edge extends SemanticNetwork<String, String>.Edge implements Serializable{
        
        /**
         * 
         */
        private static final long serialVersionUID = 6635478705979359732L;

        private String relation;
        private float confidence;
        private String destination;

        public Edge(String relation, float confidence, String destination){

        this.relation = relation;
        this.confidence = confidence;
        this.destination = destination;
        }

        /**
         * @return the relation
         */
        public String getRelation() {
            return relation;
        }

        /**
         * @param relation the relation to set
         */
        public void setRelation(String relation) {
        this.relation = relation;
        }

        /**
         * @return the confidence
         */
        public float getConfidence() {
        return confidence;
        }

        /**
         * @param confidence the confidence to set
         */
        public void setConfidence(float confidence) {
        this.confidence = confidence;
        }

        /**
         * @return the destination
         */
        public String getDestination() {
        return destination;
        }

        /**
         * @param destination the destination to set
         */
        public void setDestination(String destination) {
        this.destination = destination;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
        return "Edge [destination="
        + destination  + " confidence= "+ confidence +", relation=" + relation +"]";
        }

        @Override
        public String getOrigin() {
            // TODO Auto-generated method stub
            return null;
        }

    }
    
    public class Node implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -8026128544174189277L;
        private String name;
        // private int space = -1;
        private Vector<Edge> edges;

        public Node(String name){

        this.name = name;
        this.edges = new Vector<Edge>(5);
        }

        public Node(String name, Vector<Edge> edges){

        this.name = name;
        this.edges = edges;
        }
        
//        public Node(String name, int space){
    //
    //  this.name = name;
    //  this.edges = new Vector<Edge>(5);
    //  this.space = space;
//        }

//        public Node(String name, Vector<Edge> edges, int space){
    //
    //  this.name = name;
    //  this.edges = edges;
    //  this.space = space;
//        }

        public boolean addEdge(Edge E){

        return edges.add(E);
        }

        /**
         * @return the name
         */
        public String getName() {
        return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
        this.name = name;
        }

        /**
         * @return the edges
         */
        public Vector<RAM_SemanticNetwork.Edge> getEdges() {
            return edges;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
        // return "Node [name=" + name + ", space = " + space + ", edges=" + edges + "]";
        return "Node [name=" + name + ", edges=" + edges + "]";
        }

//        public void setSpace(int space) {
    //  this.space = space;
//        }
    //
//        public int getSpace() {
    //  return space;
//        }
    }
    
    private static final char fileseparator = ((String) System.getProperties().get("file.separator")).charAt(0);

    private StrongHashTable<String, Node> semanticNetwork;

    public RAM_SemanticNetwork(){

	semanticNetwork = new StrongHashTable<String, Node>(300000, 0.5f);
    }

    public RAM_SemanticNetwork(int capacity){

	semanticNetwork = new StrongHashTable<String, Node>(capacity, 0.5f);
    }

    /* (non-Javadoc)
     * @see semnet.SemanticNetwork#addNode(semnet.structs.Node)
     */
    public void addNode(Node n) {
        Node node = (Node) semanticNetwork.get(n.getName());

        if (n == node) {
            return;
        } else if (node == null) {
            semanticNetwork.put(n.getName(), n);
        } else {
            Vector<Edge> V = n.getEdges();
            for (int i = 0; i < V.size(); i++) {
                node.addEdge(V.elementAt(i));
            }
        }
    }

    /* (non-Javadoc)
     * @see semnet.SemanticNetwork#getNode()
     */
    public Node getNode(String nodeName) {				
        return (Node)semanticNetwork.get(nodeName);
    }

    public void saveNetwork(String savePath) {

	System.out.println(savePath);

	Date D = new Date();
	Date D2;
	
	for(int i = 0 ; i < semanticNetwork.size() ; i++){

	    if(i%1000==0){

		System.out.println(i);
		D2 = new Date();
		System.out.println(TimeUtils.formatTime(D2.getTime() - D.getTime()));
		D = D2;
	    }
	    saveNode(savePath, i+"", semanticNetwork.elementAt(i));
	}
    }

    public void loadNetwork(String path){

	Node N;
	File F = new File(path);
	File[] Tfiles = F.listFiles();
	for(int i = 0; i < Tfiles.length ; i++){

	    System.out.println(Tfiles[i]);
	    F = Tfiles[i];
	    File[] Tfiles2 = F.listFiles();
	    try {
		for(int j = 0; j < Tfiles2.length ; j++){

		    N = loadNode(Tfiles2[j].getAbsolutePath());
		    semanticNetwork.put(N.getName(), N);
		}
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println(Tfiles2);
	    }
	}

    }

    private boolean saveNode(String savePath, String name, Node N){

	String savePathNode = savePath + fileseparator + (int)N.getName().charAt(0);
	//System.out.println("1 "+savePathNode);
	(new File(savePathNode)).mkdir();
	savePathNode= savePathNode + fileseparator + name + ".j";
	//System.out.println("2 "+savePathNode);
	try {

	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePathNode));
	    oos.writeObject(N);
	    oos.flush();
	    oos.close();
	}
	catch (java.io.IOException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    private Node loadNode(String path){

	try {

	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
	    return (Node) ois.readObject();
	}
	catch (java.io.IOException e) {
	    e.printStackTrace();
	}
	catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "SemanticNetworkStrongHashTable [semanticNetwork="
	+ semanticNetwork + "]";
    }

    /* (non-Javadoc)
     * @see semnet.SemanticNetwork#size()
     */
    public int getNbNodes() {

	return semanticNetwork.size();
    }
    
    public int getNbEdges(){
	
	int nbEdges = 0;
	
	for (int i = 0 ; i < semanticNetwork.size() ; i++){
	    
	    nbEdges+=semanticNetwork.elementAt(i).getEdges().size();
	    
	}
	
	return nbEdges;
    }

    public static void main(String[] argv){

//	SemanticNetworkStrongHashTable semNet = new SemanticNetworkStrongHashTable(30);
	/*  Node N = new Node("vie");
    semNet.addNode(N);
    N = new Node("chien");
    semNet.addNode(N);
    N = new Node("chat");
    semNet.addNode(N);
    N = new Node("dormir");
    semNet.addNode(N);
    N = new Node("as");
    semNet.addNode(N);
    semNet.saveNetwork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");
    semNet.loadNetwork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");
    System.out.println(semNet.semanticNetwork.size());*/
    }

    @Override
    public void addNode(String node) {
        addNode(new Node(node));
    }


    @Override
    public Collection<? extends RAM_SemanticNetwork.Edge> getEdges(String node) {
        Node n = (Node) semanticNetwork.get(node);
        return n.getEdges();
    }

    @Override
    public Iterator<String> getNodesIterator() {
        throw new RuntimeException("Unimplemented abstract method.");
    }
    
    /* (non-Javadoc)
     * @see org.getalp.blexisma.api.SemanticNetwork#getInfiniteNodesIterator()
     */
    @Override
    public Iterator<String> getInfiniteNodesIterator() {
        throw new RuntimeException("Unimplemented abstract method.");
    }

    @Override
    public Iterator<Edge> getEdgesIterator() {
        throw new RuntimeException("Unimplemented abstract method.");
    }
    
    @Override
    public  Iterator<Edge> getInfiniteEdgesIterator() {
        throw new RuntimeException("Unimplemented abstract method.");
    }
    
    
    @Override
    public void addRelation(String origin, String destination, float confidence, String relation) {
        Node from = new Node(origin);
        Edge edg = new Edge(relation, confidence, destination);
        
        from.addEdge(edg);
        
        addNode(from);
    }

    @Override
    public void clear() {
        semanticNetwork.clear();
    }

}
