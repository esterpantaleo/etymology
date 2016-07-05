package org.getalp.blexisma.cougaarcom;

import java.io.Serializable;
import java.util.ArrayList;

import org.cougaar.core.service.LoggingService;
import org.cougaar.core.util.UID;
import org.getalp.blexisma.api.ConceptualVector;

public class ProxVectorRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9163036785789974300L;
	private final UID uid;
	private final String lang;
	private final ConceptualVector cv;
	private final int size;
	private ArrayList<String> proxList;
	private boolean end;
	
	public ProxVectorRequest(ConceptualVector cv, String lang, UID uid){
		this.cv = cv;
		this.lang = lang;
		this.uid = uid;
		this.size = 10;
		proxList = new ArrayList<String>();
		this.end = false;
	}
	
	public ProxVectorRequest(ConceptualVector cv, String lang, UID uid, int size, LoggingService log){
		if (log.isDebugEnabled()) log.debug("création de la requête");
		this.cv = cv;
		if (log.isDebugEnabled()) log.debug("cv sauvegardé");
		this.lang = lang;
		if (log.isDebugEnabled()) log.debug("langue enregistrée "+this.lang);
		this.uid = uid;
		if (log.isDebugEnabled()) log.debug("uid ok "+this.uid.toString());
		this.size = size;
		if (log.isDebugEnabled()) log.debug("taille check "+this.size);
		proxList = new ArrayList<String>();
		log.debug("liste créée");
		this.end = false;
		if (log.isDebugEnabled()) log.debug("booléen ok");
	}
	
	public ArrayList<String> getProxList() {
		return proxList;
	}

	public void setProxList(ArrayList<String> proxList) {
		this.proxList = proxList;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public UID getUid() {
		return uid;
	}

	public String getLang() {
		return lang;
	}

	public ConceptualVector getCv() {
		return cv;
	}

	public int getSize() {
		return size;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	} 
	
	
}
