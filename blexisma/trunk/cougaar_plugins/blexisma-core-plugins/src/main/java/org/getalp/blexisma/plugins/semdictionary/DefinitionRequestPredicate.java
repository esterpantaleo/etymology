package org.getalp.blexisma.plugins.semdictionary;

import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.DefinitionRequest;

public class DefinitionRequestPredicate implements UnaryPredicate{

	/**
	 * Auto generated serial version UID
	 */
	private static final long serialVersionUID = 3523185063305252321L;

	@Override
	public boolean execute(Object o) {
		boolean pred = false;
		
		if (o instanceof DefinitionRequest)
		{
			if (((DefinitionRequest) o).getDef() == null) pred = true;
		}
		
		return pred;
	}

}
