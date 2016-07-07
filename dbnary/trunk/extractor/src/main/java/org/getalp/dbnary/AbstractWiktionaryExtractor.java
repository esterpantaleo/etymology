package org.getalp.dbnary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.wiki.WikiPatterns;

/** 
* @author ?, pantaleo 
* 
*/
public abstract class AbstractWiktionaryExtractor implements IWiktionaryExtractor {
    
    // TODO: Alter the extraction process by allowing multiple lines in a macro and evaluate the final result
    // TODO: Determine how many nested macro are used in the different wiktionary languages.
    // These should be independent of the language
    protected String pageContent;
    protected IWiktionaryDataHandler wdh;
    protected String wiktionaryPageName;
    
    protected WiktionaryIndex wi = null;
	
    public AbstractWiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super();
        this.wdh = wdh;
    }

    @Override
    public void setWiktionaryIndex(WiktionaryIndex wi) {
	this.wi = wi;
    }


    // Suppression des commentaires XML d'un texte     
    protected final static String debutOrfinDecomPatternString;

    static {
		debutOrfinDecomPatternString= "(?:" +
				"(<!--)" +
				")|(?:" +
				"(-->)" +
				")";
    }
    protected final static Pattern xmlCommentPattern;

    static {
		xmlCommentPattern=Pattern.compile(debutOrfinDecomPatternString, Pattern.DOTALL);
    }

    private static final int A = 0;
    private static final int B = 1;

    public static String removeXMLComments(String s) {
        if (null == s) return null;

		int ET = A;
		Matcher xmlCommentMatcher = xmlCommentPattern.matcher(s);


		int indexEnd=0;   // index du debut de la partie qui nous interesse
		int indexBegin=0; // index de la fin de la partie qui nous interesse

		StringBuffer result = new StringBuffer(); // la nouvelles chaine de caracteres

		while(xmlCommentMatcher.find()) {
			String g1 = xmlCommentMatcher.group(1); // g1 =<!-- ou null
			String g2 = xmlCommentMatcher.group(2); // g2=-> ou null

			switch (ET) {
			case A:
				if (g1!=null) {
					// On a trouvé un debut de commentaire

					//On place la fin de la partie qui nous interesse
					indexEnd = xmlCommentMatcher.start(1);
					//on change d'etat
					ET=B;
					result.append(s.substring(indexBegin, indexEnd));
				}
				break;
			case B:
				if(g2!=null){
					// On a trouvé la fin du commentaire

					// on place le debut de le partie qui nous interesse
					indexBegin= xmlCommentMatcher.end(2);
					// on change d'etat
					ET=A;
				}
				break;

			default:
				System.err.println("Unexpected state number:" + ET);
				break;
			}

		}
		if (xmlCommentMatcher.hitEnd()) {
			switch (ET) {
			case A:
				result.append(s.substring(indexBegin));
				break;
			case B:
				break;

			default:
				System.err.println("Unexpected state number:" + ET);
				break;
			}
		}
	   return result.toString();

    }
    
    //Remove bibliographic references
    protected final static String biblRefPatternString = new StringBuilder()
	    .append("&lt;ref&gt;([^.]*?)&lt;ref&gt;").toString();

    //TO DO: check that this function is working
    /*
     * A function to remove bibliographic references <ref> ... </ref> from a String
     *
     * @param s the input String
     * @return a String without bibliographic references
     */
    public static String removeBiblRef(String s) {
	if (null == s) return null;
	s.replaceAll(biblRefPatternString, "");
	return(s);
    }
        
    // DONE: filter out pages that are in specific Namespaces (Wiktionary:, Categories:, ...)
    // TODO: take Redirect page into account as alternate spelling.
    // TODO: take homography into account (ex: mousse) and separate different definitions for the same pos.
    // DONE: some xml comments may be in the string values. Remove them.
    public void extractData(String wiktionaryPageName, String pageContent) {
    	// Entries containing the special char ":" are pages belonging to specific namespaces.(Wiktionary:, Categories:, ...).
    	// Such pages are simply ignored.
    	if (wiktionaryPageName.contains(":")) {
    		return;
    	}
        this.wiktionaryPageName = wiktionaryPageName;
        
        this.pageContent = removeXMLComments(pageContent);
	this.pageContent = removeBiblRef(pageContent);
	
        if (pageContent == null) return;
        try {
        	extractData();
        } catch (RuntimeException e) {
        	System.err.println("Caught RuntimeException while parsing entry [" + this.wiktionaryPageName + "]");
        	throw e;
        }
     }

    public abstract void extractData();
    
    static String defOrExamplePatternString = new StringBuilder()
        .append("(?:")
        .append(WikiPatterns.definitionPatternString)
        .append(")|(?:")
        .append(WikiPatterns.examplePatternString)
        .append(")").toString();
    
    static Pattern defOrExamplePattern = Pattern.compile(defOrExamplePatternString, Pattern.MULTILINE);
    
    protected void extractDefinitions(int startOffset, int endOffset) { 
    	
        Matcher defOrExampleMatcher = defOrExamplePattern.matcher(this.pageContent);
        defOrExampleMatcher.region(startOffset, endOffset);
        while (defOrExampleMatcher.find()) {
        	if (null != defOrExampleMatcher.group(1)) {
        		extractDefinition(defOrExampleMatcher);        		
        	} else if (null != defOrExampleMatcher.group(2)) {
        		extractExample(defOrExampleMatcher);
        	}
        }
    }
    
	public void extractDefinition(Matcher definitionMatcher) {
		// TODO: properly handle macros in definitions. 
		String definition = definitionMatcher.group(1);
		int defLevel = 1;
		if (definitionMatcher.group().charAt(1) == '#') defLevel = 2;
		extractDefinition(definition, defLevel);
	}

    public void extractDefinition(String definition, int defLevel) {
		String def = cleanUpMarkup(definition);
        if (def != null && ! def.equals("")) {
        	wdh.registerNewDefinition(definition, defLevel);
        }
    }

	/* public void extractDefinition(String definition) {
		extractDefinition(definition, 1);
	}*/

    public static String cleanUpMarkup(String group) {
        return cleanUpMarkup(group, false);
    }

    public void extractExample(Matcher definitionMatcher) {
		String example = definitionMatcher.group(2);
		extractExample(example);
    }

    public void extractExample(String example) {
	// TODO: properly handle macros in definitions. 
        String ex = cleanUpMarkup(example);
        if (ex != null && ! ex.equals("")) {
        	wdh.registerExample(example, null);
        }	
    }
	
    // Some utility methods that should be common to all languages
    // DONE: (priority: top) keep annotated lemma (#{lemma}#) in definitions.
    // DONE: handle ''...'' and '''...'''.
    // DONE: suppress affixes that follow links, like: e in [[français]]e.
    // DONE: Extract lemma AND OCCURENCE of links in non human readable form

    /**
     * cleans up the wiktionary markup from a string in the following manner: <br>
     * str is the string to be cleaned up.
     * the result depends on the value of humanReadable.
     * Wiktionary macros are always discarded.
     * xml/xhtml comments are always discarded.
     * Wiktionary links are modified depending on the value of humanReadable.
     * e.g. str = "{{a Macro}} will be [[discard]]ed and [[feed|fed]] to the [[void]]."
     * if humanReadable is true, it will produce:
     * "will be discarded and fed to the void."
     * if humanReadable is false, it will produce:
     * "will be #{discard|discarded}# and #{feed|fed}# to the #{void|void}#."
     * @param str is the String to be cleaned up
     * @param humanReadable a boolean
     * @return a String
     */
    public static String cleanUpMarkup(String str, boolean humanReadable) {
        Matcher m = WikiPatterns.macroOrLinkPattern.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        String leftGroup, rightGroup;
        while (m.find()) {
            if ((leftGroup = m.group(1)) != null) {
                // It's a macro, ignore it for now
                m.appendReplacement(sb, "");
            } else if ((leftGroup = m.group(3)) != null) {
                // It's a link, only keep the alternate string if present.
                rightGroup = m.group(4);
                String replacement ;
                if (rightGroup == null && humanReadable) {
                    replacement = leftGroup;
                } else if (humanReadable) {
                    replacement = rightGroup;
                } else {
                    replacement = "#{" + leftGroup + "|" + ((rightGroup == null) ? leftGroup : rightGroup);
                }
                // Discard stupidly encoded morphological affixes.
                if (!humanReadable ) { // && str.length() > m.end() && Character.isLetter(str.charAt(m.end()))
                    int i = m.end();
                    StringBuffer affix = new StringBuffer();
                    while(i < str.length() && Character.isLetter(str.charAt(i))) {
                        affix.append(str.charAt(i));
                        i++;
                    }
                    replacement = replacement + affix.toString();
                	replacement = replacement + "}#";
                	replacement = Matcher.quoteReplacement(replacement);
                    m.appendReplacement(sb, replacement);
                    // Start over the match after discarded affix
                    str = str.substring(i);
                    m.reset(str); 
                } else {
                	 replacement = Matcher.quoteReplacement(replacement);
                     m.appendReplacement(sb, replacement);
                }
            } else {
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);
        // normalize whitespaces
        int l = 0;
        int i = 0; boolean previousCharIsASpace = true;
        while (i != sb.length()) {
            if (Character.isSpaceChar(sb.charAt(i))) {
                if (! previousCharIsASpace) {
                    previousCharIsASpace = true;
                    sb.setCharAt(l, ' ');
                    l++;
                } 
            } else {
                previousCharIsASpace = false;
                sb.setCharAt(l, sb.charAt(i));
                l++;
            }
            i++;
        }
        if (l > 0 && sb.charAt(l-1) == ' ') l--;
        sb.setLength(l);
        return sb.toString();
    }

    private static String  definitionMarkupString = "#\\{([^\\|]*)\\|([^\\}]*)\\}\\#";
    private static Pattern definitionMarkup = Pattern.compile(definitionMarkupString);
    public static String convertToHumanReadableForm(String def) {
    	Matcher m = definitionMarkup.matcher(def);
        StringBuffer sb = new StringBuffer(def.length());
        while (m.find()) {
        	m.appendReplacement(sb, m.group(2));
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    public static String getHumanReadableForm(String id) {
    	String def = id.substring(id.indexOf("|")+1);
    	return convertToHumanReadableForm(def);
    }
    
    // TODO: dissociates entry parsing and structure building in 2 classes.
    // So that we will factorize the matching code.
   protected void extractOrthoAlt(int startOffset, int endOffset) {
        Matcher bulletListMatcher = WikiPatterns.bulletListPattern.matcher(this.pageContent);
        bulletListMatcher.region(startOffset, endOffset);
        while (bulletListMatcher.find()) {
            String alt = cleanUpMarkup(bulletListMatcher.group(1), true);
            if (alt != null && ! alt.equals("")) {
            	wdh.registerAlternateSpelling(alt);
            }
        }      
     }
 
    // TODO: There are entries where Files, Fichier or Image Links are inside the entry and not at the end of it...
    // links.group(1).equalsIgnoreCase("Image") || 
    // links.group(1).equalsIgnoreCase("File") ||
    // links.group(1).equalsIgnoreCase("Fichier")
    protected int computeRegionEnd(int blockStart, Matcher m) {
        if (m.hitEnd()) {
            // Take out categories, files and interwiki links.
            Matcher links = WikiPatterns.categoryOrInterwikiLinkPattern.matcher(pageContent);
            links.region(blockStart, m.regionEnd());
            while (links.find()) {
                // TODO: use localized versions of the namespaces
                if (links.group(2).equals(this.wiktionaryPageName)
                 || links.group(1).equalsIgnoreCase("Catégorie")
                 || links.group(1).equalsIgnoreCase("Category")
                 || links.group(1).equalsIgnoreCase("Kategorie")
                 || links.group(1).equalsIgnoreCase("Annexe")
                 || LangTools.getCode(links.group(1)) != null) {
                    return links.start();
				} else if (links.group(1) != null) {
                	// System.out.println("--- In: " + this.wiktionaryPageName + " --->");
                	// System.out.println(links.group());
                }
            } 
            return m.regionEnd();
        } else {
            return m.start();
        }
    }

   
    // TODO: Some nyms can be placed in sublists and lists (hence with ** or ***). In this case, we currently extract the additional stars.
    protected void extractNyms(String synRelation, int startOffset, int endOffset) {
        // System.out.println(wiktionaryPageName + " contains: " + pageContent.substring(startOffset, endOffset));
        // Extract all links
        Matcher linkMatcher = WikiPatterns.linkPattern.matcher(this.pageContent);
        linkMatcher.region(startOffset, endOffset);
//        int lastNymEndOffset = startOffset;
//        int lastNymStartOffset = startOffset;
//        System.err.println("---- In: " + wiktionaryPageName + " ----");
//        System.err.println(this.pageContent.substring(startOffset, endOffset));
        while (linkMatcher.find()) {
        	// TODO: remove debug specific treatment for nym extraction and take a better heuristic
//        	if (lastNymEndOffset != startOffset) {
//        		String inbetween = this.pageContent.substring(lastNymEndOffset, linkMatcher.start());
//        		// if (! inbetween.matches(".*[,\\r\\n].*")) {	
//        		if (inbetween.equals(" ")) {
//        			System.out.println("---- In: " + wiktionaryPageName + " ----");
//        			System.out.println(this.pageContent.substring(lastNymStartOffset,linkMatcher.end()));
//        		}
//        	}
//        	lastNymStartOffset = linkMatcher.start();
//        	lastNymEndOffset = linkMatcher.end();
//        	// End of debug specific treatment for nym extraction...
//            System.err.println("Matched: " + linkMatcher.group(0));

            // It's a link, only keep the alternate string if present.
            String leftGroup = linkMatcher.group(1) ;
            if (leftGroup != null && ! leftGroup.equals("") && 
            		! leftGroup.startsWith("Wikisaurus:") &&
            		! leftGroup.startsWith("Catégorie:") &&
            		! leftGroup.startsWith("#")) {
            	wdh.registerNymRelation(leftGroup, synRelation);  
            }
        }      
    }

    // FIXME this doesn't handle nested parentheses. Is it correct?
    // Should be fixed now    --pantaleo  
    public static String stripParentheses(String s) {
		final int A = 0;
		final int B = 1;

		int ET = A;
		String resultat = "";
		int debut = 0;
		int fin = 0 ;    // la fin de partie qui nous inter
		int i = 0;
		int numberOfParentheses = 0;

		while(i!=s.length()){
			switch (ET){
			case A:
				if(s.charAt(i)=='('){
				    numberOfParentheses ++;
					// On a trouvé un debut de parenthese

					//On place la fin de la partie qui nous interesse
					fin= i;
					//on change d'etat
					ET=B;
					resultat = resultat +s.substring(debut, fin);
				}
				break;
			case B:
				if(s.charAt(i)==')'){
				    numberOfParentheses = numberOfParentheses - 1;
				    if (numberOfParentheses==0){
					// On a trouvé la fin du commentaire

					// on place le debut se le partie qui nous interesse
					debut= i+1;
					// on change d'etat
					ET=A;
				    }
				} else if (s.charAt(i)=='('){
				    numberOfParentheses ++;
				}
				break;

			default:
				System.err.println("Unexpected state number:" + ET);
				break;
			}

			// On passe au caractère suivant ;
			i=i+1;

		}
		if (i==s.length()) {
			switch (ET){
			case A:
				resultat = resultat +s.substring(debut);
				break;
			case B:
				break;

			default:
				System.err.println("Unexpected state number:" + ET);
				break;
			}
		}
		return resultat;
	}

}
