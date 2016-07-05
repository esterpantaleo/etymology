package org.getalp.blexisma.cli;

import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.ConceptualVectorRandomizerFactory;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetProxOfRandomVector {

	/**
	 * @param args
	 * @throws UninitializedRandomizerException 
	 */
	public static void main(String[] args) throws UninitializedRandomizerException {
		if (args.length != 2) {
			System.err.println("Usage: java ... " + GetProxOfRandomVector.class.getName() +  " vectors_file number_of_prox_elements");
			System.exit(-1);
		}
		int nbelem = Integer.parseInt(args[1]);

		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		ConceptualVectorRandomizer rand = ConceptualVectorRandomizerFactory.createRandomizer(vb.getCVDimension(), vb.getCVEncodingSize());
		
		System.out.println("Loaded a vectorial base of " + vb.size() + " vectors with: " + vb.getCVDimension() + " dimensions.");
		ConceptualVector c = rand.nextVector();
		ArrayList<String_RAM_VectorialBase.EntryDist> lst = vb.getProx(c, nbelem);
		for (String_RAM_VectorialBase.EntryDist ed : lst) {
			System.out.println(ed.lexObj + ": " + c.getAngularDistance(vb.getVector(ed.lexObj)));
		}
		
	}

}
