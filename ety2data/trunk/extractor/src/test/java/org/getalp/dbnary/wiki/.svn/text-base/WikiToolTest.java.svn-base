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
}