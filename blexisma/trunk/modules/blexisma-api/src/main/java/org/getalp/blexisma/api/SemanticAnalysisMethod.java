package org.getalp.blexisma.api;

import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;

public interface SemanticAnalysisMethod {

    /**
     * Computes and return a conceptual vector from a decorated syntactic analysis tree.
     * 
     * The analysis tree should be decorated by syntactic definitions according to the following principles:
     * <ol>
     *   <li>each node should be decorated by a non null {@link SemanticDefinition}</li>
     *   <li>each lemma node should be decorated by a {@link SemanticDefinition} which gathers its word senses.</li>
     * </ol>
     * 
     * @param tree An analysis tree decorated with {@link SemanticDefinition}
     * @param context A non-null {@link ConceptualVector} compatible with the decoration (same dimension and same codeLength)
     * @return the {@link ConceptualVector} of the {@link AnalysisTree}
     */
    public abstract ConceptualVector computeConceptualVector(AnalysisTree tree,
	    ConceptualVector context);
    
    /**
     * Set an internal parameter for the implementation of the analysis method.
     * 
     * If a parameter is passed that is not supported by the implementation, a new RuntimeException should be thrown.
     * 
     */
    public abstract void setParameter(String param, String value) throws UnsupportedParameterException;
    
    /**
     * @deprecated
     * Computes and return a conceptual vector from a decorated syntactic analysis tree.
     * 
     * The analysis tree should be decorated by syntactic definitions according to the following principles:
     * <ol>
     *   <li>each node should be decorated by a non null {@link SemanticDefinition}</li>
     *   <li>each lemma node should be decorated by a {@link SemanticDefinition} which gathers its word senses.</li>
     * </ol>
     * 
     * @param tree An analysis tree decorated with {@link SemanticDefinition}
     * @param context A non-null {@link ConceptualVector} compatible with the decoration (same dimension and same codeLength)
     * @param minimum distance between two steps
     * @return the {@link ConceptualVector} of the {@link AnalysisTree}
     */
    // TODO: this method should be internalized and the preceding one should lead to a up and down method when 
    // TODO: the semantic analysis method needs it.
//    public abstract ConceptualVector computeConceptualVector(AnalysisTree tree,
//	    ConceptualVector context, double dist);

}