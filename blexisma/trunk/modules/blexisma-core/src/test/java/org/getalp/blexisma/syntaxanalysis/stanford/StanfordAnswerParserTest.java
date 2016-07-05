package org.getalp.blexisma.syntaxanalysis.stanford;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.junit.Test;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;


public class StanfordAnswerParserTest {

	private static ArrayList<String> trees = new ArrayList<String>();
	private static EnglishLemmatizer lemmatizer;  
	static {
		trees.add("(ROOT [105.386] (NP [100.712] (NP [61.463] (NNP [11.670] Bing) (, [0.000] ,) (NNP [11.794] Google) (, [0.000] ,) " +
				"(CC [0.149] and) (NNP [11.850] Wolfram) (NNP [10.856] Alpha)) (PP [20.319] (IN [3.269] on) (NP [16.623] (DT [0.625] the) " +
				"(NNP [11.552] Future))) (PP [15.365] (IN [0.612] of) (NP [14.326] (NNP [11.707] Search))) (. [0.013] .)))");
	  
		trees.add("(ROOT [268.872] (S [268.728] (NP [130.171] (NP [23.569] (JJ [10.697] Complete) (NN [9.366] video)) (PP [105.268] " +
				"(IN [3.827] at) (: [0.894] :) (NP [91.499] (NP [14.666] (NN [12.592] http://fora.tv/2010/01/25/Digital_Life_Design_2010_Search)) " +
				"(VP [73.960] (VBG [11.908] Drawing) (PP [59.800] (IN [2.581] from) (NP [56.516] (NN [8.627] location) (NN [10.225] awareness) " +
				"(CC [0.149] and) (NN [9.011] voice) (NN [8.133] recognition) (NNS [8.424] technologies))))))) (, [0.000] ,) " +
				"(NP [71.589] (NP [10.208] (NNS [7.653] representatives)) (PP [60.954] (IN [3.609] from) (NP [56.918] (NNP [11.670] Bing) (, [0.000] ,) " +
				"(NNP [11.794] Google) (CC [0.149] and) (NNP [11.850] Wolfram) (NNP [10.856] Alpha)))) (VP [54.796] (VB [7.832] weigh) " +
				"(PRT [2.816] (RP [2.751] in)) (PP [39.054] (IN [2.683] on) (NP [34.830] (NP [10.383] (DT [0.625] the) (NN [7.919] future)) " +
				"(PP [23.852] (IN [0.612] of) (NP [22.812] (NN [10.218] Internet) (NN [8.978] search)))))) (. [0.003] .)))");
	  
		trees.add("(ROOT [205.369] (S [205.225] (NP [71.547] (NP [12.888] (NNP [11.670] Bing) (POS [0.075] 's)) (NNP [11.670] Blaise) (NNP [11.876] Aguera)" +
				" (NN [12.753] y) (NNS [12.733] Arcas)) (VP [130.735] (VBZ [6.715] promises) (NP [119.344] (NP [15.514] (DT [1.487] a) (VBN [4.467] revised) " +
				"(NN [7.226] form)) (PP [12.156] (IN [0.612] of) (NP [11.117] (NN [8.978] search))) (SBAR [84.535] (WHNP [1.387] (WDT [0.884] that)) " +
				"(S [82.715] (VP [82.449] (VBZ [3.852] offers) (NP [21.344] (NN [5.745] time) (CC [0.149] and) (NN [8.627] location)) (NP [48.850] " +
				"(NP [13.511] (JJ [5.550] specific) (NNS [4.765] results)) (VP [32.394] (VBN [16.307] custom-tailored) (PP [14.764] (TO [0.003] to) " +
				"(NP [12.231] (DT [0.625] the) (NN [9.323] user)))))))))) (. [0.003] .)))");
	
		try {
			lemmatizer = new EnglishLemmatizer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testStanfordAnswerParserWithMorphology() {
		BasicAnalysisTree tree = StanfordAnswerParser.buildStanfordTree(trees, lemmatizer);
		assertFalse(tree == null);
	}
}
