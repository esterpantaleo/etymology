/**
 * *
 * BuildOmniaUWBasicLinks.java
 * Created on 25 mars 2010 09:39:04
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.builders.omniaonto;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.getalp.blexisma.builders.UW.UWBuilder;
import org.getalp.blexisma.semnet.RAM_SemanticNetwork;
import org.getalp.blexisma.utils.TimeUtils;

/**
 * @author Didier SCHWAB
 * 
 */
public class OmniaOntoUWBasicLinksBuilder {

    public static final String LINK = "link";
    
    public static void buildUWBasicLinksFromFile(String path,
	    RAM_SemanticNetwork semnet) {

	String ligne;
	boolean item = true;
	String start = null, goal = null;

	StringBuilder sb = new StringBuilder(1000);
	char state = 'S';
	int index;

	try {

	    BufferedReader br = new BufferedReader(new InputStreamReader(
		    new FileInputStream(path)));

	    while ((ligne = br.readLine()) != null) {

		switch (state) {

		case ('S'): {

		    if (ligne.indexOf("<map>") >= 0) {

			state = 'A';
		    } //else {

			//System.out.println("JE NE TIENS PAS COMPTE DE " + ligne);
		    //}
		    break;
		}

		case ('A'): {//start

		    index = ligne
		    .indexOf("<entity1 rdf:resource=\"http://Kaiko.getalp.org/ontologie/volume/Kaiko_UWpp.rdf#unl.upp.");
		    if (index >= 0) {

			index += 87;
			start = ligne.substring(index, ligne.indexOf("\"",
				index));
			state = 'B';
		    }

		    break;
		}

		case ('B'): {//goal

		    index = ligne.indexOf("<entity2 rdf:resource=\"&OMNIA;");
		    if (index >= 0) {

			// System.out.println(index);
			index += 30;
			goal = ligne.substring(index, ligne
				.indexOf("\"", index));
			//System.out.println(goal);

			state = 'C';

		    }

		    break;
		}

		case('C'):{//relation

		    state = 'D';

		    break;
		}

		case('D'):{//measure

		    index = ligne.indexOf("<measure rdf:datatype=\'http://www.w3.org/2001/XMLSchema#float\'>");
		    
		    index+=63;
		    float weight = Float.parseFloat(ligne.substring(index, ligne.indexOf("<", index)));
		    
		    //semnet.getNode(start).addEdge(new Edge(Edge.LINK, weight, goal));
		    
		    semnet.addRelation(start, goal, weight, LINK);
		    
		   // System.out.println(start + " " + new Edge(Edge.LINK, weight, goal));
		    
		    state = 'S';

		    break;
		}

		default: {
		    System.out.println("SORTIE");
		    System.exit(0);
		}

		}

	    }

	    /**
	     * BufferedReader br=new BufferedReader(new InputStreamReader(new
	     * FileInputStream(path)));
	     * 
	     * while ((ligne=br.readLine())!=null){
	     * 
	     * //System.out.println(ligne);
	     * 
	     * if(item && ligne.indexOf("<UWpp rdf:about=\"&UWpp;unl.upp.")==0){
	     * 
	     * start = ligne.substring(31,ligne.indexOf('\"', 32)); item =
	     * false; } else{
	     * 
	     * if(ligne.indexOf("   <Kaiko:LINK_abs rdf:resource=\"&OMNIA")==0){
	     * 
	     * goal = ligne.substring(40, ligne.indexOf('\"',41)); //
	     * System.out.println(start); // System.out.println(goal);
	     * semnet.getNode(start).addEdge(new Edge(SemanticNetwork.LINK,
	     * 0.5f, goal)); item=true; } }
	     * 
	     * }
	     **/
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	Date start = new Date();

	RAM_SemanticNetwork semnet = new RAM_SemanticNetwork(1000000);

	
	  OmniaOntoBuilder.buildOONodesFromFile(
	  "/Users/schwab/Documents/ANRs/OMNIA/20090224_onto_OMNIA_v5.0.owl",
	  semnet);
	  
	 System.out.println("Nb nodes = " + semnet.getNbNodes());
	  System.out.println("Nb edges = " + semnet.getNbEdges());
	  
	  UWBuilder.buildUWNodesFromFile(
	  "/Users/schwab/Documents/ANRs/OMNIA/Kaiko/Volume/Kaiko_UWpp.rdf.xml",
	  semnet);
	  
	  System.out.println("Nb nodes = " + semnet.getNbNodes());
	  System.out.println("Nb edges = " + semnet.getNbEdges());
	 

	buildUWBasicLinksFromFile(
		"/Users/schwab/Documents/ANRs/OMNIA/Kaiko/link/Kaiko_align_UWpp-OMNIAv5_it0.xml",
		semnet);

	System.out.println("Nb nodes = " + semnet.getNbNodes());
	System.out.println("Nb edges = " + semnet.getNbEdges());

	// semnet.saveNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

	// semnet.loadNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

	Date end = new Date();

	System.out.println(TimeUtils
		.formatTime(end.getTime() - start.getTime()));

    }
}
