package org.getalp.blexisma.jade;

import java.io.Serializable;

import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

public class SemanticAnalysisJob implements Serializable {
	private static final long serialVersionUID = -8586184796506043434L;
	public AnalysisTree tree;
	public int cvEncodingSize;
	public int cvDimension;

	public SemanticAnalysisJob(AnalysisTree tree, int dimension, int encodingSize) {
		this.cvEncodingSize = encodingSize;
		this.cvDimension = dimension;
		this.tree = tree;
	}
}
