package org.getalp.dbnary.deu;

import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.model.WikiModelContentException;
import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.WiktionaryIndex;

import java.util.Locale;
import java.util.Map;

/**
 * Created by serasset on 07/01/15.
 */
public class GermanDBnaryWikiModel extends DbnaryWikiModel {

    public GermanDBnaryWikiModel(Locale locale, String imageBaseURL, String linkBaseURL) {
        super(locale, imageBaseURL, linkBaseURL);
    }

    public GermanDBnaryWikiModel(WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
        super(wi, locale, imageBaseURL, linkBaseURL);
    }

    // Hack: German wiktionary uses #WEITERLEITUNG instead of #REDIRECT,
    // fix it in the raw wiki text as bliki expects #redirect
    @Override
    public String getRawWikiContent(ParsedPageName parsedPagename, Map<String, String> map) throws WikiModelContentException {
        String result = super.getRawWikiContent(parsedPagename, map);
        if (result != null) {
            if (result.startsWith("#WEITERLEITUNG")) {
                result = "#REDIRECT" + result.substring(12);
            }
            return result;
        }
        return null;
    }

}
