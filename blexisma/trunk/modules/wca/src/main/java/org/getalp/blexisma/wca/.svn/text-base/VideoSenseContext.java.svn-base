package org.getalp.blexisma.wca;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import org.getalp.blexisma.wca.OpenFstTransducer.Transition;

import Dictionnary.Dictionary;
import Dictionnary.H2Dictionary;
import Dictionnary.RAMDict;
import Dictionnary.Word;
import acfast.Global;
import context.Ant;
import context.Node;
import context.NodeMot;
import context.NodeNid;
import context.Path;

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
public class VideoSenseContext extends acfast.Context {

	// protected HashMap<NodeNid, OmniaUW> Nid2UW;
	protected HashMap<Node, Transition> node2trans;

	public VideoSenseContext(String conf) {
		super(conf);
		node2trans = new HashMap<Node, Transition>(1000);
	}

	public VideoSenseContext(InputStream conf) {
		super(conf);
		node2trans = new HashMap<Node, Transition>(1000);
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
		node2trans = new HashMap<Node, Transition>(1000);
	}

	public OpenFstTransducer desamb (Dictionary dict, String fstFile) throws Exception {

		OpenFstTransducer g = new OpenFstTransducer(fstFile);

		return desamb(dict, g);
	}

	public OpenFstTransducer desamb (Dictionary dict, File fstFile) throws Exception {

		OpenFstTransducer g = new OpenFstTransducer(fstFile);

		return desamb(dict, g);
	}

	private void buildGraph(Dictionary dict, OpenFstTransducer g) throws Exception {

		HashMap<String, Node> currentNodesMots = new HashMap<String, Node>();
		Node nodeMot=null;

		// TODO: Should we link all nodeMots together to create a shortcut between all nodes in the graph ? 
		// or maybe we should expand the ants life for it to cross more transitions...
		// TODO: adapt to the new openfst format ('x' = char, zzz%n = lemma, %xyz = dbpedia named entity
		for(Transition qa : g.transitions()) {
			if (qa.lbl.startsWith("#") || qa.lbl.startsWith("%") ) {
				// It's a lemma (#) or dbpedia named entity (%) transition
				// DONE: shall we unify the node for duplicate words ? --> No create one nodeMot per occurence.
				//System.err.println("Treating Lemma:" + qa.lbl);
				// nodeMot = currentNodesMots.get(qa.lbl);
				//if(nodeMot==null) {
				String lemme = qa.lbl.substring(1);
				// DONE: get the pos and use it to distinguish word nodes
				nodeMot = new NodeMot(lemme, this, Global.NONE);

				this.addNode(nodeMot);
				currentNodesMots.put(qa.lbl, nodeMot);
				node2trans.put(nodeMot, qa);
				List<Word> ALMW = dict.getWords(lemme);

				for(Word w : ALMW) {

					NodeNid nodeNid = new NodeNid(w.getIDS(), this, Global.NONE, w, (NodeMot)nodeMot);

					this.addNode(nodeNid);
					this.addNest(nodeNid);
					this.addPath(nodeMot, nodeNid);
				}
				//}
		} else {
			// It's a french symbol node
			// System.err.println("Treating char:" + qa.lbl);
			nodeMot =  new Node(qa.from + "-" + qa.to, this);
			this.addNode(nodeMot);
			currentNodesMots.put(qa.lbl, nodeMot);
		} 
		}
		for (int i=0; i <= g.nbState(); i++) {
			for (Transition f: g.incoming(i)) {
				for (Transition t: g.outgoing(i)) {
					addPath(currentNodesMots.get(f.lbl), currentNodesMots.get(t.lbl));
				}
			}
		}

		// writeContext(System.err);
	}

	public OpenFstTransducer desamb (Dictionary dict, OpenFstTransducer g) throws Exception {

		initAll();

		// QNode initialNode = g.getInitialState();

		buildGraph(dict, g);

		simulation();

		putScore(g);


		return g;
	}

	private void putScore(OpenFstTransducer g){

		ArrayList<NodeNid> T = new ArrayList<NodeNid>(nodes.size());
		//minimum gnral

		for(Node n :nodes){
			if(n instanceof NodeNid){
				T.add((NodeNid)n);
			}
		}

		//    	int min = min(T);
		//    	int max = max(T);

		// System.err.println("max: " + max);
		for(Node n : nodes){

			if(n instanceof NodeMot && ! (n instanceof NodeNid)){
				Transition t = node2trans.get(n);
				for(Path p :n.getPaths()) {
					if(p.getTarget() instanceof NodeNid) {
						g.addTransition(t.from, t.to, p.getTarget().getLabel(), ((float) p.getTarget().getSugarLevel()));
					}
				}
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

	private static int max(ArrayList<NodeNid> nids){

		int max = Integer.MIN_VALUE;

		for(NodeNid n : nids) {
			if(n.getSugarLevel() > max)
				max = n.getSugarLevel();
		}

		return max;
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




			// System.out.println("Cycle "+i);

			//	    System.out.println("Nombre de Noeuds : "+this.nodes.size());
			//	    System.out.println("Nombre de Chemins : "+this.paths.size());
			//	    System.out.println("Nombre de Ponts : "+this.ponts.size());
			//	    System.out.println("Nombre de Fourmis : "+this.ants.size());
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

}