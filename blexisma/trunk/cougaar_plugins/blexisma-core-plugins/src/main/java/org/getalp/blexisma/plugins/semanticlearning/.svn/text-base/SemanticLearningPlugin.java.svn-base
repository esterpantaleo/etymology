package org.getalp.blexisma.plugins.semanticlearning;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.cougaar.core.agent.service.alarm.AlarmBase;
import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.blackboard.TodoSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.core.util.UID;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.SemanticLearningMethod;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.cougaarcom.BaseUpdateJob;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.semanalysis.ContextualizingSemanticAnalysis;
import org.getalp.blexisma.semanalysis.SimpleSemanticLearning;

/**
 * @author Alexandre Labadié
 * */
public class SemanticLearningPlugin extends ComponentPlugin
{
	private static long TIMERDELAY = 2000;
	private static final int COMPONENT_ID = SemanticLearningPlugin.class.getCanonicalName().hashCode();

	private IncrementalSubscription endLearning;
	private long numberOfComputedVectors = 0;
	private LoggingService log;
	private int nbOfParallelJobs;
	private UIDService uids;
	private MyAlarm currentTimer;
	private TodoSubscription expiredAlarms;
	
	private String semanticLearningClassName = "org.getalp.blexisma.semanalysis.SimpleSemanticLearning";
	protected SemanticLearningMethod learner;
	
	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#setParameter(java.lang.Object)
	 */
	@Override
	public void setParameter(Object param) {
		// TODO Auto-generated method stub
		super.setParameter(param);
		Arguments args = new Arguments(param);
		
		
		nbOfParallelJobs = args.getInt("numberOfParallelJobs");
		if (nbOfParallelJobs == -1) nbOfParallelJobs = 1;
		currentTimer = null;
		
		this.semanticLearningClassName = args.getString("learnerClass", semanticLearningClassName);

	}


	
	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#load()
	 */
	@Override
	public void load() {
		super.load();
	    uids = (UIDService) getServiceBroker().getService(this, UIDService.class, null);
		log = getServiceBroker().getService(this, LoggingService.class, null);
	}



	/* (non-Javadoc)
	 * @see org.cougaar.core.blackboard.BlackboardClientComponent#unload()
	 */
	@Override
	public void unload() {
		// TODO Auto-generated method stub
		super.unload();
		getServiceBroker().releaseService(this, UIDService.class, uids);
		getServiceBroker().releaseService(this, LoggingService.class, log);
	}



	/**
	 * Called when the Plugin is loaded.  Establish the subscriptions 
	 * */
	protected void setupSubscriptions() {
		endLearning = (IncrementalSubscription)blackboard.subscribe(new DeltaSubscription(createFinishedSemanticLearningPredicate()));
		expiredAlarms = (TodoSubscription)blackboard.subscribe(new TodoSubscription("myAlarms"));
		
		try {
			if (log.isDebugEnabled()) {
				log.debug("Analyser class name = " + semanticLearningClassName);
			}
			learner = (SemanticLearningMethod) Class.forName(semanticLearningClassName).newInstance();
		} catch (InstantiationException e) {
			if (log.isErrorEnabled()) {
				log.error("InstanciationException while creating requested learner.", e);
				log.error("Using SimpleSemanticLearning");
			}
			learner = new SimpleSemanticLearning();
		} catch (IllegalAccessException e) {
			if (log.isErrorEnabled()) {
				log.error("IllegalAccessException while creating requested learner.", e);
				log.error("Using SimpleSemanticLearning");
			}
			learner = new SimpleSemanticLearning();
		} catch (ClassNotFoundException e) {
			if (log.isErrorEnabled()) {
				log.error("ClassNotFoundException while creating requested learner.", e);
				log.error("Using SimpleSemanticLearning");
			}
			learner = new SimpleSemanticLearning();
		}
		if (log.isDebugEnabled()) {
			log.debug("Learner   = " + learner);	
		}
		
		// TODO: I think the first learninJob should be created by the execute method if there is no previous job in the blackboard.
		for (int i = 0; i < nbOfParallelJobs; i++)
			blackboard.publishAdd(new SemanticLearningJob(uids.nextUID(), COMPONENT_ID));
		if (log.isShoutEnabled()) {
			log.shout("Semantic Learning online");
			log.shout("Semantic Learning: Starting new semantic learning");
		}
	}
	

	/**
	 * 
	 * */
	protected void execute() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> new_end = endLearning.getChangedList();
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> deleted_jobs = endLearning.getRemovedList();
		
		Vector<SemanticLearningJob> del = new Vector<SemanticLearningJob>();

		if (log.isDebugEnabled()) log.debug("========  execute =============");
		while (deleted_jobs.hasMoreElements()) {
			SemanticLearningJob spTmp = deleted_jobs.nextElement();
			del.add(spTmp);
			if (log.isDebugEnabled()) log.debug(""+spTmp+" has been deleted.");
		}
		
		if (expiredAlarms.hasChanged()) {
		      for (Iterator<?> iter = expiredAlarms.getAddedCollection().iterator();
		          iter.hasNext();
		          ) {
		        MyAlarm alarm = (MyAlarm)iter.next();
		        if (alarm.getUid() == currentTimer.getUid()) {
		        	if (log.isDebugEnabled()) log.debug("Timed job launch");
			        SemanticLearningJob newJob = new SemanticLearningJob(uids.nextUID(), COMPONENT_ID);
			        blackboard.publishAdd(newJob);
		        }
		      }
		}

		while (new_end.hasMoreElements()) {
			SemanticLearningJob spTmp = null;
			ArrayList<Sense> vectors = null;
			HashMap<String,ConceptualVector> packet = null;
			ConceptualVector barycentre = null;
			
			if (log.isDebugEnabled()) 
				log.debug("All definition parsed, computing new vector.");
			spTmp = new_end.nextElement();
			if (!del.contains(spTmp)) {
				numberOfComputedVectors++;
				
				packet = new HashMap<String,ConceptualVector>();
				barycentre = learner.learn(spTmp.getSemDefinition(), spTmp.getVectSize(), spTmp.getVectNorm());
				
				vectors = spTmp.getSemDefinition().getSenseList();
				for (int i=0;i<vectors.size();i++) {
					packet.put(vectors.get(i).getBaseId(), vectors.get(i).getVector());
				}
				
				if (barycentre != null) {
					packet.put(spTmp.getId(), barycentre);
				}
				
				if (numberOfComputedVectors % 1000 == 0)
					if (log.isInfoEnabled()) 
						log.info("Computed " + numberOfComputedVectors + " since last restart.");
				blackboard.publishAdd(new BaseUpdateJob(spTmp.getLang(),packet));
				SemanticLearningJob newJob = new SemanticLearningJob(uids.nextUID(), COMPONENT_ID);
				blackboard.publishRemove(spTmp);
				if (log.isDebugEnabled()) log.debug("Deleting "+spTmp);
				currentTimer = new MyAlarm(System.currentTimeMillis()+TIMERDELAY,uids.nextUID());
				getAlarmService().addRealTimeAlarm(currentTimer);
				blackboard.publishAdd(newJob);
				if (log.isDebugEnabled()) log.debug("Publishing "+newJob);
			}
		}
		
		if (log.isDebugEnabled()) log.debug("======== /execute =============");

	}
	
	private UnaryPredicate createFinishedSemanticLearningPredicate() {
		return new UnaryPredicate(){
			private static final long serialVersionUID = 3330143783224590543L;
			public boolean execute(Object o) {
				boolean pred = false;
				SemanticLearningJob lp = null;

				if (o instanceof SemanticLearningJob) {
					lp = (SemanticLearningJob)o;
					pred = (lp.getPhase()==SemanticJobPhase.DONE) && lp.getRequestingAgent()==COMPONENT_ID;
				}

				return pred;
			}
		};
	}
	
	private class MyAlarm extends AlarmBase {
		private final UID uid;
	    public MyAlarm(long futureTime, UID uid) {
	      super(futureTime);
	      this.uid = uid;
	    }
	    
	    public void onExpire() {
	      expiredAlarms.add(this);
	    }

		public UID getUid() {
			return uid;
		}
	  }
}
