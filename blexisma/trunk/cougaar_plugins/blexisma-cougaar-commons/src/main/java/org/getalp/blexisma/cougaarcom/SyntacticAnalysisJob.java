package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;

import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

public class SyntacticAnalysisJob implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5254337135018171352L;

	private final String lang;
	private final UID uid;
	
	private ParserType parserType;
	private String text;
	private AnalysisTree tree;

	public SyntacticAnalysisJob(UID uid, String lang, String text)
	{
		this.lang = lang;
		this.text = text;
		this.uid = uid;
		this.tree = null;
	}


	/**
	 * @return the parserType type requested for the analysis
	 */
	public ParserType getParserType() {
		return this.parserType;
	}


	/**
	 * @param parserType the parserType type requested for analysis
	 */
	public void setParserType(ParserType parser) {
		this.parserType = parser;
	}


	/**
	 * @return the text to be analyzed
	 */
	public String getText() {
		return this.text;
	}


	/**
	 * @param text the text to be analyzed
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * @return the analysis tree
	 */
	public AnalysisTree getTree() {
		return this.tree;
	}


	/**
	 * @param analysisTree the analysis tree to set
	 */
	public void setTree(AnalysisTree analysisTree) {
		this.tree = analysisTree;
	}


	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}


	public UID getUID() {
		return this.uid;
	}
	
}
