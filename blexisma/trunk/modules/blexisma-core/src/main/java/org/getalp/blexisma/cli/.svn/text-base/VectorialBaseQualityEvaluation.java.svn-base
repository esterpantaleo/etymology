package org.getalp.blexisma.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.getalp.blexisma.utils.WriteInFile;

public class VectorialBaseQualityEvaluation {
	private static final int NBPROX = 10;
	private static final int NBELEM = 1000;
	private static final int NBPAIR = 1000;
//	private static final double RATIO = 0.75;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 6) {
			System.err.println("Usage: java ... " + VectorialBaseQualityEvaluation.class.getName() +  " base1_file base2_file network report_file_name language prox_lemma");
			System.exit(-1);
		}
		
		String_RAM_VectorialBase vb1 = String_RAM_VectorialBase.load(args[0]);
		String_RAM_VectorialBase vb2 = String_RAM_VectorialBase.load(args[1]);
		String proxlemma = args[5];
		try {
			SemanticNetwork<String,String> network = TextOnlySemnetReader.loadNetwork(args[2]);
			WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration net1 = new WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration(vb1,network);
			WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration net2 = new WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration(vb2,network);
			
			File result = new File(args[3]);
			StringBuffer rep = new StringBuffer();
			SemanticDefinition prox = null;
			ArrayList<String_RAM_VectorialBase.EntryDist> listProx = null;
			
			if (result.exists()) {
				result.delete();
				result = new File(args[3]);
			}
			
			rep.append("**********************************************************\n");
			rep.append("Satistiques générales\n");
			rep.append("**********************************************************\n");
			rep.append(args[0]+" :\n");
			rep.append("Nonmbre d'entrées : ");
			rep.append(vb1.size());
			rep.append("\n");
			rep.append("Code lenght : ");
			rep.append(vb1.getCVEncodingSize());
			rep.append("\n");
			rep.append("Nombre de dimensions : ");
			rep.append(vb1.getCVDimension());
			rep.append("\n");
			rep.append(args[1]+" :\n");
			rep.append("Nonmbre d'entrées : ");
			rep.append(vb2.size());
			rep.append("\n");
			rep.append("Code lenght : ");
			rep.append(vb2.getCVEncodingSize());
			rep.append("\n");
			rep.append("Nombre de dimensions : ");
			rep.append(vb2.getCVDimension());
			rep.append("\n");
			rep.append("**********************************************************\n");
			rep.append("Ecart moyen absolu sur ");
			rep.append(NBELEM);
			rep.append(" éléments\n");
			rep.append("**********************************************************\n");
			rep.append("Base "+args[0]+" définitions\n");
			rep.append("**********************************************************\n");
			for (int i=1; i<11; i++){
				rep.append("Tirage "+i+" : ");
				rep.append(vb1.getAbsoluteDistanceMeanStringForm(NBELEM));
				rep.append("\n");
			}
			rep.append("**********************************************************\n");
			rep.append("Base "+args[1]+" définitions\n");
			rep.append("**********************************************************\n");
			for (int i=1; i<11; i++){
				rep.append("Tirage "+i+" : ");
				rep.append(vb2.getAbsoluteDistanceMeanStringForm(NBELEM));
				rep.append("\n");
			}
	//		rep.append("**********************************************************\n");
	//		rep.append("Ecart moyen absolu thématique sur ");
	//		rep.append(NBELEM);
	//		rep.append(" éléments\n");
	//		rep.append("**********************************************************\n");
	//		rep.append("Base 1 : ");
	//		rep.append(vb1.getAbsoluteTopicDistanceMean(NBELEM,RATIO));
	//		rep.append("\n");
	//		rep.append("Base 2 : ");
	//		rep.append(vb2.getAbsoluteTopicDistanceMean(NBELEM,RATIO));
	//		rep.append("\n");
			rep.append("**********************************************************\n");
			rep.append("Ecart moyen entre paires sur ");
			rep.append(NBPAIR);
			rep.append(" paires\n");
			rep.append("**********************************************************\n");
			rep.append("**********************************************************\n");
			rep.append("Base "+args[0]+" définitions\n");
			rep.append("**********************************************************\n");
			for (int i=1; i<11; i++){
				rep.append("Tirage "+i+" : ");
				rep.append(vb1.getPairDistanceMean(NBPAIR));
				rep.append("\n");
			}
			rep.append("**********************************************************\n");
			rep.append("Base "+args[1]+" définitions\n");
			rep.append("**********************************************************\n");
			for (int i=1; i<11; i++){
				rep.append("Tirage "+i+" : ");
				rep.append(vb2.getPairDistanceMean(NBPAIR));
				rep.append("\n");
			}
	//		rep.append("**********************************************************\n");
	//		rep.append("Ecart moyen thématique entre paires sur ");
	//		rep.append(NBPAIR);
	//		rep.append(" paires\n");
	//		rep.append("**********************************************************\n");
	//		rep.append("Base 1 : ");
	//		rep.append(vb1.getPairTopicDistanceMean(NBELEM,RATIO));
	//		rep.append("\n");
	//		rep.append("Base 2 : ");
	//		rep.append(vb2.getPairTopicDistanceMean(NBELEM,RATIO));
	//		rep.append("\n");
			prox = net1.getDefinition(proxlemma, args[4]);
			prox.computeMainVector();
			listProx = vb1.getProx(prox.getMainVector(), NBPROX);
			rep.append("**********************************************************\n");
			rep.append("Prox pour le lemme " + proxlemma +" : ");
			rep.append(" base "+args[0]+"\n");
			rep.append("**********************************************************\n");
			rep.append("\n");
			for (int i=0; i<listProx.size(); i++) {
				rep.append(listProx.get(i).lexObj);
				rep.append(" ");
				rep.append(listProx.get(i).distance);
				rep.append("\n");
			}
			prox = net2.getDefinition(proxlemma, args[4]);
			prox.computeMainVector();
			listProx = vb2.getProx(prox.getMainVector(), NBPROX);
			rep.append("**********************************************************\n");
			rep.append("Prox pour le lemme war : ");
			rep.append(" base "+args[1]+"\n");
			rep.append("**********************************************************\n");
			rep.append("\n");
			for (int i=0; i<listProx.size(); i++) {
				rep.append(listProx.get(i).lexObj);
				rep.append(" ");
				rep.append(listProx.get(i).distance);
				rep.append("\n");
			}
	//		prox = vb1.getRandomEntry();
	//		listProx = vb1.getTopicProx(prox.CV, NBPROX,RATIO);
	//		rep.append("**********************************************************\n");
	//		rep.append("Topic prox for the lemma: ");
	//		rep.append(prox.entry);
	//		rep.append(" from base 1");
	//		rep.append("\n");
	//		for (int i=0; i<listProx.size(); i++) {
	//			rep.append(listProx.get(i).lexObj);
	//			rep.append(" ");
	//			rep.append(listProx.get(i).distance);
	//			rep.append("\n");
	//		}
	//		prox = vb2.getRandomEntry();
	//		listProx = vb2.getTopicProx(prox.CV, NBPROX,RATIO);
	//		rep.append("**********************************************************\n");
	//		rep.append("Topic prox for the lemma: ");
	//		rep.append(prox.entry);
	//		rep.append(" from base 2");
	//		rep.append("\n");
	//		for (int i=0; i<listProx.size(); i++) {
	//			rep.append(listProx.get(i).lexObj);
	//			rep.append(" ");
	//			rep.append(listProx.get(i).distance);
	//			rep.append("\n");
	//		}
			
			WriteInFile.writeText(result, rep.toString());
			System.out.println("END");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
