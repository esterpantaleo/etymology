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

import java.util.Iterator;

import org.cougaar.bootstrap.SystemProperties;
import org.cougaar.core.agent.service.alarm.Alarm;
import org.cougaar.core.agent.service.alarm.AlarmBase;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.blackboard.TodoSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.relay.SimpleRelay;
import org.cougaar.core.relay.SimpleRelaySource;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.util.Arguments;
import org.cougaar.util.UnaryPredicate;

/**
 * This plugin is an example ping source that sends relays to a remote agent.
 * <p>
 * There can be multiple copies of this plugin in a single agent, but
 * every {@link PingSender} must have a unique target.  The target is
 * specified as a plugin parameter:
 * <dl>
 *   <dt>target=<i>String</i></dt>
 *   <dd>Required remote agent name.  If the agent doesn't exist then we
 *       wait forever -- there's no alarm-based timeout in this plugin
 *       implementation.</dd></p>
 *
 *   <dt>delayMillis=<i>long</i></dt>
 *   <dd>Delay milliseconds between relay iterations.  Set the delay to zero
 *       to run the pings as fast as possible.</dd><p>
 *
 *   <dt>verbose=<i>boolean</i></dt>
 *   <dd>Output SHOUT-level logging messages.  This can also be disabled
 *       by modifying the Cougaar logging configuration to set:<pre>
 *         log4j.category.org.getalp.blexisma.ping.PingSender=FATAL
 *         log4j.category.org.getalp.blexisma.ping.PingReceiver=FATAL
 *       </pre>
 *       For simplicity we support this as a plugin parameter, so new users
 *       don't need to configure the logging service.  If enabled, also
 *       consider turning off "+/-" message send/receive logging by
 *       setting:<pre>
 *         -Dorg.cougaar.core.agent.quiet=true
 *       </pre></dd><p>
 * </dl>
 *
 * @property org.getalp.blexisma.ping.delayMillis=5000
 *   PingSender delay between ping iterations, if not set as a plugin
 *   parameter.
 *
 * @property org.getalp.blexisma.ping.verbose=true
 *   PingSender should output SHOUT-level logging messages, if not set as
 *   a plugin parameter.
 *
 * @see PingReceiver Required plugin for every agent that will receive
 *   ping relays.
 *
 * @see PingServlet Optional browser-based GUI.
 */
public class PingSender extends ComponentPlugin {

  private static final long DEFAULT_DELAY_MILLIS =
    SystemProperties.getLong(
        "org.getalp.blexisma.ping.delayMillis", 5000);

  private static final boolean DEFAULT_VERBOSE =
    SystemProperties.getBoolean(
        "org.getalp.blexisma.ping.verbose", true);

  private LoggingService log;
  private UIDService uids;

  private MessageAddress target;
  private long delayMillis;
  private boolean verbose;

  private IncrementalSubscription sub;
  private TodoSubscription expiredAlarms;

  /** This method is called when the agent is created */
  public void load() {
    super.load();

    // Get our required Cougaar services
    log = (LoggingService)
      getServiceBroker().getService(this, LoggingService.class, null);
    uids = (UIDService)
      getServiceBroker().getService(this, UIDService.class, null);

    // Parse our plugin parameters
    Arguments args = new Arguments(getParameters());
    String target_name = args.getString("target", null);
    target = MessageAddress.getMessageAddress(target_name);
    if (target == null) {
      throw new IllegalArgumentException("Must specify a target");
    } else if (target.equals(agentId)) {
      throw new IllegalArgumentException("Target matches self: "+target);
    }
    delayMillis = args.getLong("delayMillis", DEFAULT_DELAY_MILLIS);
    verbose = args.getBoolean("verbose", DEFAULT_VERBOSE);
  }

  /** This method is called when the agent starts. */
  protected void setupSubscriptions() {

    // Create a holder for alarms that have come due
    //
    // The "myAlarms" string is any arbitrary identifier, and would only be
    // significant if we made more than one TodoSubscription instance.
    if (delayMillis > 0) {
      expiredAlarms = (TodoSubscription)
        blackboard.subscribe(new TodoSubscription("myAlarms"));
    }

    // Subscribe to all relays sent by our agent
    sub = (IncrementalSubscription) blackboard.subscribe(createPredicate());

    // Get our initial counter value, which is zero unless we're restarting
    // from an agent move or persistence snapshot
    int counter = getInitialCounter();

    // Send our first relay to our target
    sendNow(null, new Integer(counter));

    // When our target publishes a response, our "execute()" method will
    // be called.
  }

  /** This method is called whenever a subscription changes. */
  protected void execute() {
    // Observe changed relays by looking at our subscription's change list
    if (sub.hasChanged()) {
      for (Iterator iter = sub.getChangedCollection().iterator();
          iter.hasNext();
          ) {
        SimpleRelay relay = (SimpleRelay) iter.next();
        handleResponse(relay);
      }
    }

    // If we're using a delay, check to see if it is time to send the
    // next ping iteration
    if (delayMillis > 0 && expiredAlarms.hasChanged()) {
      for (Iterator iter = expiredAlarms.getAddedCollection().iterator();
          iter.hasNext();
          ) {
        MyAlarm alarm = (MyAlarm) iter.next();
        handleAlarm(alarm);
      }
    }
  }

  /** Create our subscription filter */
  private UnaryPredicate createPredicate() {
    // Match any relay sent by our agent and to our specific target,
    // in case this agent contains multiple senders to different targets.
    return new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof SimpleRelay) {
          SimpleRelay relay = (SimpleRelay) o;
          if (agentId.equals(relay.getSource()) &&
              target.equals(relay.getTarget())) {
            return true;
          }
        }
        return false;
      }
    };
  }

  /** Get our initial ping iteration counter value */
  private int getInitialCounter() {
    // Check to see if we've already sent a ping, in case we're restarting
    // from an agent move or persistence snapshot.
    int ret = 0;
    if (blackboard.didRehydrate()) {
      // Get the counter from our sent ping, if any, then remove it
      for (Iterator iter = sub.iterator(); iter.hasNext(); ) {
        SimpleRelay relay = (SimpleRelay) iter.next();
        ret = ((Integer) relay.getQuery()).intValue();
        blackboard.publishRemove(relay);
      }
      if (verbose && log.isShoutEnabled()) {
        log.shout("Resuming pings to "+target+" at counter "+ret);
      }
    }
    return ret;
  }

  /** Handle a response to a ping relay that we sent */
  private void handleResponse(SimpleRelay relay) {
    // Print the target's response
    if (verbose && log.isShoutEnabled()) {
      log.shout("Received response "+relay.getReply()+" from "+target);
    }

    // Figure out our next content value
    //
    // For scalability testing we could make this a large byte array.
    Integer old_content = (Integer) relay.getQuery();
    Integer new_content = new Integer(old_content.intValue() + 1);

    if (delayMillis > 0) {
      // Set an alarm to call our "execute()" method in the future
      sendLater(relay, new_content);
    } else {
      // Send our relay now
      sendNow(relay, new_content);
    }
  }

  /**
   * Wake up from an alarm to send our next relay iteration (only use if
   * delayMillis is greater than zero),
   */
  private void handleAlarm(MyAlarm alarm) {
    // Send our next relay iteration to the target
    SimpleRelay priorRelay = alarm.getPriorRelay();
    Object content = alarm.getContent();
    sendNow(priorRelay, content);
  }

  /** Send our next relay iteration now */
  private void sendNow(SimpleRelay priorRelay, Object content) {
    if (priorRelay != null) {
      // Remove query both locally and at the remote target, to cleanup
      // the blackboard.
      blackboard.publishRemove(priorRelay); 
    }

    // Send a new relay to the target
    SimpleRelay relay = new SimpleRelaySource(
        uids.nextUID(), agentId, target, content);
    if (verbose && log.isShoutEnabled()) {
      log.shout("Sending ping "+content+" to "+target);
    }
    blackboard.publishAdd(relay);
  }

  /** Send our next relay iteration after the non-zero delayMillis */
  private void sendLater(SimpleRelay priorRelay, Object content) {
    // Set an alarm to call our "execute()" method in the future.
    //
    // An asynchronous alarm is more efficient and scalable than calling
    // a blocking "Thread.sleep(delayMillis)", since it doesn't tie up a
    // pooled Cougaar thread.  By default, a Cougaar Node (JVM) is
    // configured to have a limit of 30 pooled threads.
    //
    // Instead of removing the relay now, we hold onto it until the alarm
    // is due.  This allows the PingServlet to see the old relay on
    // blackboard during our delay time.
    if (verbose && log.isShoutEnabled()) {
      log.shout(
          "Will send ping "+content+" to "+target+" in "+
          (delayMillis/1000)+" seconds");
    }
    long futureTime = System.currentTimeMillis() + delayMillis;
    Alarm alarm = new MyAlarm(priorRelay, content, futureTime);
    getAlarmService().addRealTimeAlarm(alarm);
  }

  /** An alarm that we use to wake us up after the delayMillis */
  private class MyAlarm extends AlarmBase {
    private SimpleRelay priorRelay;
    private Object content;
    public MyAlarm(SimpleRelay priorRelay, Object content, long futureTime) {
      super(futureTime);
      this.priorRelay = priorRelay;
      this.content = content;
    }
    public SimpleRelay getPriorRelay() { return priorRelay; }
    public Object getContent() { return content; }
    // Put this alarm on the "expiredAlarms" queue and request an "execute()"
    public void onExpire() {
      expiredAlarms.add(this);
    }
  }
}
