package org.getalp.blexisma.servlet;

import java.io.Serializable;

import org.cougaar.core.util.UID;
import org.cougaar.util.FutureResult;

/**
 * @author Alexandre Labadié
 * */
public class WebRequest implements Serializable
{

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private final FutureResult future;
	private final UID uid;
	private final String request;
	
	public WebRequest(String request, FutureResult future, UID uid)
	{
		this.request = request;
		this.future = future;
		this.uid = uid;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public FutureResult getFuture() {
		return future;
	}
	
	public String getRequest() {
		return request;
	}

	public UID getUid() {
		return uid;
	}
}
