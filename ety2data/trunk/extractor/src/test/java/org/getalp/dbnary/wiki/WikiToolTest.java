package org.getalp.dbnary.wiki;

//import org.getalp.dbnary.Pair;

import org.getalp.dbnary.*;
import org.getalp.dbnary.eng.*;  
import org.junit.Test;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by serasset on 02/03/16, changed by pantaleo
 */
public class WikiToolTest {
    @Test
    public void testParse1() throws Exception {
        //test locateEnclosedString
	String sentence = "erty (s(ghj)) d";
        assertEquals(5, WikiTool.locateEnclosedString(sentence,"(",")").get(0).start);
	assertEquals(13, WikiTool.locateEnclosedString(sentence,"(",")").get(0).end);
        sentence = "erty {{ghj}} d";
        assertEquals(5, WikiTool.locateEnclosedString(sentence,"{{","}}").get(0).start);
	assertEquals(12, WikiTool.locateEnclosedString(sentence,"{{","}}").get(0).end);
	sentence = "erty ([[something]]) e";

        //test: containedIn andremove text contained in () unless () are located inside wiki link [[]] or inside wiki template {{}}, e.g. [[  (  )  ]] 
	ArrayList<Pair> linksLocation = WikiTool.locateEnclosedString(sentence, "[[", "]]");
        ArrayList<Pair> parenthesesLocation = WikiTool.locateEnclosedString(sentence, "(", ")");
        int parenthesesLocationlength = parenthesesLocation.size();
	for (int i=0; i<parenthesesLocationlength; i++){
            int j = parenthesesLocationlength-i-1;
            //check if parentheses are inside links [[  ()  ]]     
            if (parenthesesLocation.get(j).containedIn(linksLocation)){
                parenthesesLocation.remove(j);
            }
        }      
        sentence = WikiTool.removeTextWithin(sentence, parenthesesLocation);
        assertEquals("erty  e", sentence);

	//test parseArgs function
	Map<String, String> args = WikiTool.parseArgs("suffix|-onis|e||error=|gloss1=-er|gloss2={{l|en|feminine|fem.}}|lang=lv|");
        assertEquals("suffix", args.get("1"));
        assertEquals("-onis", args.get("2"));
        assertEquals("", args.get("4"));
	assertEquals("", args.get("error"));
        assertEquals("-er", args.get("gloss1"));
        assertEquals("{{l|en|feminine|fem.}}", args.get("gloss2"));
        assertEquals("lv",args.get("lang"));
        assertEquals(8, args.size());

        //test toArrayListPOE
        String etymologyString = "From {{m|it|pasta}}, from  [[it:pasta]]." ;
	ArrayListPOE etymology = WiktionaryExtractor.toArrayListPOE(etymologyString);
        assertEquals(6, etymology.size());
        assertEquals("m|it|pasta",  etymology.get(1).string);
    }

    @Test
    public void testRemoveReferences1() {
	String def = "tagada <ref name=\"toto\"/>.";
	assertEquals("tagada .", WikiTool.removeReferencesIn(def));
    }

    @Test
    public void testRemoveReferences2() {
	String def = "tagada <ref name=\"toto\">titi.";
	assertEquals("tagada ", WikiTool.removeReferencesIn(def));
    }

    @Test
    public void testRemoveReferences3() {
	String def = "tagada <ref name=\"toto\">titi</ref>.";
	assertEquals("tagada .", WikiTool.removeReferencesIn(def));
    }

    @Test
    public void testRemoveReferences4() {
	String def = "tagada <ref>titi</ref>.";
	assertEquals("tagada .", WikiTool.removeReferencesIn(def));
    }

    @Test
    public void testRemoveReferences5() {
	String def = "tagada <ref >titi</ref>.";
	assertEquals("tagada .", WikiTool.removeReferencesIn(def));
    }
}
