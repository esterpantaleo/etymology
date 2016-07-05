package org.getalp.blexisma.external.sygfranwrapper.tools;

import java.io.File;
import java.io.Serializable;

import org.getalp.blexisma.utils.OpenFile;

/**
 * Class holding parameters needed for SYGFRAN
 * */
public class SygParam implements Serializable
{
	/**
	 * Standard ID needed for serialization
	 */
	private static final long serialVersionUID = 1L;
	private String appliPath;
	private String dataPath;
	private String execPath;
	private int timeoutInSeconds = 0;
	
	/**
	 * @param appli : absolute path to the sygmart application
	 * @param data : path (relative or absolute) to the SYGFRAN data needed for the analysis
	 * @param exe : path (relative or absolute) to the execution directory. The SYGFRAN execution 
	 * directory is where the analysis file will be generated, it is wiser if it is a ramdisk
	 * */
	public SygParam(String appli, String data, String exe) {
		this(appli, data, exe, 0);
	}
	
	public SygParam(String appli, String data, String exe, int timeoutInSeconds) {
		this.appliPath = appli;
		this.dataPath = data;
		this.execPath = exe;
		this.timeoutInSeconds = timeoutInSeconds;
		ensureRuntimeDirExists();
	}
	
	/**
	 * @param appli : absolute path to the sygmart application
	 * @param data : path (relative or absolute) to the SYGFRAN data needed for the analysis
	 * @param exe : path (relative or absolute) to the execution directory. The SYGFRAN execution 
	 * directory is where the analysis file will be generated, it is wiser if it is a ramdisk
	 * */
	public SygParam(File f)
	{	
		this.appliPath = OpenFile.readLine(f, 0);
		this.dataPath = OpenFile.readLine(f, 1);
		this.execPath = OpenFile.readLine(f, 2);
		this.timeoutInSeconds = 0;
		ensureRuntimeDirExists();
	}
	
	private void ensureRuntimeDirExists() {
		// Ensure exec path exists. Create it if necessary.
		File x = new File(this.execPath); 
		if (! x.exists()) {
			x.mkdirs();
		}
	}
	/**
	 * @return The application path
	 * */
	public String getAppliPath() {
		return appliPath;
	}
	
	/**
	 * @return The analysis data path
	 * */
	public String getDataPath() {
		return dataPath;
	}
	
	/**
	 * @return The execution path
	 * */
	public String getExecPath() {
		return execPath;
	}
	
	public int getTimeoutInSeconds() {
		return this.timeoutInSeconds;
	}
}
