/**
 * *
 * UWBuilder.java
 * Created on 14 mars 2010 15:24:49
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.builders.UW;

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
public class UWBuilder {

   // public static final int dimension = 2000;
   // public static final int codeLength = 32768;
    public static final int spaceUW = 2;
    public static final String ICL = "icl";

    public static void buildUWNodesFromFile(String path, SemanticNetwork<String, String> semnet){

	String ligne;
	char state = 'S';
	int start, end;
	String name = null, subnet = null;
//	int nbUW = 0;

	try {
	    BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	    while ((ligne=br.readLine())!=null){

		//System.out.println(state);
		//System.out.println(ligne);

		switch(state){

		case 'S':{
		    if(ligne.indexOf("<UWpp rdf")==0){


			start = ligne.indexOf("unl.upp.")+8;
			end = ligne.indexOf("\"", start);
			name = ligne.substring(start, end);
			state = 'L';
		    }
		    break;
		}
		case 'L':{//lexeme


		    start = ligne.indexOf("<UWpp_lexeme>")+13;
		    end = ligne.indexOf("<", start);
		    subnet = ligne.substring(start, end);
		    add(name, subnet, semnet);
		 /*   nbUW++;
		    if(nbUW%10000==0){

			System.out.println(nbUW);
		    }*/
		    state='S';
		    break;
		}
		default:{

		    System.out.println("DEFAULT");
		}
		}
	    }
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

//	System.out.println("nbPar = " + nbPar);
//	System.out.println("Nb Node = " + semnet.size());
    }

    private static void add(String nameEntry, String subnet, SemanticNetwork<String, String> semnet){

	//System.out.println(subnet);

	char state = 'S';
	char c;
	StringBuilder buf = new StringBuilder(100);
	String name = null;
	String relation = null;
	String destination = null;
	

	for(int i = 0; i < subnet.length(); i++){

	    c = subnet.charAt(i);
	    //  System.out.println("state = " + state);
	    //  System.out.println("c = " + c);

	    switch(state){

	    case('S'):{//lemme

		if(c=='('){
		    // TODO: I did not kept the space argument in nodes. It may be kept by using another type of node label
//		    N = new Node(nameEntry, spaceUW);
//		  //  N.setCv(new ConceptualVector(dimension, codeLength));
//		    
		    name = buf.toString();
//		    N.addEdge(new Edge(ICL, 1, name));
//		    semnet.addNode(N);
		    semnet.addRelation(nameEntry, name, 1, ICL);
		    
		    // N = new Node(name, spaceUW); // ???
		//    N.setCv(new ConceptualVector(dimension, codeLength));
		    buf = new StringBuilder(100);
		    state = 'A';
		}
		else{

		    buf.append(c);
		}
		break;
	    }

	    case('A'):{//relation

		if(c=='&'){

		    relation = buf.toString();
		    buf = new StringBuilder(100);
		    state = 'B';
		}
		else{

		    buf.append(c);
		}

		break;
	    }
	    case('B'):{//&gt;

		if(c==';'){

		    state = 'C';
		}

		break;
	    }
	    case('C'):{//destination

		if(c=='(' || c==')' || c=='&'){

		    destination = buf.toString();
		    buf = new StringBuilder(100);
		    semnet.addRelation(name, destination, 1, relation);
		    
		    //N.addEdge(new Edge(Edge.getRelation(relation), 1, destination));
		    //semnet.addNode(N);
		    if(c==')'){

			state = 'Z';
		    }
		    else{
			if(c=='&'){//second restriction

			    state ='D';
			}
			else{//predicat : example com&gt;true(a)
			    
			    state='Z';
			}
		    }
		}
		else{

		    if(c==','){

			state='A';

		    }
		    else{

			buf.append(c);
		    }
		}
		break;
	    }
	    case('D'):{

		if(c==';'){

		    state = 'E';
		}
		break;
	    }
	    case('E'):{

		if(c == ')'){
		    semnet.addRelation(destination, buf.toString(), 1, relation);
		    
		 //  N = new Node(destination, spaceUW);
		 //   N.setCv(new ConceptualVector(dimension, codeLength));
		 //   N.addEdge(new Edge(Edge.getRelation(relation), 1, buf.toString()));
		 //   semnet.addNode(N);
		 //   N = new Node(buf.toString(), spaceUW);
		 //   N.setCv(new ConceptualVector(dimension, codeLength));
		 //   semnet.addNode(N);
		    buf = new StringBuilder(100);
		    state = 'Z';
		}    
		else
		    if(c == ','){

			state = 'A';
		    }
		    else{

			buf.append(c);
		    }
		break;
	    }
	    case('Z'):{

		//System.out.println(subnet);
		//System.out.println(nbPar);
		//System.out.println("Z : fini " + c);
		//System.exit(-1);
		//nbPar++;
	    }
	    }
	}


	//System.out.println("@@@@@@@@@@@@@@@@@@@@@");
	//System.out.println(semnet.size());
    }


    public static void main(String[] argv){

	Date start = new Date();

	RAM_SemanticNetwork semnet = new RAM_SemanticNetwork(1000000);

	buildUWNodesFromFile("/Users/schwab/Documents/ANRs/OMNIA/Kaiko/Volume/Kaiko_UWpp.rdf.xml", semnet);	

	//semnet.saveNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

//	semnet.loadNetWork("/Users/schwab/Documents/ANRs/OMNIA/testSemNet");

	Date end = new Date();

	System.out.println(TimeUtils.formatTime(end.getTime() - start.getTime()));
    }
}
