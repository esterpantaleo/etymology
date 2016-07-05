package org.getalp.blexisma.plugins.semdictionary;

import java.util.ArrayList;
import java.util.HashMap;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.AnaTreeInfos;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnaTreeInfos;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;

/**
 * @author Alexandre Labadié
 * */
public class XWNDisambiguationDataHandlerPlugin extends XWNDataHandlerPlugin {
	
	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#initializeNextLearningJob(org.getalp.blexisma.cougaarcom.SemanticLearningJob)
	 */
	@Override
	protected void initializeNextLearningJob(SemanticLearningJob job) {
		
		String node, lg, txt;
		do {
			node = sni.next();
			lg = XWNSemanticDictionnary.extractLemmaLg(node);
		} while (!validLanguage(lg));

		// lastId = XWNSemanticDictionnary.extractLemmaText(node);

		if (log.isDebugEnabled()) {
			log.debug("Infinite next id [" + lg + "] :" + node );
		}

		lg = ISO639_3.sharedInstance.getIdCode(lg);
		txt = XWNSemanticDictionnary.extractLemmaText(node);
		job.setId(node);
		job.setLang(lg);
		job.setSemDefinition(dictionary.getDisambiguationList(txt, lg));
		
		job.setTrees(buildDesambTrees(job.getSemDefinition(),lg));
		job.setPhase(SemanticJobPhase.WAITINGFORSEMANTIC);
		
		job.setVectNorm(vb.getCVEncodingSize());
		job.setVectSize(vb.getCVDimension());
	}

	private HashMap<String,AnalysisTree> buildDesambTrees(SemanticDefinition sem, String l) {
		HashMap<String,AnalysisTree> trees = new HashMap<String,AnalysisTree>();
		ArrayList<Sense> senses = sem.getSenseList();
		
		for (Sense s : senses){
			trees.put(s.getNetworkDef(), buildTreeFromDesamb(s.getNetworkDef(),l));
		}
		
		return trees;
	}
	
	private AnalysisTree buildTreeFromDesamb(String desamb, String l){
		AnalysisTree tree = new BasicAnalysisTree(null);
		String[] tab = desamb.split(";");
		
		for (String s : tab){
			AnaTreeInfos info = new BasicAnaTreeInfos();
			BasicAnalysisTree leaf = new BasicAnalysisTree((BasicAnalysisTree)tree);
			String lemma = s.split(":")[0];
			int pos = Integer.parseInt(s.split(":")[1]);
			SemanticDefinition def = dictionary.getDefinition(s.split(":")[0], l);
			ArrayList<Sense> monolist =  new ArrayList<Sense>();
			SemanticDefinition monosense = null;
			ArrayList<Sense> list = def.getSenseList();
			
			if (list.size()>0){
				if (list.size()<pos) {
					if (list.get(0).getVector()!=null)
						monolist.add(list.get(0));
					else {
						Sense localsense = list.get(0);
						try {
							localsense.setVector(((XWNSemanticDictionnary)dictionary).getRandomVector());
						} catch (UninitializedRandomizerException e) {
							e.printStackTrace();
						}
						monolist.add(localsense);
					}
					
				}else{
					if (list.get(pos-1).getVector()!=null)
						monolist.add(list.get(pos-1));
					else {
						Sense localsense = list.get(pos-1);
						try {
							localsense.setVector(((XWNSemanticDictionnary)dictionary).getRandomVector());
						} catch (UninitializedRandomizerException e) {
							e.printStackTrace();
						}
						monolist.add(localsense);
					}
				}
				monosense = new SemanticDefinition(lemma,new ConceptualVector(this.vb.getCVDimension(),
						this.vb.getCVEncodingSize()),monolist);
				monosense.computeMainVector();
				info.setDef(monosense);
			}
			info.setLem(lemma);
			info.setWord(lemma);
			leaf.setInfos(info);
			tree.addChild(leaf);
		}
		
		return tree;
	}

}
