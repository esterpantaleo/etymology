/**
 * *
 * ExtractBestWorst.java
 * Created on 10 oct. 2010 13:03:39
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * @author Didier SCHWAB
 *
 */
public class ExtractBestWorst {


    public static void extractBestWorst(String folderScorer, String folder, String out){

	File F = new File(folder);
	File answer;
	Runtime R = Runtime.getRuntime();
	String[] cmdarray = new String[4];
	cmdarray[0] = "perl";
	cmdarray[1] = folderScorer + "/scorer.pl";
	cmdarray[3] = "-d";

	double nbDoc = F.list().length;


	double[] values1 = new double[(int)nbDoc];
	double[] values2 = new double[(int)nbDoc];
	double[] values3 = new double[(int)nbDoc];
	double[] values4 = new double[(int)nbDoc];
	double[] values5 = new double[(int)nbDoc];
	double[] valuesT = new double[(int)nbDoc];

	/*double[] min = new double[6];

	for(int i = 0; i < min.length; i++){

	    min[i]=Integer.MAX_VALUE;
	}

	double[] max = new double[6];
	for(int i = 0; i < max.length; i++){

	    max[i]=Integer.MIN_VALUE;
	}

	double[] moy = new double[6];
	for(int i = 0; i < moy.length; i++){

	    moy[i]=0;
	}
	 */


	double buf;

	Process P;
	int numAnswer = 0;

	for(String file:F.list()){

	    int i = 0;

	    cmdarray[2] = folder + '/' + file;

	    try {
		P = R.exec(cmdarray, null, new File(folderScorer));
		BufferedReader reader = new BufferedReader(new InputStreamReader(P.getInputStream()));
		String ligneIn;
		while((ligneIn=reader.readLine()) != null){

		    switch(i){

		    case 2:{

			try {
			    values1[numAnswer] = Double.parseDouble(ligneIn.substring(33, 40));
			} catch (NumberFormatException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			    System.out.println(ligneIn);
			    System.out.println(file);	
			    System.exit(0);
			}
			break;
		    }

		    case 3:{

			values2[numAnswer] = Double.parseDouble(ligneIn.substring(33, 40));
			break;
		    }
		    case 4:{

			values3[numAnswer] = Double.parseDouble(ligneIn.substring(33, 40));
			break;
		    }
		    case 5:{

			values4[numAnswer] = Double.parseDouble(ligneIn.substring(33, 40));
			break;
		    }
		    case 6:{

			values5[numAnswer] = Double.parseDouble(ligneIn.substring(33, 40));
			break;
		    }
		    case 8:{

			valuesT[numAnswer] = Double.parseDouble(ligneIn.substring(36, 43));
			break;
		    }

		    }

		    i++;

		}
		numAnswer++;
		/*    if(i>=2 && i<=6){

			buf = Double.parseDouble(ligneIn.substring(33, 40));

			if(min[i-2] > buf)
			    min[i-2] = buf;

			if(max[i-2] < buf)
			    max[i-2] = buf;

			moy[i-2]+=buf;
		    }
		    else
			if(i==8){

			    buf = Double.parseDouble(ligneIn.substring(36, 43));
			    if(min[5] > buf)
				min[5] = buf;

			    if(max[5] < buf)
				max[5] = buf;

			    moy[5]+=buf;
			}*/

	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}


	// System.out.println("d00" + (i+1) + ": Min = " +  + " Max = " + max[i] + " diff = " + (max[i] - min[i]) + " moy = " + (moy[i]/nbDoc));
	try {
	    FileOutputStream os = new FileOutputStream(out);
	    OutputStreamWriter osw = new OutputStreamWriter(os);
	    BufferedWriter writer = new BufferedWriter(osw);

	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(4);

	    writer.append("d001 d002 d003 d004 d005 Total\n");	    
	    
	    for(int i = 0; i < values1.length;i++){
		writer.append(df.format (values1[i]*100)+ " " +df.format (values2[i]*100) + " " +df.format (values3[i]*100) + " " + df.format (values4[i]*100) + " " + df.format (values5[i]*100) + " " + df.format (valuesT[i]*100) + "\n");
	    }
	    writer.flush();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	/*
	System.out.println("d001 : Min " + df.format (Statistics.min(values1)*100) + " max = " + df.format (Statistics.max(values1)*100)  + " moy = " + df.format (Statistics.moy(values1)*100) + " écart type = " + df.format (Statistics.ecartType(values1)*100));
	System.out.println("d002 : Min " + df.format (Statistics.min(values2)*100) + " max = " + df.format (Statistics.max(values2)*100)  + " moy = " + df.format (Statistics.moy(values2)*100) + " écart type = " + df.format (Statistics.ecartType(values2)*100));
	System.out.println("d003 : Min " + df.format (Statistics.min(values3)*100) + " max = " + df.format (Statistics.max(values3)*100)  + " moy = " + df.format (Statistics.moy(values3)*100) + " écart type = " + df.format (Statistics.ecartType(values3)*100));
	System.out.println("d004 : Min " + df.format (Statistics.min(values4)*100) + " max = " + df.format (Statistics.max(values4)*100)  + " moy = " + df.format (Statistics.moy(values4)*100) + " écart type = " + df.format (Statistics.ecartType(values4)*100));
	System.out.println("d005 : Min " + df.format (Statistics.min(values5)*100) + " max = " + df.format (Statistics.max(values5)*100)  + " moy = " + df.format (Statistics.moy(values5)*100) + " écart type = " + df.format (Statistics.ecartType(values5)*100));
	System.out.println("Total : Min " + df.format (Statistics.min(valuesT)*100) + " max = " + df.format (Statistics.max(valuesT)*100)  + " moy = " + df.format (Statistics.moy(valuesT)*100) + " écart type = " + df.format (Statistics.ecartType(valuesT)*100));
	 */  }


    /**
     * @param args
     */
    public static void main(String[] args) {

	extractBestWorst("/Users/schwab/Documents/WSD/XP-schwab/new-scorer", "/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict-adapted-all-relations/execution/answers-cycles-500/finals", "/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict-adapted-all-relations/execution/answers-cycles-500/extraction-finals.csv");
	//extractBestWorst("/Users/schwab/Documents/WSD/XP-schwab/new-scorer", "/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/results-dict/test", "/Users/schwab/Documents/WSD/XP-schwab/answers/Fourmis/values.csv");
	   
    }

}
