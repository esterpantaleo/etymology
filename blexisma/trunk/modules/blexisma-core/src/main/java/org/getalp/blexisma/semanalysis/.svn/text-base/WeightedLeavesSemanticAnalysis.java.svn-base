package org.getalp.blexisma.semanalysis;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.UnsupportedParameterException;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeightedLeavesSemanticAnalysis implements SemanticAnalysisMethod {

	static Logger logger = LoggerFactory.getLogger(WeightedLeavesSemanticAnalysis.class);

	@Override
	public ConceptualVector computeConceptualVector(AnalysisTree tree,
			ConceptualVector context) {
		ConceptualVector cv = new ConceptualVector(context.getDimension(),context.getCodeLength());
		
		recursiveAdd(tree,cv);
		cv.normalise();
		// logger.debug("Computed Conceptual Vector with NO SEMANTIC ANALYSIS.");
		return cv;
	}

	private void recursiveAdd(AnalysisTree tree, ConceptualVector cv) {
		if (tree.isLeaf()) {
			ConceptualVector lemmaVect = new ConceptualVector(cv.getDimension(),cv.getCodeLength());
			for (Sense sense : tree.getInfos().getDef().getSenseList())
				if (sense.getVector() != null)
					cv.add(sense.getVector());
		} else {
			for (AnalysisTree node : tree.getChildren()) {
				recursiveAdd(node,cv);
			}
		}
		
	}

	@Override
	public void setParameter(String param, String value)
			throws UnsupportedParameterException {
		logger.debug("No parameters are taken into account. Ignoring {}: {}", param, value);
	}

}
