package org.getalp.blexisma.syntaxanalysis.basicrepresentation;

import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticDictionary;
import org.getalp.blexisma.api.syntaxanalysis.AnaTreeInfos;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Alexandre Labadié
 * */
public class TreeDecorator 
{
	private SemanticDictionary vectorProvider;
    Logger logger;

	/**
	 * @param vectorProvider : this object should give vectors for words
	 * */
	public TreeDecorator(SemanticDictionary vectorProvider) {
		logger = LoggerFactory.getLogger(this.getClass());
		this.vectorProvider = vectorProvider;
	}
	
	public AnalysisTree simpleDecorate(AnalysisTree tree, String lg,int vectSize,int vectNorm) {
		try {
			AnalysisTree t = uncheckedSimpleDecorate(tree, lg, vectSize, vectNorm);
		    if (logger.isDebugEnabled()) logger.debug(t.toXmlString());
			return t;
		} catch (RuntimeException e) {
		    logger.debug("Caught Exception " + e + " while decorating tree:");
		    if (logger.isDebugEnabled()) logger.debug(tree.toXmlString());
			throw e;
		}
	}
	
	/**
	 * @param tree : tree to be decorated
	 * @return decorated tree
	 * */
	public AnalysisTree uncheckedSimpleDecorate(AnalysisTree tree, String lg,int vectSize,int vectNorm){
		AnalysisTree currentNode = tree;
		AnaTreeInfos currentInfo = currentNode.getInfos();
		ArrayList<AnalysisTree> currentChildren = currentNode.getChildren();
		ArrayList<AnalysisTree> newChildren = new ArrayList<AnalysisTree>();
		SemanticDefinition deflemma = null;
		SemanticDefinition deflowerlemma = null;
		SemanticDefinition defword = null;
		String word = null;
		String lemma = null;
		String lowerlemma = null;
		
		if (tree.isLeaf()) {
			try {
				if (currentInfo.getMorphoProperties().contains(MorphoProperties.ADJECTIVE)||
						currentInfo.getMorphoProperties().contains(MorphoProperties.NOUN)||
						currentInfo.getMorphoProperties().contains(MorphoProperties.VERB)) {
					word = currentInfo.getWord();
					lemma = currentInfo.getLem();
					if (lemma == null) lemma = word;
					lowerlemma = lemma.toLowerCase();
					defword = vectorProvider.getDefinition(word, lg);
					deflemma = vectorProvider.getDefinition(lemma, lg);
					deflowerlemma = vectorProvider.getDefinition(lowerlemma, lg);

					SemanticDefinition finaldef = null;

					if(defword.isEmpty()) {
						if (deflemma.isEmpty()) finaldef = deflowerlemma;
						else {
							finaldef = new SemanticDefinition(deflemma.getId(),new ConceptualVector(vectSize,vectNorm),null);
							finaldef.concatDef(deflemma);
							if (!lemma.equals(lowerlemma)) finaldef.concatDef(deflowerlemma);
						}
					}
					else {
						// TODO: Should not it be lemma, then word, then lower lemma ?
						finaldef = new SemanticDefinition(defword.getId(),new ConceptualVector(vectSize,vectNorm),null);
						finaldef.concatDef(defword);
						if (!word.equals(lemma)) finaldef.concatDef(deflemma);
						if (!lemma.equals(lowerlemma)) finaldef.concatDef(deflowerlemma);
					}

					currentInfo.setDef(finaldef);
				}
				else 
					currentInfo.setDef(new SemanticDefinition(null,new ConceptualVector(vectSize,vectNorm),null));
			} catch (RuntimeException e) {
				logger.debug("Raised Exception " + e + " while decorating tree.", e);
				logger.debug("Current node = {}" + tree);
				throw e;
			}
			}
		else {
			currentInfo.setDef(new SemanticDefinition(null,new ConceptualVector(vectSize,vectNorm),null));
			for (int i=0; i < currentChildren.size(); i++) {
				newChildren.add(uncheckedSimpleDecorate(currentChildren.get(i), lg,vectSize,vectNorm));
			}
			
			currentNode.setChildren(newChildren);
		}
		
		currentNode.setInfos(currentInfo);
		
		return currentNode;
	}
}
