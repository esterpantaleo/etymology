package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;

import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

/**
 * @author Alexandre Labadié
 * 
 * Class for syntax analysis communication
 * */
public class SemanticAnalysisJob extends SemanticJob implements Serializable
{

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private final long id;
	private String data;
	private AnalysisTree tree;
	
	
	/**
	 * @param id : id of the syntax request / answer
	 * @param lang : language of the syntax request / answer
	 * @param data : text to be analyzed or analysis results
	 * Constructor / initialization methods for a SyntaxPacket
	 * */
	public SemanticAnalysisJob(long id, String lang, String data, UID uid)
	{
		super(uid);
		this.id = id;
		this.data = data;
		this.phase = SemanticJobPhase.WAITINGFORSYNTAX;
		this.lang = lang;
	}
	
	/**
	 * @return if answer is false => text to be analyzed if answer is true => analysis results
	 * */
	public String getData() {
		return data;
	}
	
	/**
	 * @param data : text to be analyzed or analysis result depending the case
	 * */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * @return the id of the request / answer
	 * */
	public long getId() {
		return id;
	}
	
	public AnalysisTree getTree() {
		return tree;
	}

	public void setTree(AnalysisTree tree) {
		this.tree = tree;
	}

}
