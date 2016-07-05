package context;


import java.util.*;

import Dictionnary.Definition;
import acfast.Context;


public class Node {

	protected Definition odor;
	protected Context context;
	private int sugarLevel;
	protected int ID;
	private String label;
	private List<Path> paths;
	
	public void step() {

	}

	public Node(String label, Context c) {
		this.odor = new Definition(c.tailleVector);
		this.paths = new ArrayList<Path>();
		this.label = label;
		this.context = c;
		this.sugarLevel = this.context.sugarInit;
	}

	public String toString() {
		return this.getClass() + " "+ this.ID + " " + label ;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}
	
	public boolean isNid() {
		return false;
	}

	public void setSugarLevel(int sugarLevel) {
		this.sugarLevel = sugarLevel;
	}
	
	public void removeSugar(int q){
		this.sugarLevel = this.sugarLevel-q;
	}

	public int getSugarLevel() {
		return sugarLevel;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public int getType() {
		return -1;
	}
	
	/*public boolean connected(Node n){
		if(this.getAntPathTo(n)!=null)
			return true;
		else return false;
	}

	public AntPath getAntPathTo(Node n){
		for(AntPath p:this.getAntPaths())
			for(AntPath p1:n.getAntPaths())
				if(p.equals(p1))
					return p;
		return null;
		
	}*/
	
	public List<Path> getPaths() {
		return this.paths;
	}

	public void addPath(Path path) {
		this.paths.add(path);
	}

	public void initOdor(Definition def) {
		this.odor = def;
	}

	public Definition getOdor() {
		return odor;
	}
	
	public void setOdor(Definition d, double coef){
		int n = (int)(this.odor.getSize()*coef);
		for(int i=0; i < n; i++){
			Random rand = new Random();
			int s1 = 1;
			if(d.getSize()>1)
				s1 = d.getSize()-1;
			int r = rand.nextInt(s1);
			int s2 = 1;
			if(this.odor.getSize()>1)
				s2 = this.odor.getSize()-1;
			int r2 = rand.nextInt(s2);
			this.odor.setElement(r2, d.getElement(r));
		}
	}
	
	public String getIdTask() {
		return "";
	}

	public void removePath(Path p) {
		this.paths.remove(p);
	}
}
