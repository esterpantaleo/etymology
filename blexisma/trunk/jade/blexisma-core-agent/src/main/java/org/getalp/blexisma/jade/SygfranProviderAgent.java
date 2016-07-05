package org.getalp.blexisma.jade;

import static org.getalp.blexisma.jade.BlexismaJadeOntology.*;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.external.sygfranwrapper.structure.tree.SygMorphoSyntacticTree;
import org.getalp.blexisma.external.sygfranwrapper.tools.MorphoTreeToXML;
import org.getalp.blexisma.external.sygfranwrapper.tools.SygLocal;
import org.getalp.blexisma.external.sygfranwrapper.tools.SygParam;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranAnswerParser;
import org.getalp.blexisma.syntaxanalysis.sygfran.SygfranParsingException;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.ExtendedProperties;

public class SygfranProviderAgent extends Agent {

	private static final long serialVersionUID = -3094026800166929481L;
	private ExtendedProperties props;

	private SygParam param;
	
	//private final static ParserType TYPE = ParserType.SYGFRAN;
	private final static String LANG = ISO639_3.sharedInstance.getIdCode("fra");

	private Log log = LogFactory.getLog(SemanticDictionaryAgent.class);
	
	private static final String SYGFRANPATH_PROP = "blexisma.sygfran.installpath";
	private static final String RULEPATH_PROP = "blexisma.sygfran.rulepath";
	private static final String RUNTIMEPATH_PROP = "blexisma.sygfran.runpath";
	private static final String TIMEOUT_PROP = "blexisma.sygfran.timeout";

	@Override
	protected void setup() {
		if (!parseProperties()) return;
		
		addBehaviour(new ParseTextRequestServer());
	}

	private boolean parseProperties() {
		props = new ExtendedProperties();
		
		// Get args. The command line contains only one arg which is a path to the property file
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			try {
				FileInputStream fis = new FileInputStream((String) args[0]);
				props.load(fis);
			} catch (IOException e) {
				if (log.isFatalEnabled()) log.fatal("Could not read properties file. Exiting.", e);
				this.doDelete();
				return false;
			}
			if (log.isDebugEnabled()) log.debug("Properties are: "+props);
			// Read the arguments
			String installPath = props.getProperty(SYGFRANPATH_PROP);
			String rulePath = props.getProperty(RULEPATH_PROP);
			String runtimePath = props.getProperty(RUNTIMEPATH_PROP);
			int timeout = props.getIntProperty(TIMEOUT_PROP, 600);

			param = new SygParam(installPath,rulePath,runtimePath, timeout);
		} else {
			if (log.isFatalEnabled()) log.fatal("No property file specified as argument. Exiting.");
			this.doDelete();
			return false;
		}
		return true;
	}


	public class ParseTextRequestServer extends CyclicBehaviour {

		private static final long serialVersionUID = 112947052428490620L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
					MessageTemplate.MatchOntology(SYNTACTIC_ANALYSIS));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// QUERY_REF Message received. Process it
				String txt = msg.getContent();
				AnalysisTree res;
				try {
					SygMorphoSyntacticTree tree = SygLocal.localAnalysis(txt, param);
					String xml = MorphoTreeToXML.outputXML(tree);
					res = SygfranAnswerParser.sygfranToTree(xml);
				if (log.isDebugEnabled())
					log.debug("Sygfran: analysis complete posting for tree building");
				} catch (SygfranParsingException e) {
					if (log.isWarnEnabled()) {
						log.warn("Got a sygfran Parser Error ", e);
					}
					res = BasicAnalysisTree.errorTreeFactory();
				} catch (RuntimeException e) {
					if (log.isErrorEnabled()) {
						log.error("Got a runtime exception while parsing with sygfran.", e);
					}
					res = BasicAnalysisTree.errorTreeFactory();
				}
				try {
					ACLMessage answer = msg.createReply();
					answer.setPerformative(ACLMessage.INFORM);
					answer.setContentObject(res);
					send(answer);
				} catch (IOException e) {
					if (log.isErrorEnabled()) log.error("Could not serialize Analysis Tree.", e);
					ACLMessage answer = msg.createReply();
					answer.setPerformative(ACLMessage.FAILURE);
				}
				
			} else {
				block();
			}	
		}
		
	}
}
