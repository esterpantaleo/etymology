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


public class GermanSubstantiveDeklinationExtractorWikiModel extends GermanTableExtractorWikiModel {
    private Logger log = LoggerFactory.getLogger(GermanSubstantiveDeklinationExtractorWikiModel.class);

	public GermanSubstantiveDeklinationExtractorWikiModel(IWiktionaryDataHandler wdh, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(wi, locale, imageBaseURL, linkBaseURL, wdh);
	}

	@Override
	protected GermanInflectionData getInflectionDataFromCellContext(List<String> context) {
		GermanInflectionData inflection = new GermanInflectionData();
		boolean isArticleColumn = false;
		for (String h : context) {
            h = h.trim();
            switch (h) {
				case "Singular":
                case "Singular 1":
                case "Singular 2":
                case "Singular 3":
                case "Singular 4":
					inflection.number = GNumber.SINGULAR;
					break;
				case "Singular m":
					inflection.number = GNumber.SINGULAR;
					inflection.genre = Genre.MASCULIN;
					break;
				case "Singular f":
					inflection.number = GNumber.SINGULAR;
					inflection.genre = Genre.FEMININ;
					break;
				case "Plural":
                case "Plural 1":
                case "Plural 2":
                case "Plural 3":
                case "Plural 4":
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
					log.debug("Substantiv Deklination Extraction: Unhandled header {} in {}", h, wdh.currentLexEntry());
			}
		}
		if (isArticleColumn) return null;
		return inflection;
	}

}
