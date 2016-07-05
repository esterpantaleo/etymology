package org.getalp.blexisma.cli;

import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class TestMagnitude {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage: java ... " + TestMagnitude.class.getName() +  " vectors_file startindex nbelem");
			System.exit(-1);
		}
		int sindex = Integer.parseInt(args[1]);
		int nbelem = Integer.parseInt(args[2]);

		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		String_RAM_VectorialBase.VBOS prec = vb.elementAt(sindex);
		System.out.println("Loaded a vectorial base of " + vb.size() + " vectors with: " + vb.getCVDimension() + " dimensions.");
		for (int i = sindex+1; i < sindex+nbelem; i++) {
			String_RAM_VectorialBase.VBOS vbos = vb.elementAt(i);
		    System.out.println(vbos.entry + " vs " + prec.entry);
		    System.out.println("Angular Distance = " + prec.CV.getAngularDistance(vbos.CV) + "| Cosine Similarity = " + prec.CV.getCosineSimilarity(vbos.CV) + "| Topic Distance = " + prec.CV.getTopicDistance(vbos.CV, 1));
		    
		    
		}
		
	}

}
