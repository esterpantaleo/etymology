package org.getalp.blexisma.plugins.syntax;

import java.io.Serializable;
import java.util.HashMap;

import org.getalp.blexisma.cougaarcom.SemanticJob;
import org.getalp.blexisma.cougaarcom.SyntacticAnalysisJob;

public class SyntacticAnalysisExpansion implements Serializable {

	private static final long serialVersionUID = -7274397297007583954L;

	private HashMap<SyntacticAnalysisJob,String> tasks;
	private SemanticJob semJob;
	
	/**
	 * @return the tasks
	 */
	public HashMap<SyntacticAnalysisJob, String> getTasks() {
		return tasks;
	}
	
	/**
	 * @param tasks the tasks to set
	 */
	public void addTask(SyntacticAnalysisJob j, String id) {
		if (this.tasks == null) this.tasks = new HashMap<SyntacticAnalysisJob,String>();
		this.tasks.put(j, id);
	}
	
	/**
	 * @return the semJob
	 */
	public SemanticJob getSemJob() {
		return semJob;
	}
	/**
	 * @param job the semJob to set
	 */
	public void setSemJob(SemanticJob job) {
		this.semJob = job;
	}
	
	
}
