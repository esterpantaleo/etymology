package org.getalp.blexisma.api;


import static org.junit.Assert.*;

import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.junit.Test;

public class CVRandomizerFactoryTest {

	private static int DIM = 2000;
	private static int CODELENGTH = 1000000;
	private static int SAMPLE_SIZE = 1000;
	
	protected String urand = "org.getalp.blexisma.api.UniformCVRandomizer";
	protected String ucrand = "org.getalp.blexisma.api.UniformCoordinatesCVRandomizer";
	protected String dbrand = "org.getalp.blexisma.api.DeviationBasedCVRandomizer";
	protected String eprand = "org.getalp.blexisma.api.EdgePlanesCVRandomizer";

	@Test(expected=UninitializedRandomizerException.class)
	public void testUninitializedCreation() throws UninitializedRandomizerException {
		String pref = System.getProperty(ConceptualVectorRandomizerFactory.CONCEPTUAL_VECTOR_RANDOMIZER_PROPERTY);
		ConceptualVectorRandomizer rand = ConceptualVectorRandomizerFactory.createRandomizer();
		if (pref == null) {
			assertTrue("Randomizer class should be the default class.", rand instanceof DeviationBasedCVRandomizer);
		}
		rand.nextVector();
	}
	
	@Test
	public void testSpecifiedClass() {
		ConceptualVectorRandomizer rand = ConceptualVectorRandomizerFactory.createRandomizer(urand);
		assertTrue("Randomizer class should be " + urand, rand instanceof UniformCVRandomizer);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(ucrand);
		assertTrue("Randomizer class should be " + ucrand, rand instanceof UniformCoordinatesCVRandomizer);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(dbrand);
		assertTrue("Randomizer class should be " + dbrand, rand instanceof DeviationBasedCVRandomizer);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(eprand);
		assertTrue("Randomizer class should be " + eprand, rand instanceof EdgePlanesCVRandomizer);
	}
	
	@Test
	public void testDimensionAndCodelength() throws UninitializedRandomizerException {
		ConceptualVectorRandomizer rand = ConceptualVectorRandomizerFactory.createRandomizer(urand);
		checkDimAndCodelength(rand);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(ucrand);
		checkDimAndCodelength(rand);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(dbrand);
		checkDimAndCodelength(rand);
		rand = ConceptualVectorRandomizerFactory.createRandomizer(eprand);
		checkDimAndCodelength(rand);
	}
	
	public void checkDimAndCodelength(ConceptualVectorRandomizer rand) throws UninitializedRandomizerException {
		rand.setDimension(10);
		rand.setCodeLength(1000);
		ConceptualVector v = rand.nextVector();
		assertEquals(v.getCodeLength(), 1000);
		assertEquals(v.getDimension(), 10);
		rand.setDimension(100);
		rand.setCodeLength(10000);
		v = rand.nextVector();
		assertEquals(v.getCodeLength(), 10000);
		assertEquals(v.getDimension(), 100);
	}

}
