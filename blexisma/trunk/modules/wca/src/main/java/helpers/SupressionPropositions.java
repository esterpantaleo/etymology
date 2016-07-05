/**
 * *
 * SupressionPropositions.java
 * Created on 3 oct. 2010 14:05:28
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @author Didier SCHWAB
 * 
 * Permet de supprimer les propositions 
 * 
 * Permet, par exemple, de ne conserver que les mots étiquetés par inTokeep dans in. Cela permet de comparer la précision sur une sous-partie du corpus.
 *
 */
public class SupressionPropositions {


    public static void suppressionPropositions(String in, String inTokeep, String out){

	try{
	    InputStream ips=new FileInputStream(in); 
	    InputStreamReader ipsr=new InputStreamReader(ips);
	    BufferedReader bufferIn = new BufferedReader(ipsr);

	    FileOutputStream os = new FileOutputStream(out);
	    OutputStreamWriter osw = new OutputStreamWriter(os);
	    BufferedWriter writer = new BufferedWriter(osw);

	    ips=new FileInputStream(inTokeep); 
	    ipsr=new InputStreamReader(ips);
	    BufferedReader bufferInTokeep = new BufferedReader(ipsr);
	    String ligneIn, ligneInToKeep;
	    String bufProp1, bufProp2;

	    ligneIn=bufferIn.readLine();
	    ligneInToKeep=bufferInTokeep.readLine();

	    while (ligneIn != null && ligneInToKeep!=null){	

		bufProp1 = ligneIn.substring(0,ligneIn.indexOf(' ', ligneIn.indexOf(' ')+1));
		bufProp2 = ligneIn.substring(0,ligneInToKeep.indexOf(' ', ligneInToKeep.indexOf(' ')+1));
		//	bufProp1 = ligneIn.substring(0,ligneIn.trim().lastIndexOf(' '));
		//	bufProp2 = ligneInToKeep.substring(0,ligneInToKeep.trim().lastIndexOf(' '));

		System.out.println(bufProp1);
		System.out.println(bufProp2);
		if(bufProp1.equals(bufProp2)){

		    System.out.println("Je garde");
		    writer.write(ligneIn);
		    writer.write('\n');
		    ligneInToKeep=bufferInTokeep.readLine();
		}
		else{

		    System.out.println("Je passe");
		}
		ligneIn=bufferIn.readLine();
	    }

	    bufferIn.close(); 
	    bufferInTokeep.close();
	    writer.flush();
	    writer.close();
	}		
	catch (Exception e){
	    System.out.println(e.toString());
	}


    }


    /**
     * @param args
     */
    public static void main(String[] args) {

	//suppressionPropositions(args[0], args[1], args[2]);
	suppressionPropositions("/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict/answer-threshold-allRel-apriori.ans", "/Users/schwab/Documents/WSD/XP-schwab/answers/Algo-Classique/adapted-all-relations-apriori/answer-adapted-all-relations-apriori.ans", "/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict/answer-threshold-allRel-apriori-compared.ans");
    }

}
