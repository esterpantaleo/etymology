package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;

import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.ConceptualVector;

/**
 * @author Alexandre Labadié
 * */
public class VectorRequest implements Serializable{
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	protected String lang = null;
	protected final UID uid;
	private String lemma;
	private ConceptualVector vector;
	
	public VectorRequest(UID uid) {
		this.uid = uid;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UID getUid() {
		return uid;
	}

	public ConceptualVector getVector() {
		return vector;
	}

	public void setVector(ConceptualVector vector) {
		this.vector = vector;
	}
}
