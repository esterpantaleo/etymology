package context;


import java.util.ArrayList;


import Dictionnary.Word;
import acfast.Context;
import acfast.Global;

public class NodeMot extends Node{

	private String lemme;
	private String idTask;
	private int categorieLex;
	private ArrayList<NodeNid> best;
	private boolean hasConverged;

	public NodeMot(String lemme, Context c, int cat) {
		super(lemme, c);
		this.lemme = lemme;
		this.categorieLex = cat;
		this.idTask = "";
		best = new ArrayList<NodeNid>();
		hasConverged = false;
	}

	public boolean hasConverged(){
		return hasConverged;
	}

	public void setLemme(String lemme) {
		this.lemme = lemme;
	}

	public String getLemme() {
		return lemme;
	}

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public String getIdTask() {
		return idTask;
	}

	public void setCategorieLex(int categorieLex) {
		this.categorieLex = categorieLex;
	}

	public int getCategorieLex() {
		return categorieLex;
	}

	public void saveBest(NodeNid n){
		this.best.add(n);
	}

	public void converge(){
		if(this.best.size() > this.context.startConverge){
			boolean ok = true;
			NodeNid nn = this.best.get(this.best.size()-1);
			if(nn != null){
				for(int i = this.best.size()-2; i > this.best.size()-this.context.nbCycleConvergence; i--){
					if(this.best.get(i) != nn){
						ok = false;
					}
				}
			}else
				ok = false;

			if(ok){
				ArrayList<Path> a = new ArrayList<Path>(this.getPaths());
				for(int i = a.size()-1; i >= 0; i--){
					Path edge = a.get(i);
					if(edge.getSource().isNid() && ((NodeNid)edge.getSource()) != nn){
						this.context.deconnectNode(edge.getSource(), this);
						nn.setSugarLevel(nn.getSugarLevel()+edge.getSource().getSugarLevel());
						this.context.removeNode(edge.getSource());
					}else if(edge.getTarget().isNid() && ((NodeNid)edge.getTarget()) != nn){
						this.context.deconnectNode(edge.getTarget(), this);
						nn.setSugarLevel(nn.getSugarLevel()+edge.getTarget().getSugarLevel());
						this.context.removeNode(edge.getTarget());
					}
				}
				hasConverged = true;
			}

		}
	}

}