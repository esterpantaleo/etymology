package org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree;

import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

/**
 * @author : Alexandre Labadié
 * 
 * Generic and simple tree class representing the syntax analysis
 * */
public class BasicAnalysisTree extends AnalysisTree
{

	/**
	 * Auto generated serial version UID
	 */
	private static final long serialVersionUID = 3603075593546835330L;
	
	public static BasicAnalysisTree errorTreeFactory()
	{
		BasicAnalysisTree error = new BasicAnalysisTree(null);
		
		return error;
	}
	
	public BasicAnalysisTree(BasicAnalysisTree parent)
	{
		super(parent);
	}
	
	public boolean isError()
	{
		return this.isLeaf()&&this.isRoot();
	}
}
