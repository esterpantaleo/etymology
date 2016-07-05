package org.getalp.blexisma.cli;

import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetRandomVectors {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: java ... " + GetRandomVectors.class.getName() +  " vectors_file number_of_vectors");
			System.exit(-1);
		}
		int nbelem = Integer.parseInt(args[1]);
		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);

		int vbsize = vb.size();

		System.out.println("Loaded a vectorial base of " + vbsize + " vectors with: " + vb.getCVDimension() + " dimensions.");
		
		for (int i = 0; i < nbelem; i++) {
			int rank = (int) Math.round(Math.random() * vbsize);
			String_RAM_VectorialBase.VBOS vbos = vb.elementAt(rank);
			System.out.println(vbos.entry + " : " + vbos.CV);
		}
	}

}
