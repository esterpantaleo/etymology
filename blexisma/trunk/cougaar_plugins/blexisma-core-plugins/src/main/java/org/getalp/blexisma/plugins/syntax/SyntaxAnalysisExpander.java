package org.getalp.blexisma.plugins.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;
import org.getalp.blexisma.cougaarcom.SemanticJob;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;
import org.getalp.dbnary.WiktionaryExtractor;

public class SyntaxAnalysisExpander extends ComponentPlugin {

	private IncrementalSubscription learning_jobs;
	private IncrementalSubscription analysis_jobs;
	private IncrementalSubscription responses;
	private LoggingService log;
	private UIDService uids;

	
	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#load()
	 */
	@Override
	public void load() {
		super.load();
		log = (LoggingService) getServiceBroker().getService(this, LoggingService.class, null);
	    uids = (UIDService) getServiceBroker().getService(this, UIDService.class, null);
	}

	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#unload()
	 */
	@Override
	public void unload() {
		super.unload();
		getServiceBroker().releaseService(this, LoggingService.class, log);
		getServiceBroker().releaseService(this, UIDService.class, uids);
	}

	@Override
	protected void setupSubscriptions() {
		learning_jobs = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(LEARNING_JOB_PREDICATE));
		analysis_jobs = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(ANALYSIS_JOB_PREDICATE));
		responses = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(createResponsePredicate()));
		if (log.isDebugEnabled()) log.debug("Analysis Expander online.");
	}

	private UnaryPredicate createResponsePredicate() {
		return new UnaryPredicate() {

			private static final long serialVersionUID = -7314981514961971056L;

			@Override
			public boolean execute(Object o) {
				if (o instanceof SyntacticAnalysisJob) {
			          return ((SyntacticAnalysisJob) o).getTree() != null;
			        }
				return false;
			}
		};
	}

	private static final UnaryPredicate LEARNING_JOB_PREDICATE = new UnaryPredicate(){
		private static final long serialVersionUID = -8370573398486709314L;
		public boolean execute(Object o) {
			if (o instanceof SemanticLearningJob) {
				SemanticLearningJob sp = (SemanticLearningJob)o;
				return (sp.getPhase() == SemanticJobPhase.WAITINGFORSYNTAX);
			} else {
				return false;
			}

		}
	};

	private static final UnaryPredicate ANALYSIS_JOB_PREDICATE = new UnaryPredicate(){
		private static final long serialVersionUID = 3762845053218791524L;
		public boolean execute(Object o) {
			if (o instanceof SemanticAnalysisJob) {
				SemanticAnalysisJob sp = (SemanticAnalysisJob)o;
				return (sp.getPhase() == SemanticJobPhase.WAITINGFORSYNTAX);
			} else {
				return false;
			}

		}
	};
	
	private static final UnaryPredicate EXPANSION_PREDICATE = new UnaryPredicate(){
		private static final long serialVersionUID = 360873305965494438L;
		public boolean execute(Object o) {
			return (o instanceof SyntacticAnalysisExpansion); 	 
		}
	};

	@Override
	protected void execute() {
		for (Iterator<?> iter = responses.getChangedCollection().iterator(); iter.hasNext();) {
			SyntacticAnalysisJob job = (SyntacticAnalysisJob) iter.next();
			handleResponse(job);
		}

		for (Iterator<?> iter = learning_jobs.getChangedCollection().iterator(); iter.hasNext();) {
			SemanticLearningJob job = (SemanticLearningJob) iter.next();
			handleNewLearningJob(job);
		}
		
		for (Iterator<?> iter = analysis_jobs.getAddedCollection().iterator(); iter.hasNext();) {
			SemanticAnalysisJob job = (SemanticAnalysisJob) iter.next();
			handleNewAnalysisJob(job);
		}
	}

	private void handleNewAnalysisJob(SemanticAnalysisJob job) {
		// Create a new syntactic analysis sub task for this job
		if (log.isDebugEnabled()) log.debug("New Analysis Job with: " + job.getData());
		SyntacticAnalysisExpansion expansion = new SyntacticAnalysisExpansion();
		job.setPhase(SemanticJobPhase.WAITINGFORSYNTAXREBUILD);
		expansion.setSemJob(job);
		String text = job.getData();
		SyntacticAnalysisJob sjob = createAnalysisJob(job, text);
		expansion.addTask(sjob, "");
		blackboard.publishChange(job);
		blackboard.publishAdd(sjob);
		blackboard.publishAdd(expansion);
	}

	private void handleNewLearningJob(SemanticLearningJob job) {
		// Create a new analysis sub task for each definition then post them and the expansion
		if (log.isDebugEnabled()) log.debug("New Learning Job for "+job.getId()+" ("+job.getSemDefinition().getSenseList().size()+" senses).");
		
		ArrayList<Sense> senses = job.getSemDefinition().getSenseList();
		
		if (senses.size() == 0) {
			job.setTrees(new HashMap<String, AnalysisTree>());
			job.setPhase(SemanticJobPhase.WAITINGFORDECORATION);
			blackboard.publishChange(job);
			return;
		}
		
		SyntacticAnalysisExpansion expansion = new SyntacticAnalysisExpansion();
		job.setPhase(SemanticJobPhase.WAITINGFORSYNTAXREBUILD);
		expansion.setSemJob(job);
		for (int i = 0; i < senses.size(); i++) {
			String id = senses.get(i).getNetworkDef();
			if (log.isDebugEnabled()) log.debug("Creating new analysis task for: " + id);
			String def = id;
			try {
				int defPosition = id.indexOf("|");
				if (defPosition != -1) {
					def = id.substring(defPosition + 1);
					def = WiktionaryExtractor.convertToHumanReadableForm(def);
				}
				SyntacticAnalysisJob sjob = createAnalysisJob(job, def);
				expansion.addTask(sjob, id);
			} catch (RuntimeException e) {
				if (log.isDebugEnabled()) {
					log.debug("Thrown runtime exception " + e);
					log.debug("while handling def: " + def);
				}
				throw e;
			}
		}
		// publish the tasks and then publish the expansion
		blackboard.publishChange(job);
		publishAnalysisJobs(expansion);
	}

	private void publishAnalysisJobs(SyntacticAnalysisExpansion expansion) {
		for (Entry<SyntacticAnalysisJob, String> jobentry : expansion.getTasks().entrySet()) {
			blackboard.publishAdd(jobentry.getKey());
		}
		if (log.isDebugEnabled()) log.debug("Published " +expansion.getTasks().size() + " learning_jobs and their associated expansion.");
		blackboard.publishAdd(expansion);		
	}

	private SyntacticAnalysisJob createAnalysisJob(SemanticJob job, String def) {
		// TODO: maybe we can compute the parser type if we have several of them for 1 language
		return new SyntacticAnalysisJob(uids.nextUID(), job.getLang(), def);
	}

	private void handleResponse(SyntacticAnalysisJob subtask) {
		if (log.isDebugEnabled()) {
			log.debug("Received response for "+subtask.getText());
		}

		// Get all info to process
		SyntacticAnalysisExpansion expansion = getExpansion(subtask);
		SemanticJob parentTask = expansion.getSemJob();
		if (parentTask instanceof SemanticAnalysisJob) {
			handleResponse(expansion, subtask, (SemanticAnalysisJob) parentTask);
		} else if (parentTask instanceof SemanticLearningJob) {
			handleResponse(expansion, subtask, (SemanticLearningJob) parentTask);
		} else {
			throw new RuntimeException("Unknown Semantic Job class");
		}

		
	}

	private void handleResponse(SyntacticAnalysisExpansion expansion,
			SyntacticAnalysisJob subtask, SemanticAnalysisJob parentTask) {
		parentTask.setTree(subtask.getTree());
		parentTask.setPhase(SemanticJobPhase.WAITINGFORDECORATION);
		blackboard.publishChange(parentTask);
		blackboard.publishRemove(expansion);
		blackboard.publishRemove(subtask);
	}

	private void handleResponse(SyntacticAnalysisExpansion expansion, SyntacticAnalysisJob subtask, SemanticLearningJob parentTask) {
		String id = expansion.getTasks().get(subtask);
		
		// Add result to parent Job
		HashMap<String, AnalysisTree> trees = parentTask.getTrees();
		if (null == trees) {
			trees = new HashMap<String,AnalysisTree>();
			parentTask.setTrees(trees);
		}
		trees.put(id, subtask.getTree());
		blackboard.publishChange(parentTask);

		// Remove the subtask from the blackboard
		blackboard.publishRemove(subtask);

		// Remove subtask from expansion
		expansion.getTasks().remove(subtask);
		// Finalize the expansion if no more tasks are pending.
		if (expansion.getTasks().size() == 0) {
			parentTask.setPhase(SemanticJobPhase.WAITINGFORDECORATION);
			blackboard.publishChange(parentTask);
			blackboard.publishRemove(expansion);
		} else {
			blackboard.publishChange(expansion);
		}
	}

	@SuppressWarnings("unchecked")
	private SyntacticAnalysisExpansion getExpansion(SyntacticAnalysisJob task) {
		Collection<SyntacticAnalysisExpansion> exps = blackboard.query(EXPANSION_PREDICATE);
		for (SyntacticAnalysisExpansion exp : exps) {
			if (exp.getTasks().containsKey(task)) return exp;
		}
		return null;
	}

}
