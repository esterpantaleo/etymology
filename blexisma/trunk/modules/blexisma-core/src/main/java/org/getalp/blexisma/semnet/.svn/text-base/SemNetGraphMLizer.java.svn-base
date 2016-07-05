package org.getalp.blexisma.semnet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import org.getalp.blexisma.api.GraphMLizableElement;
import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.api.SemanticNetworkGraphMLizer;

public class SemNetGraphMLizer extends SemanticNetworkGraphMLizer {

    protected static final String xmlHeader1 = "<?xml version=\"1.0\" encoding=\"";
    protected static final String xmlHeader2 = "\"?>";
    protected static final String graphmlHeader = "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns "
            + " http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">";
    protected static final String graphMLFooter = "</graphml>";

    protected OutputStreamWriter out = null;
    protected InputStreamReader in = null;
    protected String encoding = "";
    protected String EOL = "\n";
    protected String graphId = "G";

    /**
     * @return the eOL
     */
    public String getEOL() {
        return EOL;
    }

    /**
     * @param eOL
     *            the eOL to set
     */
    public void setEOL(String eOL) {
        EOL = eOL;
    }

    /**
     * @return the graphId
     */
    public String getGraphId() {
        return graphId;
    }

    /**
     * @param graphId
     *            the graphId to set
     */
    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    // Constructors
    public SemNetGraphMLizer() {
        in = new InputStreamReader(System.in);
        out = new OutputStreamWriter(System.out);
    }

    public SemNetGraphMLizer(OutputStream out, String encoding) throws UnsupportedEncodingException {
        this.out = new OutputStreamWriter(out, encoding);
        this.encoding = encoding;
    }

    public SemNetGraphMLizer(OutputStream out, String encoding, String endOfLine) throws UnsupportedEncodingException {
        this.out = new OutputStreamWriter(out, encoding);
        this.encoding = encoding;
        this.EOL = endOfLine;
    }

    public SemNetGraphMLizer(OutputStreamWriter out) {
        this.out = out;
        this.encoding = out.getEncoding();
    }

    @Override
    public void load(SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement> sm) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dump(SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement> sm) throws IOException {
        out.write(xmlHeader1 + encoding + xmlHeader2);
        out.write(EOL);
        out.write(graphmlHeader + EOL);

        Iterator<? extends SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement>.Edge> edges = sm
                .getEdgesIterator();
        Iterator<? extends GraphMLizableElement> nodes = sm.getNodesIterator();

        // Write out key descriptions.
        GraphMLizableElement firstNode = null;
        GraphMLizableElement firstRelation = null;
        if (edges.hasNext()) {
            SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement>.Edge firstEdge = edges.next();
            firstNode = firstEdge.getOrigin();
            firstRelation = firstEdge.getRelation();
        } else if (nodes.hasNext()) {
            firstNode = nodes.next();
        }
        int nbnatt = 0;
        if (firstNode != null) {
            nbnatt = firstNode.getNumberOfAttributes();
            for (int i = 0; i < nbnatt; i++) {
                String defaultValue = firstNode.getAttributeDefaultForId(i);
                out.write("  <key id=\"dn" + i + "\" for=\"node\"");
                out.write(" attr.name=\"" + firstNode.getAttributeNameForId(i) + "\"");
                out.write(" attr.type=\"" + firstNode.getAttributeTypeForId(i) + "\"");

                if (defaultValue == null) {
                    out.write("/>");
                } else {
                    out.write(">" + EOL);
                    out.write("    <default>" + defaultValue + "</default>" + EOL);
                    out.write("  </key>");
                }
                out.write(EOL);
            }
        }
        if (firstRelation != null) {
            int nbratt = firstRelation.getNumberOfAttributes();
            for (int i = 0; i < nbratt; i++) {
                String defaultValue = firstRelation.getAttributeDefaultForId(i);
                out.write("  <key id=\"de" + i + "\" for=\"edge\"");
                out.write(" attr.name=\"" + firstRelation.getAttributeNameForId(i) + "\"");
                out.write(" attr.type=\"" + firstRelation.getAttributeTypeForId(i) + "\"");

                if (defaultValue == null) {
                    out.write("/>");
                } else {
                    out.write(">" + EOL);
                    out.write("    <default>" + defaultValue + "</default>" + EOL);
                    out.write("  </key>");
                }
                out.write(EOL);
            }
        }

        // open the graph element
        out.write("<graph id=\"" + graphId + "\" edgedefault=\"directed\">");
        // Write out nodes, keep a node number for each of them
        HashMap<GraphMLizableElement, Integer> nodeNumbers = new HashMap<GraphMLizableElement, Integer>((int) (sm.getNbNodes() * 1.25));
        edges = sm.getEdgesIterator();
        nodes = sm.getNodesIterator();
        int cnid = 0;

        // Write out the node
        while (nodes.hasNext()) {
            GraphMLizableElement node = nodes.next();
            nodeNumbers.put(node, cnid);
            writeNode(node, cnid);
            out.flush();
            cnid++;
        }

        // Write out the node
        while (edges.hasNext()) {
            SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement>.Edge edge = edges.next();
            nodeNumbers.put(edge.getRelation(), cnid);
            writeEdge(edge, nodeNumbers, cnid);
            out.flush();
            cnid++;
        }
        // terminate graph element
        out.write(EOL + "  </graph>"+ EOL);
        // close the graphml file
        out.write(graphMLFooter + EOL);
        out.flush();
    }

    private void writeNode(GraphMLizableElement node, int id) throws IOException {
        out.write(EOL + "     <node id=\"n" + id + "\"");
        boolean datawritten = writeData(node, "dn");
        if (datawritten) out.write(EOL + "    </node>"); else out.write("/>");
    }

    private void writeEdge(SemanticNetwork<? extends GraphMLizableElement, ? extends GraphMLizableElement>.Edge edge, 
            HashMap<GraphMLizableElement, Integer> nodeNumbers, int cnid) throws IOException {
        out.write(EOL + "    <edge id=\"e" + cnid + "\" ");
        int sid = nodeNumbers.get(edge.getOrigin()).intValue();
        int tid = nodeNumbers.get(edge.getDestination()).intValue();
        out.write("source=\"n" + sid + "\" ");
        out.write("target=\"n" + tid + "\"");
        GraphMLizableElement relation = edge.getRelation();
        boolean datawritten = writeData(relation, "de");
        if (datawritten) out.write(EOL + "    </edge>"); else out.write("/>");
    }

    private boolean writeData(GraphMLizableElement e, String keyPrefix) throws IOException {
        int nbnatt = e.getNumberOfAttributes();
        boolean dataHasBeenWritten = false;
        for (int i = 0; i < nbnatt; i++) {
            String value = e.getAttributeValueForId(i);
            if (value != null) {
                // close graph element if data is to be written
                if (!dataHasBeenWritten) out.write(">");
                dataHasBeenWritten = true;
                // TODO don't forget to write the key of the data.
                out.write(EOL + "      <data key=\"" + keyPrefix + i + "\">" + value + "</data>");
            }
        }
        return dataHasBeenWritten;
    }


}
