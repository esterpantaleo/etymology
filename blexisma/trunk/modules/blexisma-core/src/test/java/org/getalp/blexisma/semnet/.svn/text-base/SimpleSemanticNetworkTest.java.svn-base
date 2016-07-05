package org.getalp.blexisma.semnet;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class SimpleSemanticNetworkTest {

    SimpleSemanticNetwork<String,String> sn1;
    // SimpleSemanticNetwork<Integer, Integer> sn2;
    
    @Before
    public void setUp() throws Exception {
        sn1 = new SimpleSemanticNetwork<String, String>();
        // sn2 = new SimpleSemanticNetwork<Integer, Integer>();
    }
    
    @Test
    public void testAddNode() {
        // fail("Not yet implemented");
        String s1 = new String("Test");
        s1 = s1 + "1";
        String s2 = new String("Test1");
        String s = "Origin";
        
        assertNotSame(s1, s2);
        assertEquals(s1, s2);
        
        sn1.addRelation(s, s1, 1, "x");
        sn1.addRelation(s, s2, 1, "x");
        
        assertEquals(sn1.getNbNodes(), 2);
        
        sn1.addRelation(s, s2, 1, "y");

        assertEquals(sn1.getEdges("Origin").size(), 3);

        Iterator<SimpleSemanticNetwork<String,String>.Relation> it = sn1.getEdges("Origin").iterator();
        String t1 = it.next().getDestination();
        String t2 = it.next().getDestination();
        
        assertEquals(t1, t2);
    }
    
}
