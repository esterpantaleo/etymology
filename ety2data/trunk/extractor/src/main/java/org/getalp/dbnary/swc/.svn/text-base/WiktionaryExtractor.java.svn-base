package org.getalp.dbnary.swc;

import org.getalp.dbnary.AbstractWiktionaryExtractor;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.parser.parser.LinkTargetException;


public class WiktionaryExtractor extends AbstractWiktionaryExtractor {


	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

	public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
		super(wdh);
	}


	@Override
	public void extractData() {
        // Test swebble parser
        // Set-up a simple wiki configuration
        WikiConfig config = DefaultConfigEnWp.generate();

        final int wrapCol = 80;

        // Instantiate a compiler for wiki pages
        WtEngineImpl engine = new WtEngineImpl(config);

        // Retrieve a page
        PageTitle pageTitle = null;
        try {
            long t0 = System.currentTimeMillis();
            pageTitle = PageTitle.make(config, this.wiktionaryPageName);


            PageId pageId = new PageId(pageTitle, -1);
            long t1 = System.currentTimeMillis();
            System.out.println("Init: " + (t1 - t0));
            // Compile the retrieved page
            EngProcessedPage cp = engine.parse(pageId, pageContent, null);
            long t2 = System.currentTimeMillis();
            System.out.println("Parse: " + (t2 - t1));

//            TreeStructureConverter p = new TreeStructureConverter(config, wrapCol);
//            System.out.println(p.go(cp.getPage()));
            long t3 =  System.currentTimeMillis();
            System.out.println("Visit: " + (t3 - t2));

            XPathBasedProcessing proc = new XPathBasedProcessing();
            System.out.println(proc.process(cp, "//WtSection"));
            System.out.println(proc.process(cp, "//WtSection[@level=\"0\"]"));
            System.out.println(proc.process(cp, "//WtSection/WtBody"));
            long t4 =  System.currentTimeMillis();
            System.out.println("All Xpathes: " + (t4 - t3));

        } catch (LinkTargetException e) {
            e.printStackTrace();
        } catch (EngineException e) {
            e.printStackTrace();
        }
    }

}
