/**
 * *
 * WSD.java
 * Created on 29 juin 2010 10:54:45
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package fr.lig.getalp.omnia.wsd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import fr.lig.getalp.falaise.minisysq.omnia.OmniaGraph;

/**
 * @author Didier SCHWAB
 *
 */
public class WSD extends Thread {	

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
	// TODO Auto-generated method stub

	/*Context C = new Context("/Users/schwab/Documents/ANRs/OMNIA/WSD/config.xml");


	OmniaGraph g = C.desamb(new File("/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix/1000016.gq"));

	FileWriter fw = new FileWriter("/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix-desamb/1000016.gq", true);
	BufferedWriter output = new BufferedWriter(fw);
	output.write(g.toString());
	output.flush();
	output.close();*/

	//	C.desamb(new File("/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix/1000016.gq"));
	//	C.desamb("/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix/1000016.gq");


	//C.desamb(new File("/Users/schwab/Documents/ANRs/OMNIA/WSD/testDS-1000016.gq"));

	//desambAll("/Users/schwab/Documents/ANRs/OMNIA/WSD/config.xml", "/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix/","/Users/schwab/Documents/ANRs/OMNIA/CLEF09-Belga-TestData-v0.3.8-nopics/out/uwix-desamb/", 2);
	/*System.out.println("0 " + args[0]);
	System.out.println("1 " + args[1]);
	System.out.println("2 " + args[2]);
	System.out.println("3 " + args[3]);*/

	long time = System.currentTimeMillis();
	desambAll(args[0],args[1], args[2], Integer.parseInt(args[3]));
	System.out.println("Time:" + (System.currentTimeMillis()-time));

    }

    
    public static void desambAll(String antConfig, String inputFolder, String outputFolder, int nbthreads){

	Date D = new Date();

	computeWSD[] T = new computeWSD[nbthreads];

	File F = new File(inputFolder);

	int numFile = 0;

	String[] files = F.list();	    

	String file;

	long length;

	//initialisation
	for(int i = 0; i < nbthreads; i++){

	    file = files[numFile++];
	    //System.out.println(inputFolder+file);
	    //System.out.println(outputFolder+file);
	    T[i] = new computeWSD(new Context(antConfig), inputFolder+file, outputFolder+file); 
	    T[i].start();
	}

	while(numFile < files.length){

	    for(int i = 0; i < nbthreads; i++){

		if(!T[i].isAlive()){

		    Date E = new Date();
		    length = (E.getTime() - D.getTime())/1000;

		    file = files[numFile++];
		    /*System.out.println(inputFolder+file);
		    System.out.println(numFile + "/" + files.length + '=' + ((numFile*100d)/files.length) + '%');
		    System.out.println("en " + length + " secondes");
		    System.out.println("soit " + ((double)numFile/(double)length) + " fichiers/secondes");*/
		    T[i] = new computeWSD(new Context(antConfig), inputFolder+file, outputFolder+file);
		    T[i].start();
		}
	    }
	    try {
		System.out.println("Je dors");
		sleep(500);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

    }

    public static void desambAll(String config, String inputFolder, String outputFolder){

	Context C = new Context(config);

	File F = new File(inputFolder);

	OmniaGraph g;

	int nbFile = F.list().length;

	Date D = new Date();

	int i = 0;

	for (String s : F.list()){

	    //System.out.println(inputFolder+s);
	    //System.out.println(i++ + "/" + nbFile + '=' + ((i*100d)/nbFile) + '%');
	    try {
		//Thread.sleep(1000);

		g = C.desamb(new File(inputFolder+s));

		FileWriter fw = new FileWriter(outputFolder+s, true);
		BufferedWriter output = new BufferedWriter(fw);
		output.write(g.toString());
		output.flush();
		output.close();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    Date E = new Date();

	    //System.out.println(E.getTime()-D.getTime());

	    // System.exit(0);
	    /*  try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }*/
	}


    }

}

class computeWSD extends Thread{

    Context C;
    File initFile;
    File outputFile;



    computeWSD(Context C, String inputPath, String outputPath){

	this.C = C;
	this.initFile = new File(inputPath);
	this.outputFile = new File(outputPath);

    }

    public void run(){

	try {
	    OmniaGraph g = this.C.desamb(this.initFile);
	    FileWriter fw = new FileWriter(outputFile, true);
	    BufferedWriter output = new BufferedWriter(fw);
	    output.write(g.toString());
	    output.flush();
	    output.close();

	    System.out.println("Enregistrement sur " + outputFile);
	    System.out.println("OK");
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    System.out.println(outputFile);
	}
    }
}
