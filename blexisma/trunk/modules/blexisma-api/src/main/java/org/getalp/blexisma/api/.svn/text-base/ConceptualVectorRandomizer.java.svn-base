package org.getalp.blexisma.api;

import java.io.Serializable;
import java.util.Random;

public abstract class ConceptualVectorRandomizer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1810463366706212120L;
	
	public class UninitializedRandomizerException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4740952436702222447L;
		
	}
	
	protected int dim = -1;
	protected int codeLength = -1;
	protected Random rand;
	
	protected ConceptualVectorRandomizer() {
		rand = new Random();
	}
	
	protected ConceptualVectorRandomizer(int dim, int codeLength) {
		this();
		this.dim = dim;
		this.codeLength = codeLength;
	}
	
	protected ConceptualVectorRandomizer(long seed) {
		this();
		this.setSeed(seed);
	}
	
	protected ConceptualVectorRandomizer(int dim, int codeLength, long seed) {
		this(seed);
		this.dim = dim;
		this.codeLength = codeLength;
	}
	
	
	/**
	 * @return the dimension of generated vectors.
	 */
	public int getDimension() {
		return dim;
	}

	/**
	 * @param dim the dimension of the next generated vectors.
	 */
	public void setDimension(int dim) {
		this.dim = dim;
	}

	/**
	 * @return the codeLength of the generated vectors.
	 */
	public int getCodeLength() {
		return codeLength;
	}

	/**
	 * @param codeLength the codeLength of the next generated vectors
	 */
	public void setCodeLength(int codeLength) {
		this.codeLength = codeLength;
	}

	public void setSeed(long seed) {
		rand = new Random(seed);
	}
	
	/**
	 * returns a conceptual vector with the randomizer's dimension and codelength.
	 * 
	 * If dimension or codeLength has never been assigned, implemented classes should throw an
	 * {@link UninitializedRandomizerException}.
	 * @return
	 */
	public abstract ConceptualVector nextVector() throws UninitializedRandomizerException;

	/**
	 * Convenience method used by implementing classes to get additional options.
	 * Implementing classes should override this method to allow the use to set non standard options
	 * of type double.
	 * 
	 * If the name of the option is unknown, the implementing class should quietly ignore the request.
	 * 
	 * @param name the name of the option to set.
	 * @param value the value for the option.
	 */
	public void setOption(String name, double value) {
		// Ignore options of type double
	}
	
	public void setOption(String name, int value) {
		// Ignore options of type double
	}
	
	public void setOption(String name, String value) {
		// Ignore options of type double
	}
	
	public void setOption(String name, long value) {
		// Ignore options of type double
	}
	
	public void setOption(String name, float value) {
		// Ignore options of type double
	}

	public void setOption(String name, boolean value) {
		// Ignore options of type double
	}

}
