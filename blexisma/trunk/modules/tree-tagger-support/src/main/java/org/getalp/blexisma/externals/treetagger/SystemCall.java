package org.getalp.blexisma.externals.treetagger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class SystemCall {

	  /** @returns the command line as a String array where the first element is the command, followed by each argument.
     */
    public abstract String [] commandLine();
    
    /** writes the data to the process input stream. 
    * Do nothing if you do not have any input data to be given to the process input stream
     *
     */
    public abstract void writeInputToProcess(OutputStream pin);
    
    /** This method is called back if the process returned a non 0 error code. 
    */
    public abstract void displayErrorOutput(int resCode, String errorMessage);

    // SUBCLASS LISTENING TO THE ERROR STREAM
    static class ErrReader implements Runnable {
        InputStream is;
        StringBuffer sb = new StringBuffer();
        
        public ErrReader(InputStream is) {
            this.is = is;
        }
        
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String temp = null;
                while ((temp = in.readLine()) != null) {
                    sb.append(temp);
                    sb.append("\n");
                }
                is.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        public String getErrorMessage() {
            return sb.toString();
        }
    }
    
    // SUBCLASS LISTENING TO THE RESULT STREAM
    static class ResultReader implements Runnable {
        InputStream is;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        public ResultReader(InputStream is) {
            this.is = is;
        }
        
        public void run() {
            try {
                BufferedInputStream in = new BufferedInputStream(is);
                byte[] buf = new byte[4096];
                int l=0;
                while ((l = in.read(buf, 0, 4096)) != -1) {
                    os.write(buf, 0, l);
                }
                os.close();   /// ????
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        public ByteArrayOutputStream getResult() {
            return os;
        }
    }
    
    public ByteArrayOutputStream call() {
        ByteArrayOutputStream result = null;
        
        String [] args = commandLine();
        
        try {
            Process proc = Runtime.getRuntime().exec(args);
            
            InputStream pstdout = proc.getInputStream();
            InputStream pstderr = proc.getErrorStream();
            ErrReader errReader = new ErrReader(pstderr);
            ResultReader resReader = new ResultReader(pstdout);
            // Install the output and error listeners
            Thread err = new Thread(errReader);
            Thread out = new Thread(resReader);
            out.start();
            err.start();
            
            // give the dot Code to the process
            //Writer dot = new OutputStreamWriter(new BufferedOutputStream(dotProcess.getOutputStream()));
            //dot.write(dotCode);
            //dot.flush();
            //dot.close();
            writeInputToProcess(proc.getOutputStream());
            
            // Wait for the process to complete...
            proc.waitFor();
            
            // The process finished, it may be with an error or with a success...
            int resCode = proc.exitValue();
            if (resCode == 0) {
                result = resReader.getResult();
            } else {
                displayErrorOutput(resCode, errReader.getErrorMessage());
                //System.out.println(cmd + "\\");
//                System.out.println("   -Tgif\\");
//                System.out.println("   " + fontpath);
//                System.out.println("returned " + String.valueOf(resCode));
//                System.out.println("--> " + errReader.getErrorMessage());
            }
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        catch(java.lang.InterruptedException e){
            e.printStackTrace();
        }
        return result;
    }
    

}
