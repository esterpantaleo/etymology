/**
 * *
 * BibtexOntoBuilder.java
 * Created on 16 mai 2010 18:57:32
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.builders.bibtex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.semnet.RAM_SemanticNetwork;
import org.getalp.blexisma.utils.TimeUtils;

/**
 * @author Didier SCHWAB
 * 
 */
public class BibtexOntoBuilder {

    public static final int spaceBibtexOnto = 4;

    public static void buildBibtexOntoNodesFromFile(String path, SemanticNetwork<String,String> semnet) {

	String ligne;

	final int OWL_CLASS = 0;
	final int HSUBCLASS = 1;
	final int SUBCLASS = 2;

	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(
		    new FileInputStream(path)));
	    int state = OWL_CLASS;
	    int index = -1;
	    // Node N = null, N2 = null;
	    String n1 = null, n2 = null;
	    
	    while ((ligne = br.readLine()) != null) {

		switch (state) {

		case (OWL_CLASS): {

		    index = ligne.indexOf("<owl:Class rdf:ID=\"");
		    if (index >= 0) {
		        // TODO: space has been dropped from the current Implementation. Should I keep it ?
		        
		        n1 = ligne.substring(index+19, ligne.indexOf("\"",20));
		        // N = new Node(ligne.substring(index+19, ligne.indexOf("\"",20)), spaceBibtexOnto);
			state = SUBCLASS;
		    }

		    break;
		}

		case(HSUBCLASS):{

		    index = ligne.indexOf("<rdfs:subClassOf rdf:resource=\"#");
		    if (index >= 0) {

			state = SUBCLASS;
		    }
		    else{
			
			index = ligne.indexOf("<rdfs:subClassOf rdf:resource=\"#");
			
			
		    }
		    break;
		}

		case(SUBCLASS):{

		    n2 = ligne.substring(index+32, ligne.indexOf("\"",33));
		    // N2 = new Node(ligne.substring(index+32, ligne.indexOf("\"",33)), spaceBibtexOnto);
		    System.out.println(n2);
		    System.exit(0);
		    break;
		}

		default:{

		    System.out.println("PROBLÈME");
		    break;
		}

		}
		//		    N.addEdge(new Edge(Edge.ONTO_RELATION, 1, N2.getName()));
		//		    semnet.addNode(N);
		//		    semnet.addNode(N2);

	    }
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

	buildBibtexOntoNodesFromFile("/Users/schwab/Documents/ANRs/OMNIA/ontolex/OntoLexTestAlign/edu.mit.visus.bibtex.owl", semnet);

	Date end = new Date();

	System.out.println(TimeUtils.formatTime(end.getTime() - start.getTime()));
    }

}
