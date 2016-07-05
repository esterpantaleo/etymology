package org.getalp.blexisma.plugins.syntax;

import java.util.HashMap;
import java.util.Iterator;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.relay.SimpleRelaySource;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;

/**
 * @author Alexandre Labadié
 * */
public class SyntaxAnalysisDispatcher extends ComponentPlugin{
	private HashMap<MessageAddress,Integer> targets;
	private IncrementalSubscription jobs;
	private IncrementalSubscription relays;
	private LoggingService log;
	private UIDService uids;
	private String lang;
	
	public void load(){
		super.load();
		Arguments args = new Arguments(getParameters(), getClass());
	    String targets_string = args.getString("targets");
	    lang = ISO639_3.sharedInstance.getIdCode(args.getString("language"));
	    
	    log = (LoggingService) getServiceBroker().getService(this, LoggingService.class, null);
	    uids = (UIDService) getServiceBroker().getService(this, UIDService.class, null);
	    targets = parseTargets(targets_string);
	}
	
	protected void setupSubscriptions() {
		jobs = (IncrementalSubscription) blackboard.subscribe(createJobPredicate());
		relays = (IncrementalSubscription) blackboard.subscribe(new DeltaSubscription(createRelayPredicate()));
		if (log.isDebugEnabled()) log.debug(lang+" Analysis Dispatcher online.");
		
	}
	
	protected void execute() {
		for (Iterator<?> iter = jobs.getAddedCollection().iterator(); iter.hasNext();) {
			SyntacticAnalysisJob job = (SyntacticAnalysisJob) iter.next();
			sendJob(job);
		}
		
		for (Iterator<?> iter = relays.getChangedCollection().iterator(); iter.hasNext();) {
			SimpleRelay relay = (SimpleRelay) iter.next();
			handleResponse(relay);
		}
	}
	
	private void sendJob(SyntacticAnalysisJob j) {
		SimpleRelay relay = new SimpleRelaySource(uids.nextUID(), agentId, chooseNextTarget(), j);
		
		if (log.isDebugEnabled()) {
		      log.debug("Sending syntax analysis job to "+relay.getTarget()+" nb task:"+targets.get(relay.getTarget()));
		}
		blackboard.publishAdd(relay);
	}
	
	private void handleResponse(SimpleRelay relay) {

		@SuppressWarnings("unchecked")
		Iterator<SyntacticAnalysisJob> iter =  jobs.getCollection().iterator();
		SyntacticAnalysisJob tmpj = null;
		SyntacticAnalysisJob job = null;
	    if (log.isDebugEnabled()) {
	    	log.debug("Received response from "+relay.getTarget());
	    }
	    
	    tmpj = (SyntacticAnalysisJob)relay.getReply();
	    
	    do {
	    	job = iter.next();
	    } while (!job.getUID().equals(tmpj.getUID()));
	    
	    job.setTree(tmpj.getTree());
	    job.setParserType(tmpj.getParserType());
	    
	    targets.put(relay.getTarget(),targets.get(relay.getTarget())-1);
	    blackboard.publishChange(job);
	    blackboard.publishRemove(relay);
	  }
	
	private HashMap<MessageAddress,Integer> parseTargets(String s) {
	    String[] targetList = s.split(",");
	    MessageAddress target = null;
	    HashMap<MessageAddress,Integer> retList = new HashMap<MessageAddress,Integer>();
	    for (int i=0; i< targetList.length; i++) {
	    	target = MessageAddress.getMessageAddress(targetList[i]);
	    	if (target == null) {
	    	      throw new IllegalArgumentException("Must specify a target");
	    	    } else if (target.equals(agentId)) {
	    	      throw new IllegalArgumentException("Target matches self: "+target);
	    	    }
	    	retList.put(target,0);
	    }
	    return retList;
	  }
	
	private MessageAddress chooseNextTarget() {
		Iterator<MessageAddress> iter = targets.keySet().iterator();
		MessageAddress nextTarget = iter.next();
		MessageAddress tmpTarget = null;
		int minVal = targets.get(nextTarget);
		
		while (iter.hasNext())
		{
			tmpTarget = iter.next();
			if (minVal>targets.get(tmpTarget)) {
				minVal = targets.get(tmpTarget);
				nextTarget = tmpTarget;
			}
		}
		
		targets.put(nextTarget, minVal+1);
		
		return nextTarget;
	}
	
	private UnaryPredicate createJobPredicate() {
		return new UnaryPredicate() {
			private static final long serialVersionUID = -6423046106041455689L;
			public boolean execute(Object o) {
				if (o instanceof SyntacticAnalysisJob) {
					SyntacticAnalysisJob j = (SyntacticAnalysisJob)o;
					return (j.getText() != null)&&(j.getLang().equals(lang));
				}
				return false; 	 
			}
		};
	}
	
	private UnaryPredicate createRelayPredicate() {
		return new UnaryPredicate() {
			private static final long serialVersionUID = 9081341133989758405L;
		    public boolean execute(Object o) {
		    	if (o instanceof SimpleRelay) {
		    		SimpleRelay relay = (SimpleRelay) o;
		    		if (agentId.equals(relay.getSource())&&(relay.getReply() instanceof SyntacticAnalysisJob)) {
		    			return true;
		    		}
		    	}
		    	return false;
		    }
		};
	}
}
