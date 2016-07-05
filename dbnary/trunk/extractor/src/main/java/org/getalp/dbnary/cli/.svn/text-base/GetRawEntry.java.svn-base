package org.getalp.dbnary.cli;

import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.WiktionaryIndexerException;

public class GetRawEntry {

    /**
     * @param args
     * @throws WiktionaryIndexerException if any error occurs with indexer.
     */
    public static void main(String[] args) throws WiktionaryIndexerException {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }
        Boolean allxml = false;
        int startIndex = 0;
        while (args[startIndex].startsWith("-")) {
            if (args[startIndex].equals("--all") || args[startIndex].equals("-a")) {
                allxml = true;
                startIndex++;
            } else if (args[0].equals("--")) {
                startIndex++;
                break;
            } else {
                printUsage();
                System.exit(1);
            }
        }
        
        WiktionaryIndex wi = new WiktionaryIndex(args[startIndex]);
        startIndex++;
        
        for(int i = startIndex; i < args.length; i++) {
            if (allxml) {
                System.out.println(wi.get(args[i])); 
            } else {
                System.out.println(wi.getTextOfPage(args[i])); 
            }
        }
    }

    
    public static void printUsage() {
        System.err.println("Usage: ");
        System.err.println("  java org.getalp.dbnary.cli.GetRawEntry [OPTIONS] wiktionaryDumpFile entryname ...");
        System.err.println("Displays the raw text of the wiktionary page named \"entryname\".");
        System.err.println("OPTIONS:");
        System.err.println("  --all (-a): Display all the xml elements defining the page.");
        System.err.println("  --        : Stops the sequence of options and start the sequence of entrynames.");   
        System.err.println("              This option is usefull when the wiktionaryDumpFile begins with a \"-\".");
    }
}
