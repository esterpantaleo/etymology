package org.getalp.blexisma.impl.semdico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ConceptualVectorRandomizer;
import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.getalp.blexisma.api.ConceptualVectorRandomizerFactory;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.SemanticDictionary;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.api.syntaxanalysis.MorphoProperties;
import org.getalp.blexisma.impl.vectorialbase.String_RAM_VectorialBase;

/**
 * @author Alexandre Labadié
 * */
public class XWNSemanticDictionnary implements SemanticDictionary {
	private static final double defaultCoeffVar = 1.5;
	
	private SemanticNetwork<String,String> network;
	private String_RAM_VectorialBase base;
	protected ConceptualVectorRandomizer randomizer;
		
	public XWNSemanticDictionnary(String_RAM_VectorialBase vb,
			SemanticNetwork<String, String> network) {
		this.base = vb;
		this.network = network;
		this.randomizer = ConceptualVectorRandomizerFactory.createRandomizer(base.getCVDimension(),base.getCVEncodingSize());
		this.randomizer.setOption("coeffVar", defaultCoeffVar);
	}
	
	/**
	 * Build semantic definition for a lemma where each sense will have a classic text definition
	 * */
	public SemanticDefinition getDefinition(String txt, String lg){
		String id = "#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt;
		Collection<? extends SemanticNetwork<String,String>.Edge> rels = network.getEdges("#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt);
		SemanticDefinition sem = new SemanticDefinition(id,base.getCVDimension(), base.getCVEncodingSize());
		
		if (rels!=null&&rels.size()>0)
			for (SemanticNetwork<String,String>.Edge r : rels)
				if (isDef(r.getDestination())){
					Collection<? extends SemanticNetwork<String,String>.Edge> defRels = network.getEdges(r.getDestination());
					ArrayList<Integer> listPos = new ArrayList<Integer>();
					
					for (SemanticNetwork<String,String>.Edge dr : defRels)
						if (isPos(dr.getDestination()))
							listPos.add(posNumber(extractPos(dr.getDestination())));
					
					for (int i=0; i<listPos.size();i++){
						ArrayList<MorphoProperties> morph = new ArrayList<MorphoProperties>();
						morph.add(posMorpho(listPos.get(i)));
						sem.addSense(new Sense(listPos.get(i)+extractDefId(r.getDestination()),extractDefTxt(r.getDestination()),
								base.getVector(listPos.get(i)+extractDefId(r.getDestination())),morph));
					}
					
				}
		
		return sem;
	}
	
	/**
	 * Build semantic definition for a lemma where each sense will have a syntactic analysis definition
	 * extracted from XWN
	 * */
	public SemanticDefinition getSyntaxDefinition(String txt, String lg){
		String id = "#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt;
		Collection<? extends SemanticNetwork<String,String>.Edge> rels = network.getEdges("#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt);
		SemanticDefinition sem = new SemanticDefinition(id,base.getCVDimension(), base.getCVEncodingSize());
		
		if (rels!=null&&rels.size()>0)
			for (SemanticNetwork<String,String>.Edge r : rels)
				if (isDef(r.getDestination())){
					Collection<? extends SemanticNetwork<String,String>.Edge> defRels = network.getEdges(r.getDestination());
					ArrayList<Integer> listPos = new ArrayList<Integer>();
					
					for (SemanticNetwork<String,String>.Edge dr : defRels)
						if (isPos(dr.getDestination()))
							listPos.add(posNumber(extractPos(dr.getDestination())));
					for (int i=0; i<listPos.size();i++){
						ArrayList<MorphoProperties> morph = new ArrayList<MorphoProperties>();
						morph.add(posMorpho(listPos.get(i)));
						
						for (SemanticNetwork<String,String>.Edge dr : defRels){
							if (isSynt(dr.getDestination())){
								sem.addSense(new Sense(listPos.get(i)+extractDefId(r.getDestination()),extractSyntAna(dr.getDestination()),
								base.getVector(listPos.get(i)+extractDefId(r.getDestination())),morph));
							}
						}
					}
				}
		
		return sem;
	}
	
	/**
	 * Build semantic definition for a lemma where each sense will have a desamb definition
	 * extracted from XWN
	 * */
	public SemanticDefinition getDisambiguationList(String txt, String lg){
		String id = "#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt;
		Collection<? extends SemanticNetwork<String,String>.Edge> rels = network.getEdges("#"+ISO639_3.sharedInstance.getIdCode(lg)+"|"+txt);
		SemanticDefinition sem = new SemanticDefinition(id,base.getCVDimension(), base.getCVEncodingSize());
		
		if (rels!=null&&rels.size()>0)
			for (SemanticNetwork<String,String>.Edge r : rels) {
				if (isDef(r.getDestination())) {
					Collection<? extends SemanticNetwork<String,String>.Edge> defRels = network.getEdges(r.getDestination());
					ArrayList<Integer> listPos = new ArrayList<Integer>();
					
					for (SemanticNetwork<String,String>.Edge dr : defRels)
						if (isPos(dr.getDestination()))
							listPos.add(posNumber(extractPos(dr.getDestination())));
					for (int i=0; i<listPos.size();i++){
						ArrayList<MorphoProperties> morph = new ArrayList<MorphoProperties>();
						morph.add(posMorpho(listPos.get(i)));
						
						for (SemanticNetwork<String,String>.Edge dr : defRels){
							if (isDesamb(dr.getDestination())){
								sem.addSense(new Sense(listPos.get(i)+extractDefId(r.getDestination()),extractDesamb(dr.getDestination()),
								base.getVector(listPos.get(i)+extractDefId(r.getDestination())),morph));
							}
						}
					}
				}
			}
		return sem;
	}


	@Override
	public ArrayList<String> getProx(String lemme, String lang, int nb) {
		SemanticDefinition def = getDefinition(lemme, lang);
		ArrayList<String_RAM_VectorialBase.EntryDist> proxDef = new ArrayList<String_RAM_VectorialBase.EntryDist>();
		ArrayList<String> proxs = new ArrayList<String>();
		
		def.computeMainVector();
		
		proxDef = base.getProx(def.getMainVector(), nb);
		
		for (String_RAM_VectorialBase.EntryDist entry : proxDef){
			proxs.add(retreiveNetNodeFromBaseId(entry.lexObj)+": "+entry.distance);
		}
		
		return proxs;
	}

	@Override
	public ArrayList<String> getProx(String lemme, String lang, String regex,
			int nb) {
		SemanticDefinition def = getDefinition(lemme, lang);
		ArrayList<String_RAM_VectorialBase.EntryDist> proxDef = new ArrayList<String_RAM_VectorialBase.EntryDist>();
		ArrayList<String> proxs = new ArrayList<String>();
		
		def.computeMainVector();
		
		proxDef = base.getProx(def.getMainVector(), nb,regex);
		
		for (String_RAM_VectorialBase.EntryDist entry : proxDef){
			proxs.add(retreiveNetNodeFromBaseId(entry.lexObj)+": "+entry.distance);
		}
		
		return proxs;
	}

	@Override
	public ArrayList<String> getProx(ConceptualVector cv, int nb) {
		ArrayList<String> listProxLemma = new ArrayList<String>();
		ArrayList<String_RAM_VectorialBase.EntryDist> listProxId = base.getProx(cv, nb);
		
		for (String_RAM_VectorialBase.EntryDist entry : listProxId){
			listProxLemma.add(retreiveNetNodeFromBaseId(entry.lexObj)+": "+entry.distance);
		}
		
		return listProxLemma;
	}
	
	private String retreiveNetNodeFromBaseId(String baseId){
		String id = baseId.substring(1);
		MorphoProperties pos = posMorpho(Integer.parseInt(baseId.substring(0, 1)));
		Iterator<?> edges = network.getEdgesIterator();
		
		while (edges.hasNext()){
			@SuppressWarnings("unchecked")
			SemanticNetwork<String,String>.Edge e = (SemanticNetwork.Edge)edges.next();
			if (e.getDestination().contains(id)){
				if (defHasMorpho(e.getDestination(),pos))
					return e.getOrigin();
			}
		}
		return "In base but not in network";
	}
	
	private boolean defHasMorpho(String def, MorphoProperties pos){
		
		if (isDef(def)){
			Iterator<?> edges = network.getEdges(def).iterator();
			while (edges.hasNext()){
				@SuppressWarnings("unchecked")
				SemanticNetwork<String,String>.Edge e = (SemanticNetwork.Edge)edges.next();
				if (isPos(e.getDestination())){
					if (posNumber(extractPos(e.getDestination()))==morphoNumber(pos))
						return true;
				}
			}
		}
		
		return false;
	}
	
	private int morphoNumber(MorphoProperties m){
		if (m==MorphoProperties.NOUN) return 1;
		else if (m==MorphoProperties.VERB) return 2;
		else if (m==MorphoProperties.ADJECTIVE) return 3;
		else return 4;
	}
	
	public static boolean isDef(String s){
		return s.contains("#def|")&&(s.split("\\|").length==4);
	}
	
	public static String extractDefId(String s){
		return s.split("\\|")[2];
	}
	
	public static String extractDefLg(String s){
		return s.split("\\|")[1];
	}
	
	public static String extractDefTxt(String s){
		return s.split("\\|")[3];
	}
	
	public static boolean isPos(String s){
		return s.contains("#pos|")&&(s.split("\\|").length==2);
	}
	
	public static String extractPos(String s){
		return s.split("\\|")[1];
	}
	
	private int posNumber(String p){
		if (p.equals("noun")) return 1;
		if (p.equals("verb")) return 2;
		if (p.equals("adj")) return 3;
		else return 4;
	}
	
	private MorphoProperties posMorpho(int i){
		if (i==1) return MorphoProperties.NOUN;
		if (i==2) return MorphoProperties.VERB;
		if (i==3) return MorphoProperties.ADJECTIVE;
		else return MorphoProperties.ADVERB;
	}
	
	public static boolean isLemma(String s, String l){
		return s.contains("#"+ISO639_3.sharedInstance.getIdCode(l)+"|")&&(s.split("\\|").length==2);
	}
	
	public static String extractLemmaText(String s) {
		return s.split("\\|")[1];
	}
	
	public static String extractLemmaLg(String s) {
		return s.split("\\|")[0].substring(1);
	}
	
	public static boolean isSynt(String s){
		return s.contains("#synt|")&&(s.split("\\|").length==2);
	}
	
	public static String extractSyntAna(String s){
		return s.split("\\|")[1];
	}
	
	public static boolean isDesamb(String s){
		return s.contains("#desamb|")&&(s.split("\\|").length==2);
	}
	
	public static String extractDesamb(String s){
		return s.split("\\|")[1];
	}
	
//	private HashMap<String,String> extractDesambMap(String s){
//		HashMap<String,String> map = new HashMap<String,String>();
//		String[] tab = s.split("\\|")[1].split(";");
//		
//		for (String d : tab){
//			if (!d.equals("")){
//				map.put(d.split(",")[0], d.split(",")[1]);
//			}
//		}
//		
//		return map;
//	}


	public void setCoeffVar(double c) {
		this.randomizer.setOption("coeffVar", c);
	}

	public ConceptualVector getRandomVector() throws UninitializedRandomizerException {
		return randomizer.nextVector();
	}
	
	
}
