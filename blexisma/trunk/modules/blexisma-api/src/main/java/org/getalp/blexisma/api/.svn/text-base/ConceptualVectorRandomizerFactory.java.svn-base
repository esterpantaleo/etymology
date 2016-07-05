package org.getalp.blexisma.api;

import java.io.Serializable;
import java.util.Random;

/**
 * Factory method that creates a conceptual vector randomizer for a given dimension and encoding.
 * 
 * The actual class used as an implementation of ConceptualVectorRandomizer is determined by the system
 * property "org.getalp.blexisma.randomizer".
 * 
 * If the property is not set or if it does not denote a valid ConceptualVectorRandomizer then a 
 * randomizer of class {@link org.getalp.blexisma.api.DeviationBasedCVRandomizer} is returned.
 *  
 * @author serasset
 *
 */
public class ConceptualVectorRandomizerFactory {

	private static final String defaultRandomizer = "org.getalp.blexisma.api.DeviationBasedCVRandomizer";
	public static final String CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY = "org.getalp.blexisma.randomizer";
	
	public static ConceptualVectorRandomizer createRandomizer() {
		String randomizerClassname = System.getProperty(CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY, defaultRandomizer);
		return createRandomizer(randomizerClassname);
	}
	public static ConceptualVectorRandomizer createRandomizer(long seed) {
		String randomizerClassname = System.getProperty(CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY, defaultRandomizer);
		ConceptualVectorRandomizer rand = createRandomizer(randomizerClassname);
		rand.setSeed(seed);
		return rand;
	}

	public static ConceptualVectorRandomizer createRandomizer(int cvDimension, int cvEncodingSize) {
		String randomizerClassname = System.getProperty(CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY, defaultRandomizer);
		return createRandomizer(cvDimension, cvEncodingSize, randomizerClassname);
	}

	public static ConceptualVectorRandomizer createRandomizer(int cvDimension, int cvEncodingSize, long seed) {
		String randomizerClassname = System.getProperty(CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY, defaultRandomizer);
		return createRandomizer(cvDimension, cvEncodingSize, seed, randomizerClassname);
	}

	public static ConceptualVectorRandomizer createRandomizer(int dimension, int codeLength, String qualifiedRandomizerClassname) {
		ConceptualVectorRandomizer rand = createRandomizer(qualifiedRandomizerClassname);
		rand.setDimension(dimension);
		rand.setCodeLength(codeLength);
		return rand;
	}
	
	public static ConceptualVectorRandomizer createRandomizer(int dimension, int codeLength, long seed, String qualifiedRandomizerClassname) {
		ConceptualVectorRandomizer rand = createRandomizer(dimension, codeLength, qualifiedRandomizerClassname);
		rand.setSeed(seed);
		return rand;
	}
	
	public static ConceptualVectorRandomizer createRandomizer(String qualifiedRandomizerClassname) {
		ConceptualVectorRandomizer rand;
		try {
			rand = (ConceptualVectorRandomizer) Class.forName(qualifiedRandomizerClassname).newInstance();
		} catch (Exception e) {
			rand = new DeviationBasedCVRandomizer();
			e.printStackTrace();
		}
		return rand;
	}
	

}
