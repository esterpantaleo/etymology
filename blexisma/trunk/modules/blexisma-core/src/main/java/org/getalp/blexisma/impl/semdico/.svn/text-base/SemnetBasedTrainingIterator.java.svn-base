package org.getalp.blexisma.impl.semdico;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.getalp.blexisma.api.SemanticNetwork;
import org.getalp.blexisma.utils.OpenFile;
import org.getalp.blexisma.utils.WriteInFile;

public class SemnetBasedTrainingIterator implements
		PersistentlyIteratingSemanticNetwork {

	protected String lastId;
	protected String baseDir;
	protected String lastIdFile;
	protected SemanticNetwork<String, String> network;
	protected Iterator<String> infiniteNodeIterator;
	private static final Logger log = LoggerFactory.getLogger(SemnetBasedTrainingIterator.class);

	public SemnetBasedTrainingIterator(String basePath, SemanticNetwork<String, String> network) {
		this.baseDir = new File(basePath).getParent();
		this.network = network;
		File f = null;
		
		lastIdFile = baseDir+File.separator+"lastid.txt";
		
		f = new File(lastIdFile);

		if (f.exists()) {
			lastId = OpenFile.readFullTextFile(f);
			if (!validId(lastId)) {
				f.delete();
				try {
					f.createNewFile();
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error("Could not override file: " + f.getAbsolutePath(), e);
					}
				}
				lastId = null;
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				if (log.isErrorEnabled()) log.error("Could not create file: " + f.getAbsolutePath(), e);
			}
			lastId = null;
		}
		
		infiniteNodeIterator = (Iterator<String>) network.getInfiniteNodesIterator();
		log.info("Last computed vector was: " + lastId);

		if (lastId!=null) {
			if (log.isDebugEnabled()) {
				log.debug("Searching for last computed vector.");
			}
			while (!infiniteNodeIterator.next().equals(lastId));
			if (log.isInfoEnabled()) {
				log.info("Resuming training at lemma: " + lastId);
			}
		}
	}
	
	protected boolean validId(String id){
		return !id.equals("");
	}

	@Override
	public void saveIterationState() {
		WriteInFile.writeText(new File(lastIdFile), lastId);
	}

	@Override
	public String next() {
		return lastId = infiniteNodeIterator.next();
	}

	@Override
	public String lastId() {
		return lastId;
	}

}
