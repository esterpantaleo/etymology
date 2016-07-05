package org.getalp.dbnary.experiment.preprocessing;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFrenchPreprocessing {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNormalizer() {
        assertTrue("trim invalide.", "toto".equals(AbstractGlossFilter.normalize("   toto   ")));
        assertTrue("normalisation invalide.", "toto titi".equals(AbstractGlossFilter.normalize("   toto   titi ")));
        assertTrue("normalisation invalide.", "toto titi".equals(AbstractGlossFilter.normalize("toto titi")));

        assertTrue("normalisation invalide", "l'oxygène est un gaz".equals(AbstractGlossFilter.normalize(" l'oxygène '''est      un ''gaz'''''")));
    }


}
