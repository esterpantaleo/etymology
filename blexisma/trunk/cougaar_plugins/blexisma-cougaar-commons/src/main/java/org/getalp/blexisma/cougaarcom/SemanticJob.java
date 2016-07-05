package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;

import org.cougaar.core.util.UID;

/**
 * @author Alexandre Labadié
 * 
 * Class for syntax analysis communication
 * */
public abstract class SemanticJob implements Serializable
{

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	protected String lang = null;
	protected final UID uid;
	protected SemanticJobPhase phase;
	protected ParserType parser;
	protected int vectSize = -1;
	protected int vectNorm = -1;
	
	protected SemanticJob(UID uid) {
		this.uid = uid;
	}

	/**
	 * @return the language of the request / answer
	 * */
	public String getLang() {
		return lang;
	}
	
	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		if (null != this.lang) throw new RuntimeException("Should not override the language a Semantic Job.");
		this.lang = lang;
	}

	/**
	 * @return which parser was used to parse these data
	 * */
	public ParserType getParser() {
		return parser;
	}
	
	/**
	 * @param parser : the parser used
	 * */
	public void setParser(ParserType parser) {
		this.parser = parser;
	}
	
	/**
	 * @return the last analysis phase 
	 * */
	public SemanticJobPhase getPhase() {
		return phase;
	}
	
	/**
	 * @param phase : le last phase done
	 * */
	public void setPhase(SemanticJobPhase phase) {
		this.phase = phase;
	}
	
	public int getVectSize() {
		return vectSize;
	}

	public int getVectNorm() {
		return vectNorm;
	}

	/**
	 * @param vectSize the vectSize to set
	 */
	public void setVectSize(int vectSize) {
		if (this.vectSize != -1) throw new RuntimeException("Should not override the vector size of a Semantic Job.");
		this.vectSize = vectSize;
	}

	/**
	 * @param vectNorm the vectNorm to set
	 */
	public void setVectNorm(int vectNorm) {
		if (this.vectNorm != -1) throw new RuntimeException("Should not override the vector norm of a Semantic Job.");
		this.vectNorm = vectNorm;
	}

	
	/**
	 * @return the uid for synchronization
	 * */
	public UID getUid() {
		return uid;
	}

	public int hashCode() { return uid.hashCode(); }
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof SemanticJob)) return false;
		return uid.equals(((SemanticJob) o).uid);
	}

	  public String toString() { return "(job uid="+uid+" phase=" + phase +")"; }
}
