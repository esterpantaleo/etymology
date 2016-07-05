package org.getalp.dbnary.experiment.preprocessing;

import org.getalp.dbnary.experiment.preprocessing.fin.GlossFilter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFinnishGlossFilter {

	private GlossFilter filter;

	@Before
	public void setUp() throws Exception {
		filter = new GlossFilter();
	}

	@Test
	public void test1() {
		StructuredGloss sg = filter.extractGlossStructure("1., 2.");
		assertTrue(sg.gloss == null);
		assertTrue(sg.senseNumber.equals("1., 2."));
	}
	
	@Test
	public void test2() {
		StructuredGloss sg = filter.extractGlossStructure("1|3|4|soiti");
		assertTrue(sg.gloss.equals("soiti"));
		assertTrue(sg.senseNumber.equals("1,3,4"));
	}
}
