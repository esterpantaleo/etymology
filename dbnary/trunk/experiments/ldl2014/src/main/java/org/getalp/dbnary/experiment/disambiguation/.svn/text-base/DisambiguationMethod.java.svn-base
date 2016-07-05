package org.getalp.dbnary.experiment.disambiguation;

import com.hp.hpl.jena.rdf.model.Resource;

import java.util.Set;

public interface DisambiguationMethod {
	
	/**
	 * returns a set of LexicalSense that are considered relevant by the method as a disambiguation for a 
	 * LexicalEntry in a specific context.
	 * @param lexicalEntry the resource of the LEMON lexicalEntry to be disambiguated
	 * @param context : the context used by the specific method as a disambiguation criterion 
	 * @return
	 */
	public Set<? extends Resource> selectWordSenses(Resource lexicalEntry, Object context) throws InvalidContextException, InvalidEntryException;

}
