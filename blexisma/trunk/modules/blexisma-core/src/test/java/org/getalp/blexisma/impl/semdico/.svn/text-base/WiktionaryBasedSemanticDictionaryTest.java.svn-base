package org.getalp.blexisma.impl.semdico;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.DeviationBasedCVRandomizer;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.SemNetReaderTest;
import org.getalp.blexisma.semnet.SimpleSemanticNetwork;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class WiktionaryBasedSemanticDictionaryTest {
    private Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    

	SimpleSemanticNetwork<String, String> wn;
	String_RAM_VectorialBase vb = context.mock(String_RAM_VectorialBase.class);
	BufferedReader br;
	InputStream fis;
    ConceptualVectorRandomizer randomizer = new DeviationBasedCVRandomizer(2000, 2000000);

	WiktionaryBasedSemanticDictionary dict;
	
	@Before
	public void setUp() throws Exception {
		
		fis = SemNetReaderTest.class.getResourceAsStream("sample-semnet.snet");
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        wn = new SimpleSemanticNetwork<String, String>();
		TextOnlySemnetReader.readFromReader(wn, br);
		
		
		dict = new WiktionaryBasedSemanticDictionary(vb, wn);
	}
	
	@After
	public void tearDown() throws Exception {
		br.close();
		br = null;
		fis.close();
		fis = null;
	}

	@Test
	public void testSemanticDictionary() throws UninitializedRandomizerException {
		context.checking(new Expectations() {{
			allowing(vb).getVector(with(aNonNull(String.class)));   
			will(returnValue(randomizer.nextVector()));
	    }});

		SemanticDefinition sdef = dict.getDefinition("dictionnaire","fra");
		
		assertEquals("Incorrect number of senses.", sdef.getSenseList().size(), 10);
		assertTrue("Morpho should be a noun.", sdef.getSenseList().get(0).getMorpho().contains(MorphoProperties.NOUN));
	}

	@Test
	public void testDefinitionIsNotNull() throws UninitializedRandomizerException {
		context.checking(new Expectations() {{
			allowing(vb).getVector(with(aNonNull(String.class)));
		    will(returnValue(randomizer.nextVector()));
	    }});

		SemanticDefinition sdef = dict.getDefinition("tagada","fra");
		
		assertNotNull("getDefinition should never return null.", sdef);
		assertNotNull("Sense list should not be null.", sdef.getSenseList());
		assertEquals("Sense list should be empty.", sdef.getSenseList().size(), 0);
	}

	@Test
	public void testDefinitionWithUnknownSenses() throws UninitializedRandomizerException {
		context.checking(new Expectations() {{
			allowing(vb).getVector(with(aNonNull(String.class)));   
			will(returnValue(null));
			allowing(vb).addVector(with(aNonNull(String.class)), with(aNonNull(ConceptualVector.class)));   
			will(returnValue(true));
	    }});

		SemanticDefinition sdef = dict.getDefinition("dictionnaire","fra");
		
		assertEquals("Incorrect number of senses.", sdef.getSenseList().size(), 10);
		assertTrue("Morpho should be a noun.", sdef.getSenseList().get(0).getMorpho().contains(MorphoProperties.NOUN));
		for (Sense sense : sdef.getSenseList()) {
			assertNotNull("Vectors of unknown word sense should not be null.", sense.getVector());
		}
	}

}
