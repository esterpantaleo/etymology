package org.getalp.blexisma.cougaarcom;

/**
 * @author Alexandre Labadié
 * 
 * List of analysis phase for a syntax packet
 * NO : no analysis done
 * SYNTAX : syntax parsing done
 * SEMANTIC : semantic analysis done
 * */
public enum SemanticJobPhase 
{
	/**
	 * The job is waiting for a syntactic analyzer to parse its data.
	 */
	WAITINGFORSYNTAX, 
	
	/**
	 * @deprecated
	 */
	WAITINGFORTREEBUILDING, 
	/**
	 * The job has trees that need to be decorated by semantic definition so that the global vector can be computed.
	 */
	WAITINGFORDECORATION, 
	/**
	 * The job has been syntactically analyzed and te trees have been decorated. Waiting for the final semantic processing.
	 */
	WAITINGFORSEMANTIC, 
	/**
	 * The job has finished all processing.
	 */
	DONE,
	/**
	 * Semantic learning job need to be assigned a specific learning task
	 */
	ASKINGFORNETWORKDATA,
	/**
	 * Semantic job waiting for syntax rebuilding
	 */
	WAITINGFORSYNTAXREBUILD,
	/**
	 * @deprecated
	 */
	WAITINGFORPREPROC;
}
