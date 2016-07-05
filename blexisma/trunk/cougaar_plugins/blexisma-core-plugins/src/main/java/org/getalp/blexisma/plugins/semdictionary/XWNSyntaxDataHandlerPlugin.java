package org.getalp.blexisma.plugins.semdictionary;

import java.util.ArrayList;
import java.util.HashMap;

import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.syntaxanalysis.stanford.StanfordAnswerParser;

public class XWNSyntaxDataHandlerPlugin extends
		XWNDataHandlerPlugin {

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#initializeNextLearningJob(org.getalp.blexisma.cougaarcom.SemanticLearningJob)
	 */
	@Override
	protected void initializeNextLearningJob(SemanticLearningJob job) {
		
		String node, lg, txt;
		do {
			node = sni.next();
			lg = XWNSemanticDictionnary.extractLemmaLg(node);
		} while (!validLanguage(lg));

		// lastId = XWNSemanticDictionnary.extractLemmaText(node);

		if (log.isDebugEnabled()) {
			log.debug("Infinite next id [" + lg + "] :" + node );
		}

		lg = ISO639_3.sharedInstance.getIdCode(lg);
		txt = XWNSemanticDictionnary.extractLemmaText(node);
		job.setId(node);
		job.setLang(lg);
		job.setSemDefinition(dictionary.getSyntaxDefinition(txt, lg));
		
		job.setTrees(buildAnaTrees(job.getSemDefinition()));
		job.setPhase(SemanticJobPhase.WAITINGFORSEMANTIC);
		
		job.setVectNorm(cvEncodingSize);
		job.setVectSize(cvDimension);
	}

	private HashMap<String,AnalysisTree> buildAnaTrees(SemanticDefinition sem) {
		HashMap<String,AnalysisTree> trees = new HashMap<String,AnalysisTree>();
		ArrayList<Sense> senses = sem.getSenseList();
		AnalysisTree tmpTree = null;
		
		for (int i=0; i<senses.size(); i++) {
			ArrayList<String> sentences = new ArrayList<String>();
			sentences.add(senses.get(i).getNetworkDef());
			tmpTree = StanfordAnswerParser.buildStanfordTree(sentences, lemmatizer);
			tmpTree = decorateAnalysisTree(tmpTree, XWNSemanticDictionnary.extractLemmaLg(sem.getId()));
			trees.put(senses.get(i).getNetworkDef(), tmpTree);
		}
		
		return trees;
	}

}
