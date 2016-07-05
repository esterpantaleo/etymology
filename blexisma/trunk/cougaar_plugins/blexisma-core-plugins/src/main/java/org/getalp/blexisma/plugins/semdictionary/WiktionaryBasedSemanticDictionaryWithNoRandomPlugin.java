package org.getalp.blexisma.plugins.semdictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.cougaar.util.Arguments;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.TreeDecorator;

public class WiktionaryBasedSemanticDictionaryWithNoRandomPlugin extends
		WiktionaryBasedSemanticDictionaryPlugin {

	protected HashSet<String> nailedDefs;
	protected String nailedDefPath = null;
	
	
	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.WiktionaryBasedSemanticDictionaryPlugin#setParameter(java.lang.Object)
	 */
	@Override
	public void setParameter(Object o) {
		super.setParameter(o);

		Arguments args = new Arguments(o);
		
		nailedDefPath = args.getString("nailedDefPath");
	}

	/**
	 * Called when the Plugin is loaded.  Establish the subscriptions
	 * */
	protected void setupSubscriptions() {
		super.setupSubscriptions();
		dictionary = new WiktionaryBasedSemanticDictionaryWithNoRandomVectorGeneration(vb,network);
		
		loadNailedDefs();
		
		decorator = new TreeDecorator(dictionary);
		if (log.isShoutEnabled()) {
			log.shout("Wiktionary Based Semantic Dictionary online");
		}
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.WiktionaryBasedSemanticDictionaryPlugin#updateVector(java.lang.String, java.lang.String, org.getalp.blexisma.api.ConceptualVector)
	 */
	private void loadNailedDefs() {
		FileInputStream in;
		nailedDefs = new HashSet<String>();
		try {
			in = new FileInputStream(nailedDefPath);
		
			BufferedReader brdr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			while ((line = brdr.readLine()) != null) {
				nailedDefs.add(line);
			}
		} catch (FileNotFoundException e) {
			if (log.isErrorEnabled())
				log.error("Nailed Definition File Not found. No definition will be nailed.", e);
		} catch (UnsupportedEncodingException e) {
			// This should never happen
			e.printStackTrace();
		} catch (IOException e) {
			if (log.isErrorEnabled())
				log.error("IO error while reading nailed definition list.", e);
		}
		if (log.isShoutEnabled()) log.shout("Nailed Definitions file loaded, nailing " + nailedDefs.size() + " definitions.");
	}
	
	@Override
	protected void updateVector(String key, String lg, ConceptualVector cv) {
		if (! nailedDefs.contains(key))
			vb.addVector(key, cv);
	}

	
}
