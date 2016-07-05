package context;

import java.util.Random;



import Dictionnary.Word;
import acfast.Context;


public class NodeNid extends NodeMot implements Comparable<Object>{

	public Word word;
	private double nbAntsProducted = 0;
	private Node parent;
	private int number;
	
	public NodeNid(String label, Context c, int cat, Word w, NodeMot p) {
		super(label, c, cat);
		this.word = w;
		this.parent = p;
		this.odor = w.getDef();
		this.number = -1;
	}
	
	public void step() {

		Random generator = new Random();
		double i1 = generator.nextDouble();
		double i2 = (Math.atan(this.getSugarLevel())/Math.PI)+0.5;
		//System.out.println(this.getSugarLevel()+" : "+i2+"  et "+i1);
		if (i1<i2) {
			this.nbAntsProducted++;
			Ant ant = new Ant(this, this.context);
			ant.setActualSugar(1);
			this.removeSugar(1);
			//System.out.println(this.getSugarLevel());
			
			context.addAnt(ant);
		}
	}

	public void setNbAntsProducted(double nbAntsProducted) {
		this.nbAntsProducted = nbAntsProducted;
	}

	public double getNbAntsProducted() {
		return nbAntsProducted;
	}
	
	public boolean isNid() {
		return true;
	}
	
	public Node getParent(){
		return this.parent;
	}

	@Override
	public int compareTo(Object o) {
		NodeNid n2 = (NodeNid)o;
		
		return this.getIdTask().compareTo(n2.getIdTask());
	}

	public void setNumber(int num) {
		this.number = num;
	}
	
	public int getNumber() {
		return this.number ;
	}

}