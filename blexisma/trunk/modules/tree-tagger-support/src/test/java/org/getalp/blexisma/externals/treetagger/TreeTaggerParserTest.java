package org.getalp.blexisma.externals.treetagger;

import static org.junit.Assert.*;

import org.getalp.blexisma.externals.treetagger.TreeTaggerAnalysisTree.Node;
import org.junit.Test;


public class TreeTaggerParserTest {

	String test1 = "Bonjour	NOM	bonjour\n" +
	"à	PRP	à\n" + 
	"tous	PRO:IND	tout\n" +
	"les	DET:ART	le\n" +
	"gens	NOM	gens\n" +
	"de	PRP	de\n" +
	"la	DET:ART	le\n" +
	"planète	NOM	planète\n" +
	"U.	ABR	<unknown>\n" +
	"R.	ABR	<unknown>\n" +
	"S.	ABR	<unknown>\n" +
	"S.	ABR	<unknown>\n" +
	".	SENT	.";

	@Test
	public void testSimpleParse() {
		Node n = TreeTaggerAnalysisTree.parse(test1);
		assertNotNull(n);
		assertNotNull(n.getChildren());
		assertEquals("bonjour", n.getChildren().get(0).getLemma());
		assertEquals("PRP", n.getChildren().get(1).getPos());
		assertEquals("Bonjour", n.getChildren().get(0).getOccurence());
		assertEquals(13, n.getChildren().size());
		// System.err.println(XMLTreeTaggerOutputFormatter.xmlFormat(n));
	}
}
