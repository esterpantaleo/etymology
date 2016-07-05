package org.getalp.blexisma.cli;

import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetProx {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length <= 2) {
			System.err.println("Usage: java ... " + GetProx.class.getName() +  " vectors_file number_of_prox_elements element ...");
			System.exit(-1);
		}
		int nbelem = Integer.parseInt(args[1]);

		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		
		System.out.println("Loaded a vectorial base of " + vb.size() + " vectors with: " + vb.getCVDimension() + " dimensions.");
		for (int i = 2; i < args.length; i++) {
		    ConceptualVector c = vb.getVector(args[i]);
		    System.out.println(" ============= Prox of: " + args[i] + " ===============");
		    ArrayList<String_RAM_VectorialBase.EntryDist> lst = vb.getProx(c, nbelem);
		    for (String_RAM_VectorialBase.EntryDist ed : lst) {
				System.out.println(ed.lexObj + ": \n" 
						+ ed.distance + " | cs = "+ c.getCosineSimilarity(vb.getVector(ed.lexObj))
						+" | ad = " + c.getAngularDistance(vb.getVector(ed.lexObj))
						+ " | norm = "+ vb.getVector(ed.lexObj).getMagnitude());
		    }
		}
		
	}

}
