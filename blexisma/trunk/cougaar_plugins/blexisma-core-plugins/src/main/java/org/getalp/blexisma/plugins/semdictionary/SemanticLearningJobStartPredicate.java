package org.getalp.blexisma.plugins.semdictionary;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;

/**
 * @author Alexandre Labadié
 * */
public class SemanticLearningJobStartPredicate implements UnaryPredicate
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3256279307406683864L;

	/**
	 * @param o : object to be tested by the predicate
	 * @return if the predicate is verified
	 * */
	public boolean execute(Object o)
	{
		boolean pred = false;
		SemanticLearningJob sp = null;
		
		if (o instanceof SemanticLearningJob)
		{
			sp = (SemanticLearningJob)o;
			if (sp.getPhase() == SemanticJobPhase.ASKINGFORNETWORKDATA) pred = true;
		}
		
		return pred;
	}
}
