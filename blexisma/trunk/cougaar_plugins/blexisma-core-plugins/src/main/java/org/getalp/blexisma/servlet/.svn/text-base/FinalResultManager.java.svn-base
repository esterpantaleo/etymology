package org.getalp.blexisma.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.cougaar.core.blackboard.DeltaSubscription;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.util.UID;
import org.cougaar.util.Arguments;
import org.cougaar.util.FutureResult;
import org.cougaar.util.UnaryPredicate;
import org.getalp.blexisma.api.ConceptualVector;
import org.getalp.blexisma.api.ISO639_3;
import org.getalp.blexisma.api.SemanticDefinition;
import org.getalp.blexisma.api.Sense;
import org.getalp.blexisma.cougaarcom.DefinitionRequest;
import org.getalp.blexisma.cougaarcom.ProxRequest;
import org.getalp.blexisma.cougaarcom.ProxVectorRequest;
import org.getalp.blexisma.cougaarcom.SemanticAnalysisJob;
import org.getalp.blexisma.cougaarcom.SemanticJobPhase;
import org.getalp.blexisma.cougaarcom.SemanticLearningJob;
import org.getalp.blexisma.cougaarcom.VectorRequest;
import org.getalp.blexisma.utils.WriteInFile;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author Alexandre Labadié
 * */
public class FinalResultManager extends ComponentPlugin
{
	private static final int COMPONENT_ID = FinalResultManager.class.getCanonicalName().hashCode();
	private IncrementalSubscription requests;
	private IncrementalSubscription answers;
	private IncrementalSubscription proxWord;
	private IncrementalSubscription proxVect;
	private IncrementalSubscription vect;
	private IncrementalSubscription def;
	private IncrementalSubscription learns;
	private HashMap<UID,FutureResult> references;
	private HashMap<UID,String> traceMap;
	private File traceFile;
	private int dimension;
	private int codeLength;
	private String[] languageList;
	private LoggingService log;
	
	private enum RequestType {
		ANALYSIS,PROX,VECTOR,DEFINITION,PROXVECTOR, LEARN;
	};
	
	/**
	 * Called before the plugin is loaded. Initialize the parameters
	 * */
	public void setParameter(Object o)
	{
		Arguments args = new Arguments(o);
		dimension = args.getInt("dimension");
		// TODO: remove the dimension and codelength parameter from final result manager.
		codeLength = args.getInt("codeLength");
		languageList = args.getString("languageList").split(",");
		for (int i=0; i<languageList.length; i++) languageList[i]=ISO639_3.sharedInstance.getIdCode(languageList[i]);
	}
	
	/**
	 * Called when the Plugin is loaded.  Establish the subscription for
	 * SygfranRequest objects
	 * */
	protected void setupSubscriptions() 
	{
		log = getServiceBroker().getService(this, LoggingService.class, null);
		requests = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new WebRequestPredicate()));
		answers = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new FinalJobPredicate()));
		proxWord = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new ProxRequestPredicate()));
		proxVect = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new ProxVectorRequestPredicate()));
		vect = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new VectorRequestPredicate()));
		def = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(new DefinitionRequestPredicate()));
		learns = (IncrementalSubscription)getBlackboardService().subscribe(new DeltaSubscription(createFinishedSemanticLearningPredicate(COMPONENT_ID)));
		references = new HashMap<UID,FutureResult>();
		traceMap = new HashMap<UID,String>();
		traceFile = new File("text_vector.trace");
		if (log.isShoutEnabled()) log.shout("Semantic Analysis Requests manager online.");
	}
	
	/**
	   * Called when there is a change on my subscription(s).
	   * This plugin will publish on the blackboard the matching SygfranAnswer
	   */
	protected void execute() 
	{
		webManagement();
		finalJobManagement();
		finalProxManagement();
		finalVectManagement();
		finalDefManagement();
		finalLearnManagement();
		finalProxVectorManagement();
	}
	
	private void webManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<WebRequest> new_requests = requests.getAddedList();
		WebRequest rTmp = null;
		SemanticAnalysisJob jTmp = null;
		ProxRequest pTmp = null;
		VectorRequest vect = null;
		ProxVectorRequest proxvect = null;
		RequestType type = null;

		while (new_requests.hasMoreElements()) {
			if (log.isDebugEnabled()) log.debug("Manager: receiving new web request");
			rTmp = new_requests.nextElement();
			try {
				type = chooseRequestType(rTmp.getRequest());
				
				if (log.isDebugEnabled()) log.debug("type="+type);
				
				switch(type) {
					case ANALYSIS :
						if (log.isDebugEnabled()) log.debug("ANALYSIS");
						jTmp = xmlToJob(rTmp.getRequest(),rTmp.getUid());
						if (validLanguage(jTmp.getLang())) {
							traceMap.put(rTmp.getUid(), jTmp.getData());
							references.put(rTmp.getUid(), rTmp.getFuture());
							if (log.isDebugEnabled()) log.debug("Manager: posting new analysis job" + jTmp);
							getBlackboardService().publishAdd(jTmp);
						} else {
							rTmp.getFuture().set("<error>Not a valid language</error>");
						}
					break;
					case DEFINITION :
						if (log.isDebugEnabled()) log.debug("DEFINITION");
						DefinitionRequest def = xmlToDef(rTmp.getRequest(),rTmp.getUid());
						if (validLanguage(def.getLang())) {
							references.put(rTmp.getUid(), rTmp.getFuture());
							if (log.isDebugEnabled()) log.debug("Manager: posting new definition request " + def.getLemma());
							getBlackboardService().publishAdd(def);
						} else {
							rTmp.getFuture().set("<error>Not a valid language</error>");
						}
					break;
					case LEARN :
						if (log.isDebugEnabled()) log.debug("LEARN Request");
						SemanticLearningJob learn = xmlToSemanticLearningJob(rTmp.getRequest(),rTmp.getUid());
						int p = learn.getId().indexOf("|");
						String lang = "";
						if (p != -1) lang = learn.getId().substring(1,p);
						if (validLanguage(lang)) {
							references.put(rTmp.getUid(), rTmp.getFuture());
							if (log.isDebugEnabled()) log.debug("Manager: posting new learn request " + learn.getId());
							getBlackboardService().publishAdd(learn);
						} else {
							rTmp.getFuture().set("<error>Not a valid language</error>");
						}
					break;
					case PROX :
						if (log.isDebugEnabled()) log.debug("PROX");
						pTmp = xmlToProx(rTmp.getRequest(),rTmp.getUid());
						if (validLanguage(pTmp.getLang())) {
							references.put(rTmp.getUid(), rTmp.getFuture());
							if (log.isDebugEnabled()) log.debug("Manager: posting new prox request " + pTmp.getLemme());
							getBlackboardService().publishAdd(pTmp);
						} else {
							rTmp.getFuture().set("<error>Not a valid language</error>");
						}
					break;
					case VECTOR :
						if (log.isDebugEnabled()) log.debug("VECTOR");
						vect = xmlToVR(rTmp.getRequest(),rTmp.getUid());
						if (validLanguage(vect.getLang())) {
							references.put(rTmp.getUid(), rTmp.getFuture());
							if (log.isDebugEnabled()) log.debug("Manager: posting new vector request " + vect.getLemma());
							getBlackboardService().publishAdd(vect);
						}
						else {
							rTmp.getFuture().set("<error>Not a valid language</error>");
						}
					break;
					case PROXVECTOR :
						if (log.isDebugEnabled()) log.debug("PROXVECTOR");
						proxvect = xmlToProxVect(rTmp.getRequest(),rTmp.getUid());
						if (log.isDebugEnabled()) log.debug("proxvector created");
						references.put(rTmp.getUid(), rTmp.getFuture());
						if (log.isDebugEnabled()) log.debug("Manager: posting new prox vector request ");
						getBlackboardService().publishAdd(proxvect);
					break;
					default :
					break;
				}
			} catch (RuntimeException e) {
				PrintWriter pw = new PrintWriter(new StringWriter());
				e.printStackTrace(pw);
				rTmp.getFuture().set("<error>Exception raised: "+ e.getMessage() + "\n" + pw.toString() +
						"</error>");
			}
		}
	}
	
	private void finalJobManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticAnalysisJob> new_answers = answers.getChangedList();
		SemanticAnalysisJob jTmp = null;
		FutureResult fTmp = null;
		
		while (new_answers.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished job");
			jTmp = new_answers.nextElement();
			
			fTmp = references.get(jTmp.getUid());
			try {
				fTmp.set(jobToXML(jTmp));
				WriteInFile.appendText(traceFile, traceMap.get(jTmp.getUid()).replaceAll("[\r\n]+", " ")+"|"+jTmp.getData()+"\n","UTF-8");
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(jTmp.getUid());
			getBlackboardService().publishRemove(jTmp);
		}
	}
	
	private void finalProxManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<ProxRequest> new_prox = proxWord.getChangedList();
		ProxRequest pReq = null;
		FutureResult fTmp = null;
		
		while (new_prox.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished prox request");
			pReq = new_prox.nextElement();
			
			fTmp = references.get(pReq.getUid());
			try {
				fTmp.set(proxToXML(pReq));
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(pReq.getUid());
			getBlackboardService().publishRemove(pReq);
		}
	}
	
	private void finalVectManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<VectorRequest> new_vect = vect.getChangedList();
		VectorRequest vectR = null;
		FutureResult fTmp = null;
		
		while (new_vect.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished vector request");
			vectR = new_vect.nextElement();
			
			fTmp = references.get(vectR.getUid());
			try {
				fTmp.set(vectToXML(vectR));
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(vectR.getUid());
			getBlackboardService().publishRemove(vectR);
		}
	}
	
	private void finalDefManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<DefinitionRequest> new_def = def.getChangedList();
		DefinitionRequest defreq = null;
		FutureResult fTmp = null;
		
		while (new_def.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished definition request");
			defreq = new_def.nextElement();
			
			fTmp = references.get(defreq.getUid());
			try {
				fTmp.set(defToXML(defreq.getDef()));
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(defreq.getUid());
			getBlackboardService().publishRemove(defreq);
		}
	}
	
	private void finalLearnManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<SemanticLearningJob> new_learns = learns.getChangedList();
		SemanticLearningJob req = null;
		FutureResult fTmp = null;
		
		while (new_learns.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished learn request");
			req = new_learns.nextElement();
			
			fTmp = references.get(req.getUid());
			try {
				fTmp.set(defToXML(req.getSemDefinition()));
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(req.getUid());
			getBlackboardService().publishRemove(req);
		}		
	}

	private void finalProxVectorManagement() {
		@SuppressWarnings("unchecked")
		Enumeration<ProxVectorRequest> new_pv = proxVect.getChangedList();
		ProxVectorRequest pvr = null;
		FutureResult fTmp = null;
		
		while (new_pv.hasMoreElements())
		{
			if (log.isDebugEnabled()) log.debug("Manager: receiving finished prox vector request");
			pvr = new_pv.nextElement();
			
			fTmp = references.get(pvr.getUid());
			try {
				fTmp.set(proxVectToXML(pvr));
			} catch (Exception e) {
				fTmp.setException(e);
			}
			if (log.isDebugEnabled()) log.debug("Manager: removing finished job from references");
			references.remove(pvr.getUid());
			getBlackboardService().publishRemove(pvr);
		}
	}
	
	private ProxRequest xmlToProx(String r,UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		ProxRequest req = null;
		String lemme = null;
		String lang = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		lang = ISO639_3.sharedInstance.getIdCode(root.getChild("language").getTextTrim());
		lemme = root.getChild("prox").getTextTrim().split(" ")[0];
		Element nr = root.getChild("regex");
		String regex = null;
		if (nr != null)
			regex = nr.getTextTrim();
		Element n = root.getChild("nbprox");
		int nbprox = 10;
		if (n != null)
			nbprox = Integer.parseInt(n.getTextTrim());
		if (regex != null) {
			req = new ProxRequest(lemme,lang,regex,nbprox,uid);
		} else {
			req = new ProxRequest(lemme,lang,uid);
		}
		return req;
	}
	
	private SemanticAnalysisJob xmlToJob(String r,UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		SemanticAnalysisJob job = null;
		long id = 0;
		String lang = null;
		String data = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		id = new Long(root.getChild("id").getTextTrim());
		if (log.isDebugEnabled()) log.debug("id="+id);
		lang = ISO639_3.sharedInstance.getIdCode(root.getChild("language").getTextTrim());
		if (log.isDebugEnabled()) log.debug("lang="+lang);
		// TODO: Maybe add a final stop to the title for better syntactic parsing.
		data = root.getChild("title").getTextTrim()+". "+root.getChild("text").getTextTrim();
		if (log.isDebugEnabled()) log.debug("data="+data);
		
		job = new SemanticAnalysisJob(id, lang, data,uid);
		// TODO: should be removed !
		job.setVectNorm(codeLength);
		job.setVectSize(dimension);
		
		return job;
	}
	
	private VectorRequest xmlToVR(String r, UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		VectorRequest req = new VectorRequest(uid);
		String lang = null;
		String lemma = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		lang = ISO639_3.sharedInstance.getIdCode(root.getChild("language").getTextTrim());
		if (log.isDebugEnabled()) log.debug("lang="+lang);
		lemma = root.getChild("vectorlemma").getTextTrim();
		
		req.setLang(lang);
		req.setLemma(lemma);
		req.setVector(null);
		
		return req;
	}
	
	private DefinitionRequest xmlToDef(String r, UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		DefinitionRequest req = new DefinitionRequest(uid);
		String lang = null;
		String lemma = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		lang = ISO639_3.sharedInstance.getIdCode(root.getChild("language").getTextTrim());
		if (log.isDebugEnabled()) log.debug("lang="+lang);
		lemma = root.getChild("definition").getTextTrim();
		if (log.isDebugEnabled()) log.debug("lemma="+lemma);
		
		req.setLang(lang);
		req.setLemma(lemma);
		req.setDef(null);
		
		return req;
	}
	
	private SemanticLearningJob xmlToSemanticLearningJob(String r, UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		SemanticLearningJob req = new SemanticLearningJob(uid, COMPONENT_ID);
		String lang = null;
		String lemmaNodeId = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		lemmaNodeId = root.getChild("learn").getTextTrim();
		if (log.isDebugEnabled()) log.debug("lemma="+lemmaNodeId);
		
		req.setId(lemmaNodeId);
		
		return req;
	}
	
	
	private ProxVectorRequest xmlToProxVect(String r, UID uid) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		ProxVectorRequest req =null;
		String lang = null;
		ConceptualVector cv = null;
		int nb = 0;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root = doc.getRootElement();
		lang = ISO639_3.sharedInstance.getIdCode(root.getChild("language").getTextTrim());
		if (log.isDebugEnabled()) log.debug("lang="+lang);
		cv = new ConceptualVector(root.getChild("vectprox").getTextTrim(),dimension,codeLength);
		nb = Integer.parseInt(root.getChild("nb_prox").getTextTrim());
		
		req = new ProxVectorRequest(cv,lang, uid,nb,log);
		
		return req;
	}
	
	private boolean validLanguage(String lg)
	{
		String code = ISO639_3.sharedInstance.getIdCode(lg);
		
		if (log.isDebugEnabled()) {
			log.debug("language before coding: "+lg);
			log.debug("language after coding: "+code);
			for (int i=0; i<languageList.length; i++)
				log.debug("language list: "+languageList[i]);
		}
		
		if (code !=null) {
			for (int i=0;i<languageList.length;i++) {
				if (code.equals(languageList[i])) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param job : the job to convert into XML
	 * @return the job in XML
	 * */
	private String jobToXML(SemanticAnalysisJob job)
	{
		XMLOutputter output = new XMLOutputter();
		Element root = new Element("conceptual_vector");
		root.addContent(new Element("id").setText(new Long(job.getId()).toString()));
		root.addContent(new Element("dim").setText(new Long(job.getVectSize()).toString()));
		root.addContent(new Element("norm").setText(new Long(job.getVectNorm()).toString()));
		root.addContent(new Element("vect").setText(job.getData()));
		
		Document doc = new Document(root);
		
		output.setFormat(Format.getCompactFormat());
		
		return output.outputString(doc);
	}
	
	private String proxToXML(ProxRequest p) {
		XMLOutputter output = new XMLOutputter();
		Element root = new Element("List_of_prox");
		ArrayList<String> list = p.getProxList();
		if (log.isDebugEnabled()) log.debug("Prox to XML list "+list.toString());
		root.addContent(new Element("lemme").setText(p.getLemme()));
		root.addContent(new Element("language").setText(p.getLang()));
		
		for (int i=0;i<list.size();i++) {
			if (log.isDebugEnabled()) log.debug("Prox to XML adding "+list.get(i));
			root.addContent(new Element("prox_"+i).setText(list.get(i)));
		}
		
		Document doc = new Document(root);
		
		output.setFormat(Format.getCompactFormat());
		
		return output.outputString(doc);
	}
	
	private String vectToXML(VectorRequest vr) {
		XMLOutputter output = new XMLOutputter();
		Element root = new Element("vectorRequest");
		root.addContent(new Element("lemma").setText(vr.getLemma()));
		root.addContent(new Element("dim").setText(new Integer(vr.getVector().getDimension()).toString()));
		root.addContent(new Element("norm").setText(new Integer(vr.getVector().getCodeLength()).toString()));
		root.addContent(new Element("language").setText(vr.getLang()));
		root.addContent(new Element("vect").setText(vr.getVector().toStringHexa()));
		
		Document doc = new Document(root);
		
		output.setFormat(Format.getCompactFormat());
		
		return output.outputString(doc);
	}
	
	private String defToXML(SemanticDefinition def) {
		XMLOutputter output = new XMLOutputter();
		Element root = new Element("definitionRequest");
		Element listDef = new Element("listdef");
		Element currentDef = null;
		ArrayList<Sense> list = def.getSenseList();
		
		root.addContent(new Element("lemma").setText(def.getId()));
		
		for (int i=0; i<list.size(); i++) {
			currentDef = new Element("Sense_"+i);
			currentDef.addContent(new Comment(list.get(i).getNetworkDef()));
			currentDef.addContent(new Element("dim").setText(new Integer(list.get(i).getVector().getDimension()).toString()));
			currentDef.addContent(new Element("norm").setText(new Integer(list.get(i).getVector().getCodeLength()).toString()));
			currentDef.addContent(new Element("vect").setText(list.get(i).getVector().toStringHexa()));
			listDef.addContent(currentDef);
		}
		
		root.addContent(listDef);
		
		Document doc = new Document(root);
		
		output.setFormat(Format.getCompactFormat());
		
		return output.outputString(doc);
	}
	
	private String proxVectToXML(ProxVectorRequest p) {
		XMLOutputter output = new XMLOutputter();
		Element root = new Element("List_of_prox");
		ArrayList<String> list = p.getProxList();
		if (log.isDebugEnabled()) log.debug("Prox to XML list "+list.toString());
		root.addContent(new Element("language").setText(p.getLang()));
		
		for (int i=0;i<list.size();i++) {
			if (log.isDebugEnabled()) log.debug("Prox to XML adding "+list.get(i));
			root.addContent(new Element("prox_"+i).setText(list.get(i)));
		}
		
		Document doc = new Document(root);
		
		output.setFormat(Format.getCompactFormat());
		
		return output.outputString(doc);
	}
	
	private RequestType chooseRequestType(String r) {
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		Element root = null;
		
		try {
			doc = sb.build(new ByteArrayInputStream(r.getBytes("UTF-8")));
		} catch (JDOMException e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug("Mauvais format de données:");
				log.debug(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = doc.getRootElement();
		
		if (root.getChild("text")!=null) return RequestType.ANALYSIS;
		if (root.getChild("prox")!=null) return RequestType.PROX;
		if (root.getChild("vectprox")!=null) return RequestType.PROXVECTOR;
		if (root.getChild("vectorlemma")!=null) return RequestType.VECTOR;
		if (root.getChild("definition")!=null) return RequestType.DEFINITION;
		if (root.getChild("learn")!=null) return RequestType.LEARN;

		return null;
	}
	
	private UnaryPredicate createFinishedSemanticLearningPredicate(final int requester) {
		return new UnaryPredicate(){
			private static final long serialVersionUID = 3330143783224590543L;
			public boolean execute(Object o) {
				boolean pred = false;
				SemanticLearningJob lp = null;

				if (o instanceof SemanticLearningJob) {
					lp = (SemanticLearningJob)o;
					pred = (lp.getPhase()==SemanticJobPhase.DONE) && lp.getRequestingAgent()==requester;
				}

				return pred;
			}
		};
	}
}
