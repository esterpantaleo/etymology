package org.getalp.blexisma.external.plugins.nosyntax;

import java.util.Enumeration;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;
import org.getalp.blexisma.syntaxanalysis.baseline.NoSyntaxAnalysis;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;


public class NoSyntaxPlugin extends ComponentPlugin {
	private IncrementalSubscription syntaxJobs;
	private LoggingService log;
	private EnglishLemmatizer lemmatizer;
	private String lang;
	@Override
	protected void execute() {
		@SuppressWarnings("unchecked")
		Enumeration<SyntacticAnalysisJob> new_requests = syntaxJobs.getAddedList();
		SyntacticAnalysisJob job = null;
		
		while (new_requests.hasMoreElements())
		{
			if (log.isDebugEnabled())
				log.debug("Nosyntax parser receveived tasks.");
			job = new_requests.nextElement();
			job.setTree(NoSyntaxAnalysis.buildBasicEnglishTree(job.getText(), lemmatizer));
			getBlackboardService().publishChange(job);
		}
	}

	@Override
	protected void setupSubscriptions() {
		log = getServiceBroker().getService(this, LoggingService.class, null);
		syntaxJobs = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSyntacticAnalysisPredicate()));
		if (log.isShoutEnabled())
			log.shout("Nosyntax parser online, language: "+lang);
	}
	
	/**
	 * @param o : arguments for the plugin should be : the complete path to Stanford data file,
	 * and the language associated
	 * */
	public void setParameter(Object o)
	{
		Arguments args = new Arguments(o);
		
		this.lang = ISO639_3.sharedInstance.getIdCode(args.getString("language"));
	    try {
			lemmatizer = new EnglishLemmatizer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private UnaryPredicate createSyntacticAnalysisPredicate() {
		return new UnaryPredicate(){

			private static final long serialVersionUID = -4493909843368197583L;

			@Override
			public boolean execute(Object o) {
				if (o instanceof SyntacticAnalysisJob) {
					SyntacticAnalysisJob job = (SyntacticAnalysisJob)o;
					return (lang.equals(job.getLang()));
				} else 
					return false;
			}
		};
	}
}
