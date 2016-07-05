/**
 * *
 * SemanticTree.java
 * Created on 15 avr. 2011 08:21:32
 * 
 * Copyright (c) 2011 Didier Schwab
 */
package org.getalp.blexisma.semanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.UnsupportedParameterException;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

/**
 * @author Didier SCHWAB
 *
 */
public class ContextualizingSemanticAnalysis implements SemanticAnalysisMethod {

    public static final String STRONG_CONTEXTUALIZATION_DELTA = "org.getalp.blexisma.semanalysis.delta";
	public static int MAX_ITER = 20;
	private static final Logger log = LoggerFactory.getLogger(ContextualizingSemanticAnalysis.class);
	private SinglePassSemanticAnalysis singlePassAnalyser = new SinglePassSemanticAnalysis();
	private double angle;

    
    public ConceptualVector computeConceptualVector(AnalysisTree tree, ConceptualVector context) {
	
	if(angle<=0)
	    angle = 0.1;
	ConceptualVector previous = new ConceptualVector(context.getDimension(), context.getCodeLength());
	ConceptualVector current = singlePassAnalyser.computeConceptualVector(tree, context);
	int step = 0;
	while(previous.getAngularDistance(current) > angle && step < MAX_ITER) {
	    
	    previous = current;
	    current = singlePassAnalyser.computeConceptualVector(tree, previous);
	    step++;
	}
	
	if (log.isDebugEnabled()) log.debug("Computed Conceptual Vector in " + step + " contextualization iteration(s).");
	
	return current;
    }

	@Override
	public void setParameter(String param, String value)
			throws UnsupportedParameterException {
		if (param.equals(STRONG_CONTEXTUALIZATION_DELTA))
			this.angle = Double.parseDouble(value);
		else
			throw new UnsupportedParameterException("Unsupported parameter: " + param);
		
	}
}
