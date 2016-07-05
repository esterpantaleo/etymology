package org.getalp.blexisma.plugins.semdictionary;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;

/**
 * @author Alexandre Labadié
 * */
public class SemanticLearningJobDecoratePredicate implements UnaryPredicate
{
	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = -2007712201290776015L;

	/**
	 * @param o : object to be tested by the predicate
	 * @return if the predicate is verified
	 * */
	public boolean execute(Object o)
	{
		boolean pred = false;
		
		if (o instanceof SemanticLearningJob)
		{
			if (((SemanticLearningJob) o).getPhase() == SemanticJobPhase.WAITINGFORDECORATION) pred = true;
		}
		
		return pred;
	}
}
