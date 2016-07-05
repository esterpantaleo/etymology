package org.getalp.blexisma.external.sygfranwrapper.structure.tree;

import java.util.ArrayList;

import org.getalp.blexisma.external.sygfranwrapper.structure.info.MorphoSyntacticInformations;

/**
 * @author Alexandre Labadié
 * 
 * Interface defining a Morpho-syntactic tree
 * */
public interface MorphoSyntacticTree 
{
	public boolean isLeaf();
	public ArrayList<MorphoSyntacticTree> getLeaves();
	public int getNumberOfChilds();
	public MorphoSyntacticTree getChild(int i);
	public void addChild(MorphoSyntacticTree tree);
	public void addChild(MorphoSyntacticTree tree, int i);
	public void removeChild(int i);
	public MorphoSyntacticInformations getInfos();
	public void setInfos(MorphoSyntacticInformations infos);
	public ArrayList<MorphoSyntacticTree> getChildren();
	public void setChildren(ArrayList<MorphoSyntacticTree> children);
	public String toString();
	public String completeToString();
}

