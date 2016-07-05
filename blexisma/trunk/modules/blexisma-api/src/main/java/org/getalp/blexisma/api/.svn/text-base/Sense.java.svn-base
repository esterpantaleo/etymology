package org.getalp.blexisma.api;

import java.io.Serializable;
import java.util.List;

import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;

/**
 * @author Alexandre Labadié
 * */
public class Sense implements Serializable
{

	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 6809406719414360804L;
	private ConceptualVector vector;
	private final List<MorphoProperties> morpho;
	private final String networkDef;
	private final String baseId;
	//TODO Meilleure gestion de la langue
	
	
	/**
	 * @param id : id of the sense
	 * @param vector : conceptual vector of the meaning packet
	 * @param morpho : list of properties for the packet
	 * */
	public Sense(String baseId, String networkDef, ConceptualVector vector, List<MorphoProperties> morpho){
		this.baseId = baseId;
		this.networkDef = networkDef;
		this.vector = vector;
		this.morpho = morpho;
	}
	
	/**
	 * @return the serial version UID
	 * */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @return the conceptual vector of the meaning packet
	 * */
	public ConceptualVector getVector() {
		return vector;
	}
	
	/**
	 * @return morphology of the sense
	 * */
	public List<MorphoProperties> getMorpho() {
		return morpho;
	}

	public String getBaseId() {
		return baseId;
	}

	public void setVector(ConceptualVector vector) {
		this.vector = vector;
	}
	
	public String getNetworkDef() {
		return networkDef;
	}

	public String toString() {
		String ret = this.baseId + " "+ this.networkDef;
		if (this.vector!=null) ret = ret + this.vector.toStringHexa();
		return ret;
	}
	
	public String toSimpleString() {
		return this.baseId + " "+ this.networkDef;
	}
	
	public boolean equals(Object o){
		if (o.getClass()!=Sense.class) 
			return false;
		else {
			Sense s = (Sense)o;
			return this.baseId.equals(s.baseId)&&this.networkDef.equals(s.networkDef);
		}
	}
}
