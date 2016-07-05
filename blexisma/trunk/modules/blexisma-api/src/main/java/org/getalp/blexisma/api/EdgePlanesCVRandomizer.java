package org.getalp.blexisma.api;

/**
 * Class generating vectors that have only 2 components activated.
 * 
 * @author serasset
 *
 */
public class EdgePlanesCVRandomizer extends ConceptualVectorRandomizer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EdgePlanesCVRandomizer() {
		super();
	}

	public EdgePlanesCVRandomizer(int dim, int codeLength) {
		super(dim, codeLength);
	}
	
	public EdgePlanesCVRandomizer(int dim, int codeLength, long seed) {
		super(dim, codeLength, seed);
	}
	
	
	@Override
	public ConceptualVector nextVector() {
		
		ConceptualVector randomCV = new ConceptualVector(dim, codeLength);
		
		for (int i=0; i<dim; i++) {
			randomCV.setElementAt(i, 1);
		}
		
		// get 2 dimensions
		int d1;
		int d2;
		do {
			d1 = rand.nextInt() % dim;
		} while (d1 < 0);
		do {
			d2 = rand.nextInt() % dim;
		} while (d2 < 0 || d2 == d1);
		
		randomCV.setElementAt(d1, rand.nextInt() % codeLength);
		randomCV.setElementAt(d2, rand.nextInt() % codeLength);
		
		randomCV.normalise();
		return randomCV;
	}

}
