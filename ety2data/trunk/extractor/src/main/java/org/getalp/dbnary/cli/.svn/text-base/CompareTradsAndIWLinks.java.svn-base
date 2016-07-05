package org.getalp.dbnary.cli;

import info.bliki.api.Connector;
import info.bliki.api.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.LangTools;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

import org.getalp.dbnary.DBnaryOnt;
import org.getalp.dbnary.LemonOnt;

public class CompareTradsAndIWLinks extends DbnaryModel {

	protected class IncrementableInt {
		int val;
		
		public IncrementableInt() {
			val = 0;
		}
		
		public IncrementableInt(int val) {
			this.val = val;
		}
		
		public void incr() {
			this.val++;
		}
		
		public void incr(int step) {
			this.val = this.val + step;
		}
		
		public String toString() {
			return Integer.toString(this.val);
		}
	}
	
	private static Options options = null; // Command line options

	private static final String LANGUAGE_OPTION = "l";
	private static final String DEFAULT_LANGUAGE = "fr";

	private static final String OUTPUT_FORMAT_OPTION = "f";
	private static final String DEFAULT_OUTPUT_FORMAT = "turtle";	

	private static final String COUNT_LANGUAGE_OPTION = "c";
	private static final String DEFAULT_COUNT_LANGUAGE = "eng,fra,deu,por";	

	private CommandLine cmd = null; // Command Line arguments

	private static Set<String> ignorableInterwikiLinks= new HashSet<String>();
	
	static{
		ignorableInterwikiLinks.add("w");
		ignorableInterwikiLinks.add("s");
		ignorableInterwikiLinks.add("silcode");
		ignorableInterwikiLinks.add("wikipedia");
		ignorableInterwikiLinks.add("q");
		ignorableInterwikiLinks.add("wikiquote");
		ignorableInterwikiLinks.add("wikispecies");


	}
	private String outputFormat = DEFAULT_OUTPUT_FORMAT;
	private String language = DEFAULT_LANGUAGE;
	private String wktLangCode = DEFAULT_LANGUAGE;
	private String countLanguages = DEFAULT_COUNT_LANGUAGE;

	ObjectMapper mapper = new ObjectMapper();
	
	// TODO: extract iso code from lexvo entity.
	private SortedMap<String, IncrementableInt> counts = new TreeMap<String,IncrementableInt>();
	
	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(LANGUAGE_OPTION, true, 
				"Language (fra, eng or deu). " + DEFAULT_LANGUAGE + " by default.");
		options.addOption(OUTPUT_FORMAT_OPTION, true, 
				"Output format (graphml, raw, rdf, turtle, ntriple, n3, ttl or rdfabbrev). " + DEFAULT_OUTPUT_FORMAT + " by default.");
		options.addOption(COUNT_LANGUAGE_OPTION, true, 
				"Languages to count (as a comma separated list). " + DEFAULT_COUNT_LANGUAGE + " by default.");
	}	

	String[] remainingArgs;

	Model m1;
	
	String NS;
	
	private void initializeTBox(String lang) {
		NS = DBNARY_NS_PREFIX + "/" + lang + "/";
	}
	
	private void loadArgs(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing arguments: " + e.getLocalizedMessage());
			printUsage();
			System.exit(1);
		}

		// Check for args
		if (cmd.hasOption("h")){
			printUsage();
			System.exit(0);
		}

		if (cmd.hasOption(OUTPUT_FORMAT_OPTION)){
			outputFormat = cmd.getOptionValue(OUTPUT_FORMAT_OPTION);
		}
		outputFormat = outputFormat.toUpperCase();

		language = cmd.getOptionValue(LANGUAGE_OPTION, DEFAULT_LANGUAGE);
		// Lang lg = ISO639_3.sharedInstance.getLang(language);
		language = LangTools.getCode(language);
		wktLangCode = LangTools.getPart1(language);
		

		if (cmd.hasOption(COUNT_LANGUAGE_OPTION)){
			countLanguages = cmd.getOptionValue(COUNT_LANGUAGE_OPTION);
		}
		String clangs[] = countLanguages.split(",");
		int i = 0;
		while(i != clangs.length) {
			counts.put(LangTools.getCode(clangs[i]), new IncrementableInt());
			i = i + 1;
		}

		remainingArgs = cmd.getArgs();
		if (remainingArgs.length < 1) {
			printUsage();
			System.exit(1);
		}
		
		initializeTBox(language);
		m1 = ModelFactory.createDefaultModel();
		
		if (	outputFormat.equals("RDF") || 
				outputFormat.equals("TURTLE") ||
				outputFormat.equals("NTRIPLE") ||
				outputFormat.equals("N3") ||
				outputFormat.equals("TTL") ||
				outputFormat.equals("RDFABBREV") ) {
			if ("-".equals(remainingArgs[0])) {
				System.err.println("Reading extract from stdin.");
				m1.read(System.in, outputFormat, "file:///dev/stdin");
			} else {
				System.err.println("Reading extract from " + remainingArgs[0]);
				m1.read(remainingArgs[0], outputFormat);
			}
		} else {
			System.err.println("unsupported format :" + outputFormat);
			System.exit(1);
		}
	}


	private String getCode(Resource resource) {
		// TODO Auto-generated method stub
		return resource.getLocalName();
	}

	public static void main(String args[]) {
		CompareTradsAndIWLinks cliProg = new CompareTradsAndIWLinks();
		cliProg.loadArgs(args);
		int sample = 3000;
		
		Set<String> randomEntries = cliProg.getRandomEntries(sample);
		
		// System.err.println(randomEntries);
		int nbTrans = 0;
		int nbIwlinks = 0;
		
		int nbNullTrans = 0;
		int nbe = 0;
		
		for (String t : randomEntries) {
			IntPair ip = cliProg.stats(t);
			nbTrans += ip.dbtrans;
			nbIwlinks += ip.iwls;
			
			if (0 == ip.dbtrans) nbNullTrans++;
			nbe++;
		}
		
		System.out.println("language & \\% extr & nbnotrans");
		System.out.println( cliProg.language + " & " + nbTrans + " (" + (nbTrans / (float) nbIwlinks)*100 + " \\%) & " + nbNullTrans + " (" + (nbNullTrans / (float)randomEntries.size())*100 + " \\%)");
	}
	
	protected class IntPair {
		
		int dbtrans;
		int iwls;
		
		public IntPair(int ts, int ls) {
			dbtrans = ts;
			iwls = ls;
		}
	}

	private IntPair stats(String name) {
		// System.out.print(name + ": ");
		Set<Resource> translations = getTranslationsFor(name);
		// System.out.print(translations.size() +"/");
		Set<String> iwlinks = getInterWikiLinks(name);
		// System.out.println(iwlinks.size());
		if (iwlinks.size() > translations.size()) System.out.println(name + ": " + translations.size() +"/" + iwlinks.size());
		return new IntPair(translations.size(), iwlinks.size());
		
	}
	
	private HashSet<Resource> getTranslationsFor(String name) {
		HashSet<Resource> res = new HashSet<Resource>();
		Resource voc = m1.getResource(NS+name);
		StmtIterator entries = m1.listStatements(voc, DBnaryOnt.refersTo, (RDFNode) null);
		
		while (entries.hasNext()) {
			Resource e = entries.next().getResource();
			
			StmtIterator translations = m1.listStatements(null, DBnaryOnt.isTranslationOf, e);
			while (translations.hasNext()) {
				Resource t = translations.next().getSubject();
				
				res.add(t);
			}
			
			StmtIterator senses = m1.listStatements(e, LemonOnt.sense, (RDFNode) null);
			while (senses.hasNext()) {
				Resource s = senses.next().getObject().asResource();
				StmtIterator senseTranslations = m1.listStatements(null, DBnaryOnt.isTranslationOf, s);
				while (senseTranslations.hasNext()) {
					Resource t = translations.next().getSubject();
					
					res.add(t);
				}
			}
		}
		

		// System.out.println(res);
		return res;
	}
	
	private HashSet<String> getInterWikiLinks(String name) {
		HashSet<String> res = new HashSet<String>();
		User user = new User("", "", "http://" + wktLangCode + ".wiktionary.org/w/api.php");
		user.login();
		String[] valuePairs = { "action", "parse", "prop", "iwlinks", "page", name , "format", "json"};
		Connector connector = new Connector();
		String rawJsonResponse = connector.queryXML(user, valuePairs);
		if (rawJsonResponse == null) {
			System.err.println("Got no result for the query: " + name);
		}
		try {
			Map<String,Object> userData = mapper.readValue(rawJsonResponse, Map.class);
			Map parseRes = (Map) userData.get("parse");
			if (null == parseRes) {
				System.err.println(rawJsonResponse);
				return res;
			}
			ArrayList links = (ArrayList) parseRes.get("iwlinks");
			if (null == links) return res;
			for (Object link : links) {
				if (! ignorableInterwikiLinks.contains(((Map) link).get("prefix"))) {
					res.add((String) ((Map) link).get("*"));
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(res);


		return res;
	}
	
	
	private HashSet<String> getRandomEntries(int n) {
		HashSet<String> res = new HashSet<String>();
		int total = countResourcesOfType(LemonOnt.LexicalEntry);
		int stepWidth = total / n;
				
		ResIterator vocables = m1.listResourcesWithProperty(RDF.type, DBnaryOnt.Vocable);		
		
		// Only keep vocable that are valid pages.
		ExtendedIterator<Resource> vocs = vocables.filterKeep(new Filter<Resource>(){

			@Override
			public boolean accept(Resource o) {
				return o.hasProperty(DBnaryOnt.refersTo);
			}});
		int i = 0;
		while (vocs.hasNext() && i != n) {
			int step = (int) (Math.random() * stepWidth);
			int s = 1;
			Resource term = vocs.next() ;
			while (vocs.hasNext() & s < step) {
				term = vocs.next();
				s++;
			}
			res.add(term.getLocalName());
			i++;
		}
		
		return res;
	}
	
	private int countResourcesOfType(Resource type) {
		ResIterator resit = m1.listResourcesWithProperty(RDF.type, type);
		int nb = 0;
		while(resit.hasNext()) {
			nb++;
			resit.next();
		}
		resit.close();
		return nb;
	}

	private int countRelations(Property prop) {
		ResIterator resit = m1.listResourcesWithProperty(prop);
		int nb = 0;

		while(resit.hasNext()) {
			Resource rel = resit.next();

			nb++;
		}
		resit.close();

		return nb;
	}

	private void printEquivalentsStats() {
		// Number of relations
		ResIterator relations = m1.listResourcesWithProperty(RDF.type, DBnaryOnt.Translation);
		HashSet<String> langs = new HashSet<String>();
		int others = 0;
		while(relations.hasNext()) {
			Resource r = relations.next();
			Statement t = r.getProperty(DBnaryOnt.targetLanguage);
			if (null != t) {
				RDFNode lang = t.getObject();
				langs.add(getCode(lang.asResource()));
				if (counts.containsKey(getCode(lang.asResource()))) {
					counts.get(getCode(lang.asResource())).incr();
				} else {
					others = others + 1;
				}
			}
		}
		relations.close();
		
		int total = 0;
			
		for (Entry<String, IncrementableInt> i : counts.entrySet()) {
			total = total + i.getValue().val;
			System.out.print(" & " + i.getKey());
		}
		total = total + others;
		System.out.println("& others & Total \\\\");
		System.out.print(language);
		for (Entry<String, IncrementableInt> i : counts.entrySet()) {
			System.out.print(" & " + i.getValue().val);
		}
		System.out.println(" & " + others + " & " + total + "\\\\");
		
		System.out.println("-------------------------");
		System.out.println(langs.size() + " different target languages.");
		for (String l : langs) {
			System.out.print(l + " ");
		}
		
		
	}

	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help = 
			"url must point on an RDF model file extracted from wiktionary." +
			System.getProperty("line.separator", "\n") +
			"Displays stats on the LMF based RDF dump.";
		formatter.printHelp("java -cp /path/to/dbnary.jar org.getalp.dbnary.cli.StatRDFExtract [OPTIONS] url", 
				"With OPTIONS in:", options, 
				help, false);
	}

}
