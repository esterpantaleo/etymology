package org.getalp.blexisma.servlet;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.VectorRequest;

public class VectorRequestPredicate implements UnaryPredicate{
	
	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 766223697655354752L;

	@Override
	public boolean execute(Object o) {
		boolean pred = false;
		
		if (o instanceof VectorRequest)
		{
			if (((VectorRequest) o).getVector() != null) pred = true;
		}
		
		return pred;
	}

}
