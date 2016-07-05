package org.getalp.blexisma.impl.semdico;

import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.ADJECTIVE;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.ADVERB;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.NOUN;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.OTHER;
import static org.getalp.blexisma.api.syntaxanalysis.MorphoProperties.VERB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

public class WiktionaryBasedSemanticDictionary implements SemanticDictionary {
	private static final HashMap<String, List<MorphoProperties>> morphoProperties;
	private static final List<MorphoProperties> other = Arrays.asList(OTHER);
	private static final String DEF_RELATION = "def";
	private static final String POS_RELATION = "pos";
	private static final double defaultCoeffVar=1.5;

	static {
		morphoProperties = new HashMap<String, List<MorphoProperties>>(100);
		morphoProperties.put("#pos|-adj-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-/2", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-dém-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-excl-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-indéf-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-int-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-num-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adj-pos-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-adv-", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|-adv-int-", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|-adv-pron-", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|-adv-rel-", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|-aux-", Arrays.asList(VERB));
		morphoProperties.put("#pos|-loc-adj-", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|-loc-adv-", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|-loc-nom-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-loc-verb-", Arrays.asList(VERB));
		morphoProperties.put("#pos|-nom-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-fam-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-ni-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-nu-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-nn-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-npl-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-pr-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-nom-sciences-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-onoma-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-prénom-", Arrays.asList(NOUN));
		morphoProperties.put("#pos|-verb-", Arrays.asList(VERB));
		morphoProperties.put("#pos|-verb-pr-", Arrays.asList(VERB));

		morphoProperties.put("#pos|Noun", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Adjective", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|Adverb", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|Verb", Arrays.asList(VERB));
		morphoProperties.put("#pos|Proper noun", Arrays.asList(NOUN));

		morphoProperties.put("#pos|Adjektiv", Arrays.asList(ADJECTIVE));
		morphoProperties.put("#pos|Adverb", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|Eigenname", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Erweiterter Infinitiv", Arrays.asList(VERB));
		morphoProperties.put("#pos|Fokuspartikel", Arrays.asList(OTHER));
		morphoProperties.put("#pos|Hilfsverb", Arrays.asList(VERB));
		morphoProperties.put("#pos|Interrogativadverb", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|Konjugierte Form", Arrays.asList(VERB));
		morphoProperties.put("#pos|Nachname", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Onomatopoetikum", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Ortsnamen-Grundwort", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Pronominaladverb", Arrays.asList(ADVERB));
		morphoProperties.put("#pos|Substantiv", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Toponym", Arrays.asList(NOUN));
		morphoProperties.put("#pos|Verb", Arrays.asList(VERB));
		morphoProperties.put("#pos|Vorname", Arrays.asList(VERB));

		// ADJECTIVE,ADVERB,NOUN,VERB,OTHER,MASCULINE,FEMININE,NEUTRAL,TRANSITIVE,INTRANSITIVE,DIRECTTRANSITIVE,SINGULAR,
		// PLURAL;
	}

	private SemanticNetwork<String, String> wiktionaryNetwork;
	protected String_RAM_VectorialBase vectorialBase;
	private ConceptualVectorRandomizer randomizer;

	public WiktionaryBasedSemanticDictionary(String_RAM_VectorialBase vb,
			SemanticNetwork<String, String> sn) {
		this.wiktionaryNetwork = sn;
		this.vectorialBase = vb;
		randomizer = ConceptualVectorRandomizerFactory.createRandomizer(this.vectorialBase.getCVDimension(), this.vectorialBase.getCVEncodingSize());
		randomizer.setOption("coefVar", defaultCoeffVar);
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.api.SemanticDictionary#getDefinition(java.lang.String, java.lang.String)
	 */
	@Override
	public SemanticDefinition getDefinition(String txt, String lg) {
		SemanticDefinition sdef = getDefinitionWithNullVectorsWhenUnknown(txt, lg);
		for(Sense sense : sdef.getSenseList()) {
			if (sense == null) continue;
			if (sense.getVector() == null) {
				try {
					sense.setVector(randomizer.nextVector());
				} catch (UninitializedRandomizerException e) {
					// The randomizer should be initialized correctly.
					e.printStackTrace();
				}
				vectorialBase.addVector(sdef.getId(), sense.getVector());
			}
		}
		return sdef;
	}
	
	protected SemanticDefinition getDefinitionWithNullVectorsWhenUnknown(String txt, String lg) {
		String nodename = getNodeName(txt, lg);
		Collection<? extends SemanticNetwork<String, String>.Edge> edges = wiktionaryNetwork
				.getEdges(nodename);
		ArrayList<Sense> senses = new ArrayList<Sense>();
		ConceptualVector mcv = vectorialBase.getVector(nodename);

		if (null == edges)
			return new SemanticDefinition(nodename, mcv, senses);

		for (SemanticNetwork<String, String>.Edge edge : edges) {
			if (edge.getRelation().equals(DEF_RELATION)) {
				String def = edge.getDestination();
				ConceptualVector cv = vectorialBase.getVector(def);
				List<MorphoProperties> morph = getMorphoProperties(def);
				Sense s = new Sense(def,def, cv, morph);
				senses.add(s);
			}
		}
		return new SemanticDefinition(nodename, mcv, senses);
	}
	
	public ArrayList<String> getProx(String lemme, String lang, int nb) {
		ArrayList<String> list = null;
		ArrayList<String_RAM_VectorialBase.EntryDist> tmpList = null;
		String l = getNodeName(lemme,lang);
		ConceptualVector vec = vectorialBase.getVector(l);
		
		if (vec!=null) {
			list = new ArrayList<String>();
			tmpList = vectorialBase.getProx(vec, nb);
			for (int i=0; i<tmpList.size(); i++) {
				list.add(tmpList.get(i).lexObj);
			}
		}
		
		return list;
	}

	/* (non-Javadoc)
	 * @see org.getalp.blexisma.api.SemanticDictionary#getProx(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public ArrayList<String> getProx(String lemme, String lang, String regex,
			int nb) {
		ArrayList<String> list = null;
		ArrayList<String_RAM_VectorialBase.EntryDist> tmpList = null;
		String l = getNodeName(lemme,lang);
		ConceptualVector vec = vectorialBase.getVector(l);
		
		if (vec!=null) {
			list = new ArrayList<String>();
			tmpList = vectorialBase.getProx(vec, nb, regex);
			for (int i=0; i<tmpList.size(); i++) {
				list.add(tmpList.get(i).lexObj);
			}
		}
		return list;
	}
	
	@Override
	public ArrayList<String> getProx(ConceptualVector cv, int nb) {
		ArrayList<String> list = null;
		ArrayList<String_RAM_VectorialBase.EntryDist> tmpList = null;
		
		if (cv!=null) {
			list = new ArrayList<String>();
			tmpList = vectorialBase.getProx(cv, nb);
			for (int i=0; i<tmpList.size(); i++) {
				list.add(tmpList.get(i).lexObj);
			}
		}
		
		return list;
	}

	private static String getNodeName(String txt, String lg) {
		String lang = ISO639_3.sharedInstance.getIdCode(lg);
		return "#" + lang + "|" + txt;
	}

	private List<MorphoProperties> getMorphoProperties(String def) {
		Collection<? extends SemanticNetwork<String, String>.Edge> edges = wiktionaryNetwork
				.getEdges(def);
		for (SemanticNetwork<String, String>.Edge edge : edges) {
			if (edge.getRelation().equals(POS_RELATION)) {
				List<MorphoProperties> morph = morphoProperties.get(edge
						.getDestination());
				return (null == morph) ? other : morph;
			}
		}
		return other;
	}

	public void setCoeffVar(double c) {
		randomizer.setOption("coefVar", c);
	}
}
