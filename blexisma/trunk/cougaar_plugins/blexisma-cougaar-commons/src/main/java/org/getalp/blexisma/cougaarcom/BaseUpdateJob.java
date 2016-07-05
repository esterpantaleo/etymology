package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;
import java.util.HashMap;

import org.getalp.blexisma.api.ConceptualVector;

/**
 * @author Alexandre Labadié
 * */
public class BaseUpdateJob implements Serializable
{

	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 3248858509223535871L;
	private final String lg;
	private final HashMap<String,ConceptualVector> cvs;
	
	/**
	 * @param lg : language of the word to be learn
	 * @param word : word to be learn
	 * @param cv : conceptual vector of the word
	 * */
	public BaseUpdateJob(String lg,HashMap<String,ConceptualVector> cvs)
	{
		this.lg = lg;
		this.cvs = cvs;
	}
	
	/**
	 * @return the serial version UID
	 * */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @return the language of the word
	 * */
	public String getLg() {
		return lg;
	}
	
	/**
	 * @return the conceptual vector of the word
	 * */
	public HashMap<String,ConceptualVector> getCvs() {
		return cvs;
	}
	
	
}
