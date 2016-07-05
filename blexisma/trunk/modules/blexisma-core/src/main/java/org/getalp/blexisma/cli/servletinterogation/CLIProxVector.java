package org.getalp.blexisma.cli.servletinterogation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.getalp.blexisma.utils.OpenFile;

public class CLIProxVector {
	public static void main(String[] args) {
		if (args.length!=4) {
			System.err.println("Usage: java ... " + CLIProxVector.class.getName() + " language applet_url vector_file nb_prox");
			System.exit(-1);
		}
		
		String analyse = OpenFile.readFullTextFile(new File(args[2]));
		String lg = args[0];
		String request = null;
		int nb = Integer.parseInt(args[3]);
		
		
		request = ServletFormatManager.proxVectFormat(analyse, lg, nb);
		
		try {
			// Request
			URL url = new URL(args[1]);
			System.out.println("création connection");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			System.out.println("création envoi");
	        OutputStream writer = conn.getOutputStream();
	        System.out.println("envoi packet");
	        writer.write(request.getBytes());
	        writer.flush();
	        // Answer
            StringBuffer answer = new StringBuffer();
            System.out.println("création reception");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println("réception ligne");
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
