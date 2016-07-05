package org.getalp.blexisma.external.plugins.sygfran;

import java.util.Enumeration;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;
import org.getalp.blexisma.external.sygfranwrapper.structure.tree.SygMorphoSyntacticTree;
import org.getalp.blexisma.external.sygfranwrapper.tools.MorphoTreeToXML;
import org.getalp.blexisma.external.sygfranwrapper.tools.SygLocal;
import org.getalp.blexisma.external.sygfranwrapper.tools.SygParam;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranAnswerParser;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranParsingException;


/**
 * @author Alexandre Labadié
 * 
 * Plugin providing stanford analysis
 * */
public class SygfranPlugin extends ComponentPlugin
{
	private IncrementalSubscription syntaxJobs;
	private SygParam param;
	
	//private final static ParserType TYPE = ParserType.SYGFRAN;
	private final static String LANG = ISO639_3.sharedInstance.getIdCode("fra");

	private LoggingService log;
	//private long numberOfParsedSentences = 0;

	/**
	 * @param o : arguments for the plugin should be : the complete path to sygfran,
	 * the complete path to param file for sygfran, the complete path to the runtime
	 * directory
	 * */
	public void setParameter(Object o)
	{
		Arguments args = new Arguments(o);
		param = new SygParam(args.getString("sygpath"), args.getString("rulepath"), args.getString("execpath"), args.getInt("timeout", 600));
	}
	
	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * SygfranRequest objects
	 * */
	protected void setupSubscriptions() 
	{
		log = getServiceBroker().getService(this, LoggingService.class, null);
		syntaxJobs = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSyntacticAnalysisPredicate()));
		if (log.isShoutEnabled()){
			log.shout("Sygfran syntax parser online");
			log.shout("Applipath: "+param.getAppliPath());
			log.shout("Datapath: "+param.getDataPath());
			log.shout("Execpath: "+param.getExecPath());
			log.shout("Language: "+LANG);
		}
	}
	
	private UnaryPredicate createSyntacticAnalysisPredicate() {
		return new UnaryPredicate(){

			private static final long serialVersionUID = -4493909843368197583L;

			@Override
			public boolean execute(Object o) {
				if (o instanceof SyntacticAnalysisJob) {
					SyntacticAnalysisJob job = (SyntacticAnalysisJob)o;
					return (LANG.equals(ISO639_3.sharedInstance.getIdCode(job.getLang())));
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
		if (log.isDebugEnabled()){
			log.debug("Sygfran: parser activation");
		}
		while (new_requests.hasMoreElements())
		{
			SyntacticAnalysisJob job = new_requests.nextElement();
			if (log.isDebugEnabled())
				log.debug("Sygfran: receiving analysis job");
			AnalysisTree res;
			try {
				SygMorphoSyntacticTree tree = SygLocal.localAnalysis(job.getText(), param);
				String xml = MorphoTreeToXML.outputXML(tree);
				res = SygfranAnswerParser.sygfranToTree(xml);
			if (log.isDebugEnabled())
				log.debug("Sygfran: analysis complete posting for tree building");
			} catch (SygfranParsingException e) {
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
