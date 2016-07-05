package org.getalp.blexisma.semnet;


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SemNetReaderTest {
	
	BufferedReader br;
	InputStream fis;
	SimpleSemanticNetwork<String,String> sn;
	
	@Before
	public void setUp() throws Exception {
		fis = SemNetReaderTest.class.getResourceAsStream("sample-semnet.snet");
        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        sn = new SimpleSemanticNetwork<String, String>();
		TextOnlySemnetReader.readFromReader(sn, br);
	}
	
	@After
	public void tearDown() throws Exception {
		br.close();
		br = null;
		fis.close();
		fis = null;
	}
	
	@Test
	public void testNumberOfElements() throws Exception {
		
		assertEquals(156,sn.getNbEdges());
		assertEquals(145,sn.getNbNodes());
		// sn.dumpToWriter(System.out);
	}
	
	@Test
	public void testNumberOfFrenchNodes() throws Exception {
		Iterator<String> it = sn.getNodesIterator();
		int n = 0;
		while(it.hasNext()) {
			String node = it.next();
			if (node.startsWith("#fra")) n++;
		}
		assertEquals(13, n);
	}
	
//	@Test
//	public void testOutput() throws Exception {
//		StringSemNetGraphMLizer sout = new StringSemNetGraphMLizer(StringSemNetGraphMLizer.MULLING_OUTPUT);
//		sout.dump(sn);
//	}
}
