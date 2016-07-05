package org.getalp.dbnary.eng;

import java.util.regex.Pattern;

import org.getalp.dbnary.*; 

public class EtymologyPatterns{

    public final static String textAfterSupersededPatternString;
    //todo:deal with "equivalent to" string in etymology section
    public final static String textEquivalentToPatternString;

    public final static String plusPatternString;
    public final static String dotPatternString;
    public final static String commaPatternString;
    public final static String fromPatternString;
    public final static String cognateWithPatternString;
    public final static String templatePatternString;
    public final static String wiktionaryPatternString;
    public final static String compoundOfPatternString;
    public final static String uncertainPatternString;
    public final static String abovePatternString;
    public final static String yearPatternString; 
    public final static String andPatternString;
    public final static String orPatternString;
    public final static String withPatternString;
    public final static String possiblePatternString;
    static {

	textAfterSupersededPatternString = "(?:[Ss]uperseded|[Dd]isplaced( native)?|[Rr]eplaced|[Mm]ode(?:l)?led on)";
        textEquivalentToPatternString = "equivalent to\\s*\\{\\{[^\\}]+\\}\\}";

        plusPatternString = "\\+";
        dotPatternString = "\\.|;";
	commaPatternString = ",";
	fromPatternString = new StringBuilder()
            .append("[Ff]rom|")
            .append("[Bb]ack-formation (?:from)?|")
            .append("[Aa]bbreviat(?:ion|ed)? (?:of|from)?|")
            .append("[Cc]oined from|")
            .append("[Bb]orrow(?:ing|ed)? (?:of|from)?|")
            .append("[Cc]ontracted from|")
            .append("[Aa]dopted from|")
    	    .append("[Cc]alque(?: of)?|")
    	    .append("[Ii]terative of|") 
            .append("[Ss]hort(?:hening|hen|hened)? (?:form )?(?:of|from)?|") 
            .append("[Tt]hrough|")
    	    //"\\>", //!!
    	    .append("[Aa]lteration of|")
            .append("[Vv]ia|")
            .append("[Dd]iminutive (?:form )?of|")
            .append("[Uu]ltimately of|")
            .append("[Vv]ariant of|")
            .append("[Pp]et form of|")
            .append("[Aa]phetic variation of|")
            .append("[Dd]everbal of|")
    	    .append("\\<").toString();
        templatePatternString = new StringBuilder()
            .append("\\{\\{").toString();
        wiktionaryPatternString = new StringBuilder()
            .append("(?:'')?\\[\\[").toString();
        abovePatternString = "[Ss]ee above"; //this should precede cognateWithPatternString which matches against "[Ss]ee"
    	cognateWithPatternString = new StringBuilder() //this should follow abovePatternString which matches against "[Ss]ee above"
    	    .append("[Rr]elated(?: also)? to|")
    	    .append("[Cc]ognate(?:s)? (?:include |with |to |including )?|")
    	    .append("[Cc]ompare (?:also )?|")
            .append("[Ww]hence (?:also )?|")
            .append("(?:[Bb]elongs to the )?[Ss]ame family as |")
    	    .append("[Mm]ore at |")
    	    .append("[Aa]kin to |")
    	    .append("[Ss]ee(?:n)? (?:also )?").toString();
    	compoundOfPatternString = new StringBuilder() 
    	    .append("[Cc]ompound(?:ed)? (?:of|from) |")
    	    .append("[Mm]erg(?:ing |er )(?:of |with )?(?: earlier )?|")
    	    .append("[Uu]niverbation of ").toString();
    	uncertainPatternString = "[Oo]rigin uncertain";
    	yearPatternString = "(?:[Aa].\\s*?[Cc].?|[Bb].?\\s*[Cc].?)?\\s*\\d++\\s*(?:[Aa].?\\s*[Cc].?|[Bb].?\\s*[Cc].?|th century|\\{\\{C\\.E\\.\\}\\})?";
        andPatternString = "\\s+and\\s+";
        orPatternString = "[^a-zA-Z0-9]or[^a-zA-Z0-9]";
        withPatternString = "[^a-zA-Z0-9]with[^a-zA-Z0-9]";

        possiblePatternString = new StringBuilder()
            .append("(")
            .append(fromPatternString)
            .append(")|(")
            .append(templatePatternString)
            .append(")|(")
            .append(wiktionaryPatternString)
            .append(")|(")
            .append(abovePatternString)
	    .append(")|(")
            .append(cognateWithPatternString)
            .append(")|(")
            .append(compoundOfPatternString)
            .append(")|(")
            .append(uncertainPatternString)
	    .append(")|(")
	    .append(commaPatternString)
	    .append(")|(")
	    .append(yearPatternString)
	    .append(")|(")
	    .append(andPatternString)
            .append(")|(")
            .append(plusPatternString)
            .append(")|(")
            .append(dotPatternString)
            .append(")|(") 
	    .append(orPatternString)       
            .append(")|(")
            .append(withPatternString)
            .append(")").toString();
    }        
 
    public final static String[] possibleString = {"FROM", "", "LEMMA", "ABOVE", "COGNATE_WITH", "COMPOUND_OF", "UNCERTAIN", "COMMA", "YEAR", "AND", "PLUS", "DOT", "OR", "WITH"};
    public final static Pattern possiblePattern = Pattern.compile(possiblePatternString);
    public final static Pattern templatePattern = Pattern.compile(templatePatternString);
    public final static Pattern wiktionaryPattern = Pattern.compile(wiktionaryPatternString);

    public final static Pattern textEquivalentToPattern = Pattern.compile(textEquivalentToPatternString);
    public final static Pattern textAfterSupersededPattern = Pattern.compile(textAfterSupersededPatternString);
    //find COMPOUND_OF pattern (to replace it with compound template)
    public final static String compoundPatternString = new StringBuilder()
        .append("((COMPOUND_OF )")
        .append("(LANGUAGE )?")
        .append("(LEMMA )")
	.append("(PLUS |AND |WITH )")
        .append("(LANGUAGE )?")
        .append("(LEMMA ))|")
        .append("((LANGUAGE )?")
        .append("(LEMMA )")
        .append("(PLUS )")
        .append("(LANGUAGE )?")
	.append("(LEMMA ))").toString();
    public final static Pattern compoundPattern = Pattern.compile(compoundPatternString);

    //(FROM )?(LANGUAGE|LANGUAGE LEMMA|LEMMA) (COMMA|DOT) unless FROM LANGUAGE LEMMA,LEMMA,LEMMA with all lemma from the same language                          
    public final static String originPatternString = new StringBuilder()
	.append("(FROM )?")//aggiungere word boundary/b?????????????                                                                        
	.append("(LANGUAGE LEMMA ")
	.append("|")
	.append("LEMMA )")
	.append("(COMMA |DOT |OR )").toString();
    public final static Pattern originPattern = Pattern.compile(originPatternString);

    //(COGNATE_WITH )(?:(LANGUAGE LEMMA\s*|LEMMA\s*)+(\s*COMMA\s*)?)+(DOT)?
    public final static String cognatePatternString = new StringBuilder()
	.append("(COGNATE_WITH )")//aggiungere word boundary/b?????????????                       
	.append("(?:(LANGUAGE LEMMA |LEMMA )+")
        .append("(COMMA |AND )?)+")
	.append("(DOT )?").toString();
    public final static Pattern cognatePattern = Pattern.compile(cognatePatternString);

}