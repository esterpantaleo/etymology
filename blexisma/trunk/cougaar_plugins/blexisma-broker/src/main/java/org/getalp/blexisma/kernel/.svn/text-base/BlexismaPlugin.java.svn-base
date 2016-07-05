package org.getalp.blexisma.kernel;

import java.util.Iterator;

import org.getalp.blexisma.kernel.broker.Language;
import org.getalp.blexisma.kernel.broker.Role;
import org.getalp.blexisma.kernel.broker.predicates.PluginInfo;

import org.cougaar.bootstrap.SystemProperties;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.relay.SimpleRelaySource;
import org.cougaar.core.service.BlackboardService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;

/**
 * *
 * BlexismaPlugin.java
 * Created on 27 févr. 2010 18:21:29
 * 
 * Copyright (c) 2010 Didier Schwab
 */

/**
 * @author Didier SCHWAB
 * 
 */
public abstract class BlexismaPlugin extends ComponentPlugin {

	public static final boolean DEFAULT_VERBOSE = SystemProperties.getBoolean(
			"kernel.BlexismaPlugin.verbose", true);

	protected int language;
	protected int role;
	protected LoggingService log;
	protected UIDService uids;
	protected BlackboardService myBBService; // BlackBoard Services
	protected boolean verbose;
	private MessageAddress broker;
	protected IncrementalSubscription sub;

	/** This method is called when the agent is created */
	@Override
	public void load() {
		super.load();

		// Get our required Cougaar services
		log = (LoggingService) getServiceBroker().getService(this,
				LoggingService.class, null);
		uids = (UIDService) getServiceBroker().getService(this,
				UIDService.class, null);
		myBBService = this.getBlackboardService();

		// Parse our plugin parameters
		Arguments args = new Arguments(getParameters());
		verbose = args.getBoolean("verbose", DEFAULT_VERBOSE);

		String broker_name = args.getString("broker", null);
		broker = MessageAddress.getMessageAddress(broker_name);
		if (broker == null) {
			throw new IllegalArgumentException("Must specify a broker");
		}

		String language = args.getString("language", null);
		if (language == null) {
			throw new IllegalArgumentException("Must specify a language");
		} else if (Language.convert(language) == -1) {
			throw new IllegalArgumentException("Language \"" + language
					+ "\" doesn't exist in kernel.brokerPlugin.Language");
		}
		this.language = Language.convert(language);

		String role = args.getString("role", null);
		if (role == null) {
			throw new IllegalArgumentException("Must specify a role");
		} else if (Role.convert(role) == -1) {
			throw new IllegalArgumentException("Role \"" + role
					+ "\" doesn't exist in kernel.brokerPlugin.Role");
		}

		this.role = Role.convert(role);

		if (verbose) {

			log.shout("load OK");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.plugin.ComponentPlugin#execute()
	 */
	@Override
	protected void execute() {

		if (verbose) {
			log.shout("execute");
		}

		// Observe changed relays by looking at our subscription's change list
		if (sub.hasChanged()) {

			if (verbose) {
				log.shout("sub has changed");
			}
			for (Iterator<?> iter = sub.getAddedCollection().iterator(); iter
					.hasNext();) {
				SimpleRelay SR = (SimpleRelay) iter.next();
				AnswerQuery(SR);
			}
		}
		if (verbose) {

			log.shout("execute OK");
		}
	}

	protected abstract void AnswerQuery(SimpleRelay relay);

	/** This method is called when the agent starts. */
	@Override
	protected void setupSubscriptions() {

		sub = (IncrementalSubscription) blackboard.subscribe(createPredicate());

		sendBroker(null, new PluginInfo(language, role, this.agentId));

		if (verbose) {
			log.shout("setupSubscriptions OK");
		}
	}

	/** Send our next relay iteration now */
	protected void sendBroker(SimpleRelay brokerRelay, PluginInfo PI) {
		if (brokerRelay != null) {
			// Remove query both locally and at the remote target, to cleanup
			// the blackboard.
			blackboard.publishRemove(brokerRelay);
		}

		// Send a new relay to the target
		SimpleRelay relay = new SimpleRelaySource(uids.nextUID(), agentId,
				broker, PI);

		if (verbose && log.isShoutEnabled()) {
			log.shout("Sending PI \"" + PI + "\" to " + broker);
		}
		blackboard.publishAdd(relay);
	}

	/** Create our subscription filter */
	protected UnaryPredicate createPredicate() {
		// Matches any relay sent to our agent
		return new UnaryPredicate() {
			/**
	     * 
	     */
			private static final long serialVersionUID = 7211031489982262592L;

			public boolean execute(Object o) {
				return ((o instanceof SimpleRelay) && agentId
						.equals(((SimpleRelay) o).getTarget()));
			}
		};
	}

}
