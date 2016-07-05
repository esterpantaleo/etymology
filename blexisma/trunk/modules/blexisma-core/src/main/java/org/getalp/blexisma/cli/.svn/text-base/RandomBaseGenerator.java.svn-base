package org.getalp.blexisma.cli;

import java.io.IOException;
import java.util.ArrayList;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.ConceptualVectorRandomizerFactory;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.SimpleSemanticNetwork;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.getalp.blexisma.utils.BaseFilter;

public class RandomBaseGenerator {

	public static void main(String[] args) throws UninitializedRandomizerException, IOException {
		if (args.length != 6) {
			System.err.println("Usage: java ... " + RandomBaseGenerator.class.getName() +  " source_base_path network_path result_base_path");
			System.exit(-1);
		}
		
		String_RAM_VectorialBase source = String_RAM_VectorialBase.load(args[0]);
		String_RAM_VectorialBase target = new String_RAM_VectorialBase(source.getCVEncodingSize(), source.getCVDimension());
		BaseFilter filter = BaseFilter.createDefFilter();
		
		// TODO: allow the specification of the randomizer class on the command line ?
		ConceptualVectorRandomizer randomizer = ConceptualVectorRandomizerFactory.createRandomizer(source.getCVDimension(), source.getCVEncodingSize());
		
		SemanticNetwork<String, String> network = TextOnlySemnetReader.loadNetwork(args[1]);
		ConceptualVector vb = null;
		String_RAM_VectorialBase.VBOS tmpVbos = null;
		
		for (int i=0; i<source.size(); i++) {
			tmpVbos = source.elementAt(i);
			if (filter.matchFilter(tmpVbos.entry)) {
				System.out.println("Generating vector for: "+tmpVbos.entry);
				target.addVector(tmpVbos.entry, randomizer.nextVector());
			}
		}

		// TODO Compute all lemma vectors.
//		ArrayList<ExtendedWordnetDef> listDef = null;
//		for (int i=0; i<source.size(); i++) {
//			tmpVbos = source.elementAt(i);
//			if (!filter.matchFilter(tmpVbos.entry)) {
//				vb = new ConceptualVector(tmpVbos.CV.getDimension(),tmpVbos.CV.getCodeLength());
//				listDef = network.getDef(tmpVbos.entry);
//				System.out.println("Building vector for: "+tmpVbos.entry);
//				for (int j=0; j<listDef.size(); j++) {
//					System.out.println("Def: "+j);
//					vb = vb.normalisedSum(target.getVector(listDef.get(j).getDef()));
//				}
//				target.addVector(tmpVbos.entry, vb);
//			}
//		}
		
		target.save(args[5]);
	}
	
}
