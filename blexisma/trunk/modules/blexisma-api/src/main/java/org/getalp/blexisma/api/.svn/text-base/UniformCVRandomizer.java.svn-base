package org.getalp.blexisma.api;


public class UniformCVRandomizer extends ConceptualVectorRandomizer {

	public UniformCVRandomizer() {
		super();
	}

	public UniformCVRandomizer(int dim, int codeLength) {
		super(dim, codeLength);
	}
	
	public UniformCVRandomizer(int dim, int codeLength, long seed) {
		super(dim, codeLength, seed);
	}
	
	
	@Override
	public ConceptualVector nextVector() {
		// cf http://math.univ-lille1.fr/~suquet/ens/Agr/simul06.pdf
		// Soient N1, . . . , Nd des variables aléatoires i.i.d. N(0, 1). Posons R:=(N1^2 +···+Nd^2)1/2.
		// Alors le vecteur aléatoire (N1 , . . . , Nd) suit la loi uniforme sur Cd.
		
		ConceptualVector randomCV = new ConceptualVector(dim, codeLength);

		// Calculate a limit that will avoid long overflow on normalization.
		// Here we take max/dim, so that sum(Ni^2) is always less that dim * limit^2.
		long limit = Integer.MAX_VALUE / this.dim;
		
		for (int i=0; i<dim; i++) {
			double g ;
			do {
				g = rand.nextGaussian();
				if (g <= 0) continue;
				g = g * this.codeLength;
				if (g < limit) break;
			} while (true);
			
			randomCV.setElementAt(i, (int) Math.round(g));
		}
		randomCV.normalise();
		return randomCV;
	}

}
