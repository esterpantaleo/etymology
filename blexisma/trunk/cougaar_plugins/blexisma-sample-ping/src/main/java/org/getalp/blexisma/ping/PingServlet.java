/*
 * <copyright>
 *  
 *  Copyright 1997-2006 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 * 
 *  You can redistribute this software and/or modify it under the
 *  terms of the Cougaar Open Source License as published on the
 *  Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * </copyright>
 */

package org.getalp.blexisma.ping;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.service.BlackboardQueryService;
import org.cougaar.core.servlet.ComponentServlet;
import org.cougaar.util.UnaryPredicate;

/**
 * This servlet shows our ping relays as an HTML page.
 * <p>
 * Supports an optional Servlet path parameter, which defaults to "/ping".
 * <p>
 * For simplicity, it's easiest to load a copy of this servlet into every
 * agent.
 */
public class PingServlet extends ComponentServlet {

  private long loadTime;

  private BlackboardQueryService blackboard;

  /** @return a default path if a plugin parameter is not specified */
  protected String getPath() {
    String ret = super.getPath();
    return (ret == null ? "/ping" : ret);
  }

  /** This method is called when the agent is created */
  public void load() {
    super.load();

    // Record our load time
    loadTime = System.currentTimeMillis();

    // Get our required Cougaar services
    this.blackboard = (BlackboardQueryService)
      getServiceBroker().getService(
          this, BlackboardQueryService.class, null);
  }

  /** This method is called whenever the browser loads our URL. */
  public void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    // Begin our HTML page response
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    String title = "Agent "+getEncodedAgentName();
    out.println(
        "<html>"+
        "<head><title>"+title+"</title></head>"+
        "<body><h1>"+title+"</h1>");

    // Write how long we've been running, to make it easy for the user
    // to calculate the ping throughput
    long runTime = System.currentTimeMillis() - loadTime;
    out.println(
        "Milliseconds since agent load: "+runTime+"<p>");

    // Query the blackboard for relays
    UnaryPredicate pred = new UnaryPredicate() {
      public boolean execute(Object o) {
        return (o instanceof SimpleRelay);
      }
    };
    Collection col = blackboard.query(pred);

    // Sort by source then target
    List l = sortRelays(new ArrayList(col));

    // Write the relays as an HTML table
    out.println(
        "<table border=1>"+
        "<tr>"+
        "  <th></th>"+
        "  <th>UID</th>"+
        "  <th>Source</th>"+
        "  <th>Target</th>"+
        "  <th>Content</th>"+
        "  <th>Response</th>"+
        "  <th><i>Pings Per Second</i></th>"+
        "</tr>");
    DecimalFormat formatter = new DecimalFormat("0.000");
    for (int i = 0; i < l.size(); i++) {
      SimpleRelay relay = (SimpleRelay) l.get(i);

      double throughput = Double.NaN;
      if (relay.getQuery() instanceof Number && runTime > 0) {
        double count = ((Number) relay.getQuery()).doubleValue();
        double seconds = (runTime / 1000);
        throughput = (count / seconds);
      }

      out.println(
          "<tr align=right>"+
          "  <td>"+i+"</td>"+
          "  <td>"+relay.getUID()+"</td>"+
          "  <td>"+relay.getSource()+"</td>"+
          "  <td>"+relay.getTarget()+"</td>"+
          "  <td>"+relay.getQuery()+"</td>"+
          "  <td>"+relay.getReply()+"</td>"+
          "  <td>"+formatter.format(throughput)+"</td>"+
          "<tr>");
    }
    out.println("</table>");

    // Create a "reload" button for the user to invoke our servlet again
    out.println(
        "<form method=\"get\" action=\""+request.getRequestURI()+"\">"+
        "  <input type=\"submit\" value=\"Reload\">"+
        "</form>");

    // End our HTML page
    out.println("</body></html>");
  }
  

  /** Sorting methods */
  private static List sortRelays(List l) {
    Comparator c = new Comparator() {
      public int compare(Object o1, Object o2) {
        return compareRelays((SimpleRelay) o1, (SimpleRelay) o2);
      }
    };
    Collections.sort(l, c);
    return l;
  }
  private static int compareRelays(SimpleRelay r1, SimpleRelay r2) {
    int ret;
    ret = compareAddresses(r1.getSource(), r2.getSource());
    if (ret != 0) return ret;
    ret = compareAddresses(r1.getTarget(), r2.getTarget());
    if (ret != 0) return ret;
    return compare(r1.getUID(), r2.getUID());
  }
  private static int compareAddresses(MessageAddress m1, MessageAddress m2) {
    String a = (m1 == null ? null : m1.getAddress());
    String b = (m2 == null ? null : m2.getAddress());
    return compare(a, b);
  }
  private static int compare(Comparable a, Comparable b) {
    return 
      (a == null ? (b == null ? 0 : 1) :
       b == null ? -1 : a.compareTo(b));
  }
}
