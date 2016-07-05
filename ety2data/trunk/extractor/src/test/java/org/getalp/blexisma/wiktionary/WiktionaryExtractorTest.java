package org.getalp.blexisma.wiktionary;

import static org.junit.Assert.assertEquals;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.junit.Test;


public class WiktionaryExtractorTest {
    
    @Test
    public void testLeadingChars() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("   XYZ");
        assertEquals("cleanUp failed", "XYZ", result);
    }

    @Test
    public void testTrailingChars() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("XYZ    ");
        assertEquals("cleanUp failed", "XYZ", result);
    }
    
    @Test
    public void testInsiders() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("   X   Y   Z ");
        assertEquals("cleanUp failed", "X Y Z", result);
    }
    
    @Test
    public void testAllWhites() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("          ");
        assertEquals("cleanUp failed", "", result);
    }
    
    @Test
    public void testEmpty() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("");
        assertEquals("cleanUp failed", "", result);
    }
    
    @Test
    public void testMacroIsIgnored() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("{{toto|titi}} XYZ");
        assertEquals("cleanUp failed", "XYZ", result);
    }
    
    @Test
    public void testLinkIsKeptInHumanReadableForm() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("[[lemma|occurence]] XYZ", true);
        assertEquals("cleanUp failed", "occurence XYZ", result);
    }
    
    @Test
    public void testLinkIsKeptInDefaultForm() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("[[lemma|occurence]] XYZ");
        assertEquals("cleanUp failed", "#{lemma|occurence}# XYZ", result);
    }
    
    @Test
    public void testLinkWithoutOccurenceIsKeptInDefaultForm() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("[[lemma]] XYZ");
        assertEquals("cleanUp failed", "#{lemma|lemma}# XYZ", result);
    }
    
    @Test
    public void testLinkWithoutOccurenceIsHumanReadableForm() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("[[lemma]] XYZ", true);
        assertEquals("cleanUp failed", "lemma XYZ", result);
    }
    
    @Test
    public void testLinkWithStupidlyEncodedMorphology() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("[[avion]]s", false);
        assertEquals("cleanUp failed", "#{avion|avions}#", result);
    }
    
    @Test
    public void testDefWithStupidlyEncodedMorphology() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("A failing grade in a class or course.  The next best grade is a [[D]].  Some institutions issue [[E]]s instead of [[F]]s.", false);
        assertEquals("cleanUp failed", "A failing grade in a class or course. The next best grade is a #{D|D}#. Some institutions issue #{E|Es}# instead of #{F|Fs}#.", result);
    }
    
    @Test  
    public void testDocumentationExampleNonHumanReadable() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("{{a Macro}} will be [[discard]]ed and [[feed|fed]] to the [[void]].", false);
        assertEquals("cleanUp failed", "will be #{discard|discarded}# and #{feed|fed}# to the #{void|void}#.", result);
    }

    @Test  
    public void testDocumentationExampleHumanReadable() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("{{a Macro}} will be [[discard]]ed and [[feed|fed]] to the [[void]].", true);
        assertEquals("cleanUp failed", "will be discarded and fed to the void.", result);
    }

    @Test
    public void testEmphasized() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("'''l'action''' ''compte''", false);
        assertEquals("cleanUp failed", "l'action compte", result);
    }
    
    @Test
    public void testXmlComments1() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("   X<!-- tagada ploum -- -->Y   Z ");
        assertEquals("cleanUp failed", "XY Z", result);
    }

    @Test
    public void testXmlComments2() {
        String result = AbstractWiktionaryExtractor.cleanUpMarkup("   X<!-- {{toto}} -->Y   Z ");
        assertEquals("cleanUp failed", "XY Z", result);
    }

    @Test
    public void testXmlCommentsOnRamangerie() {
    	String test="== {{=fr=}} ==\n{{ébauche|fr}}\n\n{{-étym-}}\n: {{ébauche-étym|fr}}\n\n{{-nom-|fr}}\n'''ramangerie''' {{pron||fr}} {{f}} \n# {{cuisine|fr}} Préparation à base de [[cidre]].\n#* '' le mescapié ou '''ramangerie''' de pommes (réduction d’un moût de [[cidre]] bouilli plus de 48 heures)... '' — (Delahaye Thierry, Vin Pascal, ''Le pommier'', 95 p., page 64, 1997, Actes Sud, Le nom de l'arbre) \n \n{{-trad-}}\n{{(}}\n{{)}}";
        String result = AbstractWiktionaryExtractor.removeXMLComments(test);
        assertEquals("XML Comment removal failed", test, result);
    }


    @Test  
    public void testDefinitionToHumanReadable() {
    	String data = "{{a Macro}} will be [[discard]]ed and [[feed|fed]] to the [[void]].";
        String result1 = AbstractWiktionaryExtractor.cleanUpMarkup(data, true);
        String def = AbstractWiktionaryExtractor.cleanUpMarkup(data, false);
        String result2 = AbstractWiktionaryExtractor.convertToHumanReadableForm(def);
        assertEquals("Hman readable form should be the same in both results", result1, result2);
    }
}
