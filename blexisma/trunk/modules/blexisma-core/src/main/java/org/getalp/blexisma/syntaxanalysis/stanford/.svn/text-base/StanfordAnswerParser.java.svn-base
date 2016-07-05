package org.getalp.blexisma.syntaxanalysis.stanford;

import java.util.ArrayList;

import org.getalp.blexisma.api.syntaxanalysis.AnaFunctions;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnaTreeInfos;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.utils.Patterner;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;


/**
 * @author Alexandre Labadié
 * */
public class StanfordAnswerParser {
	
	/**
	 * @param sp : analysis string to parse
	 * @return a BasicAnalysisTree representing the syntax analysis from stanford
	 * */
	public static BasicAnalysisTree buildStanfordTree(ArrayList<String> sentences, EnglishLemmatizer lemmatizer){
		if (sentences.size()<1)
			return BasicAnalysisTree.errorTreeFactory();
		
		BasicAnalysisTree node = null;
		BasicAnaTreeInfos info = null;
		
		node = new BasicAnalysisTree(null);
		info = new BasicAnaTreeInfos();
		info.setFct(AnaFunctions.OTH);
		node.setInfos(info);
		
		for (int i=0; i<sentences.size();i++) {
			node.addChild(stanfordToTree(sentences.get(i),node,lemmatizer));
		}

		
		
		return node;
	}
	
	/**
	 * @param sp : analysis string to parse
	 * @return a BasicAnalysisTree representing the syntax analysis from stanford
	 * */
	private static BasicAnalysisTree stanfordToTree(String sp, BasicAnalysisTree parent, EnglishLemmatizer lemmatizer){
		if (sp==null||sp.equals("")) return BasicAnalysisTree.errorTreeFactory();
		
		BasicAnalysisTree node = null;
		BasicAnaTreeInfos info = null;
		ArrayList<String> strChildren = null;
		boolean leaf = (sp.substring(1).indexOf(")")<sp.substring(1).indexOf("("))||(sp.substring(1).indexOf("(")<0);
		String current = sp;
		String lemma = null;
		String[] anaString = null;
		int pos = 0;
		
		if (leaf){
			node = new BasicAnalysisTree(parent);
			info = new  BasicAnaTreeInfos();
			info.setFct(AnaFunctions.OTH);
			current = Patterner.patterner(current, "\\(", "");
			current = Patterner.patterner(current, "\\)", "");
			current.trim();
			anaString = current.split(" ");
			pos = anaString.length-1;
			
			//First letter
			if (anaString[0].substring(0,1).equals("N")) {
				try {
					lemma = lemmatizer.lemmatize(anaString[pos]);
				} catch (Exception e) {
					lemma = anaString[pos];
				}
				if (isInStopList(anaString[0], lemma)) info.addMorphoProperties(MorphoProperties.OTHER);
				else info.addMorphoProperties(MorphoProperties.NOUN);
				info.setWord(anaString[pos]);
			}
			else if (anaString[0].substring(0,1).equals("V")) {
				try {
					lemma = lemmatizer.lemmatize(anaString[pos]);
				} catch (Exception e) {
					lemma = anaString[pos];
				}
				if (isInStopList(anaString[0], lemma)) info.addMorphoProperties(MorphoProperties.OTHER);
				else info.addMorphoProperties(MorphoProperties.VERB);
				info.setWord(anaString[pos]);
			}
			else if (anaString[0].substring(0,1).equals("R")) {
				info.addMorphoProperties(MorphoProperties.ADVERB);
				lemma = anaString[pos];
			}
			else if (anaString[0].substring(0,1).equals("J")) {
				try {
					lemma = lemmatizer.lemmatize(anaString[2]);
				} catch (Exception e) {
					lemma = anaString[pos];
				}
				if (isInStopList(anaString[0], lemma)) info.addMorphoProperties(MorphoProperties.OTHER);
				else info.addMorphoProperties(MorphoProperties.ADJECTIVE);
				info.setWord(anaString[pos]);
			}
			else {
				info.addMorphoProperties(MorphoProperties.OTHER);
				lemma = anaString[pos];
			}
			
			info.setWord(anaString[pos]);
			info.setLem(lemma);
				
			node.setInfos(info);
		}
		else{
			node = new BasicAnalysisTree(parent);
			info = new  BasicAnaTreeInfos();
			if (current.substring(1,2).equals("S")) info.setFct(AnaFunctions.GOV);
			else info.setFct(AnaFunctions.OTH);
			node.setInfos(info);
			strChildren = isolateChildren(current.substring(1));
			
			for (int i=0; i<strChildren.size(); i++){
				node.addChild(stanfordToTree(strChildren.get(i), node,lemmatizer));
			}
		}
		
		return node;
	}
	
	/**
	 * @param str : string to be divided in "children"
	 * @return array list of string children
	 * */
	private static ArrayList<String> isolateChildren(String str){
		ArrayList<String> res = new ArrayList<String>();
		int parCount = 0;
		int start = 0;
		int end = 0;
		
		for (int i=0; i<str.length(); i++){
			if (str.charAt(i) == '(') {
				if (parCount == 0) start = i;
				parCount++;
			}
			else if (str.charAt(i) == ')'){
				parCount--;
				if (parCount == 0) {
					end = i;
					res.add(str.substring(start, end+1));
				}
			}
		}
		
		return res;
	}
	
	private static boolean isInStopList(String t, String s){
		
		if (t.length() >= 3 && t.startsWith("V") ) {
			if (s.equals("have")) return true;
			if (s.equals("be")) return true;
		}
		
		return false;
	}
}
