package org.getalp.blexisma.semanalysis;

import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticLearningMethod;
import org.getalp.blexisma.api.Sense;

/**
 * @author Alexandre Labadié
 * */
public class SimpleSemanticLearning implements SemanticLearningMethod
{

	/**
	 * @param definition : semantic definition of the node to learn
	 * */
	public ConceptualVector learn(SemanticDefinition definition, int size, int norm) 
	{
		ArrayList<Sense> lst = definition.getSenseList();
		
		ConceptualVector cv = null;
		
		if (lst.size()>0) 
		{
			cv = new ConceptualVector(size,norm);
			for (Sense s : lst) cv.add(s.getVector());
			cv.normalise();
		}
				
		return cv;
	}
	
}
