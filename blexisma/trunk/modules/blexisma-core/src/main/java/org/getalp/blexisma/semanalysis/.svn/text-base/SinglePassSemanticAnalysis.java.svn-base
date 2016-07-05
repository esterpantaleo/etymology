/**
 * *
 * SemanticTree.java
 * Created on 15 avr. 2011 08:21:32
 * 
 * Copyright (c) 2011 Didier Schwab
 */
package org.getalp.blexisma.semanalysis;

import java.util.ArrayList;
import java.util.List;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.UnsupportedParameterException;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Didier SCHWAB
 *
 */
public class SinglePassSemanticAnalysis implements SemanticAnalysisMethod {

	private static final Logger log = LoggerFactory.getLogger(SinglePassSemanticAnalysis.class);
	
	public ConceptualVector computeConceptualVector(AnalysisTree tree, ConceptualVector context) {

		ConceptualVector rep = new ConceptualVector(context.getDimension(), context.getCodeLength());
		ConceptualVector cvBuffer;

		// TODO: where is the bonus to the governor of the syntactic structure ?
		if(tree.isLeaf()) {
			ArrayList<MorphoProperties> m = tree.getInfos().getMorphoProperties();
			SemanticDefinition def = tree.getInfos().getDef();
			if (null == def) {
				System.err.println("NULL definition in node:" + tree.toString());
			}
			cvBuffer = getStrongContextualisation(m, def.getSenseList(), context);	    
			rep = rep.sum(cvBuffer);
		} else {
			for(AnalysisTree child : tree.getChildren()) {

				ConceptualVector Vchild = computeConceptualVector(child, context);
				cvBuffer = Vchild.normalisedSum(Vchild.weakContextualisation(context));//V'(Ni)=V(Ni)+WeakContext(V(Ni),V(N)) with N mother of Ni
				rep = rep.sum(cvBuffer);
			}
		}
		
		// System.out.println(rep);
		rep.normalise();
		
		return rep;
	}

    private static ConceptualVector getStrongContextualisation(List<MorphoProperties> morpho, List<Sense> senses, ConceptualVector context){

	ConceptualVector CVbuffer = new ConceptualVector(context.getDimension(), context.getCodeLength());
	double angularWeight;
	double morphoWeight;
	String trace = "empty";
	
	for(Sense S : senses) {
		
		//DEBUT RAJOUT ALEX
		if (S.getVector()!=null){
		//FIN RAJOUT ALEX	
		    angularWeight = Math.PI/2-S.getVector().getAngularDistance(context);
		    morphoWeight = computeMorphoWeight(morpho, S.getMorpho());
	
		    if(angularWeight == 0) {
		    	CVbuffer = CVbuffer.sum(S.getVector().scalar(morphoWeight));
		    	if (log.isDebugEnabled()) trace = trace + "\n+ ((m) " + morphoWeight + " * " + "vect(" +S.toSimpleString() + "))";
		    }	else {
		    	CVbuffer = CVbuffer.sum(S.getVector().scalar(angularWeight*morphoWeight));
		    	if (log.isDebugEnabled()) trace = trace + "\n+ ((ma) " + (morphoWeight  * angularWeight) + " * " + "vect(" +S.toSimpleString() + "))";
		    }
		}
	}
	CVbuffer.normalise();
	if (log.isDebugEnabled()) {
		trace = "normalise(\n" + trace + "\n)";
		log.debug("Strong contextualization for " + morpho.toString() + "\n" + trace  );
	}
	return CVbuffer;
    }
    
    private static double computeMorphoWeight(List<MorphoProperties> morpho1, List<MorphoProperties> morpho2){
	
	
	// System.out.println(morpho1);
	// System.out.println(morpho2);
	double value = 0.1;
	//on cherche les communs
	for(int i = 0; i < morpho1.size(); i++)
	    for(int j = 0; j < morpho2.size(); j++)
		if(morpho1.get(i)==morpho2.get(j)){

		    value += getMorphoWeight(morpho1.get(i));
		}
	return Math.atan(value);
    } 


    private static double getMorphoWeight(MorphoProperties morpho){

	if(morpho==MorphoProperties.NOUN||morpho==MorphoProperties.VERB||morpho==MorphoProperties.ADJECTIVE||morpho==MorphoProperties.ADVERB)
	    return 2d;
	else
	    if(morpho==MorphoProperties.TRANSITIVE||morpho==MorphoProperties.INTRANSITIVE||morpho==MorphoProperties.DIRECTTRANSITIVE || morpho==MorphoProperties.MASCULINE||morpho==MorphoProperties.FEMININE||morpho==MorphoProperties.SINGULAR||morpho==MorphoProperties.PLURAL)
		return 1d;
	    else
		return 0.5d;
    }


	@Override
	public void setParameter(String param, String value)
			throws UnsupportedParameterException {
		throw new UnsupportedParameterException("Unsupported parameter: " + param);
		
	}
}
