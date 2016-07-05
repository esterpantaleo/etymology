package org.getalp.blexisma.cli;

import java.io.IOException;

import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticDictionary;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.XMLDataFormatter;
import org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;

public class GetDefinitionsVectors {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage: java ... " + GetDefinitionsVectors.class.getName() +  " vectors_file network_file element_id ...");
			System.exit(-1);
		}
		
		String_RAM_VectorialBase vb = String_RAM_VectorialBase.load(args[0]);
		SemanticNetwork<String, String> network = TextOnlySemnetReader.loadNetwork(args[1]);

		SemanticDictionary sd = new WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration(vb, network);
		
		for (int i = 2; i < args.length; i++) {
			String lg = args[i].substring(1,3);
			String txt = args[i].substring(5);
			
			SemanticDefinition def = sd.getDefinition(txt, lg);
			
			System.out.println(XMLDataFormatter.xmlFormat(def));
		}
		
	}

}
