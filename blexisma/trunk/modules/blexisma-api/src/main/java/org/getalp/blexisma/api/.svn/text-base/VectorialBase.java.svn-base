/**
 * *
 * VectorialBase.java
 * Created on 26 avr. 2010 11:36:13
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.api;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;

import org.getalp.blexisma.api.utils.WriteInFile;


/**
 * @author Didier SCHWAB
 *
 * TODO voir les interfaces map
 */
public abstract class VectorialBase<K> implements Serializable
{
    
	private static final long serialVersionUID = 1044429376128099671L;

	public class VBOS implements Serializable
	{

        /**
		 * Auto-generated serial version UID
		 */
		private static final long serialVersionUID = -6038017716862180289L;
		/**
         * Vectorial Base Object Storage : Object used to update Vectorial Base : (entry - conceptual vector) pairs
         * 
         */
        
        public K entry;
        public ConceptualVector CV;
        
        public VBOS(K entry, ConceptualVector CV) {
        
        this.entry = entry;
        this.CV = CV;
        }
    }

    public class EntryDist implements Serializable, Comparable<EntryDist> {

        

        /**
		 * 
		 */
		private static final long serialVersionUID = -1983908472138958637L;

		public K lexObj;

        public double distance;

        public EntryDist(K lexObj, double distance) {
            this.lexObj = lexObj;
            this.distance = distance;
        }

        public String toString() {
            return this.lexObj.toString() + " " + this.distance;
        }

        public int compareTo(EntryDist o) {
            return (int) Math.signum(this.distance - o.distance);
        }
        
    }
    
    protected int cvEncodingSize = -1;
    protected int cvDimension = -1;

    /**
     * 
     */
    public VectorialBase() {
    	// do nothing.
    }

    /**
     * Constructor with a specified cvEncodingSize and cvDimension
     * 
     * @param cvEncodingSize encoding size of the stored conceptual vectors
     * @param cvDimension number of dimensions of the stored conceptual vectors
     * 
     */
    public VectorialBase(int cvEncodingSize,int cvDimension) {
    	this.cvDimension = cvDimension;
    	this.cvEncodingSize = cvEncodingSize;
    }

    /**
     * @return Returns the dimension of the CVs used.
     */
    public final int getCVDimension() {
	return cvDimension;
    }

    /**
     * @return Returns the encoding size of the CVs used.
     */
    public final int getCVEncodingSize() {
	return cvEncodingSize;
    }

    /**
     * This function permits to store or update the conceptual vectors of an entry in the vectorial base.
     * 
     * @param entry the entry to store
     * @param cv the conceptual vector of the entry
     * @return true if storage is done, false otherwise
     */
    public abstract boolean addVector(K entry, ConceptualVector cv);

    /**
     * This function permits to store or update several entry-conceptual vectors.
     * 
     * @param vbos an ArrayList which contains VBOS (ie. (entry - conceptual vector) pairs)
     * @return true if storage is done, false otherwise
     */
    public abstract boolean addVectors(ArrayList<VBOS> vbos);

    /**
     * This function permits to get the conceptual vector associated to an entry
     * 
     * @param entry the entry we want the conceptual vector
     * @return the conceptual vector associated to the entry
     */
    public abstract ConceptualVector getVector(K entry);

    /**
     * 
     * @param entries
     * @return
     */
    public abstract ArrayList<ConceptualVector> getVectors(ArrayList<K> entries);
    
    /**
     * 
     * @return ArrayList of all vectors in the base
     */
    public abstract Enumeration<VBOS> getEntries();

    /**
     * Method to get the closest items of a conceptual vector according to the
     * angular distance
     * 
     * @param V
     *            a conceptual vector of which the neighbors are wanted.
     * @param nbProx
     *            number of neighbors wanted.
     * @return a vector of ItemDist which contains the neighbors and the
     *         distance between them and V
     */
    public abstract ArrayList<EntryDist> getProx(ConceptualVector V, int nbProx) ;

    /**
     * Method to get the closest items of a conceptual vector according to the
     * angular distance, filtering only item which matches a specified regular
     * expression.
     * 
     * @param V
     *            a conceptual vector of which the neighbors are wanted.
     * @param nbProx
     *            number of neighbors wanted.
     * @param regex
     * 			  The regular expression against which each item is matched.
     * @return a vector of ItemDist which contains the neighbors and the
     *         distance between them and V
     */
    public abstract ArrayList<EntryDist> getProx(ConceptualVector V, int nbProx, String regex);
    
    /**
     * Method to get a simple ramdom sample of the base 
     * 
     * @param sampleSize 
     * 			size of the sample, should be below the size of the base
     * 			default 10% of the base size
     * @return a sample of the base
     * */
    public abstract VectorialBase<K> simpleSampleBase(int sampleSize);
    
    /**
     * Method that export the Vectorial base in a csv format
     * This format is compatible with R for statistical explorations
     * 
     * @param filename
     * 				The path/name of the export file
     * */
    public void exportToCSVFormat(String filename){
    	Enumeration<VBOS> list = getEntries();
    	
    	//Creating a new file
    	File expfile = new File(filename);
    	if (expfile.exists()) 
    		expfile.delete();
    	
    	expfile.setReadable(true);
    	expfile.setWritable(true);
    	
    	while (list.hasMoreElements()){
    		VBOS vectinfo = list.nextElement();
    		StringBuffer line = new StringBuffer();
    		line.append(vectinfo.entry.toString());
    		line.append(" ");
    		line.append(vectinfo.CV.toStringRFormat());
    		WriteInFile.appendText(expfile, line.toString());
    	}
    }
    
    /**
     * Method that export the Vectorial base in a sql format
     * 
     * @param filename
     * 				The path/name of the export file
     * @param tablename
     * 				Name of the table in the future database
     * */
    public void exportToSQLFormat(String filename,String tablename){
    	Enumeration<VBOS> list = getEntries();
    	
    	//Creating a new file
    	File expfile = new File(filename);
    	if (expfile.exists()) 
    		expfile.delete();
    	
    	expfile.setReadable(true);
    	expfile.setWritable(true);
    	
    	StringBuffer line = new StringBuffer();
    	StringBuffer into = new StringBuffer();
    	
    	line.append("CREATE  TABLE `videosense_virtual_matrix`.`");
    	into.append("INSERT IGNORE INTO `videosense_virtual_matrix`.`");
    	line.append(tablename);
    	into.append(tablename);
    	line.append("`(`id");
    	into.append("`(");
    	line.append(tablename);
    	line.append("` VARCHAR(255) NOT NULL,");
    	into.append("`id");
    	into.append(tablename);
    	into.append("`,");
    	for (int i=0; i<cvDimension;i++){
    		line.append("`c");
    		into.append("`c");
    		line.append(i+1);
    		into.append(i+1);
    		line.append("` INT NOT NULL,");
    		into.append("`,");
    	}
    	
    	into.deleteCharAt(into.length()-1);
    	into.append(") VALUES (");
    	
    	line.append("PRIMARY KEY (`id");
    	line.append(tablename);
    	line.append("`));");
    	
    	WriteInFile.appendText(expfile, line.toString());
    	
    	while (list.hasMoreElements()){
    		VBOS vectinfo = list.nextElement();
    		line = new StringBuffer();
    		line.append(into);
    		
    		line.append("'");
    		line.append(vectinfo.entry.toString().replace("'", "\\'"));
    		line.append("',");
    		for (int i=0; i<cvDimension;i++){
    			line.append(vectinfo.CV.getElementAt(i));
    			line.append(",");
    		}
    		
    		line.deleteCharAt(line.length()-1);
    		line.append(");");
    		WriteInFile.appendText(expfile, line.toString());
    	}
    }
    
    /**
     * Method that export a simple random sample of the Vectorial base 
     * in a sql format
     * 
     * @param filename
     * 				The path/name of the export file
     * @param sampleSize
     * 				Size of the sample
     * */
    public void exportCSVSimpleSample(String filename, int sampleSize){
    	this.simpleSampleBase(sampleSize).exportToCSVFormat(filename);
    }
    
    /**
     * Method that export a simple random sample of the Vectorial base 
     * in a csv R compatible format
     * 
     * @param filename
     * 				The path/name of the export file
     * @param tablename
     * 				Name of the table in the future database
     * @param sampleSize
     * 				Size of the sample
     * */
    public void exportSQLSimpleSample(String filename,String tablename, int sampleSize){
    	this.simpleSampleBase(sampleSize).exportToSQLFormat(filename, tablename);
    }

//    /**
//     * Generates a random ConceptualVector that is compatible with this
//     * dispensor's dimensions and encoding size.
//     * 
//     * @param coeffVar
//     *            The coefficient of variance you like.
//     * @return A randomly generated ConceptualVector.
//     */
//    public ConceptualVector nextRandomCV( double coeffVar ) {
//    	if (randomizer != null && randomizer instanceof DeviationBasedCVRandomizer) {
//    		DeviationBasedCVRandomizer rand = (DeviationBasedCVRandomizer) randomizer;
//    		return rand.nextVector(coeffVar, 1);
//    	} else 
//    		throw new ClassCastException("Current vector based randomizer is not a DeviationBasedRandomizer.");
//    }
//
//    /**
//     * Generates a random ConceptualVector that is compatible with this
//     * dispensor's dimensions and encoding size.
//     * 
//     * @return A randomly generated ConceptualVector.
//     */
//    public ConceptualVector nextRandomCV() {
//    	if (randomizer != null && randomizer instanceof DeviationBasedCVRandomizer) {
//    		DeviationBasedCVRandomizer rand = (DeviationBasedCVRandomizer) randomizer;
//    		return rand.nextVector(1.5, 1);
//    	} else 
//    	return this.randomizer.nextVector();
//    }

}
