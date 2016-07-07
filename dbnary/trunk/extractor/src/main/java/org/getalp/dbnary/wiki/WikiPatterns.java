package org.getalp.dbnary.wiki;

import java.util.regex.Pattern;

public class WikiPatterns {

    public final static String macroPatternString;
    public final static String linkPatternString;
    public final static String macroOrLinkPatternString;
    public final static String definitionPatternString = "^#{1,2}([^\\*#:].*)$";
    public final static String bulletListPatternString = "\\*\\s*(.*)";
    /**
     * Pattern used to parse Section Descendants
     */
    public final static String multipleBulletListPatternString = "(\\*+)\\s*(.*)"; 
    public final static String examplePatternString = "^#{1,2}\\*\\s*(.*)$";

    public final static String catOrInterwikiLink = "^\\s*\\[\\[([^\\:\\]]*)\\:([^\\]]*)\\]\\]\\s*$";
    public final static Pattern categoryOrInterwikiLinkPattern;

    static {
    	// DONE: Validate the fact that links and macro should be on one line or may be on several...
    	// DONE: for this, evaluate the difference in extraction !
        linkPatternString = 
            new StringBuilder()
            .append("\\[\\[")
            .append("([^\\]\\|\n\r]*)(?:\\|([^\\]\n\r]*))?")
            .append("\\]\\]")
            .toString();
        macroPatternString = 
            new StringBuilder().append("\\{\\{")
            .append("([^\\}\\|\n\r]*)(?:\\|([^\\}\n\r]*))?")
            .append("\\}\\}")
            .toString();
        // TODO: We should suppress multiline xml comments even if macros or line are to be on a single line.
        macroOrLinkPatternString = new StringBuilder()
        .append("(?:")
        .append(macroPatternString)
        .append(")|(?:")
        .append(linkPatternString)
        .append(")|(?:")
        .append("'{2,3}")
        .append(")|(?:")
        .append("<!--.*-->")
        .append(")").toString();
        
        categoryOrInterwikiLinkPattern = Pattern.compile(catOrInterwikiLink, Pattern.MULTILINE);

    }
    
    public final static Pattern macroPattern;
    public final static Pattern linkPattern;
    public final static Pattern macroOrLinkPattern;
    public final static Pattern definitionPattern;
    public final static Pattern bulletListPattern;
    public final static Pattern multipleBulletListPattern;
    
    static {
        macroPattern = Pattern.compile(macroPatternString);
        linkPattern = Pattern.compile(linkPatternString);
        macroOrLinkPattern = Pattern.compile(macroOrLinkPatternString);
        definitionPattern = Pattern.compile(definitionPatternString, Pattern.MULTILINE);
        bulletListPattern = Pattern.compile(bulletListPatternString);
	multipleBulletListPattern = Pattern.compile(multipleBulletListPatternString); 
    }
}
