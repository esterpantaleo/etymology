package org.getalp.blexisma.plugins.dummy;

import java.util.Enumeration;

import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.DeviationBasedCVRandomizer;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;

public class SemCalculatorDummy extends ComponentPlugin {
	private IncrementalSubscription requests;
	private int dimension = -1;
	private int codeLength = -1;
	private DeviationBasedCVRandomizer randomizer;

	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * SygfranRequest objects
	 * */
	protected void setupSubscriptions() 
	{
		requests = (IncrementalSubscription)getBlackboardService().subscribe(getPredicate());
		System.out.println("Dummy calculator online");
	}
	
	/**
	 * Called before the plugin is loaded. Initialize the parameters
	 * */
	public void setParameter(Object o) {
		Arguments args = new Arguments(o);
		dimension = args.getInt("dimension");
		codeLength = args.getInt("codeLength");
		randomizer = new DeviationBasedCVRandomizer(dimension, codeLength);
	}

	/**
	   * Called when there is a change on my subscription(s).
	   * This plugin will publish on the blackboard the matching SygfranAnswer
	   */
	protected void execute() 
	{
		@SuppressWarnings("unchecked")
		Enumeration<SemanticAnalysisJob> new_requests = requests.getAddedList();
		SemanticAnalysisJob spTmp = null;
		
		System.out.println("CALCULATOR Change on blackboard, job to manage: "+new_requests.hasMoreElements());
		
		while (new_requests.hasMoreElements())
		{
			spTmp = new_requests.nextElement();
			
			spTmp.setData(randomizer.nextVector(2., 1).toStringHexa());
			spTmp.setVectSize(dimension);
			spTmp.setVectNorm(codeLength);
			spTmp.setPhase(SemanticJobPhase.DONE);
			
			getBlackboardService().publishChange(spTmp);
		}
	}
	
	private UnaryPredicate getPredicate() {
		return new UnaryPredicate(){
			private static final long serialVersionUID = 1L;

			public boolean execute(Object o) 
			{
				boolean pred = false;
				SemanticAnalysisJob sp = null;
				
				if (o instanceof SemanticAnalysisJob)
				{
					sp = (SemanticAnalysisJob)o;
					if (sp.getPhase()==SemanticJobPhase.WAITINGFORSYNTAX) pred = true;
				}
				
				return pred;
			}
		};
	}

}
