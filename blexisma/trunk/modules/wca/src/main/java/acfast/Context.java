package acfast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import Parser.ParserConfig;
import context.Ant;
import context.Node;
import context.NodeMot;
import context.NodeNid;
import context.Path;

public class Context {

    protected ArrayList<Node> nodes;
    protected ArrayList<Node> nests;
    protected ArrayList<Path> paths;
    protected ArrayList<Ant> ants;

    protected int idAnt;
    protected int idNode;
    protected int idPath;

    public String task, in, out, dico;
    public String fileTocompare;
//    public Dictionary dict;
    public int graph;
    public int cycles;

    public int sugarInit;

    public int pheroInit;
    public int pheroDepot;
    public double pheroEvap;

    public int antLife;
    public int maxSugar;
    public int getSugar;
    public int tailleVector;
    public double depotVector;

    public int converge;
    public int startConverge;
    public int nbCycleConvergence;

    private boolean pasFini;

    public Context(String conf) {
    	this(new InputSource(conf));
    }
    
    public Context(InputStream conf) {
    	this(new InputSource(conf));
    }
    
    public Context(InputSource conf) {

    	//Initialisation Variable
    	this.task = "";
    	this.in = "";
    	this.out = "";
    	this.dico = "";
    	this.fileTocompare="";
    	this.graph = 0;
    	this.cycles = 30;
    	this.sugarInit = 100;
    	this.pheroInit = 5;
    	this.pheroDepot = 1;
    	this.pheroEvap = 0.1;
    	this.antLife = 10;
    	this.maxSugar = 5;
    	this.getSugar = 1;
    	this.tailleVector = 20;
    	this.depotVector = 0.1;
    	this.nodes = new ArrayList<Node>(1000000);
    	this.nests = new ArrayList<Node>(1000000);
    	this.paths = new ArrayList<Path>(1000000);
    	this.ants = new ArrayList<Ant>(1000000);
    	//	this.ponts = new HashSet<Path>();
    	this.idAnt = 0;
    	this.idNode = 0;
    	this.idPath = 0;
    	this.converge = 0;
    	this.startConverge = 200;
    	this.nbCycleConvergence = 20;

    	//Lecture Conf 
    	try {
    		XMLReader saxReader = XMLReaderFactory.createXMLReader();
    		saxReader.setContentHandler(new ParserConfig(this));
    		saxReader.parse(conf);
    	}catch(Throwable t) {
    		t.printStackTrace();
    	}
    	
    }


    public void step() {

    	for(int i = 0; i < nests.size();i++){
    		nests.get(i).step();
    	}

    	ArrayList<Ant> alivesAnts = new ArrayList<Ant>(this.ants.size());
    	Ant A;
    	Path P;


    	for(int i = 0; i < ants.size();i++){

    		A = ants.get(i);
    		if(A.step()){

    			alivesAnts.add(A);
    		}
    	}

    	// System.err.println((ants.size() - alivesAnts.size()) + " fourmis mortes");

    	ants = alivesAnts;

    	ArrayList<Path> pathskept = new ArrayList<Path>(this.paths.size());

    	for(int i = 0; i < paths.size();i++){

    		P = paths.get(i);
    		if(P.step()){

    			pathskept.add(P);
    		}
    	}

    	// System.err.println((paths.size() - pathskept.size()) + " chemins vapors");

    	paths = pathskept;
    }

    //Ajoute une fourmie du context
    public void addAnt(Ant a) {
    	a.setId(idAnt);
    	this.ants.add(a);
    	this.idAnt++;
    }

    public void deconnectNode(Node n, NodeMot mot){

    	ArrayList<Path> a = new ArrayList<Path>(n.getPaths());
    	for(int i = a.size()-1; i >= 0; i--){
    		Path p = a.get(i);
    		this.removePath(p);
    	}
    	ArrayList<Ant> b = new ArrayList<Ant>(this.ants);
    	for(int i = b.size()-1; i >= 0; i--){
    		Ant ant = b.get(i);
    		if(ant.currentNode() == n || ant.origine() == n){

    			ant.currentNode().setSugarLevel(ant.currentNode().getSugarLevel()+ant.getActualSugar());

    			this.ants.remove(ant);
    		}
    		ant.setLastNode(mot);
    	}
    }

    //Enleve un noeud du context
    public void removeNode(Node n){
	this.nodes.remove(n);
    }

    //Retourne un noeud du context
    /*public Node getNode(int id){
		return this.nodes.get(id);
	}*/

    //Ajoute un noeud du context
    public void addNode(Node n){
	n.setID(idNode);
	this.nodes.add(n);
	this.idNode++;

	//System.out.println("Noeud cr : " + n);
    }

    //Ajoute un nid du context
    public void addNest(Node n){
	n.setID(idNode);
	this.nests.add(n);
	this.idNode++;

	//System.out.println("Noeud cr : " + n);
    }

    //Enleve un chemin du context
    public void removePath(Path p){
	p.getSource().removePath(p);
	p.getTarget().removePath(p);
	this.paths.remove(p);
    }

    //Retourne un chemin du context
    /*public Path getPath(int id){
		return this.paths.get(id);
	}*/

    //Ajoute un chemin au context
    public Path addPath(Node n1, Node n2){
	Path p = new Path(idPath, n1, n2, this);
	n1.addPath(p);
	n2.addPath(p);
	this.paths.add(p);
	this.idPath++;

	return p;
    }

    //Ajoute un pont au context
    public Path addPont(Node n1, Node n2){
	Path p = this.existPont(n1, n2);
	if(p == null){
	    p = this.addPath(n1, n2);
	    p.setPont(true);
	    // ponts.add(p);
	    // System.out.println("Creation Pont entre "+n1.getLabel()+" et "+n2.getLabel());
	}
	return p;
    }

    //Ajoute un chemin du context
    public Path existPont(Node n1, Node n2){
	Path p = null;
	Iterator<Path> it = n1.getPaths().iterator();
	while(it.hasNext() && p==null){
	    Path t = it.next();
	    if(t.getOtherNode(n1)==n2)
		p = t;
	}
	return p;
    }

    public void saveBest(){

	//On parcourt les noeuds Mots 
	boolean isOver = true;
	ArrayList<Node> a1 = new ArrayList<Node>(nodes);
	for(int c = a1.size()-1; c >= 0; c--){

	    Node node = a1.get(c);

	    if((node.getClass().equals(NodeMot.class))&&(((NodeMot)node).getIdTask()!="")&&!(((NodeMot)node).hasConverged())){
		isOver = false;
		NodeMot nm = (NodeMot)node;
		//Pour chaque noeuds mots on recupere la liste des noeuds nids
		Iterator<Path> i2 = nm.getPaths().iterator();
		ArrayList<NodeNid> nodenids = new ArrayList<NodeNid>();
		while(i2.hasNext()){
		    Path edge = i2.next();
		    if(edge.getSource().isNid())
			nodenids.add((NodeNid)edge.getSource());
		    else if(edge.getTarget().isNid())
			nodenids.add((NodeNid)edge.getTarget());
		}

		double[] tab = new double[nodenids.size()];
		for(int y = 0; y <nodenids.size(); y++){
		    NodeNid nn = nodenids.get(y);
		    tab[y] = nn.getSugarLevel();
		}

		int best = 0;
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(0);
		for(int j = 1; j < tab.length; j++){
		    if(tab[best]<tab[j]){
			best = j;
			a = new ArrayList<Integer>();
			a.add(j);
		    }else if(tab[best]==tab[j]){
			a.add(j);
		    }
		}
		if(a.size() == 1){
		    nm.saveBest(nodenids.get(a.get(0)));
		}else{
		    nm.saveBest(null);
		}

		if(converge==1)

		    nm.converge();

		//------------------------------------------------
	    }
	}
	if(isOver)
	    this.pasFini=false;
    }

    public void writeContext(OutputStream out) {
    	PrintStream o = new PrintStream(out);
    	for (Path p: this.paths) {
    		o.println(p.getID() + ": " + p.getSource().getLabel() + " <---> " + p.getTarget().getLabel());
    	}
    }
    
    public void writeResult(String outFile) throws FileNotFoundException {
	//On parcourt les noeuds Mots
	Iterator<Node> i = nodes.iterator();
	ArrayList<NodeNid> result = new ArrayList<NodeNid>();
	while(i.hasNext()){
	    Node node = i.next();
	    if((node.getClass().equals(NodeMot.class))&&(((NodeMot)node).getIdTask()!="")){
		NodeMot nm = (NodeMot)node;
		//Pour chaque noeuds mots on recupere la liste des noeuds nids
		Iterator<Path> i2 = nm.getPaths().iterator();
		ArrayList<NodeNid> nodenids = new ArrayList<NodeNid>();
		while(i2.hasNext()){
		    Path edge = i2.next();
		    if(edge.getSource().isNid())
			nodenids.add((NodeNid)edge.getSource());
		    else if(edge.getTarget().isNid())
			nodenids.add((NodeNid)edge.getTarget());
		}
		//On cherche le noeud nid qui a la meilleur valeur
		//System.out.println("Le mot : "+nm.getIdTask()+" a "+nodenids.size()+" possibilits.");

		double[] tab = new double[nodenids.size()];
		for(int y = 0; y <nodenids.size(); y++){
		    NodeNid nn = nodenids.get(y);
		    /*ArrayList<Path> t = nn.getPaths();
					int nb = 0;
					double tot = 0;
					for(int j = 0; j <t.size(); j++){
						if(t.get(j).isPont()){
							tot += t.get(j).getPheromoneLevel();
							nb++;
						}
					}*/
		    //System.out.println(nn.getSugarLevel());
		    tab[y] = nn.getSugarLevel();//tot;
		}

		int best = 0;
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(0);
		for(int j = 1; j < tab.length; j++){
		    if(tab[best]<tab[j]){
			best = j;
			a = new ArrayList<Integer>();
			a.add(j);
		    }else if(tab[best]==tab[j]){
			a.add(j);
		    }
		}

		//System.out.println("Choisi la possibilite n¡ : "+best);

		if(best < nodenids.size()){
		    Random r = new Random();
		    result.add(nodenids.get(a.get(r.nextInt(a.size()))));
		    //result.add(nodenids.get(a.get(0)));
		}
		/*if(a.size() == 1){
					result.add(nodenids.get(a.get(0)));
				}*/

		//------------------------------------------------
	    }
	}
	//---------On enregistre les resultats------------
	//On trie le resultat
	Collections.sort(result, new Comparator<NodeNid>(){
	    public int compare(NodeNid o1, NodeNid o2) {
		return o1.compareTo(o2);
	    }
	});
	//on l'enregistre dans un fichier
	File out = new File(outFile);
	PrintWriter o = new PrintWriter(out);
	String s = "";
	for(int j = 0; result.size()>j; j++){
	    StringTokenizer st = new StringTokenizer(result.get(j).getIdTask(), ".");
	    s += st.nextToken()+" "+result.get(j).getIdTask()+" "+result.get(j).word.getIDS();
	    s += "\n";
	    //System.out.println(st.nextToken()+" "+result.get(j).getIdTask()+" "+result.get(j).word.getSenseKey());
	}
	o.write(s);
	o.flush();
	o.close();
	//------------------------------------------------

    }

    public void simulation() {
	System.out.println("Debut de la simulation");
	double startTime = System.currentTimeMillis();
	this.pasFini = true;
	int random = (int)(Math.random()*1000000000);//num alatoire de la simul
	File saveFolder = new File(this.out + "/" + random);
	saveFolder.mkdir();
	
	
	for(int i = 0; i < this.cycles && this.pasFini; i++){
	    System.out.println("Cycle "+i);
	    this.step();
	    if(this.converge == 1)
		this.saveBest();
	    System.out.println("Nombre de Noeuds : "+this.nodes.size());
	    System.out.println("Nombre de Chemins : "+this.paths.size());
	    System.out.println("Nombre de Fourmis : "+this.ants.size());
	    try {
		String s = "000";
		if(i > 9){
		    s = "00";
		}
		else
		    if(i > 99){
			s = "0";
		    }
		    else
			if(i > 999){
			    s = "";
			}


		this.writeResult(saveFolder.getAbsolutePath() + "/cycle" + s + i + ".ans");
	    } catch (FileNotFoundException e){
		e.printStackTrace();
	    }
	}
	System.out.println("Fin de la simulation");
	System.out.println("Finished sim: "
		+ (System.currentTimeMillis() - startTime));
    }



}