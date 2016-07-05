package org.getalp.dbnary.cli;

import info.bliki.api.Connector;
import info.bliki.api.User;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xml.sax.SAXException;

public class TestInterwikiLinks {
        public TestInterwikiLinks() {
                super();
        }

        public static void testQueryLangLinks() {
                User user = new User("", "", "http://en.wiktionary.org/w/api.php");
                user.login();
                String[] valuePairs = { "action", "parse", "prop", "iwlinks", "page", "flight" , "format", "json"};
                Connector connector = new Connector();
                String rawXmlResponse = connector.queryXML(user, valuePairs);
                if (rawXmlResponse == null) {
                        System.out.println("Got no XML result for the query");
                }
                System.out.println(rawXmlResponse);

        }

        public static void main(String[] args) {
                testQueryLangLinks();
        }
}
