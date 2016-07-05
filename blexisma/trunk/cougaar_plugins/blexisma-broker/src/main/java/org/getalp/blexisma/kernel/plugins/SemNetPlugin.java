/**
 * *
 * SemNetPlugin.java
 * Created on 28 mars 2010 11:28:32
 * 
 * Copyright (c) 2010 Didier Schwab
 */
package org.getalp.blexisma.kernel.plugins;

import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.util.Arguments;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.kernel.BlexismaPlugin;
import org.getalp.blexisma.kernel.broker.predicates.PluginInfo;
import org.getalp.blexisma.semnet.RAM_SemanticNetwork;

/**
 * @author Didier SCHWAB
 * 
 */
public class SemNetPlugin extends BlexismaPlugin {

	private SemanticNetwork<String,String> semnet;

	/**
     * 
     */
	public SemNetPlugin() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kernel.BlexismaPlugin#load()
	 */
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();

		// Parse our plugin parameters
		Arguments args = new Arguments(getParameters());

		String semnetType = args.getString("type", null);

		if (semnetType == null) {
			throw new IllegalArgumentException(
					"Must specify a type of semantic network : now null");
		} else if (semnetType.equals("RAM")) {

			int size = args.getInt("size", -1);

			if (size == -1) {

				semnet = new RAM_SemanticNetwork();
				if (verbose) {

					log.shout("Plugin SemNetPlugin OK : no size");
				}
			} else {

				semnet = new RAM_SemanticNetwork(size);

				if (verbose) {

					log.shout("Plugin SemNetPlugin OK with size : " + size);
				}
			}
		} else {

			throw new IllegalArgumentException(
					"Must specify a correct type of semantic network : now "
							+ semnetType);
		}
	}

	/** This method is called whenever a subscription changes. */
	protected void execute() {
		super.execute();
	}

	protected void AnswerQuery(SimpleRelay relay) {

		Object query = relay.getQuery();
		if (query instanceof PluginInfo) {
			PluginInfo PI = (PluginInfo) query;
			// updatePlugins(PI);
		} else {

			if (verbose) {

				log.shout("Unknown Query : " + query);
			}
		}
	}
}
