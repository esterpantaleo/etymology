package org.getalp.blexisma.semnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.blexisma.api.SemanticNetwork;

public class TextOnlySemnetReader {

    private final static String nodeMatcher = "^\\s*-O-\\s*(.*)$";
    private final static String relMatcher = "^\\s*-R-\\s*(.*)$";
    private final static String confidenceMatcher = "^\\s*-C-\\s*(.*)$";
    private final static String targetMatcher = "^\\s*-D-\\s*(.*)$";
    
    private final static String dumpMatcher = new StringBuilder()
    .append("(?:")
    .append(nodeMatcher)
    .append(")|(?:")
    .append(relMatcher)
    .append(")|(?:")
    .append(confidenceMatcher)
    .append(")|(?:")
    .append(targetMatcher)
    .append(")").toString();
    
    private final static Pattern dumpPattern = Pattern.compile(dumpMatcher);
   
    // TODO, take this out and put it in an external class
    public static void readFromReader(SemanticNetwork<String, String> semnet, BufferedReader in) throws IOException {
        String cl;
        String currentOrigin = null, currentRelation = null;
        float currentConfidence = 0;
        while((cl = in.readLine()) != null) {
            Matcher m = dumpPattern.matcher(cl);
            if (m.matches()) {
                if (m.group(1) != null) {
                    currentOrigin = m.group(1);
                } else if (m.group(2) != null) {
                    // relation label
                    currentRelation = m.group(2);
                } else if (m.group(3) != null) {
                    // confidence
                    currentConfidence = Float.valueOf(m.group(3));
                } else {
                    // target node
                    semnet.addRelation(currentOrigin, m.group(4), currentConfidence, currentRelation);
                    currentRelation = null;
                    currentConfidence = 0;
                }
            } 
        }
        // System.out.println("");
    }
    
    public static SemanticNetwork<String,String> loadNetwork(String networkFilename) throws IOException {
    	SemanticNetwork<String,String> network = new SimpleSemanticNetwork<String, String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(networkFilename)),
						"UTF-8"));
			TextOnlySemnetReader.readFromReader(network, br);
		} catch (UnsupportedEncodingException e) {
			// Should not happen
			e.printStackTrace();
		} 
		return network;
    	
    }
}
