/**
 *
 */
package org.getalp.dbnary.eng;

import java.util.*;

import org.getalp.dbnary.*;
import org.getalp.dbnary.wiki.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pantaleo
 *
 */
public class POE {
    public ArrayList<String> part;
    public Map<String, String> args;
    public String string;
    static Logger log = LoggerFactory.getLogger(POE.class);

    public POE(String ss, String pp){
        part = new ArrayList<String>();
        part.add(pp);
        string = ss;
    }
 
    public String cleanUp(String word){
        word = word.replaceAll("\\[","").replaceAll("\\]", "").trim().replaceAll("'", "__").replaceAll("\\*", "_").replaceAll("^-", "__-");
        return word;
    }
   
    public POE(String group, int index){
        //System.out.format("%s ", EtymologyPatterns.possibleString[index]);
        string = group;
        part = new ArrayList<String>();
        if (index == 1){ //it's a template  
            args = WikiTool.parseArgs(group);
	    if (args.get("1").equals("cog") || args.get("1").equals("cognate")){//e.g.:       {{cog|fr|orgue}}
	        //System.out.format("COGNATE_WITH");
                part.add("COGNATE_WITH");
                //args.get("2") is the language and is compulsory
		if (args.get("3") != null){
		    //System.out.format(" LEMMA");
                    part.add("LEMMA");
		}
	    } else if (args.get("1").equals("etymtwin")){//e.g.:    {{etymtwin|lang=en}} {{m|en|foo}}
                //args.get("lang") is the language and is compulsory
		//System.out.format("COGNATE_WITH");//doublet
                part.add("COGNATE_WITH");
	    } else if (args.get("1").equals("inh") || args.get("1").equals("inherited") || args.get("1").equals("der") || args.get("1").equals("derived") || args.get("1").equals("bor") || args.get("1").equals("borrowing") || args.get("1").equals("loan")){//1=language, 2=language, (3=term), (4|alt=alternative), (tr=translation),(pos=) || 1=language, 2=language, (3=term)   || //1=language, 2=language, (3=term), (4|alt=alternative), (tr=translation),(pos=)
		if (args.get("lang") != null){
		    if (args.get("3") != null){
                        //System.out.format("LEMMA ");
                        part.add("LEMMA");
                        args.put("word1", cleanUp(args.get("3")));
                        //args.get("3").split(",")[0].removeAll("[").removeAll("]").trim();
                        args.remove("3");
		    }
                    if (args.get("2") != null){
			args.put("lang", args.get("2"));
			args.remove("2");
		    }
                } else {
                    if (args.get("3") != null){
      	      	      	args.put("lang", args.get("3"));
                        args.remove("3");
                    }
                    if (args.get("4") != null){
                        args.put("word1", cleanUp(args.get("4")));
			args.remove("4"); 
                        //System.out.format("LEMMA ");
                        part.add("LEMMA");
                    } else {
		        //System.out.format("LANGUAGE ");
                        part.add("LANGUAGE");
		    }
                    if (args.get("5") != null){
                        args.put("alt", args.get("5"));
                        args.remove("5");
		    }
                    if (args.get("6") != null){
      	      	      	args.put("gloss", args.get("6"));
      	      	      	args.remove("6");
                    }
		}
            } else if (args.get("1").equals("compound") || args.get("1").equals("calque") || args.get("1").equals("blend")){
		if (args.get("lang") == null){
		    args.put("lang", args.get("2"));
		    args.remove("2");
                    if (args.get("3") != null){//(1=language - can be empty) 2=first word 3=third word
                        //System.out.format("LEMMA");
                        part.add("LEMMA");
                        args.put("word1", cleanUp(args.get("3")));
		        args.remove("3");
		    }
                    for (int kk=4; kk<10; kk++){
                        if (args.get(Integer.toString(kk)) != null){
                            args.put("word"+Integer.toString(kk-2), cleanUp(args.get(Integer.toString(kk))));
                            args.remove(Integer.toString(kk));
	        	} else {
                            break;
      		        }
		    }
		} else {
		    if (args.get("2") != null){//(1=language - can be empty) 2=first word 3=third word                         
			//System.out.format("LEMMA");                                                                                                           
		        part.add("LEMMA");
		        args.put("word1", cleanUp(args.get("2")));
		        args.remove("2");
		    }
                    for (int kk=3; kk<10; kk++){
                        if (args.get(Integer.toString(kk)) != null){
                            args.put("word"+Integer.toString(kk-1), cleanUp(args.get(Integer.toString(kk))));
                            args.remove(Integer.toString(kk));
                        } else {
                            break;
                        }
                    }
		}
	    } else if (args.get("1").equals("etycomp")){ //e.g.: {{etycomp|lang1=de|inf1=|case1=|word1=dumm|trans1=dumb|lang2=|inf2=|case2=|word2=Kopf|trans2=head}} All parameters except word1= can be omitted.
		args.put("1", "compound");
                //System.out.format("LEMMA");
		part.add("LEMMA");
		for (int kk=1; kk<10; kk++){
		    if (args.get("word"+Integer.toString(kk)) != null){
			args.put("word"+Integer.toString(kk), cleanUp(args.get("word"+Integer.toString(kk))));
		    }
		}
	    } else if (args.get("1").equals("vi-etym-sino")){
                args.put("word1", cleanUp(args.get("1")));
                args.remove("1");
	        if (args.get("2") != null){
		    args.put("gloss1", args.get("2"));
		    args.remove("2");
	        }
                if (args.get("3") != null){
                    args.put("word2", cleanUp(args.get("3")));
                    args.remove("3");
                }
                if (args.get("4") != null){
                    args.put("gloss2", args.get("4"));
                    args.remove("4");
                }
		if (args.get("5") != null){
                    args.put("word3", cleanUp(args.get("5")));
                    args.remove("5");
                }
      	      	if (args.get("6") != null){
                    args.put("gloss3", args.get("6"));
                    args.remove("6");
                }
                //System.out.format("LEMMA"); //{{calque|année|lumière|etyl lang=en|etyl term=light year|lang=fr}} 1=language, 2=first term, (3=second term), (4=third term) etc       
                part.add("LEMMA");
	    } else if (args.get("1").equals("abbreviation of")){
                //System.out.format("FROM ");
                part.add("FROM");
                //System.out.format("LEMMA"); 
		part.add("LEMMA");
                args.put("word1", cleanUp(args.get("2")));
                args.remove("2");
                if (args.get("4") != null){
                    args.put("gloss", args.get("4"));
                    args.remove("4");
                }
	    } else if (args.get("1").equals("back-form") || args.get("1").equals("named-after")){ //1=term, (2=display form)                             
                //System.out.format("FROM ");
	        part.add("FROM");
	        //System.out.format("LEMMA");
	        part.add("LEMMA");
	        args.put("word1", cleanUp(args.get("2")));
	        args.remove("2");
            } else if (args.get("1").equals("m") || args.get("1").equals("mention") || args.get("1").equals("l") || args.get("1").equals("link")){
                args.put("1", "link");
                if (args.get("2") != null){
                    args.put("lang", args.get("2"));
                    args.remove("2");
                }
                if (args.get("3") != null){
                    if (args.get("3").equals("")){
                        args.remove("3");
			if (args.get("4") != null){
			    part.add("LEMMA");
			    args.put("word1", cleanUp(args.get("4")));
			    args.remove("4");
                            //System.out.format("args=%s\n", args);
			}
		    } else {
                        //System.out.format("LEMMA");
                        part.add("LEMMA");
		        args.put("word1", cleanUp(args.get("3")));
                        args.remove("3");
		        if (args.get("4") != null){
			    args.put("alt", args.get("4"));
			    args.remove("4");
		        }
		        if (args.get("5") != null){
			    args.put("gloss", args.get("5"));
			    args.remove("5");
		        }
		    }
		}
	    } else if (args.get("1").equals("affix") || args.get("1").equals("confix") ||args.get("1").equals("prefix") || args.get("1").equals("suffix")){
		//System.out.format("LEMMA");
                part.add("LEMMA");
                //System.out.format("args in suffix: =%s\n",args);
                if (args.get("lang") == null){
                    args.put("lang", args.get("2"));
      	      	    args.remove("2");
		    for (int kk=3; kk<10; kk++){
		        if (args.get(Integer.toString(kk)) != null){
			    args.put("word"+Integer.toString(kk-2), cleanUp(args.get(Integer.toString(kk))));
			    args.remove(Integer.toString(kk));
		        } else {
			    break;
		        }
		    }
		} else {
		    for (int kk=2; kk<9; kk++){
			if (args.get(Integer.toString(kk)) != null){
			    args.put("word"+Integer.toString(kk-1), cleanUp(args.get(Integer.toString(kk))));
			    args.remove(Integer.toString(kk));
			} else {
			    break;
			}
		    }
		}
	    } else if (args.get("1").equals("infix") || args.get("1").equals("circumfix") || args.get("1").equals("clipping") || args.get("1").equals("hu-prefix") || args.get("1").equals("hu-suffix")){
                args.put("word1", cleanUp(args.get("2")));
		args.remove("2");
                if (args.get("3") != null){
                    args.put("word2", cleanUp(args.get("3")));
                    args.remove("3");
                    if (args.get("4") != null){
		        args.put("word3", cleanUp(args.get("4")));
		        args.remove("4");
		    }
		}
                //System.out.format("LEMMA");
                part.add("LEMMA");
		//} else if (args.get("1").equals("term")){//e.g.: {{term|de-|di-|away}} 
                //log.debug("Deprecated template {} - Ignoring it", string);
 
                //args.put("word1", cleanUp(args.get("2")));
                //args.remove("2");
                //if (args.get("3") != null){ 
                //    args.put("alt", args.get("3"));
                //    args.remove("3");
		//}
                //if (args.get("4") != null){
                //    args.put("gloss", args.get("4"));
                //    args.remove("4");
                //}
                //System.out.format("LEMMA");
      	      	//part.add("LEMMA");
	    } else if (args.get("1").equals("etyl")){
		//System.out.format("LANGUAGE");
                part.add("LANGUAGE");
	    } else if (args.get("1").equals("etystub") || args.get("1").equals("rfe")){
		//System.out.format("EMPTY");
                part.add("EMPTY");
	    } else if (args.get("1").equals("-er")){
		args.put("word1", cleanUp(args.get("2")));
	        args.remove("2");
                args.put("word2", cleanUp(args.get("1")));
                args.put("1", "agent noun ending in -er");
                args.put("lang", "en");
                part.add("LEMMA");
	    } else if (args.get("1").equals("-or")){
		args.put("word1", cleanUp(args.get("2")));
	        args.remove("2");
		args.put("word2", cleanUp(args.get("1")));
	        args.put("1", "agent noun ending in -or");
	        args.put("lang", "en");       
	    } else {
                log.debug("Ignoring template {} in either Etymology or Derived terms or Descendants section", string);
                args.clear();
                string = null;
		part = null;
                //part.add("ERROR");
	    }
	    //for (String key: args.keySet()){		
	    //String value = args.get(key);
		//System.out.format("%s=%s; ", key, value);
	    //}
	} else if (index == 2){ //it's a Wiktionary link
            String[] subs = string.split("\\|");
            if (subs.length > 1){
		log.debug("Ignoring unexpected argument {} in wiktionary link", string);
                string = null;
		args = null;
                part = null;
	    } else {
                if (string.startsWith("Kanien'keh")){
                    string = "Kanienkehaka";
                }
                String[] substring = string.split(":");
                if (substring.length == 1){ //it's a Wiktionary link to the English version of Wiktionary
                    //System.out.format("it's a wiktionary link to english word\n");
                    log.debug("Processing wiki link {} as English word", string);
                    args = new HashMap<String, String>();
                    args.put("1", "l");
                    args.put("word1", substring[0]);
                    args.put("lang", "en");
                    //System.out.format("LEMMA");
                    part.add("LEMMA");
                    string="l|en|"+substring[0];
	        } else {
                    if (substring[0].length() == 0 || substring[0].equals("Image") || substring[0].equals("Category") || substring[0].equals("File")){ //it's not a Wiktionary link eg:  [[:Category:English words derived from: load (noun)]]
                        //log.debug("Unknown Wiktionary link");
			log.debug("Ignoring unexpected argument {} in wiki link", string);
                        args = null;
                        string = null;
			part = null;
                        //System.out.format("ERROR (it's not a wiktionary link)\n");
                        //part.add("ERROR");
		    } else {
                        args = new HashMap<String, String>();
                        args.put("1", "l");
                        args.put("lang", substring[0]);
                        args.put("word1", substring[1]);
                        //System.out.format("LEMMA (from a wiktionary link)");
                        part.add("LEMMA");
			log.debug("Processing wiki link {} as {} template \\{\\{{}\\}{}\\}\\}", string,"l|"+substring[0], substring[1]);
                        string="l|"+substring[0]+"|"+substring[1];
		    }
		}
	    }
	} else {
            //System.out.format(EtymologyPatterns.possibleString[index]);
            part.add(EtymologyPatterns.possibleString[index]);
        }
        if (args != null){
            if (args.containsKey("lang")){
		//normalize args.get("lang") 
                String languageCode = args.get("lang");
                //String languageName = EnglishLangToCode.getLanguageName(languageCode);
                //languageCode = EnglishLangToCode.getLanguageCode(languageName);
                languageCode = EnglishLangToCode.threeLettersCode(languageCode);
                if (languageCode != null){
                   args.put("lang", languageCode);
		}//else leave it as it is
	    }
	}
    }
}
