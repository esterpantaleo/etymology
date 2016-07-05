package org.getalp.blexisma.plugins.semdictionary;

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
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.impl.semdico.PersistentlyIteratingSemanticNetwork;
import org.getalp.blexisma.impl.semdico.SemnetBasedTrainingIterator;
import org.getalp.blexisma.impl.semdico.XWNSemanticDictionnary;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.TreeDecorator;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;

public class XWNDataHandlerPlugin extends AbstractSemanticDictionaryPlugin {

	protected EnglishLemmatizer lemmatizer;

	protected XWNSemanticDictionnary dictionary;
	protected PersistentlyIteratingSemanticNetwork sni;
	protected TreeDecorator decorator;
	protected String basePath;
	protected String netPath;

	protected String_RAM_VectorialBase vb;
	protected SemanticNetwork<String, String> network;
	
	/**
	 * Called before the plugin is loaded. Initialize the parameters
	 * */
	@Override
	public void setParameter(Object o)  {
		// WARNING: This method is only called if parameters are given to the plugins. So do not give default values to the parameters.
		super.setParameter(o);
		Arguments args = new Arguments(o);

		this.basePath = args.getString("basePath");
		this.netPath = args.getString("netPath");
		
		try {
			lemmatizer = new EnglishLemmatizer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the Plugin is loaded.  Establish the subscriptions
	 * */
	@Override
	protected void setupSubscriptions() {
		super.setupSubscriptions();
		if (log.isInfoEnabled()) {
			log.info("Max heap size: "+Runtime.getRuntime().maxMemory());
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
			log.info("Loading Semantic Dictionary");
		}
		vb = String_RAM_VectorialBase.load(basePath);
		try {
			network = TextOnlySemnetReader.loadNetwork(netPath);
			} catch (IOException e) {
			if (log.isFatalEnabled()) {
				log.fatal("Could not read Semantic Network.", e);
				throw new RuntimeException("Could not read Semantic Network.", e);
			}
		}
			
		dictionary = new XWNSemanticDictionnary(vb,network);
		dictionary.setCoeffVar(coeffVar);
		if (log.isInfoEnabled()) {
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
		}
		
		sni = new SemnetBasedTrainingIterator(basePath, network); 
		
		Iterator<String> nodes = network.getNodesIterator();
		while (nodes.hasNext()){
			String n = nodes.next();
			if (XWNSemanticDictionnary.isLemma(n, "eng")) CYCLE_SAVE_INTERVAL++;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("CYCLE SAVE INTERVAL: "+CYCLE_SAVE_INTERVAL);
		}
		
		
		decorator = new TreeDecorator(dictionary);
		if (log.isShoutEnabled()) {
			log.shout("Semantic Dictionary online");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#decorateAnalysisTree(org.getalp.blexisma.api.syntaxanalysis.AnalysisTree)
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
		String node, lg, txt;
		do {
			node = sni.next();
			lg = XWNSemanticDictionnary.extractLemmaLg(node);
		} while (!validLanguage(lg));

		// lastId = XWNSemanticDictionnary.extractLemmaText(node);

		if (log.isDebugEnabled()) {
			log.debug("Infinite next id [" + lg + "] :" + node );
		}

		lg = ISO639_3.sharedInstance.getIdCode(lg);
		txt = XWNSemanticDictionnary.extractLemmaText(node);
		job.setId(node);
		job.setLang(lg);
		job.setSemDefinition(dictionary.getDefinition(txt, lg));
		job.setPhase(SemanticJobPhase.WAITINGFORSYNTAX);
		
		job.setVectNorm(vb.getCVEncodingSize());
		job.setVectSize(vb.getCVDimension());
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#saveSemanticDictionary()
	 */
	@Override
	protected void saveSemanticDictionary(int cyclenumber) {
		sni.saveIterationState();
		if (null != basePath) {
			this.vb.save(basePath);
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
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#retreiveVector(java.lang.String, java.lang.String)
	 */
	@Override
	protected ConceptualVector retrieveVector(String lang, String lemma) {
		return dictionary.getDefinition(lemma, lang).getMainVector();
	}


	/* (non-Javadoc)
	 * @see org.getalp.blexisma.plugins.semdictionary.AbstractSemanticDictionaryPlugin#retreiveDefinition(java.lang.String, java.lang.String)
	 */
	@Override
	protected SemanticDefinition retrieveDefinition(String lang, String lemma) {
		return dictionary.getDefinition(lemma, lang);
	}

	@Override
	protected ArrayList<String> retrieveProxOfVector(ConceptualVector vect, int size) {
		return dictionary.getProx(vect, size);
	}
}
