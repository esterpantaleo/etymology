package org.getalp.blexisma.semnet;


import static org.junit.Assert.*;

import java.io.IOException;

import org.getalp.blexisma.api.GraphMLizableElement;
import org.getalp.blexisma.api.SemanticNetwork;
import org.junit.Before;
import org.junit.Test;

public class SemNetGraphMLizerTest {

    private class N implements GraphMLizableElement {
        int x, y, z;
        String t;
        
        public N(int x, int y, int z, String t) {
            this.x = x; this.y = y; this.z = z; this.t = t;
        }
        // Note: in order to make to nodes with the same value equals, one has to
        // override equals and hashCode consistently.
        @Override
        public boolean equals(Object o) {
            if (o == null || (! (o instanceof N))) return false;
            N on = (N) o;
            return (x == on.x && y == on.y && z == on.z && t.equals(on.t));
        }
        @Override
        public int hashCode() {
            return t.hashCode() + x + y + z;
        }
        @Override
        public int getNumberOfAttributes() {
            return 4;
        }
        @Override
        public String getAttributeNameForId(int i) {
            switch (i) {
            case 0: return "x";
            case 1: return "y";
            case 2: return "z";
            case 3: return "t";
            default:
                return null;
            }
        }
        @Override
        public String getAttributeTypeForId(int i) {
            switch (i) {
            case 3: return "string";
            default: return "int";
            }
        }
        @Override
        public String getAttributeDefaultForId(int i) {
            return null;
        }
        @Override
        public String getAttributeValueForId(int i) {
            switch (i) {
            case 0: return Integer.toString(x);
            case 1: return Integer.toString(y);
            case 2: return Integer.toString(z);
            case 3: return t;
            default:
                return null;
            }
        }
        
    }
    
    private class E implements GraphMLizableElement {
        String t;
        public E(String t){this.t = t;}
        public E(){this.t = "def";}
        @Override
        public int getNumberOfAttributes() {
            return 1;
        }
        @Override
        public String getAttributeNameForId(int i) {
            return "rel";
        }
        @Override
        public String getAttributeTypeForId(int i) {
            return "string";
        }
        @Override
        public String getAttributeDefaultForId(int i) {
            return "def";
        }
        @Override
        public String getAttributeValueForId(int i) {
            if (t.equals("def")) return null; else return t;
        }
        
    }
    
    SemanticNetwork<N, E> sm = new SimpleSemanticNetwork<N, E>();
    SemNetGraphMLizer gout = new SemNetGraphMLizer();
    @Before
    public void setUp() throws Exception {
        sm.addRelation(new N(1,2,3,"x"), new N(4,5,6,"y"), 1.0f, new E());
        sm.addRelation(new N(1,2,3,"x"), new N(1,2,3,"y"), 1.0f, new E("pos"));
    }
  
    @Test
    public void testHashCodeEquals() {
        N x = new N(1,2,3,"x");
        N y = new N(1,2,3,"x");
        
        assertEquals("x and y should be equal.", x, y);
        assertEquals("x and y should have the same hashCode.", x.hashCode(), y.hashCode());
    }
    
    @Test
    public void testGraphML() throws IOException {
        // gout.dump(sm);
    }

}
