package org.getalp.blexisma.external.sygfranwrapper.tools;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.getalp.blexisma.external.sygfranwrapper.structure.tree.SygMorphoSyntacticTree;
import org.getalp.blexisma.utils.OpenFile;
import org.getalp.blexisma.utils.WriteInFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexandre Labadié
 * Static and local call to SYGFRAN
 * */
public class SygLocal 
{
	static Logger logger = LoggerFactory.getLogger(SygLocal.class);
	
	/**
	 * Just a simple TimerTask that interrupts the specified thread when run.
	 */
	static class InterruptTimerTask
	        extends TimerTask
	{

	    private Thread thread;

	    public InterruptTimerTask(Thread t)
	    {
	        this.thread = t;
	    }

	    public void run()
	    {
	        thread.interrupt();
	    }

	}
	
	/**
	 * @param txt : text to analyze
	 * @param param : Sygmart parameters
	 * @return A morpho-syntactic tree of the analyzed text
	 * */
	public final static SygMorphoSyntacticTree localAnalysis(String txt,SygParam param)
	{
		File tmpAna;
		Runtime r = Runtime.getRuntime();
		Timer timer = null;
		Process p = null;
		String[] com = null;
		BufferedReader bfrd = null;
		File resana = null;
		String analyse = null;
		SygMorphoSyntacticTree result = null;
		

		try {
			tmpAna = File.createTempFile("textana", ".ana", new File(param.getExecPath()));
			// System.out.println("Sygfran: creating tmp file with text: "+txt);
		
		
		/* Writing it into the temporary file */
		WriteInFile.writeText(tmpAna, txt);
			
			/* Launching SYGFRAN analysis */
			com = new String[]{param.getAppliPath(),param.getDataPath(),tmpAna.getAbsolutePath()};
			try {
				// System.out.println("Sygfran: launching SYGFRAN");
				timer = new Timer(true);
	            InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread());
	            if (param.getTimeoutInSeconds() > 0) {
	            	timer.schedule(interrupter, param.getTimeoutInSeconds() * 1000);
	            }
				p = r.exec(com,null,new File(param.getExecPath()));
				bfrd = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while (bfrd.ready()) System.out.println(bfrd.readLine());
				p.waitFor();
			} catch(InterruptedException e) {
	            // do something to handle the timeout here
				logger.warn("SygFran analysis timed out for text: \"{}\"", txt);
				// process destroy will be done in finally block
	        }catch (IOException e) {
				logger.warn("IOException while executing SygFran.", e);
				e.printStackTrace();
			} finally {
				timer.cancel();     // If the process returns within the timeout period, we have to stop the interrupter
                // so that it does not unexpectedly interrupt some other code later.

				Thread.interrupted();   // We need to clear the interrupt flag on the current thread just in case
                    // interrupter executed after waitFor had already returned but before timer.cancel
                    // took effect.

				if (p != null) {
					close(p.getErrorStream());
					close(p.getInputStream());
					close(p.getOutputStream());
					p.destroy();
				}
			}
			
			/* Reading analysis results */
			// System.out.println("Sygfran: reading analysis");
			resana = new File(param.getExecPath()+tmpAna.getName().substring(0, tmpAna.getName().lastIndexOf("."))+".stx");
			analyse = OpenFile.readFullTextFile(resana);
			
			tmpAna.delete();
			resana.delete();
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		/* Building analysis tree */
		// System.out.println("Sygfran: building tree");
		result = new SygMorphoSyntacticTree(analyse);
		
		return result;
	}
	
	private static void close(Closeable c) {
	    if (c != null) {
	      try {
	        c.close();
	      } catch (IOException e) {
	        // ignored
	      }
	    }
	  }
	
	/**
	 * @param txt : text to analyze
	 * @param aPath : application path to Sygmart application (must be absolute)
	 * @param dPath : path to SYGFRAN data
	 * @param ePath : execution path for the analysis
	 * @return A morpho-syntactic tree of the analyzed text
	 * */
	public final static SygMorphoSyntacticTree localAnalysis(String txt, String aPath, String dPath, String ePath)
	{
		SygParam p = new SygParam(aPath,dPath,ePath);
		
		return localAnalysis(txt,p);
	}
}
