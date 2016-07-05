package context;


import java.util.ArrayList;
import java.util.Random;

import Dictionnary.Definition;
import acfast.Context;

public class Ant {
    NodeNid origine;
    Node currentNode;
    Node lastNode;
    Path lastPath;
    private Definition odor;
    private int life;
    private int maxSugarTrans;
    private int sugarGet;
    private int currentSugar;
    //mode recherche ou retour
    private boolean modeRetour;
    // pourcentage que la fourmi depose de son vecteur
    private double coefDepoVector;
    private Context context;

    private int id;

    public Ant(NodeNid origine, Context c) {

	this.origine = origine;
	this.currentNode = origine;
	this.context = c;

	this.currentSugar = 0;

	this.modeRetour = false;

	this.odor = origine.getOdor();

	// recuperation des parametres saisis par l'utilisateur
	this.life = this.context.antLife;
	this.maxSugarTrans = this.context.maxSugar;
	this.sugarGet = this.context.getSugar;
	this.coefDepoVector = this.context.depotVector;

    }

    /**
     * methode appele a chaque pas de temps
     * @return true si la fourmie est vivante, false sinon
     */
    public boolean step() {
	// si la fourmi n'est pas morte elle se deplace au meilleur endroit
	if (this.isDead()){
	    // sinon elle depose son sucre sur le noeud actuel
	    this.currentNode.setSugarLevel(this.currentNode.getSugarLevel()+this.currentSugar);
	    //context.removeAnt(this);
	    return false;
	} else{
	    life--;
	    // la fourmi se deplace au meilleur endroit possible
	    this.moveToTheBestSpot();
	    return true;
	}
    }

    /**
     * Deplace la fourmi a la meilleure possibilite de deplacement
     */
    public void moveToTheBestSpot() {

	Path p = null;
	Node node = null;

	//Creation de pont si necessaire
	/*	if((actuelNode.isNid()) && ((((NodeNid)actuelNode).getParent())!=this.origine.getParent()) && (this.lastMove!=this.origine)){
	    double res = actuelNode.getOdor().getSim(this.odor);
	    if(res > (int)(Math.random()*this.context.tailleVector)){
		//System.out.println(temp);
		p = this.context.addPont(this.actuelNode, this.origine);
		node = this.origine;
	    }
	}*/

	//if(p == null){
	// plus la quantite de sucre transporte est plus importante plus il y a
	// des chances pour que
	// la fourmi passe en mode retour
	if((!this.modeRetour && Math.random() < this.currentSugar / (double) this.maxSugarTrans))
	    modeRetour = true;

	if (modeRetour){
	    p = this.getTheBestPossGoHome();
	}else{
	    p = this.getTheBestPossSearch();
	}

	if(p != null)
	    node = p.getOtherNode(currentNode);
	else{

	    if(lastPath!=null){
		
		p=lastPath;
		node = lastNode;
	    }
	    else{
		
		
		//System.out.println("p et lastPath est null PQ ??????????");
		return;
	    }
	}


	// lache de la pheromone
	p.setPheromoneLevel(p.getPheromoneLevel() + this.context.pheroDepot);
	// actualise sa memoire
	this.lastNode = currentNode;
	this.lastPath = p;
	// actualise le noeud actuel
	currentNode = node;

	// si la fourmis est sur sa fourmiliere d'origine elle depose son sucre
	if(currentNode.equals(this.origine)){

	    /*    System.out.println("Fourmilire");
	    System.out.println(this.actuelNode.getSugarLevel());
	    System.out.println(this.actualSugar);*/
	    this.currentNode.setSugarLevel(this.currentNode.getSugarLevel() + this.currentSugar);
	    //  System.out.println(this.actuelNode.getSugarLevel());
	    this.currentSugar = 0;
	    this.modeRetour = false;
	}else{
	    //On depose l'odeur
	    if(!currentNode.isNid()){
		currentNode.setOdor(this.odor, this.coefDepoVector);
		if(currentNode.getSugarLevel() >= this.sugarGet){
		    currentNode.setSugarLevel(currentNode.getSugarLevel() - this.sugarGet);
		    this.currentSugar += this.sugarGet;
		}else{
		    this.currentSugar += currentNode.getSugarLevel();
		    currentNode.setSugarLevel(0);
		}
	    }
	}

    }

    /**
     * 
     * @return La meilleure la possibilite quand etat recherche
     */
    private Path getTheBestPossSearch() {
	//Trouver le noeud qui a le meilleur rapport sucre pheromone

	ArrayList<pathWeight> sol = new ArrayList<pathWeight>(20);

	for(Path p : currentNode.getPaths()){
	    Node dest = p.getOtherNode(currentNode);
	    if(dest!=this.lastNode){

		sol.add(new pathWeight(p,(double)(1+dest.getSugarLevel())/(1+p.getPheromoneLevel())));

		//ps.add(p);
	    }
	}
	if(this.origine!=currentNode && currentNode.isNid()){

	    sol.add(new pathWeight(null,(double)(1+origine.getSugarLevel())));
	}

	//choix

	double overallweight = 0;

	for(pathWeight pw : sol){

	    overallweight+=pw.weight;
	}

	double r = Math.random();
	double cumul = 0;

	for(pathWeight pw : sol){

	    cumul+=pw.weight/overallweight;
	    if(cumul >= r){//on choisi ce chemin

		if(pw.p==null){ //il s'agit d'un pont que l'on cr

		    return this.context.addPont(this.currentNode, this.origine);
		}
		else{

		    return pw.p;
		}

	    }


	}
	return null;
    }

    /**
     * 
     * @return La meilleure la possibilite quand etat retour
     */
    private Path getTheBestPossGoHome() {

	ArrayList<pathWeight> sol = new ArrayList<pathWeight>(20);

	for(Path p : currentNode.getPaths()){
	    Node dest = p.getOtherNode(currentNode);
	    if(dest!=this.lastNode){

		sol.add(new pathWeight(p,(double)(currentNode.getOdor().getSim(dest.getOdor())+1)/(1+p.getPheromoneLevel())));

		//ps.add(p);
	    }
	}
	if(this.origine!=currentNode && currentNode.isNid()){

	    sol.add(new pathWeight(null,(double)currentNode.getOdor().getSim(origine.getOdor())+1));
	}

	//choix

	double overallweight = 0;

	for(pathWeight pw : sol){

	    overallweight+=pw.weight;
	}

	double r = Math.random();
	double cumul = 0;

	for(pathWeight pw : sol){

	    cumul+=pw.weight/overallweight;
	    if(cumul >= r){//on choisi ce chemin

		if(pw.p==null){ //il s'agit d'un pont que l'on cr

		    return this.context.addPont(this.currentNode, this.origine);
		}
		else{

		    return pw.p;
		}

	    }


	}
	return null;

	//Node res = this.context.getNode(this.context.getPath(actuelNode.getPaths().get(0)).getOtherNode(actuelNode.ID));
	/*
	Path res1 = currentNode.getPaths().get(0);
	double best = 0.0;
	ArrayList<Path> ps = new ArrayList<Path>();
	for(Path p : currentNode.getPaths()){
	    Node n = p.getOtherNode(currentNode);
	    double temp = (double)((currentNode.getOdor().getSim(n.getOdor())+1)/(1+p.getPheromoneLevel()));
	    if(temp > best && n!=this.lastNode){
		best = temp;
		ps = new ArrayList<Path>();
		ps.add(p);
		//res1 = p;
	    }else if(temp == best && n!=this.lastNode){
		ps.add(p);
	    }
	}
	if(ps.size()>0){
	    Random r = new Random();
	    res1 = ps.get(r.nextInt(ps.size()));
	    //result.add(nodenids.get(a.get(0)));
	}
	return res1;*/
    }

    /**
     * Methode qui remplit la liste des deplacements possibles
     * 
     * @param goWork
     *            si true remplit tous les deplacements possibles
     */

    public boolean isDead() {
	return life <= 0;
    }

    public int getLife() {
	return life;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getId() {
	return id;
    }

    public void setCoefDepoVector(double coefDepoVector) {
	this.coefDepoVector = coefDepoVector;
    }

    public double getCoefDepoVector() {
	return coefDepoVector;
    }

    public void setActualSugar(int actualSugar) {
	this.currentSugar = actualSugar;
    }

    public int getActualSugar() {
	return currentSugar;
    }

    public Node currentNode() {
	return this.currentNode;
    }

    public Node origine() {
	return this.origine;
    }

    /**
     * @return the lastNode
     */
    public Node getLastNode() {
        return lastNode;
    }

    /**
     * @param lastNode the lastNode to set
     */
    public void setLastNode(Node lastNode) {
        this.lastNode = lastNode;
    }

    /**
     * @return the lastPath
     */
    public Path getLastPath() {
        return lastPath;
    }

    /**
     * @param lastPath the lastPath to set
     */
    public void setLastPath(Path lastPath) {
        this.lastPath = lastPath;
    }
    
    
}

class pathWeight{

    public Path p;
    public double weight; 

    public pathWeight(Path p,double weight){

	this.p = p;
	this.weight = weight;

    }
}