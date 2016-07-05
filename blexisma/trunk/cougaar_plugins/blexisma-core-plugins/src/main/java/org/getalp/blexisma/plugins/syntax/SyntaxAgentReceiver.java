package org.getalp.blexisma.plugins.syntax;

import java.util.HashMap;
import java.util.Iterator;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.util.UID;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;

/**
 * @author Alexandre Labadié
 * */
public class SyntaxAgentReceiver extends ComponentPlugin{
	private IncrementalSubscription jobs;
	private IncrementalSubscription relays;
	private LoggingService log;
	@SuppressWarnings("unused")
	private MessageAddress target;
	private HashMap<UID,SimpleRelay> relayMemory;
	
	public void load(){
		super.load();
		Arguments args = new Arguments(getParameters(), getClass());
	    String target_string = args.getString("target");
	    
	    log = (LoggingService) getServiceBroker().getService(this, LoggingService.class, null);
	    target = MessageAddress.getMessageAddress(target_string);
	}
	
	protected void setupSubscriptions() {
		jobs = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(SYNTAXJOB_PREDICATE));
		relays = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(createRelayPredicate()));
		if (log.isShoutEnabled()) log.shout("Analysis Receiver online.");
		relayMemory = new HashMap<UID,SimpleRelay>();
		
	}
	
	protected void execute() {
		for (Iterator<?> iter = jobs.getChangedCollection().iterator(); iter.hasNext();) {
			SyntacticAnalysisJob job = (SyntacticAnalysisJob) iter.next();
			sendAnswer(job);
		}
		
		for (Iterator<?> iter = relays.getAddedCollection().iterator(); iter.hasNext();) {
			SimpleRelay relay = (SimpleRelay) iter.next();
			handleQuery(relay);
		}
	}
	
	private void handleQuery(SimpleRelay relay) {
		SyntacticAnalysisJob job = (SyntacticAnalysisJob)relay.getQuery();
		relayMemory.put(job.getUID(), relay);
		if (log.isDebugEnabled()) {
			log.debug("Publishing syntax analysis job "+job.getText()+" obtained from "+relay.getSource());
		}
		blackboard.publishAdd(job);
	}
	
	private void sendAnswer(SyntacticAnalysisJob job) {
		SimpleRelay relay = relayMemory.get(job.getUID());
		relay.setReply(job);
		relayMemory.remove(job.getUID());
		if (log.isDebugEnabled()) {
			log.debug("Returning syntax analysis job "+job.getTree().toString()+" to "+relay.getSource());
		}
		blackboard.publishChange(relay);
	}
	
	private static final UnaryPredicate SYNTAXJOB_PREDICATE = new UnaryPredicate(){
		private static final long serialVersionUID = -7991867307219676092L;
		public boolean execute(Object o) {
			if (o instanceof SyntacticAnalysisJob) {
				SyntacticAnalysisJob j = (SyntacticAnalysisJob)o;
				return j.getTree() != null;
			}
			return false; 	 
		}
	};
	
	private UnaryPredicate createRelayPredicate() {
		return new UnaryPredicate() {
			private static final long serialVersionUID = -3092438677701979938L;
			public boolean execute(Object o) {
		    	if (o instanceof SimpleRelay) {
		    		SimpleRelay relay = (SimpleRelay) o;
		    		if (agentId.equals(relay.getTarget())&&(relay.getQuery() instanceof SyntacticAnalysisJob)) {
		    			return true;
		    		}
		    	}
		    	return false;
		    }
		};
	}
}
