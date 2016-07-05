package org.getalp.dbnary;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WiktionaryDataHandlerFactory {
	private static Logger log = LoggerFactory.getLogger(WiktionaryDataHandlerFactory.class);

	public static IWiktionaryDataHandler getDataHandler(String language) {
		return getDataHandler("WiktionaryDataHandler", language);
	}

	public static IWiktionaryDataHandler getForeignDataHandler(String language) {
		return getDataHandler("ForeignLanguagesWiktionaryDataHandler", language);
	}

	private static IWiktionaryDataHandler getDataHandler(String className, String language) {
		IWiktionaryDataHandler wdh = null;

		String cname = WiktionaryDataHandlerFactory.class.getCanonicalName();
		int dpos = cname.lastIndexOf('.');
		String pack = cname.substring(0, dpos);
		try {
			Class<?> wdhc = Class.forName(pack + "." + language + "." + className);
			wdh = (IWiktionaryDataHandler) wdhc.getConstructor(String.class).newInstance(language);
		} catch (ClassNotFoundException e) {
			log.debug("No wiktionary data handler found for {}", language);
		} catch (InstantiationException e) {
			log.debug("Could not instanciate wiktionary data handler for {}", language);
		} catch (IllegalAccessException e) {
			log.debug("Illegal access to wiktionary data handler for {}", language);
		} catch (IllegalArgumentException e) {
			log.debug("Illegal argument passed to wiktionary data handler constructor for {}", language);
			e.printStackTrace(System.err);
		} catch (SecurityException e) {
			log.debug("Security exception while instanciating wiktionary data handler for {}", language);
			e.printStackTrace(System.err);
		} catch (InvocationTargetException e) {
			log.debug("InvocationTargetException exception while instanciating wiktionary data handler for {}", language);
			e.printStackTrace(System.err);
		} catch (NoSuchMethodException e) {
			log.debug("No appropriate constructor when instanciating wiktionary data handler for {}", language);
		}

		if (null == wdh) {
			log.debug("Using default data handler.", language);
			wdh = new LemonBasedRDFDataHandler(language);
		}
		return wdh;
	}

}
