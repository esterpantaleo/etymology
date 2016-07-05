package org.getalp.blexisma.externals.treetagger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class TreeTagger extends SystemCall {
	
	protected String path;
	protected String text;
	
	public TreeTagger(String path) {
		this.path = path;
	}
	
	  /** @returns the command line as a String array where the first element is the command, followed by each argument.
     */
    public String [] commandLine() {
		String[] cmd = new String[1];
		cmd[0] = this.path;
    	return cmd;
    }
    
    /** writes the data to the process input stream. 
    * Do nothing if you do not have any input data to be given to the process input stream
     *
     */
    public void writeInputToProcess(OutputStream pin) {
    	Writer dot;
		try {
			dot = new OutputStreamWriter(new BufferedOutputStream(pin), "UTF-8");
			dot.write(this.text);
			dot.flush();
			dot.close();
		} catch (UnsupportedEncodingException e) {
			// Should never happen
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /** This method is called back if the process returned a non 0 error code. 
    */
    public void displayErrorOutput(int resCode, String errorMessage) {
    	System.err.println("Error while calling Tree Tagger command: [" + resCode + "] " + errorMessage);
    }

    public ByteArrayOutputStream call(String text) {
    	this.text = text;
    	return this.call();
    }

    public static void main(String args[]) throws UnsupportedEncodingException {
    	TreeTagger tt = new TreeTagger(args[0]);
    	ByteArrayOutputStream os = tt.call("Bonjour à tous les gens de la planète U.R.S.S. .");
    	System.out.println(os.toString("UTF-8"));
    }
}
