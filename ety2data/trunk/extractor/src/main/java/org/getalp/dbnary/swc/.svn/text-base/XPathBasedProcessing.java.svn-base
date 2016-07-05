package org.getalp.dbnary.swc;

import de.fau.cs.osr.ptk.common.jxpath.AstNodePointerFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.utils.WtRtDataPrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by serasset on 05/05/15.
 */
public class XPathBasedProcessing {

    /**
     * IMPORTANT! Do not remove this, otherwise XPath queries won't work
     * properly on ASTs.
     */
    static
    {
        JXPathContextReferenceImpl.addNodePointerFactory(
                new AstNodePointerFactory());
    }

    public String process(EngProcessedPage cp, String query) {
        Iterator<?> results = null;
        try
        {
            JXPathContext context = JXPathContext.newContext(cp.getPage());

            results = context.iteratePointers(query);
        }
        catch (Throwable t)
        {
            System.err.println("An error occurred when executing XPath query.");
            t.printStackTrace();
        }

        if (results != null)
        {
            if (!results.hasNext())
            {
                System.err.println("XPath result empty!");
            }
            else
            {
                List<Object> r = new ArrayList<Object>();
                while (results.hasNext())
                    r.add(results.next());

                System.err.println("Found " + r.size() + " matching nodes.");

                StringBuilder b = new StringBuilder();

                int i = 1;
                for (Object o : r)
                {
                    Pointer n = (Pointer) o;
                    b.append('(');
                    b.append(query);
                    b.append(")[");
                    b.append(i);
                    b.append("]:\n\"\"\"");
                    b.append(n.asPath());
                    b.append("\"\"\"\n\n");
                    ++i;
                }

                return b.toString();
            }
        }

        return "";
    }

}

