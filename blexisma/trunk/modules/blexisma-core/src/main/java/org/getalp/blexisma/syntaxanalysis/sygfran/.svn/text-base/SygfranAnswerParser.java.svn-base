package org.getalp.blexisma.syntaxanalysis.sygfran;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnaTreeInfos;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @author Alexandre Labadié
 * */
public class SygfranAnswerParser {

	public static BasicAnalysisTree sygfranToTree(String sp) throws SygfranParsingException
	{
		Document doc = null;
		BasicAnalysisTree result = null;
		
		try {
			doc = new SAXBuilder().build(new ByteArrayInputStream(sp.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new SygfranParsingException("Sygfran parsing exception", e);
		} 
		
		result = buildFromElem(doc.getRootElement(),null);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static BasicAnalysisTree buildFromElem(Element elem, BasicAnalysisTree parent)
	{
		BasicAnalysisTree node = new BasicAnalysisTree(parent);
		BasicAnaTreeInfos infos = new BasicAnaTreeInfos();
		ArrayList<AnalysisTree> children = null;
		Iterator<Element> iter = null;
		Element next = null;
		
		if (elem.getAttributeValue("forme")!=null) infos.setWord(elem.getAttributeValue("forme"));
		if (elem.getAttributeValue("lemme")!=null) infos.setLem(elem.getAttributeValue("lemme"));
		if (elem.getAttributeValue("type_nom")!=null) infos.addMorphoProperties(MorphoProperties.NOUN);
		if (elem.getAttributeValue("type_verbe")!=null) infos.addMorphoProperties(MorphoProperties.VERB);
		if (elem.getAttributeValue("type_adjoint")!=null) infos.addMorphoProperties(MorphoProperties.ADJECTIVE);
		if (elem.getAttributeValue("type_representant")!=null) infos.addMorphoProperties(MorphoProperties.ADVERB);
		if (elem.getAttributeValue("type_ponctuation")!=null) infos.addMorphoProperties(MorphoProperties.OTHER);
		if (elem.getAttributeValue("type_conjonction")!=null) infos.addMorphoProperties(MorphoProperties.OTHER);
		if (elem.getAttributeValue("type_déterminant")!=null) infos.addMorphoProperties(MorphoProperties.OTHER);
		
		if (elem.getAttributeValue("genre")!=null)
		{
			if (elem.getAttributeValue("genre").equals("MAS")) infos.addMorphoProperties(MorphoProperties.MASCULINE);
			if (elem.getAttributeValue("genre").equals("FEM")) infos.addMorphoProperties(MorphoProperties.FEMININE);
		}
		
		if (elem.getAttributeValue("fonction")!=null)infos.setFct(SygfranTagConverter.sygfranToFCT(elem.getAttributeValue("fonction")));
		
		if (elem.getChildren().size()>0)
		{
			children = new ArrayList<AnalysisTree>();
			iter = elem.getChildren().iterator();
			
			while (iter.hasNext()) 
			{
				next = (Element)iter.next();
				
				if (!(next.getAttributeValue("isLeaf").equals("true") && next.getAttribute("forme")==null))
					children.add(buildFromElem(next,node));
			}
		}
		
		node.setInfos(infos);
		node.setChildren(children);
		
		return node;
	}
}
