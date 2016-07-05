package org.getalp.blexisma.cli.servletinterogation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CLIProx {
	public static void main(String[] args) {
		if (!(args.length==2||args.length==3)) {
			System.err.println("Usage: java ... " + CLIProx.class.getName() + " language applet_url (optional regex)");
			System.exit(-1);
		}
		
		InputStreamReader ir = new InputStreamReader(System.in);
		BufferedReader bfrd = new BufferedReader(ir);
		StringBuffer analyse = new StringBuffer();
		String s = null;
		String lg = args[0];
		String request = null;
		
		try {
			s = bfrd.readLine();
			while (s != null) {
				analyse.append(s+"\n");
				s = bfrd.readLine();
			}
			bfrd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (args.length==2) 
			request = ServletFormatManager.proxFormat(analyse.toString(),null, lg);
		else
			request = ServletFormatManager.proxFormat(analyse.toString(),args[2], lg);
		
		try {
			// Request
			URL url = new URL(args[1]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
	        OutputStream writer = conn.getOutputStream();
	        writer.write(request.getBytes());
	        writer.flush();
	        // Answer
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();
            
            // Output 
            System.out.println(answer.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
