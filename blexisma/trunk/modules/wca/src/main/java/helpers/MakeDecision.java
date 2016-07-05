/**
 * *
 * MakeDecision.java
 * Created on 9 oct. 2010 18:42:12
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * @author Didier SCHWAB
 *
 */
public class MakeDecision {
    
    /**
     * 
     * @param folder Répertoire où sont tous les fichiers réponses
     * @param out Fichier où sera mis la proposition
     * @param threshold Le pourcentage de fichiers devant comporter la même proposition. Si -1, on choisit la majorité.
     */

    public static void makeDecision(String folder, String out, int threshold){

	File F = new File(folder);
	File answer;
	ArrayList<ArrayList <Prop>> propositions = new ArrayList<ArrayList <Prop>>(10000);

	int nbFiles = F.list().length;

	for(String file:F.list()){

	    int i = 0;
	    answer = new File(folder + '/' + file);
	    // System.out.println(answer);
	    try {
		InputStream ips=new FileInputStream(answer); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader bufferIn = new BufferedReader(ipsr);

		String ligneIn;

		if(propositions.size()==0)

		    while((ligneIn=bufferIn.readLine()) != null){

			propositions.add(new ArrayList <Prop>(10));
			propositions.get(i).add(new Prop(ligneIn));
			i++;
		    }
		else
		    while((ligneIn=bufferIn.readLine()) != null){

			ArrayList <Prop> props = propositions.get(i);
			int j = 0;
			for(j = 0; j < props.size() && ! props.get(j).proposition.equals(ligneIn); j++);

			/*	System.out.println("i est " + i);
			System.out.println("j est " + (j < props.size()) + " " + j);
			System.out.println();
			System.out.println();


			try {
			    Thread.sleep(100);
			} catch (InterruptedException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}*/

			if(j < props.size()){

			    props.get(j).weight++;
			}
			else{

			    props.add(new Prop(ligneIn));

			}
			i++;
		    }
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	//choix et sauvegarde

	try {
	    FileOutputStream os = new FileOutputStream(out);
	    OutputStreamWriter osw = new OutputStreamWriter(os);
	    BufferedWriter writer = new BufferedWriter(osw);

	    if(threshold==-1){

		for(int k = 0; k < propositions.size(); k++){

		    ArrayList <Prop> props = propositions.get(k);

		    int best = 0;
		    int indexBest = 0;

		    for(int l = 0; l < props.size(); l++){

			if(props.get(l).weight > best){

			    best = props.get(l).weight;
			    indexBest = l;
			}
		    }

		    //System.out.println(props.get(l).proposition + " " + ((double)props.get(l).weight/(double)nbFiles));
		    writer.append(props.get(indexBest).proposition);
		    writer.append('\n');
		}
	    }
	    else
		for(int k = 0; k < propositions.size(); k++){

		    ArrayList <Prop> props = propositions.get(k); 
		    for(int l = 0; l < props.size(); l++){

			if(((double)props.get(l).weight/(double)nbFiles)>=((double)threshold/100d)){

			    //System.out.println(props.get(l).proposition + " " + ((double)props.get(l).weight/(double)nbFiles));
			    writer.append(props.get(l).proposition);
			    writer.append('\n');
			}
		    }

		}

	    writer.flush();
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
	// TODO Auto-generated method stub

	makeDecision("/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict-adapted-all-relations/convergence/convA200/results-cyc200-conv20","/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/answer-threshold-allRel-all-convergence-cyc200-conv20.ans", 80);	
    }

}

class Prop{

    public String proposition;
    public int weight;

    public Prop(String proposition){

	this.proposition = proposition;
	this.weight = 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Prop [proposition=" + proposition + ", weight=" + weight + "]";
    }



}
