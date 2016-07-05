package org.getalp.blexisma.wca;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import Dictionnary.Dictionary;

public class SingleTextProcessor implements Runnable {

	private String config;
	private Dictionary dict;
	private String tfile;
	private String rfile;

	public SingleTextProcessor(String config, Dictionary dict, String text, String result) {
		this.config = config;
		this.dict = dict;
		this.tfile = text;
		this.rfile = result;
	}
	
	
	@Override
	public void run() {
		VideoSenseContext ctxt = new VideoSenseContext(this.config);
		OpenFstTransducer g;
		try {
			double time = System.currentTimeMillis();
			g = ctxt.desamb(dict, this.tfile);
			writeResult(g.toString());
			System.err.println(this.tfile + "[" + (System.currentTimeMillis()-time) + "]");
		} catch (Exception e) {
			System.err.println("Exception caught while processing file " + this.tfile);
			e.printStackTrace(System.err);
		}
	}


	private void writeResult(String string) throws IOException {
		FileOutputStream o = new FileOutputStream(this.rfile);
		OutputStreamWriter p = new OutputStreamWriter(o, "UTF-8");
		p.append(string);
		p.flush();
		p.close();
	}

}
