package org.getalp.blexisma.api;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Alexandre Labadié
 * */
public interface SemanticDictionary {
	
	/**
	 * Return the definitions of the given lemma according to the underlying lexical resources. Each word sense is
	 * annotated by a conceptual vector.
	 * 
	 * The following behavior should be implemented:
	 * <ul> 
	 * <li>If the conceptual vector of the lemma is unknown at the time this method is called, then a null pointer is 
	 * returned in the returned {@link SemanticDefinition}.</li>
	 * <li>If the conceptual vector of a word sense is unknown at the time this method is called, then a random conceptual 
	 * vector is computed and assigned to the word sense in the underlying vector base.</li>
	 * <li>If the lemma is unknown in the underlying lexical resource, the dictionary returns a {@link SemanticDefinition} with an
	 * empty set of morpho properties, a nil vector and an empty list of word senses.</li>
	 * <li>If the lemma has no definition in the underlying lexical resource, the dictionary returns a {@link SemanticDefinition}
	 * with a non null empty list of word senses</li>
	 * </ul>
	 * @param txt the lemma which is queried
	 * @param lg the language of the lemma
	 * @return a {@link SemanticDefinition} containing the vector of the lemma (null if unknown) and the vector of the 
	 * word senses (randomly generated and stored if unknown).
	 */
	public SemanticDefinition getDefinition(String txt, String lg);
	
	/* non javadoc: method is never used...
	 * Returns 
	 * @param txt
	 * @param lg
	 * @return
	 */
	// public ConceptualVector getVector(String txt, String lg);
	
	/**
	 * Assign the vector cv to ID txt. For language lg.
	 * 
	 *  Warning: the caller should pass the ID of the node, not the lemma.
	 * @param txt the ID of the node.
	 * @param lg the language of the node
	 * @param cv the vector to set.
	 */
	// TODO: all these methods should be removed.
	// public VectorialBase getBase();
	// public String getBaseDirectory();
	// public String getNextToLearn();
	// public Iterator<String> getInfiniteNodeIterator();
	public ArrayList<String> getProx(String lemme, String lang, int nb);
	public ArrayList<String> getProx(String lemme, String lang, String regex, int nb);
	public ArrayList<String> getProx(ConceptualVector cv, int nb);
}
