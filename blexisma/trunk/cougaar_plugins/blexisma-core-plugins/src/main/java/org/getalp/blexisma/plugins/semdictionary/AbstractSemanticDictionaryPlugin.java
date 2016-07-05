package org.getalp.blexisma.plugins.semdictionary;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.BaseUpdateJob;
import org.getalp.blexisma.cougaarcom.DefinitionRequest;
import org.getalp.blexisma.cougaarcom.ProxRequest;
import org.getalp.blexisma.cougaarcom.ProxVectorRequest;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.cougaarcom.VectorRequest;


/**
 * @author Alexandre Labadié
 * */
public abstract class AbstractSemanticDictionaryPlugin extends ComponentPlugin {
	
	protected IncrementalSubscription decoanarequests;
	protected IncrementalSubscription endlearnrequests;
	protected IncrementalSubscription startlearnrequests;
	protected IncrementalSubscription decolearnrequests;
	protected IncrementalSubscription proxrequests;
	protected IncrementalSubscription vectorrequests;
	protected IncrementalSubscription definitionrequests;
	protected IncrementalSubscription proxvectorrequests;

	protected LoggingService log;

	protected int TIMES;
	protected int LOGGING_INTERVAL = 500;
	protected int CYCLE_SAVE_INTERVAL = 0;
	protected int cycleSaveNumber;
	protected int saveCount;

	protected int cvEncodingSize;
	protected int cvDimension;
	protected double coeffVar;
	protected String[] languageList;
	
	protected int nbVectorsSet = 0;
	protected int nbLemmaTrained = 0;
	protected long startTime;
	protected long lastLogTime;
	
	
	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#load()
	 */
	@Override
	public void load() {
		super.load();
		log = getServiceBroker().getService(this, LoggingService.class, null);
	}

	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#unload()
	 */
	@Override
	public void unload() {
		super.unload();
		getServiceBroker().releaseService(this, LoggingService.class, log);
	}
	
	/**
	 * Called before the plugin is loaded. Initialize the parameters
	 * */
	public void setParameter(Object o){
		super.setParameter(o);
		Arguments args = new Arguments(o);
		
		TIMES = args.getInt("saveCount");
		cvEncodingSize = args.getInt("codeLength");
		cvDimension = args.getInt("dimension");
		coeffVar = args.getInt("coeffVar");
		if (coeffVar == -1) coeffVar=1.5;
		languageList = args.getString("languageList").split(",");
		for (int i=0; i<languageList.length; i++) languageList[i]=ISO639_3.sharedInstance.getIdCode(languageList[i]);
		saveCount = TIMES;

	}
	
	/**
	 * 
	 * */
	protected void execute(){
		if (log.isDebugEnabled()) log.debug("============ Execute ===============");
		handleAnalysisJobDecoration();
		handleLearningJobStart();
		handleLearningJobDecoration();
		handleBaseUpdateJob();
		handleProxRequest();
		handleVectorRequest();
		handleDefinitionRequest();
		handleProxVectorManagement();

		if (log.isDebugEnabled()) log.debug("============ /Execute ===============");
	}

	/**
	 * Called when the Plugin is loaded.  Establish the subscriptions
	 * */
	protected void setupSubscriptions() {
		
		endlearnrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new VectorialBaseUpdatePredicate()));
		decoanarequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new SemanticAnalysisJobDecoratePredicate()));
		startlearnrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new SemanticLearningJobStartPredicate()));
		decolearnrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new SemanticLearningJobDecoratePredicate()));
		proxrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new ProxRequestPredicate()));
		vectorrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new VectorRequestPredicate()));
		definitionrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new DefinitionRequestPredicate()));
		proxvectorrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new ProxVectorRequestPredicate()));
	
		startTime = System.currentTimeMillis();
		lastLogTime = System.currentTimeMillis();
		cycleSaveNumber = 0;
		
	}
	
	protected void handleAnalysisJobDecoration() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticAnalysisJob> new_requests = decoanarequests.getChangedList();
		SemanticAnalysisJob tmpJob = null;
		AnalysisTree tmpTree = null;
		
		while (new_requests.hasMoreElements()) {
			if (log.isDebugEnabled()) log.debug("Semantic Dictionary: starting analysis requests decoration");
			tmpJob = new_requests.nextElement();
			tmpTree = tmpJob.getTree();
			tmpTree = decorateAnalysisTree(tmpTree, tmpJob.getLang());
			tmpJob.setTree(tmpTree);
			tmpJob.setPhase(SemanticJobPhase.WAITINGFORSEMANTIC);
			if (log.isDebugEnabled()) log.debug("Semantic dictionary: posting decoration");
			getBlackboardService().publishChange(tmpJob);
		}
	}
	
	/**
	 * return the given analysis tree decorated with semantic definitions.
	 * 
	 * @param tmpTree
	 * @param lang
	 * @return
	 */
	protected abstract AnalysisTree decorateAnalysisTree(AnalysisTree tree, String lang) ;
	
	protected void handleLearningJobDecoration() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> new_requests = decolearnrequests.getChangedList();
		SemanticLearningJob tmpJob = null;
		HashMap<String,AnalysisTree> listTree = null;
		AnalysisTree tmpTree = null;
		
		while (new_requests.hasMoreElements()) {
			try {
				tmpJob = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: starting learning requests decoration for "+tmpJob);
				listTree = tmpJob.getTrees();
	
				for (String key : listTree.keySet()) {
					tmpTree = listTree.get(key);
					tmpTree = decorateAnalysisTree(tmpTree, tmpJob.getLang());
					listTree.put(key, tmpTree);
				}
				tmpJob.setTrees(listTree);
				tmpJob.setPhase(SemanticJobPhase.WAITINGFORSEMANTIC);
				getBlackboardService().publishChange(tmpJob);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while Decorating trees for" + (tmpJob == null ? "unknown job": tmpJob.getId()), e);
				throw e;
			}
		}
	}
	
	protected void handleLearningJobStart(){
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> new_requests = startlearnrequests.getAddedList();
		SemanticLearningJob tmpJob = null;
		if (log.isDebugEnabled()) {
			log.debug("-------------------------------");
		}
		int nbreq = 0;
		while (new_requests.hasMoreElements()) {
			try {
				tmpJob = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: launching learning requests for " + tmpJob);
				// Set the phase before initialization so that the implementation can eventually change it.
				tmpJob.setPhase(SemanticJobPhase.WAITINGFORSYNTAX);
				initializeNextLearningJob(tmpJob);
				getBlackboardService().publishChange(tmpJob);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while starting learning for " + (tmpJob == null ? "unknown job": tmpJob.getId()), e);
				throw e;
			}
			nbreq++;
		}
		if (log.isDebugEnabled()) {
			log.debug("------------ "+nbreq+" ---------");
		}
	}
	
	/**
	 * Initialize the next learning job. This method is called to modify the job given as a parameter.
	 * 
	 * The implementor has the responsibility to set the job's id, lang, vector norm 
	 * and vector size of the provided job. 
	 * 
	 * @param tmpJob
	 */
	protected abstract void initializeNextLearningJob(SemanticLearningJob job);

	protected void handleBaseUpdateJob(){
		@SuppressWarnings("unchecked")
		Enumeration<BaseUpdateJob> new_requests = endlearnrequests.getAddedList();
		BaseUpdateJob buJob = null;
		
		while (new_requests.hasMoreElements()) {
			try {
				buJob = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: ending learning requests for "+buJob);
				nbLemmaTrained++; nbVectorsSet++;
				
				String lang = buJob.getLg();
				for ( Entry<String, ConceptualVector> entry : buJob.getCvs().entrySet())  {
					nbVectorsSet++;
					if (log.isDebugEnabled()) log.debug("Updating vector for "+entry.getKey());
					updateVector(entry.getKey(), lang, entry.getValue());
				}
				if ((CYCLE_SAVE_INTERVAL>0)&&(nbLemmaTrained % CYCLE_SAVE_INTERVAL == 0))
					cycleSaveNumber++;
				
				if (nbLemmaTrained % LOGGING_INTERVAL == 0) {
					long ctime = System.currentTimeMillis();
					if (log.isShoutEnabled()) 
						log.shout("Trained " + LOGGING_INTERVAL + " lemmas in " + (ctime - lastLogTime) + " ms.");
					log.shout("Trained " + nbLemmaTrained + " lemmas (" + nbVectorsSet + " vectors computed) in " + (ctime - startTime) + " ms.");
					log.shout("Current training speed: " + ((ctime - startTime)/nbLemmaTrained) + " ms/lemma.");
					lastLogTime = ctime;
				}
				getBlackboardService().publishRemove(buJob);
				saveCount--;
				if (saveCount < 1) {
					saveCount = TIMES;
					if (log.isShoutEnabled()) log.shout("Saving ConceptualVector base...");
					long t = System.currentTimeMillis();
					saveSemanticDictionary(cycleSaveNumber);
					if (log.isShoutEnabled()) log.shout("ConceptualVector base saved in " + (System.currentTimeMillis() - t) + " ms.");
				}
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while upating vectorial base.", e);
				throw e;
			}
		}
	}
	
	/**
	 * Save the semantic dictionary. This method is called once in a while by the plugin 
	 * to allow for permanent saving of the vector base.
	 * 
	 * The implementor is responsible for the permanent saving of the data.
	 * 
	 */
	protected abstract void saveSemanticDictionary(int cycleNumber);
	
	/**
	 * update the entry whose id is derived from key and lg with the provided vector.
	 * @param key
	 * @param lg
	 * @param cv
	 */
	protected abstract void updateVector(String key, String lg, ConceptualVector cv);

	protected void handleProxRequest() {
		@SuppressWarnings("unchecked")
		Enumeration<ProxRequest> new_requests = proxrequests.getAddedList();
		ProxRequest prox = null;
		ArrayList<String> list = null;
		
		while (new_requests.hasMoreElements()) {
			try {
				prox = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: proxy requests for "+"#"+prox.getLang()+"|"+prox.getLemme());
				list = computeProxRequest(prox);
				if (log.isDebugEnabled()) {
					if (list == null) {
						log.debug("Proxy list null for " + prox.getLemme());
					} else {
						log.debug("Proxy list: " + list.toString());
					}
				}
				prox.setProxList(list);
				prox.setEnd(true);
				getBlackboardService().publishChange(prox);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while computing prox list.", e);
				throw e;
			}
		}
	}
	
	
	/**
	 * returns an ArrayList of String
	 * @param prox
	 * @return 
	 */
	protected abstract ArrayList<String> computeProxRequest(ProxRequest prox);
	
	protected void handleVectorRequest() {
		@SuppressWarnings("unchecked")
		Enumeration<VectorRequest> new_requests = vectorrequests.getAddedList();
		VectorRequest vect = null;
		ConceptualVector cv = null;		
		
		while (new_requests.hasMoreElements()) {
			try {
				vect = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: vector requests for "+"#"+vect.getLang()+"|"+vect.getLemma());
				cv = retrieveVector(vect.getLang(), vect.getLemma());
				if (log.isDebugEnabled()) {
					if (cv == null) {
						log.debug("No vector for "+vect.getLemma());
						cv = new ConceptualVector(cvDimension, cvEncodingSize);
					} else {
						log.debug("Vector for "+vect.getLemma()+": "+cv.toStringHexa());
					}
				}
				vect.setVector(cv);
				getBlackboardService().publishChange(vect);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while upating vectorial base.", e);
				throw e;
			}
		}
	}
	
	protected abstract ConceptualVector retrieveVector(String lang, String lemma);

	protected void handleDefinitionRequest() {
		@SuppressWarnings("unchecked")
		Enumeration<DefinitionRequest> new_requests = definitionrequests.getAddedList();
		DefinitionRequest defreq = null;
		SemanticDefinition def = null;
		
		while (new_requests.hasMoreElements()) {
			try {
				defreq = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: defition requests for "+"#"+defreq.getLang()+"|"+defreq.getLemma());
				def = retrieveDefinition(defreq.getLang(), defreq.getLemma());
				if (log.isDebugEnabled()) {
					if (def == null) {
						log.debug("No definition for "+defreq.getLemma());
						// Give an empty definition
						def = new SemanticDefinition(defreq.getLemma(), new ConceptualVector(cvDimension, cvEncodingSize), null);
					} else {
						log.debug("defition for "+defreq.getLemma()+": "+def.toString());
					}
				}
				defreq.setDef(def);
				getBlackboardService().publishChange(defreq);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while upating vectorial base.", e);
				throw e;
			}
		}
	}
	
	protected abstract SemanticDefinition retrieveDefinition(String lang, String lemma);
	
	
	protected void handleProxVectorManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<ProxVectorRequest> new_requests = proxvectorrequests.getAddedList();
		ProxVectorRequest prox = null;
		ArrayList<String> list = null;
		
		while (new_requests.hasMoreElements()) {
			try {
				prox = new_requests.nextElement();
				if (log.isDebugEnabled()) log.debug("Semantic Dictionary: proxy requests for vector");
				
				list = retrieveProxOfVector(prox.getCv(), prox.getSize());
				// list = dictionary.getProx(prox.getCv(), prox.getSize());

				if (log.isDebugEnabled()) {
					if (list == null) {
						log.debug("Proxy list null for vector");
					} else {
						log.debug("Proxy list: "+list.toString());
					}
				}
				prox.setProxList(list);
				prox.setEnd(true);
				getBlackboardService().publishChange(prox);
			} catch (RuntimeException e) {
				if (log.isErrorEnabled()) log.error("Caught an Exception while upating vectorial base.", e);
				throw e;
			}
		}
	}

	protected abstract ArrayList<String> retrieveProxOfVector(ConceptualVector vect, int size);
	
	protected boolean validLanguage(String lg){
		String code = ISO639_3.sharedInstance.getIdCode(lg);
		
		if (code !=null){
			for (int i=0;i<languageList.length;i++){
				if (code.equals(languageList[i])) {
					return true;
				}
			}
		}
		
		return false;
	}
}
