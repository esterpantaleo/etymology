package org.getalp.dbnary;

import static org.junit.Assert.assertEquals;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.junit.Test;


public class AbstractWiktionaryExtractorUtilitiesTest {
    @Test
    public void stripParentheses() {
	    // this set of tests is based on the behavior of the supParenthese that
	    // was in AbstractWiktionaryExtractor.java in June 2014.
	    // it was created to tests its new version, stripParentheses.

	    assertEquals(AbstractWiktionaryExtractor.stripParentheses(""), "");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("()"), "");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("("), "");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses(")"), ")");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("hello (bonjour) world"), "hello  world");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("(bonjour) world"), " world");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("hello (monde)"), "hello ");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("(bonjour le monde)"), "");
	    assertEquals(AbstractWiktionaryExtractor.stripParentheses("hello (bonjour) (le) world (monde)"), "hello   world ");
    }
    @Test
    public void removeXMLComments() {
	    // this set of tests is based on the behavior of the supParenthese that
	    // was in AbstractWiktionaryExtractor.java in June 2014.
	    // it was created to tests its new version, stripParentheses.

	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments(null), null);
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments(""), "");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("<!---->"), "");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("<!--"), "");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("-->"), "-->");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("hello <!--bonjour--> world"), "hello  world");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("<!--bonjour--> world"), " world");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("hello <!--monde-->"), "hello ");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("<!--bonjour le monde-->"), "");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("hello <!--bonjour--> <!--le--> world <!--monde-->"), "hello   world ");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("hello world <!--"), "hello world ");
	    assertEquals(AbstractWiktionaryExtractor.removeXMLComments("hello world <!--     "), "hello world ");
    }
}
