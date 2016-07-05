package fr.lig.getalp.omnia.wsd;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Dictionnary.Dictionary;
import Dictionnary.RAMDict;
import Dictionnary.Word;
import acfast.Global;
import context.Ant;
import context.Node;
import context.NodeMot;
import context.NodeNid;
import context.Path;
import fr.lig.getalp.falaise.minisysq.QArc;
import fr.lig.getalp.falaise.minisysq.QNode;
import fr.lig.getalp.falaise.minisysq.omnia.OmniaArc;
import fr.lig.getalp.falaise.minisysq.omnia.OmniaGraph;
import fr.lig.getalp.falaise.minisysq.omnia.OmniaLexicalUnit;
import fr.lig.getalp.falaise.minisysq.omnia.OmniaUW;

/**
 * *
 * Context.java
 * Created on 28 juin 2010 20:55:33
 * 
 * Copyright (c) 2010 Didier Schwab
 */

/**
 * @author Didier SCHWAB
 *
 */
public class Context extends acfast.Context {

    protected HashMap<NodeNid, OmniaUW> Nid2UW;
    protected Dictionary dict;

    public Context(String conf) {
    	super(conf);
    	Nid2UW = new HashMap<NodeNid, OmniaUW>(1000);
    }

    public Context(InputStream is) {
    	super(is);
    	this.dict = new RAMDict(this.dico);
    	Nid2UW = new HashMap<NodeNid, OmniaUW>(1000);
    }

    //Lecture du fichier d'entre
    public void buildContext(){


    }

    private void initAll(){

	this.nodes = new ArrayList<Node>(100);
	this.paths = new ArrayList<Path>(100);
	this.ants = new ArrayList<Ant>(1000);
	this.nests = new ArrayList<Node>(100); 
	this.idAnt = 0;
	this.idNode = 0;
	this.idPath = 0;
	Nid2UW = new HashMap<NodeNid, OmniaUW>(1000);
    }

    public OmniaGraph desamb (String qgraph)throws Exception {

	OmniaGraph g = new OmniaGraph(qgraph, OmniaArc.ARC_TYPE_UW_1);

	//g.load(qgraph);

	return desamb(g);
    }

    public OmniaGraph desamb (File qgraph) throws Exception {

	OmniaGraph g = new OmniaGraph(qgraph, OmniaArc.ARC_TYPE_UW_1);

	//g.load(qgraph);
	
	return desamb(g);
    }

    private ArrayList<Node> buildGraph(QNode node, HashSet<QNode> SetNodes, HashMap<String, Node> currentNodesMots) throws Exception {

	if(SetNodes.contains(node)){

	    //   System.out.println("FIN " + node.getName());
	    return null;
	}

	SetNodes.add(node);

	ArrayList<Node> answer = new  ArrayList<Node>(5);

	Node nodeMot=null;

	for(QArc qa : node.getNextArcs()){

		//  System.out.println(qa.getStartNode().getName() + " -> " + qa.getEndNode().getName());

		//for(OmniaLexicalUnit olu : (new OmniaArc(qa.getStartNode(), qa.getTree(), qa.getEndNode(), OmniaArc.ARC_TYPE_UW_1)).getLexicalUnits()){

		for(OmniaLexicalUnit olu : (new OmniaArc(qa, OmniaArc.ARC_TYPE_UW_1)).getLexicalUnits()){

			nodeMot = currentNodesMots.get(olu.getId());
			//nodeMot = currentNodesMots.get(olu.getLemma().toLowerCase());

			if(nodeMot==null){

				//  System.out.println("nodeMot null");
				nodeMot = new NodeMot(olu.getLemma().toLowerCase(), this, Global.convert(olu.getCat()));

				this.addNode(nodeMot);
				currentNodesMots.put(olu.getLemma().toLowerCase(), nodeMot);
				//    System.out.println("currentNodesMots.get(olu.getLemma()) "+currentNodesMots.get(olu.getLemma()));


				for(OmniaUW ouw : olu.getUWs()){

					List<Word> ALMW = this.dict.getWords(ouw.getPivaxId());

					if(ALMW !=null){

						NodeNid nodeNid = new NodeNid(ouw.getPivaxId(), this, Global.convert(olu.getCat()), ALMW.get(0), (NodeMot)nodeMot);

						Nid2UW.put(nodeNid, ouw);

						this.addNode(nodeNid);
						this.addNest(nodeNid);
						this.addPath(nodeMot, nodeNid);
					}
				}
			}
		}
		if(nodeMot==null){

			nodeMot =  new Node(qa.getStartNode().getName() + "-" + qa.getEndNode().getName(), this);
		}

		answer.add(nodeMot);

		ArrayList<Node> listNode = buildGraph(qa.getEndNode(), SetNodes, currentNodesMots);

		if(listNode!=null)
			for(Node n : listNode){

				if(n!=null)
					addPath(nodeMot, n);
			}
	}

	return answer;
    }

    public OmniaGraph desamb (OmniaGraph g) throws Exception {

	initAll();
	
	QNode initialNode = g.getInitialState();

	HashSet<QNode> SetNodes = new HashSet<QNode>(500);

	buildGraph(initialNode, SetNodes, new HashMap<String, Node>(1000));

	simulation();

	putScore();
	

	return g;
    }

    private void putScore(){

    	ArrayList<NodeNid> T = new ArrayList<NodeNid>(nodes.size());

    	if(Nid2UW.isEmpty())
    		return;
    	else {


    		//minimum gnral

    		for(Node n :nodes){

    			if(n instanceof NodeNid){

    				T.add((NodeNid)n);
    			}
    		}

    		int min = min(T);

    		//calcul de la somme totale

    		int sum = 0;

    		for(Node n :nodes){

    			if(n instanceof NodeNid){

    				sum+=n.getSugarLevel()-min+1;
    			}
    		}

    		//calcul des scores

    		for(Node n :nodes){

    			if(n instanceof NodeMot && ! (n instanceof NodeNid)){

    				//    System.out.println("@@@@@@@@");
    				//    System.out.println(n);
    				T = new ArrayList(n.getPaths().size()); 
    				for(Path p :n.getPaths()){

    					if(p.getTarget() instanceof NodeNid){

    						T.add((NodeNid)p.getTarget());
    					}
    				}
    				if(T.size()!=0)
    					putScore(T, sum);
    			}
    		}
    	}
    }

    /**
     * Calcule les scores locaux, cad les scores pour les acceptions d'un mme item lexical et gnraux (prise en compte des trajectoires avec sum)
     * @param nids
     */

    private void putScore(ArrayList<NodeNid> nids, int sumGen){

    	int min = min(nids);
    	int sum=0;
    	if(min > 0){

    		for(NodeNid n : nids){

    			sum+=n.getSugarLevel();
    		}

    		for(NodeNid n : nids){

    			Nid2UW.get(n).setDesambScore((double)n.getSugarLevel()/(double)sum);
    			//Nid2UW.get(n).setAttrib("RawScore", (double)n.getSugarLevel());
    			Nid2UW.get(n).setAttrib("OverallScore", ((double)(n.getSugarLevel()-min+1))/(double)sumGen);
    		}
    	}
    	else{
    		System.out.println("Min is negative for:" + nids.get(0).getLemme() + "/" + nids.get(0).getLabel());
    		for(NodeNid n : nids){

    			sum+=n.getSugarLevel()-min+1;
    		}

    		for(NodeNid n : nids){

    			Nid2UW.get(n).setDesambScore((double)(n.getSugarLevel()-min+1)/(double)sum);
    			//Nid2UW.get(n).setAttrib("RawScore", (double)n.getSugarLevel());
    			Nid2UW.get(n).setAttrib("OverallScore", ((double)(n.getSugarLevel()-min+1))/(double)sumGen);

    		}
    	}
    }

    private static int min(ArrayList<NodeNid> nids){

	int min = Integer.MAX_VALUE;

	for(NodeNid n : nids){

	    if(n.getSugarLevel() < min)
		min = n.getSugarLevel();
	}

	return min;
    }


    public void simulation() {
	//	System.out.println("Debut de la simulation");
	/*System.out.println("Nombre de Noeuds : "+this.nodes.size());
	System.out.println("Nombre de Chemins : "+this.paths.size());
	System.out.println("Nombre de Ponts : "+this.ponts.size());
	System.out.println("Nombre de Fourmis : "+this.ants.size());*/
	//	double startTime = System.currentTimeMillis();
	for(int i = 0; i < this.cycles; i++){

	    this.step();

	    /*    if(i!=0 && i%50==0){



		for(Node n :this.nodes){

		    System.out.println(n.getLabel() + " : " + n.getSugarLevel());
		}
		//System.exit(0);
	    }*/




	    //  System.out.println("Cycle "+i);
	    
	    System.out.println("Nombre de Noeuds : "+this.nodes.size());
	    System.out.println("Nombre de Chemins : "+this.paths.size());
	   // System.out.println("Nombre de Ponts : "+this.ponts.size());
	    System.out.println("Nombre de Fourmis : "+this.ants.size());
	    /*    try {
		//Thread.sleep(10);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }*/
	}
	/*for(Node n :this.nodes){

	    System.out.println(n.getLabel() + " : " + n.getSugarLevel());
	}

	System.out.println("Nombre de Noeuds : "+this.nodes.size());
	System.out.println("Nombre de Chemins : "+this.paths.size());
	System.out.println("Nombre de Ponts : "+this.ponts.size());
	System.out.println("Nombre de Fourmis : "+this.ants.size());

	System.out.println("Fin de la simulation");
	System.out.println("Finished sim: "
		+ (System.currentTimeMillis() - startTime));*/
    }

    public static void main(String[] args) {
		try {
			InputStream is = Context.class.getClassLoader().getResourceAsStream("config.xml");
	    	Context c = new Context(is);
	    	OmniaGraph g;
	    	double time = System.currentTimeMillis();
			g = c.desamb(new File(args[0]));
	    	System.out.println(g.toString());
	    	System.err.println(System.currentTimeMillis()-time);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}