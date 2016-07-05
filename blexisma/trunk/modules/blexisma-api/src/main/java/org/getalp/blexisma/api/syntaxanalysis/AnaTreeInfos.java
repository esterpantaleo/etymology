package org.getalp.blexisma.api.syntaxanalysis;

import java.io.Serializable;
import java.util.ArrayList;

import org.getalp.blexisma.api.SemanticDefinition;

/**
 * @author Alexandre Labadié
 * */
public abstract class AnaTreeInfos implements Serializable
{

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private String word;
	private String lem;
	private ArrayList<MorphoProperties> morphoProperties;
	private AnaFunctions fct;
	private SemanticDefinition def;
	
	
	public AnaTreeInfos()
	{
		word = null;
		lem = null;
		morphoProperties = new ArrayList<MorphoProperties>();
		fct = null;
		def = null;
	}
	
	/**
	 * @return : the lem of the term if relevant
	 * */
	public String getLem() {
		return lem;
	}
	
	/**
	 * @return : the part of speech if relevant
	 * */
	public ArrayList<MorphoProperties> getMorphoProperties() {
		return morphoProperties;
	}
	
	/**
	 * @return : the function in the basic representation
	 * */
	public AnaFunctions getFct() {
		return fct;
	}
	
	/**
	 * @return : the serial version UID
	 * */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @param lem : the lem of the current node
	 * */
	public void setLem(String lem) {
		this.lem = lem;
	}
	
	/**
	 * @param pos : the part od speech of the current node
	 * */
	public void setMorphoProperties(ArrayList<MorphoProperties> morphoProperties) {
		this.morphoProperties = morphoProperties;
	}
	
	/**
	 * @param fct : the function of the current node
	 * */
	public void setFct(AnaFunctions fct) {
		this.fct = fct;
	}
	
	/**
	 * @return the unmodified word
	 * */
	public String getWord() {
		return word;
	}
	
	/**
	 * @param word : the word
	 * */
	public void setWord(String word) {
		this.word = word;
	}
	
	/**
	 * @return the semantic definition of the node
	 * */
	public SemanticDefinition getDef() {
		return def;
	}
	
	/**
	 * @param def : the semantic definition of the node
	 * */
	public void setDef(SemanticDefinition def) {
		this.def = def;
	}
	
	/**
	 * @param m : property to add to the morphologic properties of the node
	 * */
	public void addMorphoProperties(MorphoProperties m){
		this.morphoProperties.add(m);
	}
}
