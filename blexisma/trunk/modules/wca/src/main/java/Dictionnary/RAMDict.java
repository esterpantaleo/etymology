package Dictionnary;


import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


import acfast.Global;

import Parser.ParserAdapted;

public class RAMDict implements Dictionary {

	private HashMap<String, ArrayList<Word>> dico;

	public RAMDict(String file) {
		this.dico = new HashMap<String, ArrayList<Word>>();
		try {
			XMLReader saxReader = XMLReaderFactory.createXMLReader();
			saxReader.setContentHandler(new ParserAdapted(dico));
			saxReader.parse(file);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	//Accès direct aux Words (ajoutè par DS)
	@Override
	public ArrayList<Word> getWords(String lemme) {
		ArrayList<Word> r = this.dico.get(lemme);
		return (null != r) ? r : new ArrayList<Word>();
	}

	@Override
	public ArrayList<Word> getWords(String lemme, int categorieLex) {
		ArrayList<Word> r = new ArrayList<Word>();
		String l = lemme.toLowerCase();
		switch (categorieLex) {
		case Global.NOUN: {
			r = new ArrayList<Word>(this.dico.get(l+"%n"));
			break;
		}
		case Global.VERB: {
			r = new ArrayList<Word>(this.dico.get(l+"%v"));
			break;
		}
		case Global.ADJECTIVE: {
			r = new ArrayList<Word>(this.dico.get(l+"%a"));
			break;
		}
		case Global.ADJECTIVE_SATELLITE: {
			r = new ArrayList<Word>(this.dico.get(l+"%a"));
			break;
		}
		case Global.ADVERB: {
			r = new ArrayList<Word>(this.dico.get(l+"%r"));
			break;
		}
		}
		return r;
	}

	@Override
	public int size() {
		return this.dico.size();
	}

}
