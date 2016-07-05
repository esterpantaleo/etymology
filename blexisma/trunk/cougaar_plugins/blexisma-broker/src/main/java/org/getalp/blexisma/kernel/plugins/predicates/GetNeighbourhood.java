/**
 * *
 * GetNeighbourhood.java
 * Created on 29 mars 2010 08:19:24
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.plugins.predicates;

import org.cougaar.util.UnaryPredicate;

/**
 * @author Didier SCHWAB
 *
 */
public class GetNeighbourhood implements UnaryPredicate {
    

    /**
     * 
     */
    private static final long serialVersionUID = -7478381306683201388L;

    /* (non-Javadoc)
     * @see org.cougaar.util.UnaryPredicate#execute(java.lang.Object)
     */
    public boolean execute(Object o) {
	// TODO Auto-generated method stub
	return o instanceof GetNeighbourhood;
    }
    
}
