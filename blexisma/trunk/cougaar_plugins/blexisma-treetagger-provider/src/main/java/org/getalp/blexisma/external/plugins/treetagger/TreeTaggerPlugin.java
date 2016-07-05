package org.getalp.blexisma.external.plugins.treetagger;

import java.util.Enumeration;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;
import org.getalp.blexisma.externals.treetagger.TreeTagger;
import org.getalp.blexisma.externals.treetagger.TreeTaggerAnalysisTree;
import org.getalp.blexisma.externals.treetagger.TreeTaggerAnalysisTree.Node;
import org.getalp.blexisma.externals.treetagger.XMLTreeTaggerOutputFormatter;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.syntaxanalysis.treetagger.TreeTaggerAnswerParser;
import org.getalp.blexisma.syntaxanalysis.treetagger.TreeTaggerParsingException;

public class TreeTaggerPlugin extends ComponentPlugin {
	public static final String LANGUAGE_OPTION="lang";
	public static final String PATH_OPTION="path";
	
	private IncrementalSubscription syntaxJobs;
	private TreeTagger ttagger = null;
	private String lang = null;

	private LoggingService log;
	//private long numberOfParsedSentences = 0;

	/**
	 * @param o : arguments for the plugin should be : the complete path to sygfran,
	 * the complete path to param file for sygfran, the complete path to the runtime
	 * directory
	 * */
	public void setParameter(Object o) {
		Arguments args = new Arguments(o);
		ttagger = new TreeTagger(args.getString(PATH_OPTION));
		lang = ISO639_3.sharedInstance.getIdCode(args.getString(LANGUAGE_OPTION));
	}
	
	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * SygfranRequest objects
	 * */
	protected void setupSubscriptions() {
		log = getServiceBroker().getService(this, LoggingService.class, null);
		syntaxJobs = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSyntacticAnalysisPredicate()));
		if (null == lang || null == ttagger) {
			if (log.isErrorEnabled())
				log.error("Tree Tagger parser must be provided a \"path\" and a \"lang\" parameter.");
			throw new RuntimeException("Tree Tagger parser must be provided a \"path\" and a \"lang\" parameter.");
		}
		if (log.isShoutEnabled())
			log.shout("Tree Tagger syntax parser online for language: " + lang);
	}
	
	private UnaryPredicate createSyntacticAnalysisPredicate() {
		return new UnaryPredicate(){

			private static final long serialVersionUID = -4493909843368197583L;

			@Override
			public boolean execute(Object o) {
				if (o instanceof SyntacticAnalysisJob) {
					SyntacticAnalysisJob job = (SyntacticAnalysisJob)o;
					return (lang.equals(ISO639_3.sharedInstance.getIdCode(job.getLang())));
				} else 
					return false;
			}
		};
	}

	/**
	   * Called when there is a change on my subscription(s).
	   * This plugin will publish on the blackboard the matching SygfranAnswer
	   */
	protected void execute() {
		parse();
	}
	
	@SuppressWarnings("unchecked")
	private void parse() {
		Enumeration<SyntacticAnalysisJob> new_requests = syntaxJobs.getAddedList();
		while (new_requests.hasMoreElements()) {
			SyntacticAnalysisJob job = new_requests.nextElement();
			if (log.isDebugEnabled())
				log.debug("Tree Tagger [" + lang +"]: receiving analysis job");
			BasicAnalysisTree res;
			try {
				Node root = TreeTaggerAnalysisTree.parse(ttagger.call(job.getText()));
				String xml = XMLTreeTaggerOutputFormatter.xmlFormat(root);
				res = TreeTaggerAnswerParser.treeTaggerToTree(xml, lang);
			if (log.isDebugEnabled())
				log.debug("Tree Tagger: analysis complete posting for tree building");
			} catch (TreeTaggerParsingException e) {
				if (log.isWarnEnabled()) {
					log.warn("Got a sygfran Parser Error ");
				}
				res = BasicAnalysisTree.errorTreeFactory();
			}
			job.setTree(res);
			getBlackboardService().publishChange(job);
		}
	}
	
	
}
