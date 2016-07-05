package org.getalp.blexisma.api;

/**
 * @author Alexandre Labadié
 * */
public interface SemanticLearningMethod 
{
	/**
	 * Returns the vector for the lemma whose definition is passed as a parameter
	 * @param definition the SemanticDefinition, where all defs has been analysed
	 * @param size
	 * @param norm
	 * @return the vector of the lemma, null if the vector could not be computed.
	 */
	public ConceptualVector learn(SemanticDefinition definition, int size, int norm);
}
