package org.getalp.blexisma.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cougaar.core.plugin.ServletPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.util.Arguments;
import org.cougaar.util.FutureResult;

/**
 * @author Alexandre Labadié
 * */
public class CVServlet extends ServletPlugin
{
	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 1046281018911090172L;
	private static final long DEFAULT_TIMEOUT = 60000;
	private UIDService uidService;
	private LoggingService log;
	private static final String form;
	
	static {
		InputStream formStream = CVServlet.class.getResourceAsStream("DefaultForm.html");
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(formStream, "UTF-8"));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		form = sb.toString();
	}
	
	public void load() {
	    super.load();
		log = (LoggingService) getServiceBroker().getService(this, LoggingService.class, null);

	    // get the required unique identifier service
	    uidService = (UIDService) getServiceBroker().getService(
	        this, UIDService.class, null);
	    if (uidService == null) {
	      throw new RuntimeException("Unable to obtain the UIDService");
	    }

	  }

	  public void unload() {
	    super.unload();
	    if (uidService != null) {
	        getServiceBroker().releaseService(this, UIDService.class, uidService);
	        uidService = null;
	      }
		getServiceBroker().releaseService(this, LoggingService.class, log);
	  }

	  /**
	   * Override "isTransactional" to false.
	   * <p>
	   * By default, our "publishAdd" will be buffered until the end of our "doGet"
	   * call.  We want to publish it immediately, so the calculator plugin can see
	   * it and notify us of the result.  Therefore, we don't want our "doGet" to
	   * run in the "execute()" method's buffered blackboard transaction.
	   */
	  protected boolean isTransactional() { 
	    return false;
	  }

	
	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * SygfranRequest objects
	 * */
	protected void setupSubscriptions() {
		super.setupSubscriptions();
		if (log.isShoutEnabled()) log.shout("Semantic analysis servlet online");
	}
	
	/**
	 * Post method called when the servlet address is called (with the post method)
	 * */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (log.isDebugEnabled()) {
			StringBuffer s = new StringBuffer();
			Enumeration<?> test = request.getParameterNames();
			while(test.hasMoreElements()) 
				s.append((String)test.nextElement()+"/n");
			log.debug("Got post request..."+s.toString());
		}
		manager(request, response);
	}
	
	/**
	 * Get method called when the servlet address is called (with the post method)
	 * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (log.isDebugEnabled()) log.debug("Got get request...");
		manager(request, response);
	}
	
	private void manager(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		long timeout = DEFAULT_TIMEOUT;
		
		Arguments args = new Arguments(request.getParameterMap());
		String data = args.getString("data");
		if (data == null) {
			sendForm(request, response);
			return;
		} else if (request.getCharacterEncoding() == null) {
			data = new String(data.getBytes("ISO-8859-1"), "UTF-8");
		}
		
		FutureResult future = new FutureResult();
		WebRequest job = new WebRequest(data,future,uidService.nextUID());
		publishAdd(job);
		if (log.isDebugEnabled()) log.debug("Servlet: posting a new job");
		
		// wait for the result
	    String wait = null;
	    Exception e = null;
	    try {
	    	wait = (String) future.timedGet(timeout);
	    } catch (Exception ex) {
	      // either a timeout or an invalid argument
	      e = ex;
	    }
	    
	    // cleanup the blackboard
	    if (log.isDebugEnabled()) log.debug("Servlet: removing finished job from blackboard");
	    publishRemove(job);
	    if (log.isDebugEnabled()) log.debug("text to send "+wait);
	    if (e != null) {
	        // write error
	    	response.setContentType("text/plain");
	    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        PrintWriter out = response.getWriter();
	        e.printStackTrace(out);
	        return;
	      }
	    
	    response.setContentType("text/xml");
	    OutputStream outs = response.getOutputStream();
	    byte[] b = wait.getBytes("UTF-8");
	    // out = response.getWriter();
	    outs.write(b);
	    if (log.isDebugEnabled()) log.debug("Servlet: posted finished job");
	    outs.close();
	}

	private void sendForm(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	    response.setContentType("text/html");
	    OutputStream outs = response.getOutputStream();
	    byte[] b = form.getBytes("UTF-8");
	    // out = response.getWriter();
	    outs.write(b);
	    outs.close();		
	}
}
