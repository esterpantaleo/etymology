package org.getalp.blexisma.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.stax2.XMLInputFactory2;

public class VideoList2TextFoder {

	private static Options options = null; // Command line options

	private static final String ITEM_TAG_OPTION = "e";
	private static final String DEFAULT_ITEM_TAG = "item";

	
	private static final String TITLE_TAG_OPTION = "t";
	private static final String DEFAULT_TITLE_TAG = "title";

	private static final String CONTENT_TAG_OPTION = "c";
	private static final String DEFAULT_CONTENT_TAG = "description";

	private static final String ID_TAG_OPTION = "i";
	private static final String DEFAULT_ID_TAG = "videourl";

	private static final String ID_REGEXP_OPTION = "I";
	private static final String DEFAULT_ID_REGEXP = "\\?id=(.*)$";

	private static final String OUTPUT_ENCODING_OPTION = "o";
	private static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";

	private static String crlf = System.getProperty("line.separator");  
	
	private CommandLine cmd = null; // Command Line arguments
	
	private String itemTag = DEFAULT_ITEM_TAG;
	private String titleTag = DEFAULT_TITLE_TAG;
	private String contentTag = DEFAULT_CONTENT_TAG;
	private String idTag = DEFAULT_ID_TAG;
	private String idRegex = DEFAULT_ID_REGEXP;
	private String outputenc = DEFAULT_OUTPUT_ENCODING;
	
	private Pattern idPattern;
	private InputStream inFile;
	private String outfn;
    public static final XMLInputFactory2 xmlif;

	static{
		options = new Options();
		options.addOption("h", false, "Prints usage and exits. ");	
		options.addOption(TITLE_TAG_OPTION, true, "specify the xml tag containing the title.");
		options.addOption(CONTENT_TAG_OPTION, true, "specify the xml tag containing the content text.");
		options.addOption(ID_TAG_OPTION, true, "specify the xml tag containing the id.");
		options.addOption(ID_REGEXP_OPTION, true, "specify the regular expression (java syntax) which gives the id as group 1.");	
		options.addOption(OUTPUT_ENCODING_OPTION, true, "specify the encoding used for outputted text files.");	
 
        try {
            xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, Boolean.TRUE);
        } catch (Exception ex) {
            System.err.println("Cannot intialize XMLInputFactory while classloading VideoList2TextFoder command line.");
            throw new RuntimeException("Cannot initialize XMLInputFactory", ex);
        }
    }
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		VideoList2TextFoder cliProg = new VideoList2TextFoder();
		cliProg.loadArgs(args);
		cliProg.extractTexts();
		
	}

	private void extractTexts() throws IOException {
		XMLStreamReader xmlr = null;
        try {
            // pass the file name. all relative entity references will be
            // resolved against this as base URI.
            xmlr = xmlif.createXMLStreamReader(inFile);

            // check if there are more events in the input stream
            String title = "";
            String content = "";
            String id = null;

            int cpt = 0;
            
            Matcher m = idPattern.matcher("");
            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && xmlr.getLocalName().equals(itemTag)) {
                    title = "";
                    content = "";
                    id = null;
                    cpt++;
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(titleTag)) {
                    title = xmlr.getElementText();
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(contentTag)) {
                	content = xmlr.getElementText();
                } else if (xmlr.isStartElement() && xmlr.getLocalName().equals(idTag)) {
                	m.reset(xmlr.getElementText());
                	if (m.find()) {
                		id = m.group(1);
                	} 
                } else if (xmlr.isEndElement() && xmlr.getLocalName().equals(itemTag)) {
                	title = title.trim();
                	if (title.length() != 0) {
                		char c = title.charAt(title.length()-1);
                		if (! isPunctuation(c)) {
                			title = title + ".";
                		}
                	}
                	if (null == id) id = "anonymous_" + cpt;
                	Writer out = new OutputStreamWriter(new FileOutputStream(outfn + id), outputenc);
                	out.append(title);
                	out.append(crlf);
                	out.append(content);
                	out.flush();
                	out.close();
                }       
            }
        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.err);
        } finally {
            try {
                if (xmlr != null)
                    xmlr.close();
            } catch (XMLStreamException ex) {
                ex.printStackTrace();
            }
        }

	}

    public static boolean isPunctuation(char c) {
        return c == ','
            || c == '.'
            || c == '!'
            || c == '?'
            || c == ':'
            || c == ';'
            ;
    }
	/**
	 * Validate and set command line arguments.
	 * Exit after printing usage if anything is astray
	 * @param args String[] args as featured in public static void main()
	 */
	private void loadArgs(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing arguments: " + e.getLocalizedMessage());
			printUsage();
			System.exit(1);
		}

		if (cmd.hasOption("h")){
			printUsage();
			System.exit(0);
		}

		if (cmd.hasOption(ITEM_TAG_OPTION)) {
			itemTag = cmd.getOptionValue(ITEM_TAG_OPTION);
		}

		if (cmd.hasOption(TITLE_TAG_OPTION)) {
			titleTag = cmd.getOptionValue(TITLE_TAG_OPTION);
		}

		if (cmd.hasOption(CONTENT_TAG_OPTION)) {
			contentTag = cmd.getOptionValue(CONTENT_TAG_OPTION);
		}

		if (cmd.hasOption(ID_TAG_OPTION)) {
			idTag = cmd.getOptionValue(ID_TAG_OPTION);
		}

		if (cmd.hasOption(ID_REGEXP_OPTION)) {
			idRegex = cmd.getOptionValue(ID_REGEXP_OPTION);
		}

		if (cmd.hasOption(OUTPUT_ENCODING_OPTION)) {
			outputenc = cmd.getOptionValue(OUTPUT_ENCODING_OPTION);
			if (! Charset.isSupported(outputenc)) {
				// fallback to UTF-8
				outputenc = DEFAULT_OUTPUT_ENCODING;
			}
		}

		idPattern = Pattern.compile(idRegex, Pattern.MULTILINE);
		
		String[] remainingArgs = cmd.getArgs();
		if (remainingArgs.length != 2) {
			printUsage();
			System.exit(1);
		}

		String infn = remainingArgs[0];
		try {
			if ("-".equals(infn)) {
				 inFile = System.in;
			} else {
				inFile = new FileInputStream(infn);
			}
		} catch (FileNotFoundException e) {
			System.err.println(infn + ": file not found.");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		outfn = remainingArgs[1];
		File outputFolder = new File(outfn);
		if (outputFolder.exists() && outputFolder.isDirectory() && outputFolder.list().length != 0) {
			System.err.println("Cannot extract videos to an already existing file or folder.");
			printUsage();
			System.exit(1);
		}
		outputFolder.mkdirs();
		if (! outfn.endsWith(File.separator)) outfn = outfn + File.separator; 
		
	}

	 public static void printUsage() {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp /path/to/blexisma.jar " + VideoList2TextFoder.class.getCanonicalName() + " [OPTIONS] videolistxml outputfolder", 
					"With OPTIONS in:", options, 
					"specify - as textfile to read data from stdin.\noutputfolder should not exist and will be created.", false);
	 }
}
