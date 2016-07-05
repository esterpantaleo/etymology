package org.getalp.blexisma.external.sygfranwrapper.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.getalp.blexisma.external.sygfranwrapper.structure.tree.SygMorphoSyntacticTree;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class MorphoTreeToXML 
{
	public static void generateXML(SygMorphoSyntacticTree tree, File xml)
	{
		Element root = createNode(tree);
		Document doc = new Document(root);
		FileOutputStream fout = null;
		Format form = Format.getPrettyFormat();
		
		try 
		{  
			XMLOutputter outputter = new XMLOutputter(form); 
		
			fout = new FileOutputStream(xml); 
			outputter.output(doc, fout); 
			fout.close(); 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 
	}
	
	public static String outputXML(SygMorphoSyntacticTree tree)
	{
		Element root = createNode(tree);
		Document doc = new Document(root);
		Format form = Format.getRawFormat();
		XMLOutputter outputter = new XMLOutputter(form); 
		
		return outputter.outputString(doc);
	}
	
	private static Element createNode(SygMorphoSyntacticTree tree)
	{
		Element e = new Element("Node");
		ArrayList<SygMorphoSyntacticTree> children = tree.getChildren();
		
		e.setAttribute("isLeaf", new Boolean(tree.isLeaf()).toString());
		if (!tree.isLeaf()&&tree.getInfos().BAL!=null) e.setAttribute("Groupe",EquivChaine.equivIso(tree.getInfos().BAL));
		if (tree.getInfos().ASSERT!=null) e.setAttribute("assertion", EquivChaine.equivIso(tree.getInfos().ASSERT));
		if (tree.getInfos().CAT!=null) e.setAttribute("categorie", EquivChaine.equivIso(tree.getInfos().CAT));
		if (tree.getInfos().FLX!=null) e.setAttribute("flexion", EquivChaine.equivIso(tree.getInfos().FLX));
		if (tree.getInfos().FRM!=null) e.setAttribute("forme", EquivChaine.equivIso(tree.getInfos().FRM));
		if (tree.getInfos().FS!=null) e.setAttribute("fonction", EquivChaine.equivIso(tree.getInfos().FS));
		if (tree.getInfos().GNR!=null) e.setAttribute("genre", EquivChaine.equivIso(tree.getInfos().GNR));
		if (tree.getInfos().LEMME!=null) e.setAttribute("lemme", EquivChaine.equivIso(tree.getInfos().LEMME));
		if (tree.getInfos().NUM!=null) e.setAttribute("nombre", EquivChaine.equivIso(tree.getInfos().NUM));
		if (tree.getInfos().SOUSA!=null) e.setAttribute("type_adjoint", EquivChaine.equivIso(tree.getInfos().SOUSA));
		if (tree.getInfos().SOUSC!=null) e.setAttribute("type_conjonction", EquivChaine.equivIso(tree.getInfos().SOUSC));
		if (tree.getInfos().SOUSD!=null) e.setAttribute("type_determinant", EquivChaine.equivIso(tree.getInfos().SOUSD));
		if (tree.getInfos().SOUSN!=null) e.setAttribute("type_nom", EquivChaine.equivIso(tree.getInfos().SOUSN));
		if (tree.getInfos().SOUSP!=null) e.setAttribute("type_ponctuation", EquivChaine.equivIso(tree.getInfos().SOUSP));
		if (tree.getInfos().SOUSR!=null) e.setAttribute("type_representant", EquivChaine.equivIso(tree.getInfos().SOUSR));
		if (tree.getInfos().SOUSV!=null) e.setAttribute("type_verbe", EquivChaine.equivIso(tree.getInfos().SOUSV));
		
		if (children !=null) 
			for (int i=0; i<children.size();i++) e.addContent(createNode(children.get(i)));
		
		return e;
	}
}
