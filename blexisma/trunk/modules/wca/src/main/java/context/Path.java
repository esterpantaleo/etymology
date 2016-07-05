package context;

import acfast.Context;

public class Path {

	private int id;
	private String identifier;
	private double pheromoneLevel;
	private String description = "";
	private Node source;
	private Node target;
	private boolean isPont;
	private Context context;

	public Path(int id, Node source, Node target, Context c){
		this.id = id;
		this.description = "AntPath";// Pourquoi AntPath et pas path simplement ?
		this.source = source;
		this.target = target;
		this.isPont = false;
		this.context = c;
	}
	
	public void setPont(boolean b){
		this.isPont = b;
	}
	
	public boolean isPont(){
		return this.isPont;
	}

	public String toString() {
		return "Agent id: " + id + ", description: " + description + ", " + source + " -> " +target;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getIdentifier() {
		if (identifier == "" || identifier == null) {
			System.err.println("AntPath: error");
		}
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public boolean step() {
		this.pheromoneLevel -= this.pheromoneLevel * this.context.pheroEvap;
		if(this.isPont() && this.pheromoneLevel<=0.1){
			//System.out.println("Phero : "+this.pheromoneLevel);
			//this.context.removePath(this);
		    	return false;
		}
		return true;
	}

	public void setPheromoneLevel(double pheromoneLevel) {
		this.pheromoneLevel = pheromoneLevel;
	}

	public double getPheromoneLevel() {
		return pheromoneLevel;
	}

	public Node getOtherNode(Node node) {
		if (this.source == node)
			return this.target;
		else
			return this.source;

	}

	public Node getSource() {
		return this.source;
	}

	public Node getTarget() {
		return this.target;
	}
	
	public void setSource(Node newSource){
	    
	    this.source = newSource;
	}
	
}