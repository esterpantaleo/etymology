/**
 * *
 * ConceptualVector.java
 * Created on 12 mars 2010 14:30:05
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * This class implements a conceptual vector.
 * 
 * Based on a previous version of Didier Schwab and Lim Lian Tze
 * 
 * @author Schwab Didier, Lim Lian Tze, Labadié Alexandre
 * @version 1.0
 */
public class ConceptualVector implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6223611710011190425L;

    private int codeLength;

    private int[] V;

    /**
     * Default constructor with default dimension of 10 and a codeLength of 100.
     * 
     */
    public ConceptualVector() {

        this(10, 100);
    }

    /**
     * Construct a vector with dimension components and the specified code length all components are set to 0.
     * 
     * @param dimension
     *            The dimension of the vector to be created.
     * @param codeLength
     *            The length of the code used for the vector to be created.
     */
    public ConceptualVector(int dimension, int codeLength) {

        V = new int[dimension];
        this.codeLength = codeLength;
    }

    /**
     * Creates conceptual vector from a ready-to-use array
     * 
     * @param array
     *            A ready-to-use array
     * @param codeLength
     *            The length of the code used for the vector to be created
     */

    public ConceptualVector(int[] array, int codeLength) {

        this(array.length, codeLength);
        System.arraycopy(array, 0, this.V, 0, this.V.length);
    }

    /**
     * Creates conceptual vector from a serialized int[] object
     * 
     * @param array
     * @param codeLength
     *            The length of the code used for the vector to be created.
     */
    public ConceptualVector(byte[] array, int codeLength) {
        this();
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(array));
            int[] myArray = (int[]) in.readObject();
            in.close();
            this.set(myArray, codeLength);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            this.codeLength = codeLength;
        }
    }

    /**
     * Creates conceptual vector from a string object. All components are in a
     * hexadecimal representation and separated with a space. Example : "AB 13
     * 1F" is the conceptual vector with three components for (171 19 31). If
     * the String is too short, all others components are set to 0. If it is too
     * long an IndexOutOfBoundsException exception is thrown.
     * 
     * @param S
     *            A String representing the hexadecimal representation of the
     *            conceptual vector to be created.
     * @param dimension
     *            The dimension of the conceptual vector to be created.
     * @param codeLength
     *            The length of the code used for the vector to be created.
     * 
     * @throws IndexOutOfBoundsException
     *             if there are more components in S than dimension
     */
    public ConceptualVector(String S, int dimension, int codeLength) {

        this(S, dimension, codeLength, 16);
    }

    /**
     * Creates conceptual vector from a string object. All components are in the
     * radix specified by the last argument and separated with a space. Example :
     * "AB 13 1F" is the conceptual vector with three components for (171 19
     * 31). If the String is too short, all others components are set to 0. If
     * it is too long an IndexOutOfBoundsException exception is thrown.
     * 
     * @param S
     *            A String representing the conceptual vector to be created.
     * @param dimension
     *            The dimension of the conceptual vector to be created.
     * @param codeLength
     *            The length of the code used for the vector to be created.
     * @param radix
     *            the radix to be used while parsing S.
     * 
     * @throws IndexOutOfBoundsException
     *             if there are more components in S than dimension
     */
    public ConceptualVector(String S, int dimension, int codeLength, int radix) {

        this(dimension, codeLength);

        int i = 0;

        int debut = 0;

        while (debut > -1) {

            int fin = S.indexOf(" ", debut);
            if (fin < 0)
                fin = S.length();
            V[i] = Integer.parseInt(S.substring(debut, fin), radix);
            i++;
            if (fin != S.length())
                debut = fin + 1;
            else
                debut = -1;
        }
    }

    /**
     * Creates conceptual vector from an existing cv.
     * 
     * @param cv
     */
    public ConceptualVector(ConceptualVector cv) {
        this(cv.V, cv.codeLength);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new ConceptualVector(this);
    }

    /**
     * Returns the dimension of this vector.
     * 
     * @return The dimension (number of components) in this vector.
     */
    public int getDimension() {
        return V.length;
    }

    public int getCodeLength() {
        return codeLength;
    }
    
    /**
     * @return Returns an array representation of the conceptual vector.
     */
    public int[] getVect() {
        return (int[]) V.clone();
    }

    /**
     * @param V
     *            The vector to set.
     */
    public void set(int[] V, int codeLength) {
        this.V = V;
        this.codeLength = codeLength;
    }

    /**
     * Returns the value of the i-th element.
     * 
     * @param i
     *            the element you want the value
     * @return the value of the i-th element.
     */
    public int getElementAt(int i) {
        return V[i];
    }

    /**
     * @return true if the conceptual vector is initied (no component with 0),
     *         false otherwise
     */
    public boolean isInit() {

        for (int i = 0; i < V.length; i++)
            if (V[i] != 0) {

                return true;
            }
        return false;
    }

    /** init vector. All componants are set to 0 */
    public void init() {

        Arrays.fill(V, 0);
    }

    /**
     * Sets the value of the i-th component.
     * 
     * @param i
     *            the index of the component to change.
     * @param v
     *            the new value of the i-th component.
     * @throws ArrayIndexOutOfBoundsException
     *             if i < 0 or i >= dimension
     */
    public void setElementAt(int i, int v) {
        V[i] = v;
    }

    /**
     * 
     * @return A serialisable representation of the array.
     */
    public byte[] serializeArray() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(os);
            out.writeObject(V);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] blob = os.toByteArray();
        return blob;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (!this.getClass().isInstance(obj))
            return false;
        ConceptualVector otherCV = (ConceptualVector) obj;
        if (this.V.length != otherCV.V.length
                || this.codeLength != otherCV.codeLength)
            return false;

        for (int i = 0; i < this.getDimension(); i++)
            if (this.V[i] != otherCV.V[i])
                return false;

        return true;
    }


    /**
     * Alphanumeric representation method of a conceptual vector.
     * 
     * @return A String which represent decimal value of each componant of the
     *         vector.
     */
    public String toString() {

        if (this == null)
            return "()";
        StringBuilder res = new StringBuilder(codeLength);// the length has to
                                                            // be large to avoid
                                                            // to extend it.
                                                            // CodeLength seems
                                                            // to be enough and
                                                            // not too much (DS)
        res.append("( ");
        for (int i = 0; i < V.length; i++) {

            res.append(V[i]);
            res.append(' ');
        }
        res.append(')');
        return res.toString();
    }
    
    /**
     * Alphanumeric representation method of a conceptual vector.
     * 
     * @return A String which represent decimal value of each componant of the
     *         vector.
     */
    public String toStringRFormat() {

        if (this == null)
            return "";
        StringBuilder res = new StringBuilder(codeLength);// the length has to
                                                            // be large to avoid
                                                            // to extend it.
                                                            // CodeLength seems
                                                            // to be enough and
                                                            // not too much (DS)
        for (int i = 0; i < V.length; i++) {

            res.append(V[i]);
            res.append('\t');
        }
        return res.toString();
    }

    /**
     * Alphanumeric representation method of a conceptual vector.
     * 
     * @return A String which represent hexadecimal value of each componant of
     *         the vector.
     */
    public String toStringHexa() {

        if (this == null)
            // modified by llt
            // return "()";
            return "";

        StringBuilder res = new StringBuilder(codeLength);// the length has to
                                                            // be large to avoid
                                                            // to extend it.
                                                            // CodeLength seems
                                                            // to be enough and
                                                            // not too much (DS)
        // commented out by llt
        // res.append("( ");
        int length = V.length - 1;
        for (int i = 0; i < length; i++) {

            res.append(Integer.toString(V[i], 16));
            res.append(' ');
        }
        res.append(Integer.toString(V[length], 16));// Changed by DS, like this
                                                    // we don't have any blank
                                                    // at the end.

        // commented out by llt
        // res.append(')');
        return res.toString();
    }

    /**
     * Build an Array of int which values correspond to the highest components
     * of the conceptual vector. Values are ordonned. The highest component is
     * at the index 0.
     * 
     * @param nbConcepts
     *            The length of the returned array.
     * @return An Array of int which values correspond to the highest components
     *         of the conceptual vector.
     */
    public int[] getMostDominantConcepts(int nbConcepts) {
        return getMostDominantConcepts(this.V, nbConcepts);
    }

    private static int[] getMostDominantConcepts(int[] v, int nbConcepts) {
        int dim = v.length;

        if (nbConcepts > v.length)
            nbConcepts = v.length;
        int[] Tresult = new int[nbConcepts];
        Arrays.fill(Tresult, -1);
        boolean changed;

        for (int i = 0; i < dim; i++) {
            changed = false;

            for (int j = 0; j < Tresult.length; j++) {

                if (Tresult[j] == -1) {
                    Tresult[j] = i;
                    changed = true;
                    break;
                }

                if (v[i] > v[Tresult[j]]) {
                    System.arraycopy(Tresult, j, Tresult, j + 1, Tresult.length
                            - j - 1);
                    Tresult[j] = i;
                    changed = true;
                    break;
                }
            }
            if (changed)
                continue;

        }
        return Tresult;
    }

    /**
     * Convenience method for writing to a file. Useful for gnuplot.
     * 
     * @param filename
     *            File to output to.
     */
    public void printAsColumn(String filename) {
        printAsColumn(this.V, filename);
    }

    /**
     * Convenience method for writing to a file. Useful for gnuplot.
     * 
     * @param v
     * @param filename
     *            File to output to.
     */
    private static void printAsColumn(int[] v, String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(filename));
            for (int i = 0; i < v.length; i++)
                writer.println(v[i]);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // /////////////////////////OPERATIONS ON A CONCEPTUAL
    // VECTOR///////////////////////////////

    /**
     * Calculates the magnitude of this ConceptualVector.
     * 
     * @return Magnitude of this ConceptualVector.
     */
    public double getMagnitude() {

        long result = 0;

        for (int i = 0; i < this.V.length; i++)
            result += Math.pow(this.V[i], 2);

        return Math.sqrt(result);
    }

    /**
     * Normalises this ConceptualVector to a unit vector. After operation,
     * magnitude is equal to codeLength
     * 
     */

    public void normalise() {

        double magnitude = this.getMagnitude();

        if (magnitude != 0) {

            for (int i = 0; i < this.V.length; i++) {

                this.V[i] = Math.max(1, (int) Math.round((this.V[i] / magnitude)
                        * this.codeLength));
            }
        }
        // System.out.println( this.getMagnitude() );
    }

    /**
     * Compute a conceptual vector which components are raised to the power of
     * the seconde argument (be careful with these operation NaN is easy to get)
     * 
     * @param n
     *            the exponent.
     * @return a conceptual vector which components are raised to the power of
     *         the seconde argument
     */

    public ConceptualVector pow(double n) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        for (int i = 0; i < this.V.length; i++)
            res.V[i] = (int) Math.pow(this.V[i], n);
        return res;
    }

    /**
     * Compute a conceptual vector which components are raised to the power of
     * the seconde argument (be careful with these operation NaN is easy to
     * get). This conceptual vector is normalised.
     * 
     * @param n
     *            the exponent.
     * @return a normalised conceptual vector which components are raised to the
     *         power of the seconde argument
     */
    public ConceptualVector normalizedPow(double n) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        long sum = 0;
        for (int i = 0; i < this.V.length; i++) {

            res.V[i] = (int) Math.pow(this.V[i], n);
            sum += Math.pow(res.V[i], 2);
        }

        double magnitude = Math.sqrt(sum);
        for (int i = 0; i < this.V.length; i++) {

            res.V[i] = (int) ((res.V[i] / magnitude) * res.codeLength);
        }
        return res;
    }

    /**
     * Compute a conceptual vector result of the scalar of the product of this
     * and a double.
     * 
     * @param s
     *            the scalar.
     * 
     * @return A normalised conceptual vector result of the scalar of the
     *         product of this and s.
     */

    public ConceptualVector scalar(double s) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        for (int i = 0; i < this.V.length; i++)
            res.V[i] = (int) (this.V[i] * s);
        return res;
    }

    /**
     * @deprecated
     * Compute a conceptual vector result of the scalar of the product of this
     * and a double.
     * 
     * @param s
     *            the scalar.
     * 
     * @return A normalised conceptual vector result of the scalar of the
     *         product of this and s.
     */

    
    public ConceptualVector scalar(int s) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        for (int i = 0; i < this.V.length; i++)
            res.V[i] = this.V[i] * s;
        return res;
    }

    // ///STATISTICS////

    /**
     * Compute the sum of each component of the conceptual vector.
     * 
     * @return The sum of each component of the conceptual vector.
     */
    public long sum() {

	long S = 0;
        for (int i = 0; i < V.length; i++)
            S += V[i];
        return S;
    }

    /**
     * Compute the mean of the values of the conceptual vector.
     * 
     * @return The mean of the values of the conceptual vector.
     */
    public double mean() {

        return this.sum() / (double) V.length;
    }

    /**
     * Compute the variance of the values of the conceptual vector.
     * 
     * @return the variance of the values of the conceptual vector.
     */
    public double variance() {

        double mean = this.mean();
        double sum = 0;
        for (int i = 0; i < V.length; i++) {

            sum += Math.pow(V[i] - mean, 2);
        }

        return sum / V.length;
    }

    /**
     * Compute the standard deviation of the values of the conceptual vector.
     * 
     * @return The standard deviation of the values of the conceptual vector.
     */
    public double standardDeviation() {

        return Math.sqrt(variance());
    }

    /**
     * Compute the coefficient of variation of the values of the conceptual
     * vector conceptuel.
     * 
     * @return the coefficient of variation of the values of the conceptual
     *         vector conceptuel.
     */
    public double coeffVar() {

        return standardDeviation() / mean();
    }

    // /////////////////////////OPERATIONS ON SEVERAL CONCEPTUAL
    // VECTORS///////////////////////////////

    /**
     * Compute the normalised sum of this conceptual vector and another.
     * 
     * @param CV
     *            The conceptualVector to add.
     * @return The normalised sum of this and CV.
     */

    public ConceptualVector normalisedSum(ConceptualVector CV) {

        ConceptualVector res = new ConceptualVector(V.length, codeLength);
        int sumValues = 0;
        for (int i = 0; i < V.length; i++) {

            res.V[i] = this.V[i] + CV.V[i];
            sumValues += Math.pow(res.V[i], 2);
        }
        double magnitude = Math.sqrt(sumValues);

        for (int i = 0; i < this.V.length; i++) {

            res.V[i] = (int) ((res.V[i] / magnitude) * this.codeLength);
        }

        return res;
    }

    /**
     * Compute the sum of this conceptual vector and another.
     * 
     * @param CV
     *            The conceptualVector to add.
     * @return The sum of this and CV.
     */
    // TODO: change sum to plus, and substract to minus
    public ConceptualVector sum(ConceptualVector CV) {

        ConceptualVector res = new ConceptualVector(V.length, codeLength);
        for (int i = 0; i < V.length; i++)
            res.V[i] = this.V[i] + CV.V[i];
        return res;
    }
 
    /**
     * modify this so that this is equal to this plus cv.
     * 
     * @param CV
     *            The conceptualVector to add to this.
     */

    public void add(ConceptualVector CV) {
        for (int i = 0; i < V.length; i++)
            this.V[i] = this.V[i] + CV.V[i];
    }

    /**
     * Compute the substract of this conceptual vector and another.
     * 
     * @param CV
     *            The conceptualVector to substract.
     * @return The sum of this and CV.
     */

    public ConceptualVector substract(ConceptualVector CV) {

        ConceptualVector res = new ConceptualVector(V.length, codeLength);
        for (int i = 0; i < V.length; i++)
            res.V[i] = this.V[i] - CV.V[i];
        return res;
    }

    /**
     * Compute the normalised term to term product of this conceptual vector and
     * another. Normalisation is done by sqrt.
     * 
     * @param CV
     *            The conceptualVector to multiply.
     * @return The normalised term to term product of this and CV.
     */

    public ConceptualVector normalisedTtTProduct(ConceptualVector CV) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        for (int i = 0; i < this.V.length; i++)
            res.V[i] = (int) Math.sqrt((double)this.V[i] * CV.V[i]);
        return res;
    }

    /**
     * @deprecated
     * Compute the term to term product of this conceptual vector and another.
     * 
     * @param CV
     *            The conceptualVector to multiply.
     * @return The term to term product of this and CV.
     */

    public ConceptualVector tttProduct(ConceptualVector CV) {

        ConceptualVector res = new ConceptualVector(this.V.length,
                this.codeLength);
        for (int i = 0; i < this.V.length; i++)
            res.V[i] = (int)((double)this.V[i] * CV.V[i]);
        return res;
    }

    /**
     * Compute the scalar product (or dot product) of this and another
     * Conceptual Vector CV.
     * 
     * @param CV
     *            another Conceptual Vector.
     * 
     * @return the scalar product of this and another Conceptual Vector CV.
     */
    public double scalarProduct(ConceptualVector CV) {

        double sp = 0;
        for (int i = 0; i < this.V.length; i++)
            sp += (double)this.V[i] * CV.V[i];
        return sp;
    }

    /**
     * Compute the weak contextualisation of this and another Conceptual Vector
     * CV.
     * 
     * @param CV
     *            another Conceptual Vector.
     * 
     * @return the weak contextualisation of this and another Conceptual Vector
     *         CV.
     */

    public ConceptualVector weakContextualisation(ConceptualVector CV) {

        ConceptualVector res = this.normalisedTtTProduct(CV);
        res = this.normalisedSum(res);
        return res;
    }

    /**
     * Gets the similarity between another ConceptualVector and this one.
     * 
     * @param CV
     *            ConceptualVector to compare with this ConceptualVector.
     * @return Similarity between this ConceptualVector and cv.
     */
    public double getCosineSimilarity(ConceptualVector CV) {

        double magnitudeThis = this.getMagnitude();
        double magnitudeCV = CV.getMagnitude();
        double m = magnitudeThis * magnitudeCV;

        if (m == 0)
            return 0d;
        else {
        	double s = this.scalarProduct(CV) / m;
            return (s > 1.0) ? 1.0 : s; 
            // This is done because the cosine similarity 
            // may be 1.000000000001 due to approximation in double float calculation.
        }
    }

    /**
     * Gets the regular similarity between another ConceptualVector and this
     * one. To use when magnitude of the two vectors are the same.
     * 
     * @param CV
     *            ConceptualVector to compare with this ConceptualVector.
     * @return Similarity between this ConceptualVector and cv.
     */
    public double getRegularCosineSimilarity(ConceptualVector CV) {

        if (codeLength == 0)
            return 0d;
        else {

            return this.scalarProduct(CV) / Math.pow((double)codeLength, 2);
        }
    }

    /**
     * Gets the angular distance between another ConceptualVector and this one.
     * 
     * @param CV
     *            ConceptualVector to compare with this ConceptualVector.
     * @return Angle between this ConceptualVector and cv in radians.
     */
    public double getAngularDistance(ConceptualVector CV) {
        return Math.acos(this.getCosineSimilarity(CV));
    }

    /**
     * Gets the angular distance between another ConceptualVector and this one.
     * To use when magnitude of the two vectors are the same.
     * 
     * @param CV
     *            ConceptualVector to compare with this ConceptualVector.
     * @return Angle between this ConceptualVector and cv in radians.
     */

    public double getRegularAngularDistance(ConceptualVector CV) {
        return Math.acos(this.getRegularCosineSimilarity(CV));
    }
    
    /**
     * Gets the topic distance between another ConceptualVector and this one
     * 
     * @param CV
     * 			  ConceptualVector to compare with this ConceptualVector
     * @param ratio
     * 			  Proportion of distance between rank in the topic distance (default 0.5)
     * @return Topic distance between this ConceptualVector and CV range from 0 to pi/2
     * */
    public double getTopicDistance(ConceptualVector CV, double ratio) {
    	double valAngle = this.getAngularDistance(CV);
    	double rnkAngle = Math.acos(cosine(this.getRankVector(),CV.getRankVector()));
    	double r = ratio;
    	
    	if (r<0 || r>1) r=0.5;
    	
    	return Math.exp((1-r)*Math.log(valAngle)+r*Math.log(rnkAngle));
    }
    
    /**
     * Gets the topic similarity between another ConceptualVector and this one
     * 
     * @param CV
     * 			  ConceptualVector to compare with this ConceptualVector
     * @param ratio
     * 			  Proportion of distance between rank in the topic distance (default 0.5)
     * @return Topic distance between this ConceptualVector and CV range from 0 to pi/2
     * */
    public double getTopicSimilarity(ConceptualVector CV, double ratio) {
    	double valCos = this.getCosineSimilarity(CV);
    	double rnkCos = cosine(this.getRankVector(),CV.getRankVector());
    	double r = ratio;
    	
    	if (r<0 || r>1) r=0.5;
    	
    	return Math.exp((1-r)*Math.log(valCos)+r*Math.log(rnkCos));
    }
    
    /**
     * Get the rank vector for this ConceptualVector
     * 
     * @return The rank vector 
     * */
    public int[] getRankVector() {
    	int[] rankvector = new int[V.length];
		
		for (int i=0;i<rankvector.length;i++)
		{
			rankvector[i]=getRankPos(i);
		}
		
		return rankvector;
    }
    
    /**
     * Get the rank of one position within the ConceptualVector
     * 
     * @param pos
     * 
     * @return The rank of the designed position
     * */
    private int getRankPos(int pos) {
    	int rank = 1;
		int val = V[pos];
		
		for (int i=0;i<V.length;i++)
		{
			if (V[i]>val) rank++;
		}
		
		return rank;
    }
    
    /**
     * Tool function returning the cosine between two int vector
     * 
     * @param v1, v2
     * 
     * @return Cosine between v1 and v2
     * */
    private static double cosine(int[] v1, int[] v2)
	{
		double cos = 0;
		double tmpA = 0;
		double tmpB = 0;
		double sommeProd = 0;
		double sommeCarreA =0;
		double sommeCarreB =0;
		
		for (int i=0;i<v1.length;i++)
		{
			tmpA=v1[i];
			tmpB=v2[i];
			sommeProd = sommeProd + tmpA * tmpB;
			sommeCarreA = sommeCarreA + tmpA * tmpA;
			sommeCarreB = sommeCarreB + tmpB * tmpB;
		}
		
		/* calcul du cosinus */
		if ((sommeCarreA == 0)||(sommeCarreB == 0))
		{
			cos = new Double(0);
		}
		else
		{
			cos = sommeProd / (Math.sqrt(sommeCarreA) * Math.sqrt(sommeCarreB));
		}
		
		return cos;
	}
    
	
//    /**
//     * Generates a random vector.
//     * 
//     * @param dim
//     *            The dimension (number of elements)
//     * @param codeLength
//     *            The encoding size
//     * @param coeffVar
//     *            The target variation coefficient
//     * @param boundary +-
//     *            range allowed for the variation coefficient
//     * @return a randomized ConceptualVector.
//     */
//    public static ConceptualVector randomisedCV(int dim, int codeLength,
//            double coeffVar, int boundary) {
//        int[] randomArr = new int[dim];
//
//        // int seed = -1;
//        // while ( seed < 500 )
//        // seed = (int) (Math.random() * codeLength);
//        Arrays.fill(randomArr, codeLength);
//
//        // for ( int i = 0; i < dim; i++ ) {
//        // do {
//        // randomArr[i] = (int) (Math.random() * codeLength);
//        // } while ( randomArr[i] <= 1 );
//        // }
//
//        ConceptualVector randomCV = new ConceptualVector(randomArr, codeLength);
//        randomCV.normalise();
//        // System.out.println(randomCV.toString().substring(0,50));
//        double c = randomCV.coeffVar();
//
//        int loopCount = 0;
//        // while( (c < coeffVar - boundary || c > coeffVar + boundary) &&
//        // loopCount++ < 100 ) {
//        // randomly increase or decrease the values by 10
//        for (int i = 0; i < dim; i++) {
//            randomCV.V[i] += (Math.random() * 2000 - 1000);
//        }
//        while (c < coeffVar && loopCount++ < 100) {
//
//            // System.out.println(randomCV.toString().substring(0,50));
//
//            randomCV = randomCV.pow(1.2);
//            // for ( int i = 0; i < dim; i++ ) {
//            // if ( randomCV.V[i] <= 1 )
//            // randomCV.V[i] = Math.max(1, (int)( Math.random() * 10 ) ) ;
//            // }
//            randomCV.normalise();
//            c = randomCV.coeffVar();
//            // System.out.println( "Coeff is now " + c + " after " + loopCount +
//            // " loops");
//        }
//
//        return randomCV;
//    }

	
}
