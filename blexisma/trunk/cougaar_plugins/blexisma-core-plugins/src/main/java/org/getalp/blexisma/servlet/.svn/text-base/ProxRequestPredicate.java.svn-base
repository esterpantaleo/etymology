package org.getalp.blexisma.servlet;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.ProxRequest;

/**
 * @author Alexandre Labadié
 * */
public class ProxRequestPredicate implements UnaryPredicate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4349603205794296657L;

	@Override
	public boolean execute(Object o) {
		ProxRequest p = null;
		if (o instanceof ProxRequest) {
			p = (ProxRequest)o;
			return p.isEnd();
		}
		return false;
	}

}
