package org.getalp.blexisma.external.plugins.stanford;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;
import org.getalp.blexisma.syntaxanalysis.stanford.StanfordAnswerParser;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * @author Alexandre Labadié
 * 
 * Plugin providing stanford analysis
 * */
public class StanfordPlugin extends ComponentPlugin {
	private IncrementalSubscription syntaxJobs;
	private LoggingService log;
	private LexicalizedParser lp;
	private TreebankLanguagePack tlp;
	private TokenizerFactory<? extends HasWord> tfk;
	private DocumentPreprocessor documentPreprocessor;
	private EnglishLemmatizer lemmatizer;
	private String lang;
	//private final static ParserType TYPE = ParserType.STANFORD;
	/**
	 * @param o : arguments for the plugin should be : the complete path to Stanford data file,
	 * and the language associated
	 * */
	public void setParameter(Object o)
	{
		Arguments args = new Arguments(o);
		
		this.lang = ISO639_3.sharedInstance.getIdCode(args.getString("language"));
		this.lp = new LexicalizedParser(args.getString("parsPath"));
		this.tlp = lp.getOp().langpack();
		this.tfk = tlp.getTokenizerFactory();
		this.documentPreprocessor = new DocumentPreprocessor();
		documentPreprocessor.setTokenizerFactory(tfk);
	    documentPreprocessor.setSentenceFinalPuncWords(tlp.sentenceFinalPunctuationWords());
	    documentPreprocessor.setEncoding(lp.getOp().tlpParams.getInputEncoding());
	    try {
			lemmatizer = new EnglishLemmatizer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		lp.setOptionFlags("-outputFormatOptions", "lexicalize");
	}
	
	/**
	 * Called when the plugin is loaded.  Establish the subscription for
	 * StanfordRequest objects
	 * */
	protected void setupSubscriptions() 
	{
		log = getServiceBroker().getService(this, LoggingService.class, null);
		syntaxJobs = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createSyntacticAnalysisPredicate()));
		if (log.isShoutEnabled())
			log.shout("Stanford syntax parser online, language: "+lang);
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
	
	/**
	   * Called when there is a change on my subscription(s).
	   * This plugin will publish on the blackboard the matching StanfordAnswer
	   */
	protected void execute(){
		@SuppressWarnings("unchecked")
		Enumeration<SyntacticAnalysisJob> new_requests = syntaxJobs.getAddedList();
		SyntacticAnalysisJob job = null;
		List<List<? extends HasWord>> document = null;
		ArrayList<String> trees = null;
		
		while (new_requests.hasMoreElements())
		{
			if (log.isDebugEnabled())
				log.debug("Stanford syntax parser receveived tasks.");
			job = new_requests.nextElement();
			document = documentPreprocessor.getSentencesFromText(new BufferedReader(new StringReader(job.getText())));
			
			trees = new ArrayList<String>();
			for (int i=0; i < document.size(); i++) {
				if (log.isDebugEnabled()){
					log.debug("before applying annalysis to sentence: "+document.get(i));
				}
				trees.add(((Tree)lp.apply(document.get(i))).toString());
			}
			
			if (log.isDebugEnabled())
				log.debug(trees.toString());
			job.setTree(StanfordAnswerParser.buildStanfordTree(trees,lemmatizer));
			if (log.isDebugEnabled())
				log.debug("Stanford syntax parser sent results.");
			getBlackboardService().publishChange(job);
		}
	}
}
