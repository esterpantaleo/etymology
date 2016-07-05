package org.getalp.blexisma.servlet;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.ProxVectorRequest;

public class ProxVectorRequestPredicate implements UnaryPredicate{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4594379917767108178L;

	@Override
	public boolean execute(Object o) {
		ProxVectorRequest p = null;
		if (o instanceof ProxVectorRequest) {
			p = (ProxVectorRequest)o;
			return p.isEnd();
		}
		return false;
	}

}
