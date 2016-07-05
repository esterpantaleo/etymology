package org.getalp.blexisma.cli;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetNumberOfEmptyVectors {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java ... " + GetNumberOfEmptyVectors.class.getName() +  " vectors_file");
			System.exit(-1);
		}
		
		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		
		System.out.println("Loaded a vectorial base of " + vb.size() + " vectors with: " + vb.getCVDimension() + " dimensions.");
		int nbv = 0;
		int nbvn = 0;
		for (int i = 0; i < vb.size(); i++) {
			nbv++;
			String_RAM_VectorialBase.VBOS vbos = vb.elementAt(i);
			if (vbos.CV.equals(new ConceptualVector(vb.getCVDimension(), vb.getCVEncodingSize()))) {
				nbvn++;
				System.out.println("Vector for \"" + vbos.entry + "\" is empty.");
			}
		}
		
		System.out.println(nbv + " vectors in vector base. " + nbvn + " of them being empty."); 
	}

}
