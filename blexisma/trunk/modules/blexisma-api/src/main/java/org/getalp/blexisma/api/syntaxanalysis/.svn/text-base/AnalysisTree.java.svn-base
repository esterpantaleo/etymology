package org.getalp.blexisma.api.syntaxanalysis;

import java.io.Serializable;
import java.util.ArrayList;

import org.getalp.blexisma.api.XMLDataFormatter;

/**
 * @author Alexandre Labadié
 * */
public abstract class AnalysisTree implements Serializable
{

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<AnalysisTree> children;
	private AnalysisTree parent;
	private AnaTreeInfos infos;
	
	/**
	 * @param parent : the parent node
	 * */
	public AnalysisTree(AnalysisTree parent)
	{
		this.parent = parent;
		this.children = new ArrayList<AnalysisTree>();
	}
	
	/**
	 * @return the serial version UID
	 * */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @return : the sons of the current node
	 * */
	public ArrayList<AnalysisTree> getChildren() {
		return children;
	}
	
	/**
	 * @return : the father of the node if relevant
	 * */
	public AnalysisTree getParent() {
		return parent;
	}
	
	/**
	 * @return : true if the node is the root node
	 * */
	public boolean isRoot() {
		return parent==null;
	}
	
	/**
	 * @return : true if the node is a leaf
	 * */
	public boolean isLeaf() {
		return children == null || children.size()==0;
	}
	
	/**
	 * @param child : child to add
	 * */
	public void addChild(AnalysisTree child)
	{
		this.children.add(child);
	}
	
	/**
	 * @param children : list of children of the node
	 * */
	public void setChildren(ArrayList<AnalysisTree> children) {
		this.children = children;
	}
	
	/**
	 * @param parent : the father of the node
	 * */
	public void setParent(AnalysisTree parent) {
		this.parent = parent;
	}
	
	
	/**
	 * @return : the informations about the node
	 * */
	public AnaTreeInfos getInfos() {
		return infos;
	}
	
	/**
	 * @param infos : information about the node
	 * */
	public void setInfos(AnaTreeInfos infos) {
		this.infos = infos;
	}
	
	/**
	 * toString override
	 * */
	public String toString() {
		return this.toFormattedString("");
	}
	
	public String toFormattedString(String prefix)
	{
		StringBuffer res = new StringBuffer();
		
		res.append(prefix + "(");
		
		res.append("ROOT: "+this.isRoot()+" ");
		res.append("LEAF: "+this.isLeaf()+" ");
		res.append("ERROR: "+this.isError()+" ");
		res.append("AnaFunc: "+this.infos.getFct()+" ");
		if (this.infos.getWord()!=null) res.append("Word: "+this.infos.getWord()+" ");
		if (this.infos.getLem()!=null) res.append("Lem: "+this.infos.getLem()+" ");
		if (this.infos.getMorphoProperties().size()>0) {
			res.append("Morpho: ");
			for (int i=0;i<this.infos.getMorphoProperties().size();i++) {
				res.append(this.infos.getMorphoProperties().get(i)+".");
			}
			res.append(" ");
		}
		if (this.infos.getDef() != null) {
			res.append(this.infos.getDef());
		}
		
		if (this.children != null) {
			res.append("\n");
			for (int i=0; i<this.children.size(); i++) {
				res.append(children.get(i).toFormattedString(prefix + "  "));
			}
			res.append(prefix);
		}		
		res.append(")\n");
		
		return res.toString();
	}
	
	public String toXmlString() {
		return XMLDataFormatter.xmlFormat(this);
	}
	
	public abstract boolean isError();
}
