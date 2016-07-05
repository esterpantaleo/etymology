package org.getalp.blexisma.plugins.semdictionary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.cougaar.util.Arguments;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.ProxRequest;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionary;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.TreeDecorator;
import org.getalp.blexisma.utils.OpenFile;
import org.getalp.blexisma.utils.WriteInFile;
import org.getalp.dbnary.WiktionaryExtractor;

/**
 * @author Alexandre Labadié
 * */
public class WiktionaryBasedSemanticDictionaryPlugin extends AbstractSemanticDictionaryPlugin {	

	protected WiktionaryBasedSemanticDictionary dictionary;
	protected SemanticNetwork<String, String> network;
	protected String_RAM_VectorialBase vb;
	protected Iterator<String> infiniteNodeIterator ;
	protected TreeDecorator decorator;
	protected String basePath;
	protected String netPath;
	protected String lastIdFile;
	protected String lastId;	
	
	/**
	 * Called before the plugin is loaded. Initialize the parameters
	 * */
	public void setParameter(Object o) {
		super.setParameter(o);
		
		Arguments args = new Arguments(o);
		
		basePath = args.getString("basePath");
		netPath = args.getString("netPath");
	}
	
	/**
	 * Called when the Plugin is loaded.  Establish the subscriptions
	 * */
	protected void setupSubscriptions() {
		super.setupSubscriptions();
		
		loadNetworkAndVectorBase();
		dictionary = new WiktionaryBasedSemanticDictionary(vb,network);
		dictionary.setCoeffVar(coeffVar);

		resumeInfiniteNodeIterator();

		computeCycleSaveInterval();
		
		decorator = new TreeDecorator(dictionary);
		if (log.isShoutEnabled()) {
			log.shout("Wiktionary Based Semantic Dictionary online");
		}
	}
	
	protected void computeCycleSaveInterval() {
		Iterator<String> nodes = network.getNodesIterator();
		while (nodes.hasNext()) {
			String n = nodes.next();
			for (int i=0; i<languageList.length;i++){
				if (XWNSemanticDictionnary.isLemma(n, languageList[i])) CYCLE_SAVE_INTERVAL++;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("CYCLE SAVE INTERVAL: "+CYCLE_SAVE_INTERVAL);
		}
	}

	protected void loadNetworkAndVectorBase() {
		if (log.isInfoEnabled()) {
			log.info("Max heap size: "+Runtime.getRuntime().maxMemory());
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
			log.info("Loading Semantic Dictionary");
		}
		try {
			this.network = TextOnlySemnetReader.loadNetwork(netPath);
			this.vb = String_RAM_VectorialBase.load(basePath);
			if (this.vb == null) {
				// The vectorial base was not readable or file did not exist
				this.vb = new String_RAM_VectorialBase(this.cvEncodingSize,
						this.cvDimension);
				this.vb.save(this.basePath);
			} else if (this.vb.getCVDimension() != this.cvDimension
					|| this.vb.getCVEncodingSize() != cvEncodingSize) {
				if (log.isFatalEnabled()) log.fatal("Incompatible Vectorial Base. Exiting.", new RuntimeException());
				throw new RuntimeException("Wiktionary based semantic dictionary is not compatible with requested plugin parameters.");
			}
		} catch (IOException e) {
			if (log.isFatalEnabled()) log.fatal("Could not read wiktionary based semantic dictionary.", e);
			throw new RuntimeException(e);
		}
		if (log.isInfoEnabled()) {
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
		}	
	}
	
	protected void resumeInfiniteNodeIterator() {
		File f = null;

		lastIdFile = this.getBaseDirectory()+File.separator+"lastid.txt";
		
		f = new File(lastIdFile);

		if (f.exists()) {
			lastId = OpenFile.readFullTextFile(f);
			if (!validId(lastId)) {
				f.delete();
				try {
					f.createNewFile();
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error("Could not override file: " + f.getAbsolutePath(), e);
					}
				}
				lastId = null;
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				if (log.isErrorEnabled()) log.error("Could not create file: " + f.getAbsolutePath(), e);
			}
			lastId = null;
		}
		
		infiniteNodeIterator = (Iterator<String>) network.getInfiniteNodesIterator();
		log.shout("Last computed vector was: " + lastId);

		if (lastId!=null) {
			if (log.isShoutEnabled()) {
				log.shout("Searching for last computed vector.");
			}
			while (!infiniteNodeIterator.next().equals(lastId));
			if (log.isShoutEnabled()) {
				log.shout("Resuming training at lemma: " + lastId);
			}
		}
			
	}
	
	public String getBaseDirectory() {
		return new File(basePath).getParent();
	}
	
	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#decorateAnalysisTree(org.getalp.blexisma.api.syntaxanalysis.AnalysisTree, java.lang.String)
	 */
	@Override
	protected AnalysisTree decorateAnalysisTree(AnalysisTree tree, String lang) {
		if (!tree.isError()) tree = decorator.simpleDecorate(tree, lang, cvDimension, cvEncodingSize);
		return tree;
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#initializeNextLearningJob(org.getalp.blexisma.cougaarcom.SemanticLearningJob)
	 */
	@Override
	protected void initializeNextLearningJob(SemanticLearningJob job) {
		if (job.getId() == null) {
			String node, lang, txt;
			do {
				node = infiniteNodeIterator.next();
				lang = node.substring(1, node.indexOf("|"));
			} while (!validLanguage(lang));

			lastId = node;

			if (log.isDebugEnabled()) {
				log.debug("Infinite next id [" + lang + "] :" + node );
			}

			lang = ISO639_3.sharedInstance.getIdCode(lang);
			txt = WiktionaryExtractor.convertToHumanReadableForm(node.substring(node.indexOf("|")+1));
			job.setId(node);
			job.setLang(lang);
			job.setSemDefinition(dictionary.getDefinition(txt, lang));
			job.setVectNorm(cvEncodingSize);
			job.setVectSize(cvDimension);
		} else {
			// Id is already specified in job, complete the job information
			String node = job.getId();
			String lang = "";
			int p = node.indexOf("|");
			if (p != -1)
				lang = node.substring(1, p);
			if (validLanguage(lang)) {
				if (log.isDebugEnabled()) {
					log.debug("On demand learning job [" + lang + "] :" + node );
				}
				lang = ISO639_3.sharedInstance.getIdCode(lang);
				String txt = WiktionaryExtractor.convertToHumanReadableForm(node.substring(node.indexOf("|")+1));
				job.setLang(lang);
				job.setSemDefinition(dictionary.getDefinition(txt, lang));
				job.setVectNorm(cvEncodingSize);
				job.setVectSize(cvDimension);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#saveSemanticDictionary()
	 */
	@Override
	protected void saveSemanticDictionary(int cyclenumber) {
		if (log.isShoutEnabled()) log.shout("Last id... "+lastId);
		WriteInFile.writeText(new File(lastIdFile), lastId);
		if (this.basePath != null) {
			this.vb.save(this.basePath);
			this.vb.save(basePath+"_"+cyclenumber);
		}
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#updateVector(java.lang.String, java.lang.String, org.getalp.blexisma.api.ConceptualVector)
	 */
	@Override
	protected void updateVector(String key, String lg, ConceptualVector cv) {
		vb.addVector(key, cv);
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#computeProxRequest(org.getalp.blexisma.cougaarcom.ProxRequest)
	 */
	@Override
	protected ArrayList<String> computeProxRequest(ProxRequest prox) {
		if (prox.getRegex() == null) {
			return dictionary.getProx(prox.getLemme(), prox.getLang(), prox.getSize());
		} else {
			return dictionary.getProx(prox.getLemme(), prox.getLang(), prox.getRegex(), prox.getSize());
		}
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#retrieveVector(java.lang.String, java.lang.String)
	 */
	@Override
	protected ConceptualVector retrieveVector(String lang, String lemma) {
		return this.vb.getVector(lemma);

	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#retrieveDefinition(java.lang.String, java.lang.String)
	 */
	@Override
	protected SemanticDefinition retrieveDefinition(String lang, String lemma) {
		return dictionary.getDefinition(lemma, lang);
	}
	
	private boolean validId(String id)
	{
		return !id.equals("");
	}

	@Override
	protected ArrayList<String> retrieveProxOfVector(ConceptualVector vect,
			int size) {
		return dictionary.getProx(vect, size);
	}
}
