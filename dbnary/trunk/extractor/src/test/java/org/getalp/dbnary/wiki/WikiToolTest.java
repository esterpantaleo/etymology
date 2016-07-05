package org.getalp.dbnary.wiki;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by serasset on 02/03/16.
 */
public class WikiToolTest {
    @Test
    public void testParse1() throws Exception {
        Map<String, String> args = WikiTool.parseArgs("grc|sc=polytonic|βοῦς||ox, cow");

        assertEquals("grc", args.get("1"));
        assertEquals("polytonic", args.get("sc"));
        assertEquals("βοῦς", args.get("2"));
        assertEquals("ox, cow", args.get("4"));
        assertEquals("", args.get("3"));
        assertEquals(5, args.size());

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
