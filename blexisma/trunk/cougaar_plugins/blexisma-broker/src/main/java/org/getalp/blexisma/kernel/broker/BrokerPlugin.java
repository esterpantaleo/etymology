/**
 * *
 * BrokerPlugin.java
 * Created on 28 févr. 2010 15:02:13
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.broker;

import java.util.Iterator;
import java.util.Vector;

import org.getalp.blexisma.kernel.broker.predicates.PluginInfo;

import org.cougaar.bootstrap.SystemProperties;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;

/**
 * @author Didier SCHWAB
 *
 */
public final class BrokerPlugin extends ComponentPlugin {

    private static final boolean DEFAULT_VERBOSE =
	SystemProperties.getBoolean(
		"kernel.brokerPlugin.BrokerPlugin.verbose", true);

    private IncrementalSubscription sub;
    private Vector<?>[][] plugins;
    private boolean verbose;
    protected LoggingService log;

    public void load() {
	super.load();

	// Parse our plugin parameters
	Arguments args = new Arguments(getParameters());
	verbose = args.getBoolean("verbose", DEFAULT_VERBOSE);
	// Get our required Cougaar services
	log = (LoggingService)
	getServiceBroker().getService(this, LoggingService.class, null);
	plugins = new Vector<?>[5][5];
	if(verbose){

	    log.shout("load OK");
	}
    }

    /** This method is called whenever a subscription changes. */
    protected void execute() {

	if(verbose){

	    log.shout("execute");
	}

	// Observe changed relays by looking at our subscription's change list
	if (sub.hasChanged()) {

	    if(verbose){
		log.shout("sub has changed");
	    }
	    for (Iterator<?> iter = sub.getAddedCollection().iterator();
	    iter.hasNext();
	    ) {
		SimpleRelay SR = (SimpleRelay) iter.next();
		AnswerQuery(SR);
	    }
	}
	if(verbose){

	    log.shout("execute OK");
	}
    }

    private void AnswerQuery(SimpleRelay relay) {

	Object query = relay.getQuery();
	if(query instanceof PluginInfo){
	    PluginInfo PI = (PluginInfo) query;		
	    updatePlugins(PI);
	}
	else{

	    if(verbose){

		log.shout("Unknown Query : " + query);
	    }
	}
    }

    private void updatePlugins(PluginInfo PI) {

	if(plugins[PI.language][PI.role]==null){

	    plugins[PI.language][PI.role] = new Vector<MessageAddress>(5);
	}

	Vector<MessageAddress> V = (Vector<MessageAddress>) plugins[PI.language][PI.role];

	V.add(PI.agentAddress);

	if(verbose){

	    log.shout("Update : " + PI);
	}

    }

    @Override
    protected void setupSubscriptions() {

	sub = (IncrementalSubscription) blackboard.subscribe(createPredicate());

	if(verbose){

	    log.shout("setupSubscriptions OK");
	}
    }

    /** Create our subscription filter */
    private UnaryPredicate createPredicate() {
	// Matches any relay sent to our agent
	return new UnaryPredicate() {
	    public boolean execute(Object o) {
		return
		((o instanceof SimpleRelay) &&
			agentId.equals(((SimpleRelay) o).getTarget()));
	    }
	};
    }

}
