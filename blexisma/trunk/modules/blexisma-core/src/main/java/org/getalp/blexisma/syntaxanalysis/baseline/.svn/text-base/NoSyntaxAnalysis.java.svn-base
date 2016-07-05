package org.getalp.blexisma.syntaxanalysis.baseline;

import java.util.ArrayList;

import org.getalp.blexisma.api.syntaxanalysis.AnaFunctions;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnaTreeInfos;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.utils.Patterner;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;

public class NoSyntaxAnalysis {
	
	public static BasicAnalysisTree buildBasicEnglishTree(String s, EnglishLemmatizer lemmatizer) {
		return buildBasicEnglishTreefromSentence(propositionSegmentation(s),lemmatizer);
	}
	
	private static BasicAnalysisTree buildBasicEnglishTreefromSentence(ArrayList<String> sentences, EnglishLemmatizer lemmatizer) {
		if (sentences.size()<1)
			return BasicAnalysisTree.errorTreeFactory();
		
		BasicAnalysisTree resultTree = new BasicAnalysisTree(null);
		BasicAnalysisTree sentenceNode = null;
		BasicAnalysisTree wordLeaf = null;
		BasicAnaTreeInfos info = new BasicAnaTreeInfos();
		BasicAnaTreeInfos sentenceInfo = null;
		BasicAnaTreeInfos wordInfo = null;
		String[] currentSentence = null;
		
		info.setFct(AnaFunctions.OTH);
		resultTree.setInfos(info);
		
		for (int i=0; i<sentences.size(); i++){
			sentenceNode = new BasicAnalysisTree(resultTree);
			sentenceInfo = new BasicAnaTreeInfos();
			sentenceInfo.setFct(AnaFunctions.OTH);
			sentenceNode.setInfos(sentenceInfo);
			currentSentence = purgePunctuation(sentences.get(i));
			currentSentence = englishLemmatizeAndStopList(currentSentence, lemmatizer);
			for (int j=0; j<currentSentence.length;j++) {
				wordLeaf = new BasicAnalysisTree(sentenceNode);
				wordInfo = new BasicAnaTreeInfos();
				wordInfo.setFct(AnaFunctions.OTH);
				wordInfo.addMorphoProperties(MorphoProperties.NOUN);
				wordInfo.setLem(currentSentence[j]);
				wordInfo.setWord(currentSentence[j]);
				wordLeaf.setInfos(wordInfo);
				sentenceNode.addChild(wordLeaf);
			}
			if (currentSentence.length>0) 
				resultTree.addChild(sentenceNode);
		}
		if (resultTree.getChildren().size()>0)
			return resultTree;
		else 
			return BasicAnalysisTree.errorTreeFactory();
	}
	
	private static String[] purgePunctuation(String s) {
		return Patterner.patterner(s, "\\p{Punct}", "").split(" ");
	}
	
	private static String[] englishLemmatizeAndStopList(String[] list, EnglishLemmatizer lemmatizer){
		ArrayList<String> newList = new ArrayList<String>();
		String cw = null;
		
		for (int i=0; i<list.length; i++) {
			cw = lemmatizer.lemmatize(list[i]);
			if (stopList(cw,StopLists.getEnglishStopList()))
				newList.add(cw);
		}
		
		return (String[])newList.toArray();
	}
	
	private static boolean stopList(String w,String[] l) {
		for (int i=0;i<l.length;i++) {
			if (w.equals(l[i]))
				return false;
		}
		return true;
	}
	
	private static ArrayList<String> propositionSegmentation(String s) {
		ArrayList<String> props = new ArrayList<String>();
		String[] l = s.split("\\p{Punct}");
		
		for (int i=0; i<l.length; i++)
			props.add(l[i]);
		
		return props;
	}
}
