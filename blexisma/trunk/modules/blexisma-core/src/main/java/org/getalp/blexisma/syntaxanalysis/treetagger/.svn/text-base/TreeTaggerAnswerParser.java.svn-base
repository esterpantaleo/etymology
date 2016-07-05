package org.getalp.blexisma.syntaxanalysis.treetagger;

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

public class TreeTaggerAnswerParser {
	// TODO: do not use an intermediary xml representation...
	public static BasicAnalysisTree treeTaggerToTree(String sp, String lang) throws TreeTaggerParsingException
	{
		Document doc = null;
		BasicAnalysisTree result = null;
		
		try {
			doc = new SAXBuilder().build(new ByteArrayInputStream(sp.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new TreeTaggerParsingException("Tree Tagger parsing exception", e);
		} 
		
		result = buildFromElem(doc.getRootElement(),null, lang);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static BasicAnalysisTree buildFromElem(Element elem, BasicAnalysisTree parent, String lang)
	{
		BasicAnalysisTree node = new BasicAnalysisTree(parent);
		BasicAnaTreeInfos infos = new BasicAnaTreeInfos();
		ArrayList<AnalysisTree> children = null;
		Iterator<Element> iter = null;
		Element next = null;
		
		if (elem.getAttributeValue("occ")!=null) infos.setWord(elem.getAttributeValue("occ"));
		if (elem.getAttributeValue("lemma")!=null) infos.setLem(elem.getAttributeValue("lemma"));
		if (elem.getAttributeValue("pos")!=null) addMorphoPropertiesToInfo(infos, elem.getAttributeValue("pos"), lang);
		
		if (elem.getChildren().size()>0) {
			children = new ArrayList<AnalysisTree>();
			iter = elem.getChildren().iterator();
			
			while (iter.hasNext()) {
				next = (Element)iter.next();
				children.add(buildFromElem(next,node, lang));
			}
		}
		
		node.setInfos(infos);
		node.setChildren(children);
		
		return node;
	}

	private static void addMorphoPropertiesToInfo(BasicAnaTreeInfos infos, String v, String lang) {
		// FRENCH
		if (v.startsWith("NOM")) 
			infos.addMorphoProperties(MorphoProperties.NOUN);
		else if (v.startsWith("NAM")) 
			infos.addMorphoProperties(MorphoProperties.NOUN);
		else if (v.startsWith("VER")) 
			infos.addMorphoProperties(MorphoProperties.VERB);
		else if (v.startsWith("ADJ")) 
			infos.addMorphoProperties(MorphoProperties.ADJECTIVE);
		else if (v.startsWith("ADV")) 
			infos.addMorphoProperties(MorphoProperties.ADVERB);
		// ENGLISH
		else if (v.startsWith("JJ"))
			infos.addMorphoProperties(MorphoProperties.ADJECTIVE);
		else if (v.startsWith("RB"))
			infos.addMorphoProperties(MorphoProperties.ADVERB);
		else if (v.startsWith("NN"))
			infos.addMorphoProperties(MorphoProperties.NOUN);
		else if (v.startsWith("NP"))
			infos.addMorphoProperties(MorphoProperties.NOUN);
		else if (v.startsWith("VB"))
			infos.addMorphoProperties(MorphoProperties.VERB);
		// GERMAN
//		else if (v.startsWith("NN"))
//			infos.addMorphoProperties(MorphoProperties.NOUN);
//		else if (v.startsWith("ADJ"))
//		infos.addMorphoProperties(MorphoProperties.ADJECTIVE);
//		else if (v.startsWith("ADV"))
//		infos.addMorphoProperties(MorphoProperties.ADVERB);
		else if (v.startsWith("NE"))
			infos.addMorphoProperties(MorphoProperties.NOUN);
		else if (v.startsWith("V"))
			infos.addMorphoProperties(MorphoProperties.VERB);
		else 
			infos.addMorphoProperties( MorphoProperties.OTHER);
	}

}
