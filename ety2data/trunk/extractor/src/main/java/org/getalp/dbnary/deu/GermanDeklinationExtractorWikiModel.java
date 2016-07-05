package org.getalp.dbnary.deu;

import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.PropertyObjectPair;
import org.getalp.dbnary.WiktionaryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.getalp.dbnary.deu.GermanInflectionData.*;


public class GermanDeklinationExtractorWikiModel extends GermanTableExtractorWikiModel {
    private Logger log = LoggerFactory.getLogger(GermanDeklinationExtractorWikiModel.class);

	public GermanDeklinationExtractorWikiModel(IWiktionaryDataHandler wdh, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL, wdh);
	}

	@Override
	protected GermanInflectionData getInflectionDataFromCellContext(List<String> context) {
		GermanInflectionData inflection = new GermanInflectionData();
		boolean isArticleColumn = false;
		for (String h : context) {
            h = h.trim();
            switch (h) {
				case "Positiv":
					inflection.degree = Degree.POSITIVE;
					break;
				case "Komparativ":
					inflection.degree = Degree.COMPARATIVE;
					break;
				case "Superlativ":
					inflection.degree = Degree.SUPERLATIVE;
					break;
				case "Starke Deklination":
					inflection.inflectionType = InflectionType.STRONG;
					break;
				case "Schwache Deklination":
					inflection.inflectionType = InflectionType.WEAK;
					break;
				case "Gemischte Deklination":
					inflection.inflectionType = InflectionType.MIXED;
					break;
				case "Prädikativ":
					inflection.inflectionType = InflectionType.NOTHING;
					inflection.note.add("Prädikativ");
					break;
				case "Singular":
					inflection.number = GNumber.SINGULAR;
					break;
				case "Plural":
					inflection.number = GNumber.PLURAL;
					break;
				case "Maskulinum":
					inflection.genre = Genre.MASCULIN;
					break;
				case "Femininum":
					inflection.genre = Genre.FEMININ;
					break;
				case "Neutrum":
					inflection.genre = Genre.NEUTRUM;
					break;
				case "Artikel":
					isArticleColumn = true;
					break;
				case "Wortform":
					isArticleColumn = false;
					break;
                case "Nominativ":
                    inflection.cas = Cas.NOMINATIF;
                    break;
                case "Genitiv":
                    inflection.cas = Cas.GENITIF;
                    break;
                case "Dativ":
                    inflection.cas = Cas.DATIF;
                    break;
                case "Akkusativ":
                    inflection.cas = Cas.ACCUSATIF;
                    break;
				case "—":
                case "":
                case " ":
					break;
				default:
					log.debug("Deklination Extraction: Unhandled header {} in {}", h, wdh.currentLexEntry());
			}
		}
		if (isArticleColumn) return null;
		return inflection;
	}

	private static HashSet<String> declinatedFormMarker;
	static{
		declinatedFormMarker = new HashSet<String>();
		declinatedFormMarker.add("adjektivische Deklination");
	}


	// FIXME: check if remaining code is dead code...
	private void addForm(HashSet<PropertyObjectPair> infl, String s){
		s=s.replace("]", "").replace("[", "").replaceAll(".*\\) *", "").replace("(", "").trim();
		if (s.length() == 0 || s.equals("—") || s.equals("-")) return;

		wdh.registerInflection("deu", wdh.currentWiktionaryPos(), s, wdh.currentLexEntry(), 1, infl);
	}
	
	//extract a String in s between start and end
	private String extractString(String s, String start, String end){
		String res;
		int startIndex,endIndex;
		startIndex=getIndexOf(s, start, 0);
		endIndex=getIndexOf(s, end, startIndex);
		res=s.substring(startIndex, endIndex);
		return res;
	}
	
	//return the index of pattern in s after start
	private int getIndexOf(String s, String pattern, int start){
		int ind = s.indexOf(pattern, start);
		if (ind <=start || ind >s.length()) {
			ind=s.length();
		}
		return ind;
	}

	//for the phrasal verb, extract the part without spaces : example extractPart("ich komme an")->an
	private String extractPart(String form){
		String res="";
		int i=form.length()-1;
		char cc=form.charAt(i);
		while (0<=i && ' '!=cc) {
			res=cc+res;
			i--;
			cc=form.charAt(i);
		}
		return res;
	}

	//remove spaces before the first form's character and after the last form's character
	//and the unsecable spaces
	private String removeUselessSpaces(String form){
		form =form.replace(" "," ").replace("&nbsp;"," ").replace("\t"," ");//replace unsecable spaces
		String res=form.replace("  "," ");
		if(!res.isEmpty()){
		int debut=0,fin=res.length()-1;
		char cdebut=res.charAt(debut),cfin=res.charAt(fin);
		while (fin> debut && (' '==cdebut || ' '==cfin)) {
			if (' '==cdebut) {
				debut++;
				cdebut=res.charAt(debut);
			}
			if (' '==cfin) {
				fin--;
				cfin=res.charAt(fin);
			}
		}
		res = res.substring(debut,fin+1);
		}
		return res;
	}

	//return if the form given in parameter is a phrasal verb
	private boolean isPhrasalVerb(String form){
		int nbsp=nbSpaceForm(form);
//		return ((!reflexiv && nbsp>=2) || (reflexiv && nbsp>=3));
		return 2<=nbsp;
	}
	
	private int nbSpaceForm(String form){
		int nbsp=0;
		for(int i=1; i<form.length()-1;i++){
			if(' '==form.charAt(i)){
				nbsp++;
			}
		}
		return nbsp;
	}
	
	//otherway some phrasal verb don't have any inflected form
//	public String prepareForTransclusion(String rawWikiText) {
//		return rawWikiText;
//	}
	

}
