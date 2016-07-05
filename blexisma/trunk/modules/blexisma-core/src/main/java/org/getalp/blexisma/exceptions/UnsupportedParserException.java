package org.getalp.blexisma.exceptions;

/**
 * @author : Alexandre Labadié
 * */
public class UnsupportedParserException extends Exception 
{

	/**
	 * Auto-generated serail version UID
	 */
	private static final long serialVersionUID = -5444801926802836478L;
	
	/**
	 * Default constructor
	 * */
	public UnsupportedParserException(){
		
	}
	
	/**
	 * @param msg : the exception message
	 * */
	public UnsupportedParserException(String msg){
		super(msg);
	}

}
