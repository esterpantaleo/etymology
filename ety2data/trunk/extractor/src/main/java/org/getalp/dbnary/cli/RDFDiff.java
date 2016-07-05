package org.getalp.dbnary.cli;

import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RDFDiff {
	
	public static void main(String args[]) {
		Model m1, m2;
		if (args.length != 2) {
			usage();
			System.exit(1);
		}
		
		m1 = ModelFactory.createDefaultModel();
		m2 = ModelFactory.createDefaultModel();
		
		// read the RDF/XML files
		System.err.println("Reading first model.");
		m1.read(args[0], "TURTLE");
		System.err.println("Reading second model.");
		m2.read(args[1], "TURTLE");
		System.err.println("Building bindings.");
		buildBinding(m1,m2);
		
		System.err.println("Computing differences.");

		// merge the Models
		Model model = difference(m1,m2);
		
		for (Entry<String, String> e : m1.getNsPrefixMap().entrySet()) {
			model.setNsPrefix(e.getKey(), e.getValue());
		}
		// print the Model as RDF/XML
		model.write(System.out, "TURTLE");
	}

	static 	TreeMap<String, String> anodes2id = new TreeMap<String,String>();


	public static void buildBinding(Model m1, Model m2) {
		// Creates a binding between equivalent blank nodes in m1 and m2;
		ExtendedIterator<Node> iter = null;
		Node s;
		try {
			iter =  GraphUtil.listSubjects(m1.getGraph(), Node.ANY, Node.ANY);
			while (iter.hasNext()) {
				s = iter.next();
				if (s.isBlank()) {
                    ExtendedIterator<Triple> it = m1.getGraph().find(s, Node.ANY, Node.ANY);
                    SortedSet<String> signature = new TreeSet<String>();
					while (it.hasNext()) {
						Triple t = it.next();
						signature.add(t.getPredicate().toString() + "+" + t.getObject().toString());
					}
					StringBuffer b = new StringBuffer();
					for (String r: signature) {
						b.append(r).append("|");
					}
					String key = b.toString();
					assert anodes2id.get(s.getBlankNodeLabel()) == null;
					anodes2id.put(s.getBlankNodeLabel(), key);
				}
			}
		} finally {
			if (null != iter) iter.close();
		}
		try {
			iter = GraphUtil.listSubjects(m2.getGraph(), Node.ANY, Node.ANY);
			while (iter.hasNext()) {
				s = iter.next();
				if (s.isBlank()) {
                    ExtendedIterator<Triple> it = m2.getGraph().find(s, Node.ANY, Node.ANY);
					SortedSet<String> signature = new TreeSet<String>();
					while (it.hasNext()) {
						Triple t = it.next();
						signature.add(t.getPredicate().toString() + "+" + t.getObject().toString());
					}
					StringBuffer b = new StringBuffer();
					for (String r: signature) {
						b.append(r).append("|");
					}
					String key = b.toString();
					assert anodes2id.get(s.getBlankNodeLabel()) == null;
					anodes2id.put(s.getBlankNodeLabel(), key);
				}
			}
		} finally {
			if (null != iter) iter.close();
		}
	}
	
	private static Model difference(Model m1, Model m2) {
        Model resultModel = ModelFactory.createDefaultModel();
        ExtendedIterator<Triple> iter = null;
        Triple triple;
        int nbprocessed = 0, nbdiffs = 0;
        try {
            iter = GraphUtil.findAll(m1.getGraph());
            while (iter.hasNext()) {
                triple = iter.next();
                nbprocessed++;
                if (triple.getSubject().isBlank() && triple.getObject().isBlank()) {
                	// TODO
                } else if (triple.getSubject().isBlank()) {
                    ExtendedIterator<Node> it = null;
                	try {
                         it = GraphUtil.listSubjects(m2.getGraph(), triple.getPredicate(), triple.getObject());
                		if (it.hasNext()) {
                			Node ec = it.next();
                			while (it.hasNext() && ! bound(triple.getSubject(),ec)) {
                				ec = it.next();
                			}
                			if (! bound(triple.getSubject(),ec)) {
                				resultModel.getGraph().add(triple);
                                nbdiffs++;
                			}
                		} else {
                            resultModel.getGraph().add(triple);
                            nbdiffs++;
                        }
                	} finally {
                        if (null != it)
                    		it.close();
                	}
                	
                } else if (triple.getObject().isBlank()) {
                    ExtendedIterator<Node> it = null;
                	try {
                		it = GraphUtil.listObjects(m2.getGraph(),triple.getSubject(), triple.getPredicate());
                		if (it.hasNext()) {
                			Node ec = it.next();
                			while (it.hasNext() && ! bound(triple.getObject(),ec)) {
                				ec = it.next();
                			}
                			if (! bound(triple.getObject(),ec)) {
                				resultModel.getGraph().add(triple);
                                nbdiffs++;
                			}
                		} else {
                            resultModel.getGraph().add(triple);
                            nbdiffs++;
                        }
                	} finally {
                        if (null != it)
                    		it.close();
                	}
                	
                } else if (! m2.getGraph().contains(triple)) {
                    resultModel.getGraph().add(triple);
                    nbdiffs++;
                }
            System.err.print("" + nbdiffs + "/" + nbprocessed + "\r");
            }
        } finally {
            iter.close();
        }
		return resultModel;
	}


	private static boolean bound(Node n1, Node n2) {
		if (n2.isBlank()) {
			String k1 = anodes2id.get(n1.getBlankNodeLabel());
			String k2 = anodes2id.get(n2.getBlankNodeLabel());
			return k1 == k2 || (k1 != null && k1.equals(k2));
		}
		return false;
	}

	private static void usage() {
		System.out.println("Usage: java -Xmx8G " + RDFDiff.class.getCanonicalName() + " url1 url2");
	}
}
