package org.getalp.dbnary;

import java.lang.reflect.InvocationTargetException;


public class WiktionaryExtractorFactory {

	public static IWiktionaryExtractor getExtractor(String language, IWiktionaryDataHandler wdh) {
		return getExtractor("WiktionaryExtractor", language, wdh);
	}

	public static IWiktionaryExtractor getForeignExtractor(String language, IWiktionaryDataHandler wdh) {
		return getExtractor("ForeignLanguagesWiktionaryExtractor", language, wdh);
	}

	public static IWiktionaryExtractor getExtractor(String className, String language, IWiktionaryDataHandler wdh) {
		IWiktionaryExtractor we = null;
		
			String cname = WiktionaryExtractorFactory.class.getCanonicalName();
			int dpos = cname.lastIndexOf('.');
			String pack = cname.substring(0, dpos);
			try {
				Class<?> wec = Class.forName(pack + "." + language + "." + className);
				we = (IWiktionaryExtractor) wec.getConstructor(IWiktionaryDataHandler.class).newInstance(wdh);
			} catch (ClassNotFoundException e) {
				System.err.println("No wiktionary extractor found for " + language);
			} catch (InstantiationException e) {
				System.err.println("Could not instanciate wiktionary extractor for " + language);
			} catch (IllegalAccessException e) {
				System.err.println("Illegal access to wiktionary extractor for " + language);
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal argument passed to wiktionary extractor's constructor for " + language);
				e.printStackTrace(System.err);
			} catch (SecurityException e) {
				System.err.println("Security exception while instanciating wiktionary extractor for " + language);
				e.printStackTrace(System.err);
			} catch (InvocationTargetException e) {
				System.err.println("InvocationTargetException exception while instanciating wiktionary extractor for " + language);
				e.printStackTrace(System.err);
			} catch (NoSuchMethodException e) {
				System.err.println("No appropriate constructor when instanciating wiktionary extractor for " + language);
			}
		return we;
	}

}
