package org.getalp.blexisma.jade;

import static org.getalp.blexisma.jade.BlexismaJadeOntology.ANALYSIS_DECORATION;
import static org.getalp.blexisma.jade.BlexismaJadeOntology.BASE_UPDATE;
import static org.getalp.blexisma.jade.BlexismaJadeOntology.LEARNING_START;
import static org.getalp.blexisma.jade.BlexismaJadeOntology.SEMANTIC_ANALYSIS;
import static org.getalp.blexisma.jade.BlexismaJadeOntology.SYNTACTIC_ANALYSIS;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.ExtendedProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDictionary;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.impl.semdico.WiktionaryBasedSemanticDictionary;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;
import org.getalp.blexisma.semnet.TextOnlySemnetReader;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.TreeDecorator;
import org.getalp.blexisma.syntaxanalysis.basicrepresentation.tree.BasicAnalysisTree;
import org.getalp.blexisma.utils.OpenFile;
import org.getalp.blexisma.utils.WriteInFile;
import org.getalp.dbnary.WiktionaryExtractor;

public class SemanticDictionaryAgent extends Agent {

	ExtendedProperties props;
	// Args
	private String basePath;
	private String netPath;
	private String lastIdFile;
	private int cvEncodingSize;
	private int cvDimension;
	String[] languageList;
	
	private int TIMES;
	private int LOGGING_INTERVAL = 500;
	
	private SemanticDictionary dictionary;
	private Iterator<String> infiniteNodeIterator ;
	private TreeDecorator decorator;
		
	private int saveCount;
	private String lastId;
	private Log log = LogFactory.getLog(SemanticDictionaryAgent.class);
	
	// Profiling fields
	private int nbVectorsSet = 0;
	private int nbLemmaTrained = 0;
	private long startTime;
	private long lastLogTime;
	private SemanticNetwork<String, String> network;
	private String_RAM_VectorialBase vb;
	
	private static final long serialVersionUID = -2314084058088015123L;
	private static final String BASEPATH_PROP = "blexisma.semdict.basepath";
	private static final String NETWORKPATH_PROP = "blexisma.semdict.netpath";
	private static final String DIMENSION_PROP = "blexisma.semdict.dimension";
	private static final String ENCSIZE_PROP = "blexisma.semdict.codelength";
	private static final String LANGUAGELIST_PROP = "blexisma.semdict.langs";

	
	@Override
	protected void setup() {
		if (!parseProperties()) return;
		readVectorialBase();
		resumeTrainingIterator();
		
		decorator = new TreeDecorator(dictionary);
		
		startTime = System.currentTimeMillis();
		lastLogTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("Semantic Dictionary online");
		}
		// Create and install new behaviours
		addBehaviour(new BaseUpdateRequestServer());
		addBehaviour(new AnalysisDecorationRequestServer());
		addBehaviour(new LearningCycleBehaviour());
	}
	
	@Override
	protected void takeDown() {
		// Force saving the Semantic Dictionary.
		// saveData();
	}
	
	private AID getSyntacticAnalyser(String lang) {
		return new AID("sygfran", AID.ISLOCALNAME);
	}
	
	private AID getSemanticAnalyser() {
		return new AID("semantics", AID.ISLOCALNAME);
	}
	
	private void resumeTrainingIterator() {
		
		lastIdFile = this.getBaseDirectory()+File.separator+"lastid.txt";
		
		File f = new File(lastIdFile);

		if (f.exists()) {
			lastId = OpenFile.readFullTextFile(f);
			if (!validId(lastId)) {
				f.delete();
				try {
					f.createNewFile();
				} catch (IOException e) {
					if (log.isWarnEnabled()) log.warn("Could not write to lastid file. Continuing.", e);
				}
				lastId = null;
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				if (log.isWarnEnabled()) log.warn("Could not write to lastid file. Continuing.", e);
			}
			lastId = null;
		}
		infiniteNodeIterator = (Iterator<String>) network.getInfiniteNodesIterator();
		if (log.isInfoEnabled()) log.info("Last computed vector was: " + lastId);
		if (lastId!=null)
		{
			if (log.isInfoEnabled()) {
				log.info("Searching for last computed vector.");
			}
			while (!infiniteNodeIterator.next().equals(lastId));
			if (log.isInfoEnabled()) {
				log.info("Resuming training at lemma: " + lastId);
			}
		}		
	}

	public String getBaseDirectory() {
		return new File(basePath).getParent();
	}
	
	private void readVectorialBase() {
		if (log.isInfoEnabled()) {
			log.info("Max heap size: "+Runtime.getRuntime().maxMemory());
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
			log.info("Loading Semantic Dictionary");
		}
		try {
			this.network = TextOnlySemnetReader.loadNetwork(netPath);
			this.vb = String_RAM_VectorialBase.load(basePath);
			if (this.vb == null) {
				// The vectorial base was not readable or file did not exist
				this.vb = new String_RAM_VectorialBase(this.cvEncodingSize,
						this.cvDimension);
				this.vb.save(this.basePath);
			} else if (this.vb.getCVDimension() != this.cvDimension
					|| this.vb.getCVEncodingSize() != cvEncodingSize) {
				if (log.isFatalEnabled()) log.fatal("Incompatible Vectorial Base. Exiting.", new RuntimeException());
				doDelete();
				return;
			}
			dictionary = new WiktionaryBasedSemanticDictionary(vb,network);
		} catch (IOException e) {
			if (log.isFatalEnabled()) log.fatal("Could not read Vectorial Base. Exiting.", e);
			doDelete();
			return;
		}

		if (log.isInfoEnabled()) {
			Runtime.getRuntime().gc();
			log.info("Current free memory size: "+Runtime.getRuntime().freeMemory());
		}		
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
			this.basePath = props.getProperty(BASEPATH_PROP);
			this.netPath = props.getProperty(NETWORKPATH_PROP);
			this.cvDimension = props.getIntProperty(DIMENSION_PROP, 2000);
			this.cvEncodingSize = props.getIntProperty(ENCSIZE_PROP, 32768);
			this.languageList = props.getProperty(LANGUAGELIST_PROP).split(",");
			
			for (int i=0; i<languageList.length; i++) languageList[i]=ISO639_3.sharedInstance.getIdCode(languageList[i]);

		} else {
			if (log.isFatalEnabled()) log.fatal("No property file specified as argument. Exiting.");
			this.doDelete();
			return false;
		}
		return true;
	}

	private void saveData() {
		if (log.isInfoEnabled()) log.info("Saving ConceptualVector base before exiting...");
		long t = System.currentTimeMillis();
		if (this.basePath != null) {
			this.vb.save(this.basePath);
		}
		if (log.isInfoEnabled()) log.info("Last id... "+lastId);
		WriteInFile.writeText(new File(lastIdFile), lastId);
		if (log.isInfoEnabled()) log.info("ConceptualVector base saved in " + (System.currentTimeMillis() - t) + " ms.");		
	}

	private boolean validId(String id) {
		return !id.equals("");
	}
	
	private void saveSurvey() {
		saveCount--;
		
		if (saveCount<1) {
			saveCount = TIMES;
			saveData();
		}
	}
	
	private boolean validLanguage(String lg) {
		if ("def".equals(lg)) return true;
		
		String code = ISO639_3.sharedInstance.getIdCode(lg);
		if (code != null) {
			for (int i=0;i<languageList.length;i++){
				if (code.equals(languageList[i])) {
					return true;
				}
			}
		}
		
		return false;
	}
	public class BaseUpdateRequestServer extends CyclicBehaviour {

		private static final long serialVersionUID = -4550326521367552710L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchOntology(BASE_UPDATE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// REQUEST Message received. Process it
				try {
					BaseUpdateJob job = (BaseUpdateJob) msg.getContentObject();
					if (log.isDebugEnabled()) log.debug("Processing Base Update Request");
					nbLemmaTrained++; nbVectorsSet++;
					baseUpdate(job.getCvs(),job.getLg());
					if (nbLemmaTrained % LOGGING_INTERVAL == 0) {
						long ctime = System.currentTimeMillis();
						if (log.isInfoEnabled()) 
							log.info("Trained " + LOGGING_INTERVAL + " lemmas in " + (ctime - lastLogTime) + " ms.");
						log.info("Trained " + nbLemmaTrained + " lemmas (" + nbVectorsSet + " vectors computed) in " + (ctime - startTime) + " ms.");
						log.info("Current training speed: " + ((ctime - startTime)/nbLemmaTrained) + " ms/lemma.");
						lastLogTime = ctime;
						saveSurvey();
					}
				} catch (UnreadableException e) {
					if (log.isErrorEnabled()) log.error("Could not read Base Update Job.", e);
				}
			} else {
				block();
			}
		}
		
		private void baseUpdate(HashMap<String,ConceptualVector> data, String lg) {
			ConceptualVector cv = null;
			
			for (String key : data.keySet()) {
				nbVectorsSet++;
				cv = data.get(key);
				vb.addVector(key, cv);
			}
		}
	}
	
	// TODO: will it be used by a servlet based analysis query ?
	public class AnalysisDecorationRequestServer extends CyclicBehaviour {

		private static final long serialVersionUID = 8226098428616875199L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
					MessageTemplate.MatchOntology(ANALYSIS_DECORATION));
			// The language field of the message holds the language of the analysis tree.
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// QUERY_REF Message received. Process it
				AnalysisTree tree = null;
				try {
					tree = (AnalysisTree) msg.getContentObject();
					if (log.isDebugEnabled()) log.debug("Decorating Analysis Tree.");
					if (!tree.isError()) tree = decorator.simpleDecorate(tree, msg.getLanguage(), cvDimension, cvEncodingSize);
				} catch (UnreadableException e) {
					if (log.isErrorEnabled()) log.error("Could not read Analysis Tree.", e);
				}
				ACLMessage answer = msg.createReply();
				try {
					if (null != tree) {
						answer.setContentObject(tree);
					} else {
						answer.setContentObject(BasicAnalysisTree.errorTreeFactory());
					}
				} catch (IOException e) {
					if (log.isErrorEnabled()) log.error("Could not reply with decorated Analysis Tree.", e);
					msg.setContent("");
				}
				send(msg);
			} else {
				block();
			}
		}
	}
	
	public class LearningCycleBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 3102336594078902494L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchOntology(LEARNING_START));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// QUERY_REF Message received. Process it
				String node, lg;
				do {
					node = infiniteNodeIterator.next();
					lg = node.substring(1, node.indexOf("|"));
				} while (!validLanguage(lg));

				lastId = node;

				if (log.isDebugEnabled()) {
					log.debug("Infinite next id [" + lg + "] :" + node );
				}

				if ("def".equals(lg)) {
					// TODO: store definition language in id and extract it so that it is correctly analyzed.
					createDefinitionSemanticLearningBehaviour(node, "fra");
				} else {
					createLemmaSemanticLearningBehaviour(node, lg);
				}
			} else {
				block();
			}
		}

	}
	
	public class LearningDefinitionBehaviour extends Behaviour {

		private static final long serialVersionUID = 202612076443267473L;
		private static final int START_LEARNING = 0;
		private static final int WAITING_FOR_SYNTACTIC_ANALYSIS = 1;
		private static final int WAITING_FOR_SEMANTIC_ANALYSIS = 2;
		private static final int JOB_COMPLETE = 3;

		private int step = START_LEARNING;

		private String node;
		private AnalysisTree tree;
		private String lg;
		
		public LearningDefinitionBehaviour(String node, String lg) {
			super();
			this.node = node;
			this.lg = ISO639_3.sharedInstance.getIdCode(lg);
		}
		
		@Override
		public void action() {
			switch (step) {
			case START_LEARNING:
				sendSyntacticAnalysisMessage(node, lg);
				step = WAITING_FOR_SYNTACTIC_ANALYSIS;
				block();
				break;
			case WAITING_FOR_SYNTACTIC_ANALYSIS:
				MessageTemplate syntmt = MessageTemplate.and(MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchOntology(SYNTACTIC_ANALYSIS)),
						MessageTemplate.MatchConversationId(node));
				ACLMessage syntaxTreeMsg = myAgent.receive(syntmt);
				if (syntaxTreeMsg != null) {
					try {
						tree = (AnalysisTree) syntaxTreeMsg.getContentObject();
						// TODO: Maybe the behaviour could be kept by a scheduler agent outside the semantic dictionary.
						// TODO: in this case, we would use an AnalysisDecorationRequest sent to the semantic dictionary.
						if (!tree.isError()) tree = decorator.simpleDecorate(tree, syntaxTreeMsg.getLanguage(), cvDimension, cvEncodingSize);
						sendSemanticAnalysisMessage(node, tree, lg);
					}  catch (UnreadableException e) {
						if (log.isErrorEnabled()) log.error("Could not read Analysis Tree.", e);
					}
					step = WAITING_FOR_SEMANTIC_ANALYSIS;
				} else {
					block();
				}
				break;
			case WAITING_FOR_SEMANTIC_ANALYSIS:
				MessageTemplate semmt = MessageTemplate.and(MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchOntology(SEMANTIC_ANALYSIS)),
						MessageTemplate.MatchConversationId(node));
				ACLMessage semanticAnalysisMsg = myAgent.receive(semmt);
				if (semanticAnalysisMsg != null) {
					try {
						ConceptualVector cv = (ConceptualVector) semanticAnalysisMsg.getContentObject();
						sendBaseUpdateMessage(node, cv, lg);
					}  catch (UnreadableException e) {
						if (log.isErrorEnabled()) log.error("Could not read ConceptualVector from Semantic Analysis Answer.", e);
					}
					step = JOB_COMPLETE;
				} else {
					block();
				}
				break;
				
			default:
				break;
			}
		}

		@Override
		public boolean done() {
			return (step == JOB_COMPLETE);
		}
	}

	private void sendSyntacticAnalysisMessage(String node, String lg) {
		String txt = WiktionaryExtractor.getHumanReadableForm(node);
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.addReceiver(getSyntacticAnalyser(lg));
		msg.setOntology(SYNTACTIC_ANALYSIS);
		msg.setConversationId(node);
		msg.setLanguage(lg);
		msg.setContent(txt);
		
		send(msg);
	}

	public void sendBaseUpdateMessage(String node, ConceptualVector cv,
			String lg) {
		HashMap<String, ConceptualVector> map = new HashMap<String, ConceptualVector>();
		map.put(node, cv);
		BaseUpdateJob job = new BaseUpdateJob(lg, map);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(this.getAID());
		msg.setOntology(BASE_UPDATE);
		msg.setLanguage(lg);
		try {
			msg.setContentObject(job);
		} catch (IOException e) {
			if (log.isErrorEnabled()) log.error("Could not serialize Base Update Job.", e);
		}
		
		send(msg);
	}

	public void sendSemanticAnalysisMessage(String node, AnalysisTree tree,
			String lg) {
		
		try {
			ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
			msg.addReceiver(getSemanticAnalyser());
			msg.setOntology(SEMANTIC_ANALYSIS);
			msg.setLanguage(lg);
			msg.setConversationId(node);
			msg.setContentObject(new SemanticAnalysisJob(tree, cvDimension, cvEncodingSize));
			send(msg);
		} catch (IOException e) {
			if (log.isErrorEnabled()) log.error("Could not serialize Decorated Analysis Tree.", e);
		}
	}

	public void createDefinitionSemanticLearningBehaviour(String node,
			String lg) {
		LearningDefinitionBehaviour beh = new LearningDefinitionBehaviour(node, lg);
		this.addBehaviour(beh);
	}

	public void createLemmaSemanticLearningBehaviour(String node, String lg) {
		// TODO Auto-generated method stub
		
	}
}
