package org.getalp.blexisma.api;


import static org.junit.Assert.*;

import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.junit.Test;

public class CVRandomizerTest {

	private static int DIM = 2000;
	private static int CODELENGTH = 1000000;
	private static int SAMPLE_SIZE = 1000;
	
	protected UniformCVRandomizer urand = new UniformCVRandomizer(DIM, CODELENGTH);
	protected UniformCoordinatesCVRandomizer ucrand = new UniformCoordinatesCVRandomizer(DIM, CODELENGTH);
	protected DeviationBasedCVRandomizer dbrand = new DeviationBasedCVRandomizer(DIM, CODELENGTH);
	protected EdgePlanesCVRandomizer eprand = new EdgePlanesCVRandomizer(DIM, CODELENGTH);

	@Test
	public void testGeneration() {
		for (int i = 0; i < 100; i++) {
			ConceptualVector cv = urand.nextVector();
			assertEquals(cv.getMagnitude(), 1000000., 1.1);
		}
	}
	
	@Test
	public void testDispersionForUniform() {
		ConceptualVector[] vs = new ConceptualVector[SAMPLE_SIZE];
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			vs[i] = urand.nextVector();
		}
		
		double mean = getDispersion(vs);
		
		System.out.println("Uniform Randomizer:");
		System.out.println("Average distance to barycenter for " + SAMPLE_SIZE + " elements: " + mean);
		
	}
	
	@Test
	public void testDispersionForDeviationBased() throws UninitializedRandomizerException {
		ConceptualVector[] vs = new ConceptualVector[SAMPLE_SIZE];
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			vs[i] = dbrand.nextVector();
		}
		
		double mean = getDispersion(vs);
		
		System.out.println("Deviation Based Randomizer:");
		System.out.println("Average distance to barycenter for " + SAMPLE_SIZE + " elements: " + mean);
		
	}

	@Test
	public void testDispersionForEdgePlanes() {
		ConceptualVector[] vs = new ConceptualVector[SAMPLE_SIZE];
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			vs[i] = eprand.nextVector();
		}
		
		double mean = getDispersion(vs);
		
		System.out.println("EdgePlanes Randomizer:");
		System.out.println("Average distance to barycenter for " + SAMPLE_SIZE + " elements: " + mean);
		
	}

	@Test
	public void testDispersionForUniformCoordinates() {
		ConceptualVector[] vs = new ConceptualVector[SAMPLE_SIZE];
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			vs[i] = ucrand.nextVector();
		}
		
		double mean = getDispersion(vs);
		
		System.out.println("UniformCoordinates Randomizer:");
		System.out.println("Average distance to barycenter for " + SAMPLE_SIZE + " elements: " + mean);
		
	}

	public double getDispersion(ConceptualVector[] vs) {
		ConceptualVector barycenter = new ConceptualVector(DIM, CODELENGTH);
		for (int i = 0; i < vs.length; i++) {
			barycenter.add(vs[i]);
		}
		barycenter.normalise();
		
		double mean = 0;
		double localdist = 0;
		for (int i=0; i < vs.length; i++) {
			localdist = barycenter.getAngularDistance(vs[i]);
			mean += localdist;
		}
		return mean / vs.length;
	}
}
