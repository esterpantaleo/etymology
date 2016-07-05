package org.getalp.blexisma.vectorialbase;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.DeviationBasedCVRandomizer;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.junit.Before;
import org.junit.Test;

public class RAM_VectorialBaseTest {
    
    public String_RAM_VectorialBase base;
    public ConceptualVector cv;
    
    @Before
    public void setUp() throws Exception {
        base = new String_RAM_VectorialBase(10000, 100, 100);
        ConceptualVectorRandomizer randomizer = new DeviationBasedCVRandomizer(100, 100);

        for(int i = 0; i < 10000; i++) { 
            base.addVector(Integer.toString(i),randomizer.nextVector());
        }
        cv = randomizer.nextVector();
    }

	@Test
    public void testGetProx(){
        ArrayList<String_RAM_VectorialBase.EntryDist> prox = base.getProx(cv, 100);
        assertTrue("Size is not the requested size.", prox.size() == 100);
    }
    
    @Test
    public void testSave()
    {
    	String tempDir = System.getProperty("java.io.tmpdir");
    	String baseFile = tempDir + File.separator + "base.ser";
    	base.save(baseFile);
    	base = null;
    	base = String_RAM_VectorialBase.load(baseFile);
        ArrayList<String_RAM_VectorialBase.EntryDist> prox = base.getProx(cv, 100);
        assertTrue("Size is not the requested size.", prox.size() == 100);
    }
    
    @Test
    public void testGetProxIsOrdered(){
        ArrayList<String_RAM_VectorialBase.EntryDist> prox = base.getProx(cv, 100);
        int i = 0;
        while (i != prox.size()-1) {
        	assertTrue("Element " + i + " in prox is greater than its successor", prox.get(i).distance >= prox.get(i+1).distance);
        	i++;
        }
    }
}
