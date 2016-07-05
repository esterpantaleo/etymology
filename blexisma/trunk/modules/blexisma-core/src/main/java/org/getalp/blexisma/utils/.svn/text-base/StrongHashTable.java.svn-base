/**
 * *
 * StrongHashTable.java
 * Created on 11 mars 2010 13:42:16
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.utils;


import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

/**
 * 
 * This class implement an strong hashtable which can be accessed sequencialy
 * 
 * @author Didier Schwab
 * 
 */
public class StrongHashTable<K, V> implements Serializable
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2910168467257517345L;

	private Vector<V> vector;

	private Hashtable<K, Integer> hashTable;

	/**
	 * Constructs a new, empty strong hashtable with a default initial capacity
	 * (11) and load factor, which is 0.75.
	 * 
	 */
	public StrongHashTable() {

		hashTable = new Hashtable<K, Integer>();
		vector = new Vector<V>();
		// stack = new Stack();
	}

	/**
	 * Constructs a new, empty hashtable with the specified initial capacity and
	 * default load factor, which is 0.75.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the strong hashtable.
	 * 
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 * 
	 */

	public StrongHashTable(int initialCapacity) {

		hashTable = new Hashtable<K, Integer>(initialCapacity);
		vector = new Vector<V>(initialCapacity);
		// stack = new Stack();
	}

	/**
	 * Constructs a new, empty strong hashtable with the specified initial
	 * capacity and the specified load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the strong hashtable.
	 * @param loadFactor
	 *            the load factor of the strong hashtable.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public StrongHashTable(int initialCapacity, float loadFactor) {

		hashTable = new Hashtable<K, Integer>(initialCapacity, loadFactor);
		vector = new Vector<V>(initialCapacity);
		// stack = new Stack();
	}

	/**
	 * Clears this strong hashtable so that it contains no keys.
	 * 
	 */
	public void clear() {

		hashTable.clear();
		vector.clear();
	}

	/**
	 * Tests if some key maps into the specified value in this strong hashtable.
	 * This operation is more expensive than the containsKey method.
	 * 
	 * Note that this method is identical in functionality to containsValue,
	 * (which is part of the Map interface in the collections framework).
	 * 
	 * @param value
	 *            a value to search for.
	 * @return true if and only if some key maps to the value argument in this
	 *         strong hashtable as determined by the equals method; false
	 *         otherwise.
	 * 
	 */
	public boolean contains(Object value) {

		return vector.contains(value);
	}

	/**
	 * Tests if the specified object is a key in this strong hashtable.
	 * 
	 * @param key
	 *            possible key.
	 * @return true if and only if the specified object is a key in this strong
	 *         hashtable, as determined by the equals method; false otherwise.
	 * 
	 * @throws NullPointerException
	 *             if the key is null
	 */
	public boolean containsKey(Object key) {

		return hashTable.containsKey(key);
	}

	/**
	 * Returns true if this strong hashtable maps one or more keys to this
	 * value.
	 * 
	 * Note that this method is identical in functionality to contains (which
	 * predates the Map interface).
	 * 
	 * @param value
	 *            value whose presence in this strong hashtable is to be tested.
	 * @return true if this map maps one or more keys to the specified value.
	 * @throws if
	 *             the value is null.
	 */

	public boolean containsValue(Object value) {

		return vector.contains(value);
	}

	/**
	 * Returns an enumeration of the values in this strong hashtable. Use the
	 * Enumeration methods on the returned object to fetch the elements
	 * sequentially.
	 * 
	 * @return an enumeration of the values in this strong hashtable.
	 */
	public Enumeration<V> elements() {

		return vector.elements();
	}

	/**
	 * Tests if this strong hashtable maps no keys to values.
	 * 
	 * @return true if this strong hashtable maps no keys to values; false
	 *         otherwise.
	 */
	public boolean isEmpty() {

		return hashTable.isEmpty();
	}

	/**
	 * Returns an enumeration of the keys in this strong hashtable.
	 * 
	 * @return an enumeration of the keys in this strong hashtable.
	 */
	public Enumeration<K> keys() {

		return hashTable.keys();
	}

	/**
	 * Returns a Set view of the keys contained in this strong hashtable. The
	 * Set is backed by the strong hashtable, so changes to the strong hashtable
	 * are reflected in the Set, and vice-versa. The Set supports element
	 * removal (which removes the corresponding entry from the strong
	 * hashtable), but not element addition.
	 * 
	 * @return a set view of the keys contained in this map.
	 */
	public Set<K> keySet() {

		return hashTable.keySet();
	}

	/**
	 * Returns the number of keys in this strong hashtable.
	 * 
	 * @return the number of keys in this strong hashtable.
	 */
	public int size() {

		return hashTable.size();
	}

	/**
	 * Maps the specified key to the specified value in this strong hashtable.
	 * Neither the key nor the value can be null.
	 * 
	 * The value can be retrieved by calling the get method with a key that is
	 * equal to the original key.
	 * 
	 * @param key
	 *            the strong hashtable key.
	 * @param value
	 *            the value.
	 * @return the previous value of the specified key in this strong hashtable,
	 *         or null if it did not have one.
	 */
	public V put(K key, V value) {

		Integer I;

		I = hashTable.get(key);
		if (I == null) {

			hashTable.put(key, new Integer(vector.size()));
			vector.add(value);
			return null;
		} else {

			V obj = vector.get(I.intValue());
			vector.setElementAt(value, I.intValue());
			return obj;
		}
	}

	/**
	 * Returns the value to which the specified key is mapped in this strong
	 * hashtable.
	 * 
	 * @param key
	 *            a key in the strong hashtable.
	 * @return the value to which the key is mapped in this strong hashtable;
	 *         null if the key is not mapped to any value in this strong
	 *         hashtable.
	 */
	public Object get(Object key) {

		if (key == null)
			return null;
		else {
			Integer I = hashTable.get(key);
			if (I == null)
				return null;
			else
				return vector.elementAt(I.intValue());
		}
	}

	/**
	 * Removes the key (and its corresponding value) from this strong hashtable.
	 * This method does nothing if the key is not in the strong hashtable.
	 * 
	 * @param key
	 *            the key that needs to be removed.
	 * @return the value to which the key had been mapped in this strong
	 *         hashtable, or null if the key did not have a mapping.
	 * @throws NullPointerException
	 *             if the key is null.
	 */
	//TODO:avoid the vector shift when removing (ie.replace by the last element and update hashtable)
	public Object remove(K key) {

		Integer I = hashTable.remove(key);
		if (I == null)
			return null;
		else {

			return vector.remove(I.intValue());
		}
	}

	/**
	 * Returns the element at the specified position in this Vector
	 * 
	 * @param index
	 *            index of element to return.
	 * @return object at the specified position in the strong hashtable
	 */
	public V elementAt(int index) {
	    
		return vector.elementAt(index);
	}
	
	public String toString(){
	    
	    StringBuilder sb = new StringBuilder(1000);
	    sb.append('\n');
	    for(int i = 0; i < vector.size(); i++){
		
		sb.append(i + " = ");
		sb.append(vector.elementAt(i));
		sb.append('\n');
	    }
	    
	    return sb.toString();
	}
}
