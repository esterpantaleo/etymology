/**
 * *
 * OmniaOntoBuilder.java
 * Created on 21 mars 2010 14:13:25
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

import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.semnet.RAM_SemanticNetwork;
import org.getalp.blexisma.utils.TimeUtils;

/**
 * @author Didier SCHWAB
 *
 */
public class OmniaOntoBuilder {

    public static final int spaceOO = 3;

    public static final String ONTO_RELATION = "onto";
    
	public static void buildOONodesFromFile(String path,
			SemanticNetwork<String, String> semnet) {

		String ligne;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			boolean start = true;
			int index = -1;
			// Node N = null, N2 = null;
			String n1 = null, n2 = null;
			while ((ligne = br.readLine()) != null) {

				// System.out.println(state);
				// System.out.println(ligne);
				index = ligne.indexOf("<Class URI=\"&OntologieOMNIA;");
				// System.out.println(index);
				// System.out.println(start);
				if (index > 0 && start) {

					n1 = ligne.substring(index + 28,
							ligne.indexOf('\"', index + 28));
					// N = new Node(ligne.substring(index+28,
					// ligne.indexOf('\"',index+28)));
					// System.out.println(N);
					start = false;
				} else if (index > 0) {

					n2 = ligne.substring(index + 28,
							ligne.indexOf('\"', index + 28));
					// N2 = new Node(ligne.substring(index+28,
					// ligne.indexOf('\"',index+28)));
					// System.out.println(N2);
					semnet.addRelation(n1, n2, 1, ONTO_RELATION);
					// N.addEdge(new Edge(Edge.ONTO_RELATION, 1, N2.getName()));
					// semnet.addNode(N);
					// semnet.addNode(N2);
					start = true;
				} else {

				}

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

	buildOONodesFromFile("/Users/schwab/Documents/ANRs/OMNIA/Kaiko/link/Kaiko_align_UWpp-OMNIAv5_it0.xml", semnet);	

	System.out.println(semnet);

	//semnet.saveNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

	//	semnet.loadNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

	Date end = new Date();

	System.out.println(TimeUtils.formatTime(end.getTime() - start.getTime()));
    }

}
