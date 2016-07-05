package org.getalp.blexisma.semanalysis;


import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.ADJECTIVE;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.NOUN;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.OTHER;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.VERB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.DeviationBasedCVRandomizer;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticDictionary;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.XMLDataFormatter;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.TreeDecorator;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranAnswerParser;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranParsingException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author serasset
 *
 */
@RunWith(JMock.class)
public class SemanticAnalysisTest {
	private Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private static int DIM = 2000;
    private static final int NORM = 32768;
    private static ConceptualVectorRandomizer randomizer = new DeviationBasedCVRandomizer(DIM, NORM);
    
	private static String realTreeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Node isLeaf=\"false\" lemme=\"ROOT\"><Node isLeaf=\"false\" Groupe=\"PHAMBG\">" +
			"<Node isLeaf=\"false\" Groupe=\"PHINF\" categorie=\"verb\" type_verbe=\"infinitiveVerb\">" +
			"<Node isLeaf=\"false\" Groupe=\"GV\" categorie=\"verb\" type_verbe=\"infinitiveVerb\">" +
			"<Node isLeaf=\"true\" categorie=\"verb\" forme=\"Communiquer\" fonction=\"GOV\" lemme=\"communiquer\" type_verbe=\"infinitiveVerb\" />" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" fonction=\"OBJT\" genre=\"FEM\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"preposition\" forme=\"de\" fonction=\"DES\" lemme=\"de\" />" +
			"<Node isLeaf=\"true\" categorie=\"DETERM\" forme=\"l’\" genre=\"MAS,FEM\" lemme=\"le\" nombre=\"SIN\" type_déterminant=\"definiteArticle\" />" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"information\" fonction=\"GOV\" genre=\"FEM\" lemme=\"information\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"<Node isLeaf=\"false\" Groupe=\"adjectivalPhrase\" categorie=\"ADJOINT\" fonction=\"ATTR\" genre=\"FEM\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"fausse\" fonction=\"GOV\" genre=\"FEM\" lemme=\"faux\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\" /></Node></Node></Node>" +
			"<Node isLeaf=\"false\" Groupe=\"GADV\" categorie=\"ADJOINT\" fonction=\"COMPCIR\" type_adjoint=\"adverb\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"sciemment\" fonction=\"GOV\" lemme=\"sciemment\" type_adjoint=\"adverb\" /></Node>" +
			"<Node isLeaf=\"true\" categorie=\"PONCT\" forme=\".\" lemme=\".\" /></Node>" +
			"<Node isLeaf=\"false\" Groupe=\"PHELIS\" categorie=\"noun\" type_nom=\"properNoun\" type_verbe=\"infinitiveVerb\">" +
			"<Node isLeaf=\"false\" Groupe=\"nounPhrase\" categorie=\"noun\" fonction=\"SUJ\" type_nom=\"properNoun\" type_verbe=\"infinitiveVerb\">" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"Communiquer\" fonction=\"GOV\" lemme=\"communiquer\" type_nom=\"properNoun\" type_verbe=\"infinitiveVerb\" /></Node>" +
			"<Node isLeaf=\"false\" Groupe=\"GADV\" categorie=\"ADJOINT\" fonction=\"COMPCIR\" type_adjoint=\"adverb\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"sciemment\" fonction=\"GOV\" lemme=\"sciemment\" type_adjoint=\"adverb\" />" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" fonction=\"ATTR\" genre=\"FEM\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"preposition\" forme=\"de\" fonction=\"DES\" lemme=\"de\" /><Node isLeaf=\"true\" categorie=\"DETERM\" forme=\"l’\" genre=\"MAS,FEM\" lemme=\"le\" nombre=\"SIN\" type_déterminant=\"definiteArticle\" />" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"information\" fonction=\"GOV\" genre=\"FEM\" lemme=\"information\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"<Node isLeaf=\"false\" Groupe=\"adjectivalPhrase\" categorie=\"ADJOINT\" fonction=\"ATTR\" genre=\"FEM\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"fausse\" fonction=\"GOV\" genre=\"FEM\" lemme=\"faux\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\" />" +
			"</Node></Node></Node>" +
			"<Node isLeaf=\"true\" categorie=\"PONCT\" forme=\".\" lemme=\".\" /></Node></Node></Node>";
	
	private static String oneNodeTreeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<Node isLeaf=\"true\" categorie=\"verb\" forme=\"Communiquer\" fonction=\"GOV\" lemme=\"communiquer\" type_verbe=\"infinitiveVerb\" />";

	private static String buggyXml = "<Node isLeaf=\"false\" lemme=\"ROOT\"><Node isLeaf=\"false\" Groupe=\"PH\" categorie=\"verb\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\">" +
			"<Node isLeaf=\"false\" Groupe=\"nounPhrase\" categorie=\"REP\" fonction=\"SUJ\" genre=\"MAS\" nombre=\"SIN\" type_representant=\"personnalPronoun\">" +
			"<Node isLeaf=\"true\" categorie=\"REP\" forme=\"Il\" fonction=\"GOV\" genre=\"MAS\" lemme=\"il\" nombre=\"SIN\" type_representant=\"personnalPronoun\" />" +
			"</Node><Node isLeaf=\"false\" Groupe=\"GV\" categorie=\"verb\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\">" +
			"<Node isLeaf=\"false\" Groupe=\"nounPhrase\" categorie=\"REP\" fonction=\"OBJI\" nombre=\"SIN,PLU\" type_representant=\"REFL\"" +
			"<Node isLeaf=\"true\" categorie=\"REP\" forme=\"s'\" fonction=\"GOV\" lemme=\"se\" nombre=\"SIN,PLU\" type_representant=\"REFL\" />" +
			"</Node><Node isLeaf=\"true\" categorie=\"verb\" fonction=\"GOV\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\" />" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" fonction=\"OBJT\" genre=\"MAS\" nombre=\"PLU\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" genre=\"MAS\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"preposition\" forme=\"d\u2019\" fonction=\"DES\" lemme=\"de\" />" +
			"<Node isLeaf=\"true\" categorie=\"DETERM\" forme=\"un\" genre=\"MAS\" lemme=\"un\" nombre=\"SIN\" type_déterminant=\"ARTI\" />" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"valet\" fonction=\"GOV\" genre=\"MAS\" lemme=\"valet\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" fonction=\"ATTR\" genre=\"FEM\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"preposition\" forme=\"de\" fonction=\"DES\" lemme=\"de\" />" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"chambre\" fonction=\"GOV\" genre=\"FEM\" lemme=\"chambre\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"</Node></Node>" +
			"<Node isLeaf=\"true\" categorie=\"CONJCT\" forme=\"ou\" lemme=\"ou\" type_conjonction=\"coordinatingConjunction\" />" +
			"<Node isLeaf=\"false\" Groupe=\"GNPREP\" categorie=\"noun\" genre=\"MAS\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"preposition\" forme=\"d\u2019\" fonction=\"DES\" lemme=\"de\" />" +
			"<Node isLeaf=\"true\" categorie=\"DETERM\" forme=\"un\" genre=\"MAS\" lemme=\"un\" nombre=\"SIN\" type_déterminant=\"ARTI\" />" +
			"<Node isLeaf=\"false\" Groupe=\"adjectivalPhrase\" categorie=\"ADJOINT\" fonction=\"ATTR\" genre=\"MAS\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"autre\" fonction=\"GOV\" genre=\"MAS,FEM\" lemme=\"autre\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\" />" +
			"</Node>" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"homme\" fonction=\"GOV\" genre=\"MAS\" lemme=\"homme\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"<Node isLeaf=\"false\" Groupe=\"adjectivalPhrase\" categorie=\"ADJOINT\" fonction=\"ATTR\" genre=\"MAS\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\" type_verbe=\"PAPA\">" +
			"<Node isLeaf=\"true\" categorie=\"ADJOINT\" forme=\"gagé\" fonction=\"GOV\" genre=\"MAS\" lemme=\"gagé\" nombre=\"SIN\" type_adjoint=\"qualifierAdjective\" type_verbe=\"PAPA\" />" +
			"</Node></Node>" +
			"<Node isLeaf=\"false\" Groupe=\"PHREL\" categorie=\"verb\" fonction=\"ATTR\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\">" +
			"<Node isLeaf=\"false\" Groupe=\"nounPhrase\" categorie=\"REP\" fonction=\"SUJ\" type_representant=\"relativePronoun\">" +
			"<Node isLeaf=\"true\" categorie=\"REP\" forme=\"qui\" fonction=\"GOV\" lemme=\"qui\" type_representant=\"relativePronoun\" />" +
			"</Node><Node isLeaf=\"false\" Groupe=\"GV\" categorie=\"verb\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\">" +
			"<Node isLeaf=\"true\" categorie=\"verb\" forme=\"mène\" fonction=\"GOV\" lemme=\"mener\" nombre=\"SIN\" type_verbe=\"conjugatedVerb\" />" +
			"<Node isLeaf=\"false\" Groupe=\"nounPhrase\" categorie=\"noun\" fonction=\"OBJT\" genre=\"FEM\" nombre=\"SIN\" type_nom=\"commonNoun\">" +
			"<Node isLeaf=\"true\" categorie=\"DETERM\" forme=\"une\" genre=\"FEM\" lemme=\"un\" nombre=\"SIN\" type_déterminant=\"ARTI\" />" +
			"<Node isLeaf=\"true\" categorie=\"noun\" forme=\"dame\" fonction=\"GOV\" genre=\"FEM\" lemme=\"dame\" nombre=\"SIN\" type_nom=\"commonNoun\" />" +
			"</Node></Node></Node></Node></Node>" +
			"<Node isLeaf=\"true\" categorie=\"PONCT\" forme=\".\" lemme=\".\" /></Node></Node>";
	
	private AnalysisTree tree;
	private TreeDecorator td;
	private SemanticDictionary sd;
	private ContextualizingSemanticAnalysis analyser;
	private SinglePassSemanticAnalysis singlePassAnalyser;
	private NoSemanticAnalysis nosemAnalyser;
	private SemanticDefinition sdefCommuniquer;
	
	@Before
	public void setUp() throws Exception {
		analyser = new ContextualizingSemanticAnalysis();
		analyser.setParameter(ContextualizingSemanticAnalysis.STRONG_CONTEXTUALIZATION_DELTA, "0.1");
		singlePassAnalyser = new SinglePassSemanticAnalysis();
		nosemAnalyser = new NoSemanticAnalysis();
		sd = context.mock(SemanticDictionary.class);
		td = new TreeDecorator(sd);
	}

	/**
	 * Test case with:
	 *  - a tree consisting of a unique leaf node.
	 *  - bearing a known word with only one word sense (associated to a non null vector v1)
	 *  - in a null context
	 * Expected computed vector should be non null
	 * Expected computed vector should be equals to v1
	 * @throws SygfranParsingException
	 * @throws UninitializedRandomizerException 
	 */
	@Test
	public void testSemanticAnalysisWithSimpleTree() throws SygfranParsingException, UninitializedRandomizerException {
		tree = SygfranAnswerParser.sygfranToTree(oneNodeTreeXml);
		ConceptualVector contextVector = new ConceptualVector(DIM, NORM);
		ArrayList<Sense> meaningList = new ArrayList<Sense>();
        meaningList.add(new Sense(null,"#def|tagada", randomizer.nextVector(), Arrays.asList(VERB)));
		sdefCommuniquer = new SemanticDefinition("communiquer", new ConceptualVector(DIM, NORM), meaningList);
		final SemanticDefinition emptydef = new SemanticDefinition();
		context.checking(new Expectations() {{
			allowing(sd).getDefinition(with(equal("communiquer")), with(equal("fra")));    
			will(returnValue(sdefCommuniquer));
			allowing(sd).getDefinition(with(equal("Communiquer")), with(equal("fra")));    
			will(returnValue(emptydef));
	    }});
		
		tree = td.simpleDecorate(tree, "fra", DIM, NORM);
		ConceptualVector cv = singlePassAnalyser.computeConceptualVector(tree,contextVector);
		// Result should not be null.
		assertFalse("Resulting vector should not be null", cv.equals(new ConceptualVector(DIM, NORM)));
		// Result should be close to the only def.
		double dist = cv.getAngularDistance(sdefCommuniquer.getSenseList().get(0).getVector());
		assertEquals("Distance from initial vector should be quasi null.", 0, dist, 0.1);
		
	}
	
//	@Test
//	public void testSemanticAnalysisWithOnlyUnknownWords() throws SygfranParsingException {
//		tree = SygfranAnswerParser.sygfranToTree(realTreeXml);
//		ConceptualVector contextVector = new ConceptualVector(DIM, NORM);
//
//		context.checking(new Expectations() {{
//			
//			ArrayList<Sense> meaningList = new ArrayList<Sense>();
//			SemanticDefinition sdefUnknownWord = new SemanticDefinition("", null, meaningList);
//
//			allowing(sd).getDefinition(with(equal("communiquer")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal("de")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal("le")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal("information")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal("faux")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal("sciemment")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//		    allowing(sd).getDefinition(with(equal(".")), with(equal("fra")));    
//		    will(returnValue(sdefUnknownWord));
//	    }});
//		
//		tree = td.simpleDecorate(tree, "fra", DIM, NORM);
//		ConceptualVector cv = singlePassAnalyser.computeConceptualVector(tree,contextVector);
//		assertEquals(cv, new ConceptualVector(DIM, NORM));
//	}
	
	@Test
	public void testSemanticAnalysisWithNonNullDefinitions() throws SygfranParsingException, UninitializedRandomizerException {
		ConceptualVector contextVector = new ConceptualVector(DIM, NORM);
		tree = SygfranAnswerParser.sygfranToTree(realTreeXml);

		context.checking(new Expectations() {{
			Sense[] somesenses = new Sense[15];
			for (int i = 0; i < 15; i++) {
				somesenses[i] = new Sense(null,"#def|the def does not matter here", 
						randomizer.nextVector(), 
						Arrays.asList((i%3!=0 ? (i%3 != 1) ? VERB : ADJECTIVE : NOUN)));
			}
			ArrayList<Sense> s;
			allowing(sd).getDefinition(with(equal("communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
			s.add(somesenses[0]);
			s.add(somesenses[1]);
			allowing(sd).getDefinition(with(equal("Communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
		    will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("de")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(somesenses[2]);
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("le")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(somesenses[3]);
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("information")), with(equal("fra")));    
		    s = new ArrayList<Sense>();
			s.add(somesenses[4]);
			s.add(somesenses[5]);
			s.add(somesenses[6]);
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("faux")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			s.add(somesenses[7]);
			s.add(somesenses[8]);
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("fausse")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("sciemment")), with(equal("fra")));    
		    s = new ArrayList<Sense>();
			s.add(somesenses[9]);
			s.add(somesenses[10]);
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal(".")), with(equal("fra")));    
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			}});
		
		tree = td.simpleDecorate(tree, "fra", DIM, NORM);
		ConceptualVector cv = singlePassAnalyser.computeConceptualVector(tree,contextVector);
		assertFalse("Resulting vectors should not be null.", cv.equals(new ConceptualVector(DIM, NORM)));
		// System.out.println(cv);
	}
	
	@Test
	public void testSemanticAnalysisWithDisambiguation() throws SygfranParsingException, UninitializedRandomizerException {
		final int DIM = 10;
		final int NORM = 256;
		ConceptualVector contextVector = new ConceptualVector(DIM, NORM);
		tree = SygfranAnswerParser.sygfranToTree(realTreeXml);

		context.checking(new Expectations() {{
			Sense[] somesenses = new Sense[15];
			for (int i = 0; i < 15; i++) {
				somesenses[i] = new Sense(null,"#def|the def does not matter here", 
						randomizer.nextVector(), 
						Arrays.asList((i%3!=0 ? (i%3 != 1) ? VERB : ADJECTIVE : NOUN)));
			}
			ArrayList<Sense> s;
			allowing(sd).getDefinition(with(equal("communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("ff 1 1 1 1 1 1 1 1 1", DIM, NORM), Arrays.asList(VERB)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 1 1 1 1 1 ff", DIM, NORM), Arrays.asList(VERB)));
		    will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("Communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
		    will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("de")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("le")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("information")), with(equal("fra")));    
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("ff a0 1 1 1 1 1 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 ff 1 1 1 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 1 1 ff 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("faux")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("a0 a0 1 1 1 1 1 1 1 1", DIM, NORM), Arrays.asList(ADJECTIVE)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 1 1 1 1 ff 1", DIM, NORM), Arrays.asList(ADJECTIVE)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("fausse")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("sciemment")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal(".")), with(equal("fra")));    
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			}});
		
		tree = td.simpleDecorate(tree, "fra", DIM, NORM);
		ConceptualVector cv = singlePassAnalyser.computeConceptualVector(tree,contextVector);
		
		ConceptualVector cvcontextualized = analyser.computeConceptualVector(tree,contextVector);
		assertFalse("Resulting vectors should not be null.", cv.equals(cvcontextualized));
		// System.out.println(cv);
	}

	@Test
	public void testNoSemanticAnalysis() throws SygfranParsingException, UninitializedRandomizerException {
		final int DIM = 10;
		final int NORM = 256;
		ConceptualVector contextVector = new ConceptualVector(DIM, NORM);
		tree = SygfranAnswerParser.sygfranToTree(realTreeXml);

		context.checking(new Expectations() {{
			Sense[] somesenses = new Sense[15];
			for (int i = 0; i < 15; i++) {
				somesenses[i] = new Sense(null,"#def|the def does not matter here", 
						randomizer.nextVector(), 
						Arrays.asList((i%3!=0 ? (i%3 != 1) ? VERB : ADJECTIVE : NOUN)));
			}
			ArrayList<Sense> s;
			allowing(sd).getDefinition(with(equal("communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("ff 1 1 1 1 1 1 1 1 1", DIM, NORM), Arrays.asList(VERB)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 1 1 1 1 1 ff", DIM, NORM), Arrays.asList(VERB)));
		    will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("Communiquer")), with(equal("fra")));
			s = new ArrayList<Sense>();
		    will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("de")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("le")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
		    allowing(sd).getDefinition(with(equal("information")), with(equal("fra")));    
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 ff 1 1 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 ff 1 1 1 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("1 1 1 1 1 ff 1 1 1 1", DIM, NORM), Arrays.asList(NOUN)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("faux")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(ADJECTIVE)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("fausse")), with(equal("fra")));  
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal("sciemment")), with(equal("fra")));
		    s = new ArrayList<Sense>();
			s.add(new Sense(null,"#def|the def does not matter here", new ConceptualVector("0 0 0 0 0 0 0 0 0 0", DIM, NORM), Arrays.asList(OTHER)));
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			allowing(sd).getDefinition(with(equal(".")), with(equal("fra")));    
			s = new ArrayList<Sense>();
			will(returnValue(new SemanticDefinition("", new ConceptualVector(DIM, NORM), s)));
			}});
		
		tree = td.simpleDecorate(tree, "fra", DIM, NORM);
		// System.out.println(XMLDataFormatter.xmlFormat(tree));
		ConceptualVector cv = nosemAnalyser.computeConceptualVector(tree,contextVector);
		ConceptualVector cvt = new ConceptualVector("ff 1 1 ff ff ff 1 1 1 ff", DIM, NORM);
		cvt.normalise();
		
		assertEquals(cvt.getCosineSimilarity(cv), 1, .01);
		//System.out.println(cv);
	}

	
	public void displayBuggyXml() throws SygfranParsingException {
		tree = SygfranAnswerParser.sygfranToTree(realTreeXml);
		tree = td.simpleDecorate(tree, "fra", DIM, NORM);

		System.out.println(tree.toString());
	}

}
