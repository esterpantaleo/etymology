package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;

import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.SemanticDefinition;

public class DefinitionRequest implements Serializable{
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private String lang = null;
	protected final UID uid;
	private String lemma;
	private SemanticDefinition def;
	
	public DefinitionRequest(UID uid){
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

	public SemanticDefinition getDef() {
		return def;
	}

	public void setDef(SemanticDefinition def) {
		this.def = def;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UID getUid() {
		return uid;
	}
	
	
}
