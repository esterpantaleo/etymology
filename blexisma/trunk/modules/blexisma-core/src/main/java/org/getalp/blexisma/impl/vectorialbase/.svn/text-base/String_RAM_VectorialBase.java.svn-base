/**
 * *
 * VectorialBase.java
 * Created on 26 avr. 2010 11:36:13
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.impl.vectorialbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.VectorialBase;
import org.getalp.blexisma.utils.BaseFilter;
import org.getalp.blexisma.utils.StrongHashTable;
import org.getalp.blexisma.utils.WriteInFile;

/**
 * @author Didier SCHWAB, Alexandre Labadié
 *
 */
public class String_RAM_VectorialBase extends org.getalp.blexisma.api.VectorialBase<String>
{

    /**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 3753398069401562356L;
	private StrongHashTable<String, VBOS> base;

    /**
     * 
     */
//    	public RAM_VectorialBase() {
//
//	base = new StrongHashTable<String, VBOS>();
//    }

    /**
     * @
     * @param cvEncodingSize
     * @param cvDimension
     */

    public String_RAM_VectorialBase(int cvEncodingSize,int cvDimension) {
    	
    	super(cvEncodingSize, cvDimension);
    	base = new StrongHashTable<String, VBOS>();
    }

    /**
     * 
     * @param size
     * @param cvEncodingSize
     * @param cvDimension
     * @see @see StrongHashTable(int)
     */
    public String_RAM_VectorialBase(int size, int cvEncodingSize,int cvDimension){
    	
    	super(cvEncodingSize, cvDimension);
    	base = new StrongHashTable<String, VBOS>(size);
    }

    /**
     * 
     * @param size
     * @param loadFactor
     * @param cvEncodingSize
     * @param cvDimension
     * @see StrongHashTable(int, float)
     */
    public String_RAM_VectorialBase(int size, float loadFactor, int cvEncodingSize,int cvDimension){

	super(cvEncodingSize, cvDimension);
	base = new StrongHashTable<String, VBOS>(size, loadFactor);
    }

    /**
     * This function permits to store or update the conceptual vectors of an entry in the vectorial base.
     * 
     * @param entry the entry to store
     * @param cv the conceptual vector of the entry
     * @return true if storage is done, false otherwise
     */
    public boolean addVector(String entry, ConceptualVector cv){

	base.put(entry, new VBOS(entry,cv));
	return true;
    }

    /**
     * This function permits to store or update several entry-conceptual vectors.
     * 
     * @param vbos an ArrayList which contains VBOS (ie. (entry - conceptual vector) pairs)
     * @return true if storage is done, false otherwise
     */
	public boolean addVectors(ArrayList<VBOS> vbos){

	VBOS buffer = null;

	for(int i = 0; i < vbos.size(); i++){

	    buffer = vbos.get(i);
	    base.put(buffer.entry, new VBOS(buffer.entry, buffer.CV));
	}

	return true;
    }
    
    /**
     * This function permits to increment the conceptual vectors of an entry in the vectorial base.
     * Used to import penang base (and fuse synset into VBOS)
     * 
     * @param entry the entry to store
     * @param cv the conceptual vector of the entry
     * @return true if storage is done, false otherwise
     */
    @SuppressWarnings("unchecked")
	public boolean incrementVector(String entry, ConceptualVector cv) {
    	
    	ConceptualVector tmpcv = null;
    	
    	if (!base.containsKey(entry)) 
    		addVector(entry,cv);
    	else {
    		tmpcv = ((VBOS)base.get(entry)).CV;
    		tmpcv = tmpcv.sum(cv);
    		addVector(entry, tmpcv);
    	}
    	
    	return true;
    }

    /**
     * This function permits to get the conceptual vector associated to an entry.
     * 
     * @param entry the entry we want the conceptual vector
     * @return the conceptual vector associated to the entry, null if the entry is not associated to any vector.
     */
    @SuppressWarnings("unchecked")
	public ConceptualVector getVector(String entry){
    	
		VBOS vbos = (VBOS)base.get(entry);
		if(vbos == null){
		    
		    return null;
		}
		else{
		    
		    return vbos.CV;
		}
    }

    /**
     * 
     * @param entries
     * @return
     */
    public ArrayList<ConceptualVector> getVectors(ArrayList<String> entries){

		ArrayList<ConceptualVector> ret = new ArrayList<ConceptualVector>(entries.size());
	
		for(int i = 0; i < entries.size(); i++){
			
		    ret.add(getVector(entries.get(i)));
		}
	
		return ret;

    }

    
    
    @Override
    public ArrayList<EntryDist> getProx(ConceptualVector V, int nbProx) {
    	
		ArrayList<EntryDist> res = new ArrayList<EntryDist>(nbProx);
	
		EntryDist ed;
		VBOS vbos;
		for (int i = 0; i < base.size(); i++) {
	
		    vbos = (VBOS) base.elementAt(i);
		    ed = new EntryDist(vbos.entry, (double) V.scalarProduct(vbos.CV));
		    //System.out.println(i + " : " + kv.CV.getMagnitude());
		    insert(res, ed, nbProx);
		}
	
		return res;
    }
    
    @Override
    public ArrayList<EntryDist> getProx(ConceptualVector V, int nbProx, String regex) {

    	ArrayList<EntryDist> res = new ArrayList<EntryDist>(nbProx);
    	Matcher m = Pattern.compile(regex).matcher("");
    	EntryDist ed;
    	VBOS vbos;
    	for (int i = 0; i < base.size(); i++) {
    		vbos = (VBOS) base.elementAt(i);
    		m.reset(vbos.entry);
    		if (m.matches()) {
    			ed = new EntryDist(vbos.entry, (double) V.scalarProduct(vbos.CV));
    			//System.out.println(i + " : " + kv.CV.getMagnitude());
    			insert(res, ed, nbProx);
    		}
    	}
    	return res;
    }
    
    public ArrayList<EntryDist> getTopicProx(ConceptualVector V, int nbProx, double ratio) {

    	ArrayList<EntryDist> res = new ArrayList<EntryDist>(nbProx);
    	EntryDist ed;
    	VBOS vbos;
    	double r = ratio;
    	
    	if (r<0) r=0.5;
    	if (r>1) r=0.5;
    	
    	for (int i = 0; i < base.size(); i++) {
    		vbos = (VBOS) base.elementAt(i);
    		ed = new EntryDist(vbos.entry,(double) V.getTopicSimilarity(vbos.CV, r));
    		insert(res, ed, nbProx);
    	}
    	return res;
    }
    
//    /**
//	 * Insert in the result ArrayList a new result. This method is used to sort
//	 * neighbors.
//	 * 
//	 * @param res
//	 *            an ArrayList which contains neighbors already sorted
//	 * @param ed
//	 *            a new neighbor to put in res (or not if the distance is too
//	 *            important)
//	 * @param n
//	 *            number of neighbors wanted
//	 */
//	private static void insert(ArrayList<EntryDist> res, EntryDist ed, int n) {
//
//		if (res.isEmpty()) {
//			res.add(ed);
//			return;
//		}
//
//		int i = 0;
//		for (i = res.size() - 1; i >= 0
//				&& ed.distance > res.get(i).distance; i--)
//			;
//
//		if (i <= res.size() - 1) {
//
//			res.add(i + 1, ed);
//			if (res.size() > n)
//				res.remove(res.size() - 1);
//		} else if (i < n)
//			res.add(ed);
//	}
	
	/**
	 * Insert in the result ArrayList a new result. This method is used to sort
	 * neighbors. Given arrayList size should be <= n.
	 * 
	 * @param res
	 *            an ArrayList which contains neighbors already sorted
	 * @param ed
	 *            a new neighbor to put in res (or not if the distance is too
	 *            important)
	 * @param n
	 *            number of neighbors wanted
	 */
	private static void insert(ArrayList<EntryDist> res, EntryDist ed, int n) {
				
		// INVARIANT: i représente 
		// l'entrée courante dans la recherche, i+1 est une case disponible pour insertion.
		int i = res.size()-1;
		res.add(null);
		while (i != -1 && ed.distance > res.get(i).distance) {
			res.set(i+1, res.get(i));
			i--;
		}
		res.set(i+1, ed);
		
		// supprimer l'éventuelle entrée fictive
		if (res.size() == n+1) {
			res.remove(n);
		}
		
	}
	
	/**
	 * Save the current instance of the object
	 * @param path : complete path to the file where to save the base
	 * */
	public void save(String path){
		
		File file = null;		
		try {
			
			file = new File(path);
			File tmpFile = new File(path + ".tmp");
			if (file.exists()) {
				boolean success = file.renameTo(tmpFile);
				if (! success) 
					System.out.println("Could not backup the current file");
				else
					file = new File(path);
			}
			file.setReadable(true);
			file.setWritable(true);
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
			out.close();
			if (tmpFile.exists()) {
				tmpFile.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a RAM_VectorialBase from a file
	 * @param path : complete path to the file where to load the base
	 * @return a RAM_VectorialBase object loaded from the file
	 * */
	public static String_RAM_VectorialBase load(String path){
		
		String_RAM_VectorialBase base = null;
		File file = null;
		
		try {
			file = new File(path);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			base = (String_RAM_VectorialBase)in.readObject();
			
		} catch (FileNotFoundException e) 
		{
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return base;
	}
	
	/**
	 * Normalise all the vectors of the base
	 * */
	public void massNormalise() {
		
		for (int i=0; i < base.size(); i++) {
			base.elementAt(i).CV.normalise();
		}
	}
	
	/**
	 * @param list : list of words to be tested
	 * @return the number of words from the list present in the base
	 * */
	public int numberOfPresentWord(String[] list) {
		
		int nb = 0;
		
		for (int i=0; i<list.length; i++) {
			if (base.containsKey(list[i])||base.containsKey(list[i].toLowerCase())) nb++;
		}
		
		return nb;
	}
	
	/**
	 * @param nbElem : number of randomly chosen vector in the base
	 * @return the absolute distance mean
	 * */
	public double getAbsoluteDistanceMean(int nbElem, BaseFilter filter) {
		
		ConceptualVector bary = new ConceptualVector(elementAt(0).CV.getDimension(),
				elementAt(0).CV.getCodeLength());
		ConceptualVector rand = null;
		ArrayList<ConceptualVector> list = new ArrayList<ConceptualVector>();
		double mean = 0;
		
		for (int i=0; i<nbElem; i++) {
			rand = getRandomEntry(filter).CV;
			list.add(rand);
			bary = bary.sum(rand);
		}
		
		bary.normalise();
		
		for (int i=0; i<list.size(); i++) {
			mean += bary.getAngularDistance(list.get(i));
		}
		
		mean = mean/nbElem;
		
		return mean;
	}
	
	/**
	 * @param nbElem : number of randomly chosen vector in the base
	 * @return the absolute distance mean
	 * */
	public String getAbsoluteDistanceMeanStringForm(int nbElem, BaseFilter filter) {
		
		ConceptualVector bary = new ConceptualVector(elementAt(0).CV.getDimension(),
				elementAt(0).CV.getCodeLength());
		ConceptualVector rand = null;
		ArrayList<ConceptualVector> list = new ArrayList<ConceptualVector>();
		double mean = 0;
		double localdist = 0;
		int cpt = 0;
		String ret = null;
		
		for (int i=0; i<nbElem; i++) {
			rand = getRandomEntry(filter).CV;
			list.add(rand);
			bary = bary.sum(rand);
		}
		
		bary.normalise();
		
		for (int i=0; i<list.size(); i++) {
			localdist = bary.getAngularDistance(list.get(i));
			if (localdist>1) cpt++;
			mean += localdist;
		}
		
		mean = mean/nbElem;
		
		ret = "Absolute distance mean: "+mean+"\nnumber of distance >1: "+cpt;
		
		return ret;
	}
	
	/**
	 * @param nbElem : number of randomly chosen vector in the base
	 * @return the absolute distance mean
	 * */
	public String getAbsoluteDistanceMeanStringForm(int nbElem) {
		
		ConceptualVector bary = new ConceptualVector(elementAt(0).CV.getDimension(),
				elementAt(0).CV.getCodeLength());
		ConceptualVector rand = null;
		ArrayList<ConceptualVector> list = new ArrayList<ConceptualVector>();
		double mean = 0;
		double localdist = 0;
		int cpt = 0;
		String ret = null;
		
		for (int i=0; i<nbElem; i++) {
			rand = getRandomEntry().CV;
			list.add(rand);
			bary = bary.sum(rand);
		}
		
		bary.normalise();
		
		for (int i=0; i<list.size(); i++) {
			localdist = bary.getAngularDistance(list.get(i));
			if (localdist>1) cpt++;
			mean += localdist;
		}
		
		mean = mean/nbElem;
		
		ret = "Absolute distance mean: "+mean+"\nnumber of distance >1: "+cpt;
		
		return ret;
	}
	
	/**
	 * @param nbElem : number of randomly chosen vector in the base
	 * @return the absolute topic distance mean
	 * */
	public double getAbsoluteTopicDistanceMean(int nbElem, double ratio, BaseFilter filter) {
		
		ConceptualVector bary = new ConceptualVector(elementAt(0).CV.getDimension(),
				elementAt(0).CV.getCodeLength());
		ConceptualVector rand = null;
		ArrayList<ConceptualVector> list = new ArrayList<ConceptualVector>();
		double mean = 0;
		
		for (int i=0; i<nbElem; i++) {
			rand = getRandomEntry(filter).CV;
			list.add(rand);
			bary = bary.sum(rand);
		}
		
		bary.normalise();
		
		for (int i=0; i<list.size(); i++) {
			mean += bary.getTopicDistance(list.get(i),ratio);
		}
		
		mean = mean/nbElem;
		
		return mean;
	}
	
	/**
	 * @param nbElem : number of randomly chosen vector in the base
	 * @return the absolute distance mean
	 * */
	public String getAbsoluteTopicDistanceMeanStringForm(int nbElem, double ratio, BaseFilter filter) {
		
		ConceptualVector bary = new ConceptualVector(elementAt(0).CV.getDimension(),
				elementAt(0).CV.getCodeLength());
		ConceptualVector rand = null;
		ArrayList<ConceptualVector> list = new ArrayList<ConceptualVector>();
		double mean = 0;
		double localdist = 0;
		int cpt = 0;
		String ret = null;
		
		for (int i=0; i<nbElem; i++) {
			rand = getRandomEntry(filter).CV;
			list.add(rand);
			bary = bary.sum(rand);
		}
		
		bary.normalise();
		
		for (int i=0; i<list.size(); i++) {
			localdist = bary.getTopicDistance(list.get(i), ratio);
			if (localdist>1) cpt++;
			mean += localdist;
		}
		
		mean = mean/nbElem;
		
		ret = "Absolute distance mean: "+mean+"\nnumber of distance >1: "+cpt;
		
		return ret;
	}
	
	/**
	 * @param nbElem : number of randomly chosen pair in the base
	 * @return the pair distance mean
	 * */
	public double getPairDistanceMean(int nbElem) {
		
		double mean = 0;
		
		for (int i=0; i<nbElem; i++) {
			mean += getRandomEntry().CV.getAngularDistance(getRandomEntry().CV);
		}
		
		mean = mean/nbElem;
		
		return mean;
	}
	
	/**
	 * @param nbElem : number of randomly chosen pair in the base
	 * @return the pair distance mean
	 * */
	public double getPairDistanceMean(int nbElem, BaseFilter filter) {
		
		double mean = 0;
		
		for (int i=0; i<nbElem; i++) {
			mean += getRandomEntry(filter).CV.getAngularDistance(getRandomEntry().CV);
		}
		
		mean = mean/nbElem;
		
		return mean;
	}
	
	/**
	 * @param nbElem : number of randomly chosen pair in the base
	 * @return the pair topic distance mean
	 * */
	public double getPairTopicDistanceMean(int nbElem, double ratio, BaseFilter filter) {
		
		double mean = 0;
		
		for (int i=0; i<nbElem; i++) {
			mean += getRandomEntry(filter).CV.getTopicDistance(getRandomEntry().CV, ratio);
		}
		
		mean = mean/nbElem;
		
		return mean;
	}
	
	public int size() {
		
		return base.size();
	}
	
	public Enumeration<String> keys() {
		
		return base.keys();
	}
	
	public VBOS elementAt(int index) {
		
		return base.elementAt(index);
	}
	
	public VBOS getRandomEntry() {
		
		return base.elementAt(new Random().nextInt(base.size()));
	}
	
	public VBOS getRandomEntry(BaseFilter filter) {
		
		VBOS entry = base.elementAt(new Random().nextInt(base.size()));
		
		while (!filter.matchFilter(entry.entry)) entry = base.elementAt(new Random().nextInt(base.size()));
		
		return entry;
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public void RCompatibleOutputFile(String filePath){
		
		File f = new File(filePath);
		String line = null;
		if (f.exists()){
			f.delete();
			f = new File(filePath);
		}
		
		for (String key : base.keySet()) {
			line = key+'\t'+((VBOS)base.get(key)).CV.toStringRFormat()+'\n';
			WriteInFile.appendText(f, line);
		}
	}

	@Override
	public Enumeration<VBOS> getEntries() {

		return base.elements();
	}

	@Override
	public VectorialBase<String> simpleSampleBase(int nb) {
		int sampleSize = nb;
		HashSet<Integer> randomSet = new HashSet<Integer>();
		Random randomizer = new Random();
		String_RAM_VectorialBase sample = new String_RAM_VectorialBase(this.cvEncodingSize,this.cvDimension);
		
		if ((sampleSize>this.size())||(sampleSize<1))
			sampleSize = this.size()/10;
		
		while (randomSet.size()<sampleSize){
			randomSet.add(randomizer.nextInt(this.size()));
		}
		
		for (int pos : randomSet){
			VBOS vb = this.elementAt(pos);
			sample.addVector(vb.entry, vb.CV);
		}
		
		return sample;
	}
}
