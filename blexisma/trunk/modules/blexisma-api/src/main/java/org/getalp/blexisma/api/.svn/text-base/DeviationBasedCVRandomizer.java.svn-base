package org.getalp.blexisma.api;

import java.util.Arrays;


public class DeviationBasedCVRandomizer extends ConceptualVectorRandomizer {

	public double coefVar = 1.5;

	public DeviationBasedCVRandomizer() {
		super();
	}

	public DeviationBasedCVRandomizer(int dim, int codeLength) {
		super(dim, codeLength);
	}
	
	public DeviationBasedCVRandomizer(int dim, int codeLength, long seed) {
		super(dim, codeLength, seed);
	}
	
	@Override
	public ConceptualVector nextVector() throws UninitializedRandomizerException {
		if (dim == -1 || codeLength == -1) throw new UninitializedRandomizerException();
		return nextVector(coefVar, 1);
	}

	@Override
	public void setOption(String name, double value) {
		if ("coefVar".equals(name)) coefVar = value;
	}
	
    public ConceptualVector nextVector(double coeffVar, int boundary) {
        int[] randomArr = new int[dim];

        // int seed = -1;
        // while ( seed < 500 )
        // seed = (int) (Math.random() * codeLength);
        Arrays.fill(randomArr, codeLength);

        // for ( int i = 0; i < dim; i++ ) {
        // do {
        // randomArr[i] = (int) (Math.random() * codeLength);
        // } while ( randomArr[i] <= 1 );
        // }

        ConceptualVector randomCV = new ConceptualVector(randomArr, codeLength);
        randomCV.normalise();
        // System.out.println(randomCV.toString().substring(0,50));
        double c = randomCV.coeffVar();

        int loopCount = 0;
        // while( (c < coeffVar - boundary || c > coeffVar + boundary) &&
        // loopCount++ < 100 ) {
        // randomly increase or decrease the values by 10
        for (int i = 0; i < dim; i++) {
            randomCV.setElementAt(i, (int) (randomCV.getElementAt(i) + (Math.random() * 2000 - 1000)));
        }
        while (c < coeffVar && loopCount++ < 100) {

            // System.out.println(randomCV.toString().substring(0,50));

            randomCV = randomCV.pow(1.2);
            // for ( int i = 0; i < dim; i++ ) {
            // if ( randomCV.V[i] <= 1 )
            // randomCV.V[i] = Math.max(1, (int)( Math.random() * 10 ) ) ;
            // }
            randomCV.normalise();
            c = randomCV.coeffVar();
            // System.out.println( "Coeff is now " + c + " after " + loopCount +
            // " loops");
        }

        return randomCV;
    }
}
