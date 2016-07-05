package org.getalp.blexisma.syntax;


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranAnswerParser;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranParsingException;
import org.junit.Test;

public class sygfranSyntaxTest {

	@Test
	public void testProblematicTree() throws IOException, SygfranParsingException {
		InputStream ttt = this.getClass().getResourceAsStream("problematic_tree_from_trace.xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(ttt,"UTF-8"));
		StringBuffer strb = new StringBuffer();
		String nl = System.getProperty("line.separator", "\n");
		String s = br.readLine();
		while (null != s) {
			strb.append(s);
			strb.append(nl); 
			s = br.readLine();	
		}
		
		AnalysisTree tree = SygfranAnswerParser.sygfranToTree(strb.toString());
		
		assertFalse("Analysis tree should not contain a leaf with null lemma.", containLeafWithNullForm(tree));
	}

	public static boolean containLeafWithNullForm(AnalysisTree tree) {
		if (tree.isLeaf()) {
			return tree.getInfos().getWord() == null;
		} else {
			boolean c = false;
			for (AnalysisTree child : tree.getChildren()) {
				c = c || containLeafWithNullForm(child);
			}
			return c;
		}
	}
}
