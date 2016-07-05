package org.getalp.blexisma.semnet;


import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.getalp.blexisma.api.GraphMLizableElement;
import org.getalp.blexisma.api.SemanticNetwork;
import org.junit.Before;
import org.junit.Test;

public class StringSemNetGraphMLizerTest {
    
    SemanticNetwork<String, String> sm = new SimpleSemanticNetwork<String, String>();
    StringWriter sw = new StringWriter();
    StringSemNetGraphMLizer gout = new StringSemNetGraphMLizer(sw, "UTF-8", StringSemNetGraphMLizer.MULLING_OUTPUT);
    @Before
    public void setUp() throws Exception {
        sm.addRelation("A", "B", 1.0f, "def");
        sm.addRelation("A", "C", 1.0f, "pos");
    }
    
    @Test
    public void testGraphML() throws IOException {
        gout.dump(sm);
        assertTrue(sw.toString().contains(">A</data>"));
        assertTrue(sw.toString().contains(">def</data>"));
    }

}
