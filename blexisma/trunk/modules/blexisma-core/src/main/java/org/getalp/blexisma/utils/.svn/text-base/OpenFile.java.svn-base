package org.getalp.blexisma.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Alexandre Labadié
 * Toolbox class used to make file opening easier
 * */
public final class OpenFile 
{

	/**
	 * @param file : file to be read
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextFileLineByLine(File file) {
		return readTextFileLineByLine(file, Charset.defaultCharset().name());
	}

	/**
	 * @param file : file to be read
	 * @param charset : encoding of the file
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextFileLineByLine(File file,String charset)
	{
		BufferedReader bfrd = null;
		
			try {
				bfrd = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		
		
		return bfrd;
	}
	
	/**
	 * @param url : url where the text to be read is
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextUrlLineByLine(URL url) {
		return readTextUrlLineByLine(url, Charset.defaultCharset().name());
	}
	
	/**
	 * @param url : url where the text to be read is
	 * @param charset : encoding of the text
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextUrlLineByLine(URL url, String charset)
	{
		BufferedReader bfrd = null;
		
		
			try {
				bfrd = new BufferedReader(new InputStreamReader(url.openStream(),charset));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
		return bfrd;
	}
	
	/**
	 * @param file : file to be read
	 * @param jump : number of line to jump
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextFileLineByLineJump(File file,long jump) {
		return readTextFileLineByLineJump(file,jump,Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be read
	 * @param charset : encoding of the file
	 * @param jump : number of line to jump
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextFileLineByLineJump(File file,long jump, String charset)
	{
		BufferedReader bfrd = null;
		
		
		try {
			bfrd = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
			
			/* Jumping lines */
			for (long i = 0;i < jump;i++)
			{
				if (bfrd.ready()) bfrd.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
		
		
		return bfrd;
	}
	
	/**
	 * @param url : url to be read
	 * @param jump : number of line to jump
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextUrlLineByLineJump(URL url,long jump) {
		return readTextUrlLineByLineJump(url, jump, Charset.defaultCharset().name());
	}
	
	/**
	 * @param url : url to be read
	 * @param charset : encoding of the file
	 * @param jump : number of line to jump
	 * @return A buffered reader allowing to read the text file line by line
	 * */
	public final static BufferedReader readTextUrlLineByLineJump(URL url,long jump, String charset)
	{
		BufferedReader bfrd = null;
		
		
		try {
			bfrd = new BufferedReader(new InputStreamReader(url.openStream(),charset));
			
			/* Jumping lines */
			for (long i = 0;i < jump;i++)
			{
				if (bfrd.ready()) bfrd.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+url);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
		
		
		return bfrd;
	}
	
	/**
	 * @param indice : starting position in the file
	 * @param file : file to be read in
	 * @param nb : number of bytes to be read
	 * @return An array of n bytes read from file starting from position indice
	 * */
	public final static byte[] readBytes(int indice,File file, int n)
	{
		InputStream in = null;
		byte[] byteArray = new byte[n];
		
		try {
			/* Opening file */
			in = new FileInputStream(file);
			/* Skipping to the wanted position */
			in.skip(indice);
			/* Reading the byte array */
			in.read(byteArray,0,n);
			/* Closing */
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
		
		return byteArray;
	}
	
	/**
	 * @param file : file to be read
	 * @param charset : encoding of the file
	 * @return Number of line in the file
	 * */
	public final static int countLine(File file) {
		return countLine(file, Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be read
	 * @param charset : encoding of the file
	 * @return Number of line in the file
	 * */
	public final static int countLine(File file,String charset)
	{
		int taille = 0;
		BufferedReader bfrd = null;
		
		try {
			bfrd = readTextFileLineByLine(file,charset);
			
			while (bfrd.ready())
			{
				taille++;
				bfrd.readLine();
			}
			bfrd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IO error, corrupted or in use file");
			e1.printStackTrace();
		}
		
		return taille;
	}
	
	/**
	 * @param file : file to be raed in
	 * @param ph : line of the text to be read
	 * @return The wanted line in file, or null if it doesn't exist
	 * */
	public final static String readLine(File file, int ph) {
		return readLine(file,ph,Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be raed in
	 * @param ph : line of the text to be read
	 * @param charset : encoding of the text
	 * @return The wanted line in file, or null if it doesn't exist
	 * */
	public final static String readLine(File file, int ph,String charset)
	{
		int cpt = 0;
		BufferedReader bfrd = null;
		String tmp = null;
		
		try {
			bfrd = readTextFileLineByLine(file,charset);
			
			while (bfrd.ready())
			{
				tmp = bfrd.readLine();
				if (cpt == ph) return tmp;
				cpt++;
			}
			bfrd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IO error, corrupted or in use file");
			e1.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * @param file : file to be read
	 * @return The text in string format
	 * */
	public final static String readFullTextFile(File file) {
		return readFullTextFile(file,Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be read
	 * @param charset : encoding of the file
	 * @return The text in string format
	 * Lit un texte dans son intégralité et le renvoi sous forme de chaîne
	 * */
	public final static String readFullTextFile(File file,String charset)
	{
		BufferedReader bfrd = null;
		StringBuffer tmp = new StringBuffer();
		
		try {
			bfrd = readTextFileLineByLine(file,charset);
			
			while (bfrd.ready())
			{
				tmp.append(bfrd.readLine());
				
			}
			bfrd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+file);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IO error, corrupted or in use file");
			e1.printStackTrace();
		}
		
		return tmp.toString();
	}
	
	/**
	 * @param url : url where the text to be read is
	 * @return The text in String format
	 * */
	public final static String readFullTextUrl(URL url) {
		return readFullTextUrl(url,Charset.defaultCharset().name());
	}
	
	/**
	 * @param url : url where the text to be read is
	 * @param charset : encoding of the text
	 * @return The text in String format
	 * */
	public final static String readFullTextUrl(URL url, String charset)
	{
		BufferedReader bfrd = null;
		StringBuffer tmp =new StringBuffer();
		
		try {
			bfrd = readTextUrlLineByLine(url, charset);
			
			while (bfrd.ready())
			{
				tmp.append(bfrd.readLine());
				
			}
			bfrd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate "+url);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("IO error, corrupted or in use file");
			e1.printStackTrace();
		}
		
		return tmp.toString();
	}
}
