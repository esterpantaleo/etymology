package org.getalp.blexisma.impl.semdico;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

public class WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration
		extends WiktionaryBasedSemanticDictionary {

	public WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration(
			String_RAM_VectorialBase vb, SemanticNetwork<String, String> sn) {
		super(vb, sn);
		
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionary#getDefinition(java.lang.String, java.lang.String)
	 */
	@Override
	public SemanticDefinition getDefinition(String txt, String lg) {
		SemanticDefinition sdef = getDefinitionWithNullVectorsWhenUnknown(txt, lg);
		for(Sense sense : sdef.getSenseList()) {
			if (sense == null) continue;
			if (sense.getVector() == null) {
				// Affect an empty vector to the unknown word sense
				sense.setVector(new ConceptualVector(this.vectorialBase.getCVDimension(), this.vectorialBase.getCVEncodingSize()));
			}
		}
		return sdef;
	}

}
