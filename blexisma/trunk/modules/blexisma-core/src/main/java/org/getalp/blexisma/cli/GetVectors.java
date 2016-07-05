package org.getalp.blexisma.cli;

import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class GetVectors {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: java ... " + GetVectors.class.getName() +  " vectors_file element_id ...");
			System.exit(-1);
		}
		
		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		
		// System.out.println("Loaded a vectorial base of " + vb.size() + " vectors with: " + vb.getCVDimension() + " dimensions.");
		// TODO: use the XMLDataFormatter class...
		for (int i = 1; i < args.length; i++) {
			System.out.println("<conceptual_vector><id>" + args[i] + "</id><dim>" +vb.getCVDimension()+ "</dim><norm>"+vb.getCVEncodingSize()+"</norm>");
			System.out.println("<vect>"+ vb.getVector(args[i]).toStringHexa() + "</vect></conceptual_vector>");
		}
		
	}

}
