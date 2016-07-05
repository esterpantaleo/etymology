package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;
import java.util.HashMap;

import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

/**
 * @author Alexandre Labadié
 * */
public class SemanticLearningJob extends SemanticJob implements Serializable
{

	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 937705722670555813L;
	private String id;
	private HashMap<String,String> data;
	private HashMap<String,AnalysisTree> trees;
	private SemanticDefinition semDefinition;
	private int requestingAgent;
	
	public SemanticLearningJob(UID uid, int componentid)
	{
		super(uid);
		this.requestingAgent = componentid;
		this.id = null;
		this.data = null;
		this.trees = null;
		this.semDefinition = null;
		phase = SemanticJobPhase.ASKINGFORNETWORKDATA;
	}
	
	/**
	 * @return the semantic definition of the node
	 * */
	public SemanticDefinition getSemDefinition() {
		return semDefinition;
	}
	
	/**
	 * @param semDefinition : the semantic definition of the node
	 * */
	public void setSemDefinition(SemanticDefinition semDefinition) {
		this.semDefinition = semDefinition;
	}
	
	/**
	 * @return the id of the node
	 * */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id : the id of the node
	 * */
	public void setId(String id) {
		this.id = id;
	}

	public HashMap<String,String> getData() {
		return data;
	}

	public void setData(HashMap<String,String> data) {
		this.data = data;
	}

	public HashMap<String,AnalysisTree> getTrees() {
		return trees;
	}

	public void setTrees(HashMap<String,AnalysisTree> trees) {
		this.trees = trees;
	}

	public int getRequestingAgent() {
		return requestingAgent;
	}

	public String toString() { return "(job uid="+uid+" lemma="+id+" phase=" + phase +")"; }

}
