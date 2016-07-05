package org.getalp.dbnary.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.getalp.dbnary.LangTools;
import org.getalp.dbnary.DbnaryModel;
import org.getalp.dbnary.stats.GeneralStatistics;
import org.getalp.dbnary.stats.NymStatistics;
import org.getalp.dbnary.stats.TranslationsStatistics;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class UpdateDiachronicStatistics extends DbnaryModel {


	private static Options options = null; // Command line options

	private static final String PREFIX_DIR_OPTION = "d";
	private static final String DEFAULT_PREFIX_DIR = ".";

	private static final String COUNT_LANGUAGE_OPTION = "c";
	private static final String DEFAULT_COUNT_LANGUAGE = "eng,fra,deu,por";	

	private CommandLine cmd = null; // Command Line arguments

	private String countLanguages = DEFAULT_COUNT_LANGUAGE;

	private String extractsDir = null;
	private String statsDir = null;

	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(PREFIX_DIR_OPTION, true, 
				"directory containing the extracts and stats. " + DEFAULT_PREFIX_DIR + " by default ");	
		options.addOption(COUNT_LANGUAGE_OPTION, true, 
				"Languages to count (as a comma separated list). " + DEFAULT_COUNT_LANGUAGE + " by default.");
	}	

	String[] remainingArgs;

	Model m1;

	private String lg2;

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


		if (cmd.hasOption(COUNT_LANGUAGE_OPTION)){
			countLanguages = cmd.getOptionValue(COUNT_LANGUAGE_OPTION);
		}

		String prefixDir = DEFAULT_PREFIX_DIR;
		if (cmd.hasOption(PREFIX_DIR_OPTION)) {
			prefixDir = cmd.getOptionValue(PREFIX_DIR_OPTION);
		}

		remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 1) {
			printUsage();
			System.exit(1);
		}

		String lang = remainingArgs[0];
		lg2 = LangTools.getTerm2Code(lang);
		if (null == lg2) lg2=lang;
		extractsDir = prefixDir + File.separator + "lemon" + File.separator + lg2;
		statsDir = prefixDir + File.separator + "stats" + File.separator + lg2;

	}

	public static void main(String args[]) throws Exception {
		UpdateDiachronicStatistics cliProg = new UpdateDiachronicStatistics();
		cliProg.loadArgs(args);
		cliProg.updateStats();

	}

	private void updateStats() throws Exception {

		File d = new File(extractsDir);
		File ds = new File(statsDir);

		if (!d.isDirectory()) {
			System.err.println("Extracts directory not found: " + extractsDir);
		}
		if (!ds.isDirectory()) {
			ds.mkdirs();
		}

		Pattern dumpFilenamePattern = Pattern.compile(lg2 + "_dbnary_lemon_(\\d{8})\\..*");

		String gstatFile = statsDir + File.separator + "general_stats.csv";
		Map<String,String> gstats = readAndParseStats(gstatFile);

		String nstatFile = statsDir + File.separator + "nym_stats.csv";
		Map<String,String> nstats = readAndParseStats(nstatFile);

		String tstatFile = statsDir + File.separator + "translations_stats.csv";
		Map<String,String> tstats = readAndParseStats(tstatFile);

		for(File e : d.listFiles()) {
			Matcher m = dumpFilenamePattern.matcher(e.getName());

			if (m.matches()) {
				String date = m.group(1);
				String language = LangTools.getCode(lg2);

				String checksum = getCheckSumColumn(gstats.get(date));
				String md5 = getMD5Checksum(e);
				if (md5.equals(checksum)) {
					System.err.println("Ignoring already available stat for: " + e.getName());
					continue;
				}

				System.err.println("Computing stats for: " + e.getName());
				try {
					m1 = ModelFactory.createDefaultModel();
					InputStream in = new FileInputStream(e);
					if (e.getName().endsWith(".bz2")) {
						in = new BZip2CompressorInputStream(in);
					}
					m1.read(in, DbnaryModel.DBNARY_NS_PREFIX + "/" + language + "/", "TURTLE");

					System.err.println("Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

					// Compute general stats
					StringWriter ow = new StringWriter();

					GeneralStatistics.printStats(m1, language, new PrintWriter(ow));
					String stat = ow.toString();
					stat = date + "," + md5 + "," + stat;
					gstats.put(date, stat);

					// Compute nym stats
					ow = new StringWriter();
					NymStatistics.printStats(m1, language, new PrintWriter(ow));
					stat = ow.toString();
					stat = date + "," + stat;
					nstats.put(date, stat);

					// Compute translations stats
					ow = new StringWriter();
					TranslationsStatistics.printStats(m1, language, countLanguages, new PrintWriter(ow));
					stat = ow.toString();
					stat = date + "," + stat;
					tstats.put(date, stat);

				} catch (Exception ex) {
					System.err.println("Exception caught while computing stats for: " + e.getName());
					System.err.println(ex.getLocalizedMessage());
					ex.printStackTrace(System.err);
				}
				m1 = null;
				System.gc();
				System.err.println("Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			}
		}

		writeStats(gstats, "Date,MD5," + GeneralStatistics.getHeaders(), gstatFile);
		writeStats(nstats, "Date," + NymStatistics.getHeaders(), nstatFile);
		writeStats(tstats, "Date," + TranslationsStatistics.getHeaders(countLanguages), tstatFile);

	}

	private void writeStats(Map<String,String> gstats, String headers, String gstatFile) throws IOException {
		File gs = new File(gstatFile);

		if (!gs.exists() || (gs.isFile() && gs.canWrite())) {
			PrintWriter stats = new PrintWriter(gs, "UTF-8");
			// Print Header
			stats.println(headers);
			for (String s: gstats.values()) {
				stats.println(s);
			}
			stats.flush();
			stats.close();
		}
	}

	private String getCheckSumColumn(String s) {
		return (null == s) ? null : s.split(",")[1];
	}

	private Map<String, String> readAndParseStats(String gstatFile) throws IOException {
		TreeMap<String,String> m = new TreeMap<String,String>();

		File gs = new File(gstatFile);

		if (gs.isFile() && gs.canRead()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(gs), "UTF-8"));
			String h = br.readLine(); // reading header
			String s = br.readLine();
			while (s != null) {
				String line[] = s.split(",");
				m.put(line[0], s);
				s = br.readLine();
			}
			br.close();
		}
		return m;
	}

	public static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		String help = 
				"Update diachronic statistics based on archived extracts." +
						"lang is the language of the archived extracts.";
		formatter.printHelp("java -cp /path/to/dbnary.jar " +  UpdateDiachronicStatistics.class.getCanonicalName() + "[OPTIONS] lang", 
				"With OPTIONS in:", options, 
				help, false);
	}

	public static byte[] createChecksum(File file) throws Exception {
		InputStream fis =  new FileInputStream(file);

		byte[] buffer = new byte[4096];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(File file) throws Exception {
		byte[] b = createChecksum(file);
		String result = "";

		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

}
