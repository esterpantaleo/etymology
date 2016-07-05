package org.getalp.dbnary;

public interface IWiktionaryExtractor {
	void setWiktionaryIndex(WiktionaryIndex wi);
	void extractData(String wiktionaryPageName, String pageContent);

}
