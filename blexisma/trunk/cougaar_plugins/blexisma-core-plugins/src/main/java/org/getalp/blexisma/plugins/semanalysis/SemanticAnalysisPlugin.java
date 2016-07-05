package org.getalp.blexisma.plugins.semanalysis;

import java.util.ArrayList;
import java.util.Enumeration;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.AnaTreeInfos;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.semanalysis.ContextualizingSemanticAnalysis;

/**
 * @author Alexandre Labadié
 * */
public class SemanticAnalysisPlugin extends ComponentPlugin{
	private IncrementalSubscription analysissemrequests;
	private IncrementalSubscription learningsemrequests;
	private String semanticAnalysisClassName = "org.getalp.blexisma.semanalysis.ContextualizingSemanticAnalysis";
	private SemanticAnalysisMethod analyser;
	
	private LoggingService log;

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
	 * WARNING: his is not called if no parameter is given. Hence, the default values should be called somewhere else.
	 * */
	@Override
	public void setParameter(Object o) {
		Arguments args = new Arguments(o);
		this.semanticAnalysisClassName = args.getString("analyserClass", "org.getalp.blexisma.semanalysis.ContextualizingSemanticAnalysis");
	}

	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * semantic analysis request
	 * */
	protected void setupSubscriptions() {
		analysissemrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSemanticAnalysisPredicate()));
		learningsemrequests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSemanticLearningPredicate()));
		
		try {
			if (log.isDebugEnabled()) {
				log.debug("Analyser class name = " + semanticAnalysisClassName);
			}
			analyser = (SemanticAnalysisMethod) Class.forName(semanticAnalysisClassName).newInstance();
			analyser.setParameter(ContextualizingSemanticAnalysis.STRONG_CONTEXTUALIZATION_DELTA, "0.1");
		} catch (InstantiationException e) {
			if (log.isErrorEnabled()) log.error("InstanciationException while creating requested analyser.", e);
			analyser = new ContextualizingSemanticAnalysis();
		} catch (IllegalAccessException e) {
			if (log.isErrorEnabled()) log.error("IllegalAccessException while creating requested analyser.", e);
			analyser = new ContextualizingSemanticAnalysis();
		} catch (ClassNotFoundException e) {
			if (log.isErrorEnabled()) log.error("ClassNotFoundException while creating requested analyser.", e);
			analyser = new ContextualizingSemanticAnalysis();
		}
		if (log.isDebugEnabled()) {
			log.debug("Analyser   = " + analyser);	
		}
		
		if (log.isShoutEnabled()) 
			log.shout("Semantic Analysis online");
	}
	
	/**
	 * 
	 * */
	protected void execute() {
		analysis();
		learning();
	}
	
	private void analysis() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticAnalysisJob> new_semrequests = analysissemrequests.getChangedList();
		SemanticAnalysisJob spTmp = null;
		AnalysisTree trTmp = null;
		AnaTreeInfos infTmp = null;
		SemanticDefinition defTmp = null;
		ConceptualVector context = null;
		
		while (new_semrequests.hasMoreElements())
		{	
			spTmp = new_semrequests.nextElement();
			if (log.isDebugEnabled()) {
				log.debug("Receiving analysis job.tree:");
				log.debug("÷n" + spTmp.getTree().toXmlString());
			}
			if (context == null) context = new ConceptualVector(spTmp.getVectSize(),spTmp.getVectNorm());
			trTmp = spTmp.getTree();
			infTmp = trTmp.getInfos();
			defTmp = infTmp.getDef();
			
			try {
				if (defTmp!=null) {
					if (!trTmp.isError()) {
						defTmp.setMainVector(analyser.computeConceptualVector(trTmp, context));
					} else { 
						defTmp.setMainVector(new ConceptualVector(spTmp.getVectSize(),spTmp.getVectNorm()));
					}
				}
			} catch (RuntimeException t) {
				if (log.isFatalEnabled()) {
					log.fatal("Problem while computing conceptual vector.", t);
					log.fatal(trTmp.toString());
				}
				throw t;
			}
			infTmp.setDef(defTmp);
			trTmp.setInfos(infTmp);
			spTmp.setTree(trTmp);
			spTmp.setData(spTmp.getTree().getInfos().getDef().getMainVector().toStringHexa());
			spTmp.setPhase(SemanticJobPhase.DONE);
			
			if (log.isDebugEnabled()) log.debug("Job complete posting final version");
			getBlackboardService().publishChange(spTmp);
		}
	}
	
	private void learning() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> new_semrequests = learningsemrequests.getChangedList();
		SemanticLearningJob spTmp = null;
		ArrayList<Sense> senseList = null;
		SemanticDefinition tmpDef = null;
		ConceptualVector context = null;
		AnalysisTree trTmp = null;
		
		while (new_semrequests.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Receiving learning job");
			spTmp = new_semrequests.nextElement();
			tmpDef = spTmp.getSemDefinition();
			senseList = tmpDef.getSenseList();
			if (context == null) context = new ConceptualVector(spTmp.getVectSize(),spTmp.getVectNorm());
			
			for (int i=0;i<senseList.size();i++) {
				trTmp = spTmp.getTrees().get(senseList.get(i).getNetworkDef());
				if (log.isDebugEnabled()) {
					log.debug("Receiving learning job. tree " + i + ":\n  ");
					log.debug("\n" + trTmp.toXmlString());
				}
				try {
					// TODO: Isn't it dangerous to put an empty vector in the base when the syntactic analysis failed ?
					// TODO: we should keep the previous vector in this case...
					if (!trTmp.isError()) senseList.get(i).setVector(analyser.computeConceptualVector(trTmp,context));
					else senseList.get(i).setVector(new ConceptualVector(spTmp.getVectSize(),spTmp.getVectNorm()));
				} catch (RuntimeException e) {
					if (log.isErrorEnabled()) {
						log.error("Caught an exception while computing vector.", e);
						if (senseList.get(i) != null)
							log.error(senseList.get(i).getNetworkDef());
						if (trTmp != null)
							log.error(trTmp.toString());
					}
					throw e;
				}
			}
			tmpDef.setSenseList(senseList);
			spTmp.setSemDefinition(tmpDef);
			spTmp.setPhase(SemanticJobPhase.DONE);
			getBlackboardService().publishChange(spTmp);
		}
	}
	
	private UnaryPredicate createSemanticLearningPredicate() {
		return new UnaryPredicate(){
			private static final long serialVersionUID = 5735388140851219563L;

			/**
			 * @param o : object to be tested by the predicate
			 * @return if the predicate is verified
			 * */
			public boolean execute(Object o)
			{
				boolean pred = false;
				SemanticLearningJob sp = null;
				
				if (o instanceof SemanticLearningJob)
				{
					sp = (SemanticLearningJob)o;
					pred = (sp.getPhase()==SemanticJobPhase.WAITINGFORSEMANTIC);
				}
				
				return pred;
			}
		};
	}

	private UnaryPredicate createSemanticAnalysisPredicate() {
		return new UnaryPredicate(){
			private static final long serialVersionUID = 8867992243382748969L;

			public boolean execute(Object o)
			{
				boolean pred = false;
				SemanticAnalysisJob sp = null;
				
				if (o instanceof SemanticAnalysisJob)
				{
					sp = (SemanticAnalysisJob)o;
					pred = (sp.getPhase()==SemanticJobPhase.WAITINGFORSEMANTIC);
				}
				
				return pred;
			}
		};
	}


}
