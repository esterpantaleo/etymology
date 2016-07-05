package org.getalp.blexisma.servlet;

import org.cougaar.util.UnaryPredicate;

/**
 * @author Alexandre Labadié
 * */
public class WebRequestPredicate implements UnaryPredicate
{
	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -2105835348542373804L;

	/**
	 * @param o : object to be matched with a 
	 * @return true if o is a WebRequest
	 * */
	public boolean execute(Object o) 
	{ 
		 return (o instanceof WebRequest);
	}
}
