package org.getalp.blexisma.plugins.semdictionary;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;

/**
 * @author Alexandre Labadié
 * */
public class SemanticAnalysisJobDecoratePredicate implements UnaryPredicate
{
	
	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 6458841863536231749L;
	
	/**
	 * @param o : object to be tested by the predicate
	 * @return if the predicate is verified
	 * */
	public boolean execute(Object o)
	{
		boolean pred = false;
		SemanticAnalysisJob sp = null;
		
		if (o instanceof SemanticAnalysisJob)
		{
			sp = (SemanticAnalysisJob)o;
			pred = (sp.getPhase()==SemanticJobPhase.WAITINGFORDECORATION);
		}
		
		return pred;
	}
}
