package org.getalp.blexisma.xwn.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.utils.OpenFile;

public class ItemRAMVectorialBaseBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length!=2){
			System.err.println("Illegal number of arguments. Usage: java "+ItemRAMVectorialBaseBuilder.class+
					"itembase_file RAM_base_file");
			System.exit(0);
		}
		
		BufferedReader bfrd = OpenFile.readTextFileLineByLine(new File(args[0]));
		String_RAM_VectorialBase vbase = new String_RAM_VectorialBase(32768,2000);
		
		try {
			while (bfrd.ready()){
				String line = bfrd.readLine();
				ConceptualVector cv = new ConceptualVector(extractVector(line),2000,32768);
				vbase.addVector(extractSynset(line),cv);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		vbase.save(args[1]);
	}
	
	private static String extractSynset(String l){
		String syn = l.substring(1);
		syn = syn.split("\"")[0];
		syn = syn.split(":")[2];
		return syn;
	}
	
	private static String extractVector(String l){
		String vec = l.substring(1);
		vec = vec.split("\"")[1].substring(1).trim();
		return vec;
	}
}
