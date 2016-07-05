package org.getalp.blexisma.jade;


import static org.getalp.blexisma.jade.BlexismaJadeOntology.SEMANTIC_ANALYSIS;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.SemanticAnalysisMethod;
import org.getalp.blexisma.api.syntaxanalysis.AnalysisTree;
import org.getalp.blexisma.semanalysis.ContextualizingSemanticAnalysis;

public class SemanticAnalysisAgent extends Agent {

	private static final long serialVersionUID = -35756438578681753L;
	private Log log = LogFactory.getLog(SemanticDictionaryAgent.class);
	private SemanticAnalysisMethod analyser;

	@Override
	protected void setup() {
		analyser = new ContextualizingSemanticAnalysis();
		
		addBehaviour(new SemanticAnalysisRequestServer());
	}
	
	public class SemanticAnalysisRequestServer extends CyclicBehaviour {

		private static final long serialVersionUID = -5263627446013075628L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
					MessageTemplate.MatchOntology(SEMANTIC_ANALYSIS));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				SemanticAnalysisJob job;
				try {
					job = (SemanticAnalysisJob) msg.getContentObject();
					ConceptualVector context = new ConceptualVector(job.cvDimension,job.cvEncodingSize);
					AnalysisTree tree = job.tree;

					ConceptualVector cv;
					if (!tree.isError()) cv = analyser.computeConceptualVector(tree, context);
					else cv = new ConceptualVector(job.cvDimension,job.cvEncodingSize);

					ACLMessage answer = msg.createReply();
					answer.setPerformative(ACLMessage.INFORM);
					answer.setContentObject(cv);
					send(answer);
				} catch (UnreadableException e) {
					if (log.isErrorEnabled()) log.error("Could not deserialize SemanticAnalysisJob.", e);		
				} catch (IOException e) {
					if (log.isErrorEnabled()) log.error("Could not serialize SemanticAnalysisJob in semantic analysis answer.", e);		
				}
			} else {
				block();
			}	
		}
		
	}
	
}
