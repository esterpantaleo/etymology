package org.getalp.blexisma.servlet;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;

/**
 * @author Alexandre Labadié
 * */
public class FinalJobPredicate implements UnaryPredicate
{
	/**
	 * Auto generated serial version UID
	 */
	private static final long serialVersionUID = -1348588392216599587L;

	/**
	 * @param o : object to be matched with a 
	 * @return true if o is a Job for Sygfran, false in other cases
	 * */
	public boolean execute(Object o) 
	{
		boolean pred = false;
		SemanticAnalysisJob sp = null;
		
		 if (o instanceof SemanticAnalysisJob)
		 {
			 sp = (SemanticAnalysisJob)o;
			 
			 if (sp.getPhase()==SemanticJobPhase.DONE) pred = true;
		 }
		 
		 return pred;
	}
}
