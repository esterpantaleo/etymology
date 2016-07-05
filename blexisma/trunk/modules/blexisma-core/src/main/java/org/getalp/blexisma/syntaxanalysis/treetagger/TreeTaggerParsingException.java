package org.getalp.blexisma.syntaxanalysis.treetagger;

public class TreeTaggerParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8731360851114352394L;

	public TreeTaggerParsingException(String msg)
	{
		super(msg);
	}
	
	public TreeTaggerParsingException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
