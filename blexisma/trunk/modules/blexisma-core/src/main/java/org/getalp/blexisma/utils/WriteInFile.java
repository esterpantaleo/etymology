package org.getalp.blexisma.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * @author Alexandre Labadié
 * Toolbox to write in File
 * */
public final class WriteInFile 
{
	/**
	 * @param file : the file to be written in
	 * @param data : byte array to be written
	 * Append a byte array at the end of a file
	 * */
	public final static void appendByteArray(File file, byte[] data)
	{
		File d = file.getParentFile();
		FileOutputStream out = null;
		
		/* We create a directory if it doesn't exist */
		if (d != null) d.mkdirs();
		
		try {
			out = new FileOutputStream(file,true);
			
			out.write(data);
			/* closing the stream */
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find "+file);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param file : file to be written in
	 * @param data : String to be written
	 * Write a String formatted text in a file
	 * */
	public final static void writeText(File file,String data) {
		writeText(file,data,Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be written in
	 * @param data : String to be written
	 * @param charset : encoding of the file
	 * Write a String formatted text in a file
	 * */
	public final static void writeText(File file,String data,String charset)
	{
		File d = file.getParentFile();
		
		/* We create a directory if it doesn't exist */
		if (d != null) d.mkdirs();
		
		try {
			BufferedWriter bfwr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),charset));
			bfwr.write(data);
			/* closing the stream */
			bfwr.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find "+file);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param file : file to be written in
	 * @param data : text to be appended
	 * Append text at the end of a file
	 * */
	public final static void appendText(File file,String data) {
		appendText(file,data,Charset.defaultCharset().name());
	}
	
	/**
	 * @param file : file to be written in
	 * @param data : text to be appended
	 * @param charset : encoding of the file
	 * Append text at the end of a file
	 * */
	public final static void appendText(File file,String data, String charset)
	{
		File d = file.getParentFile();
		
		/* We create a directory if it doesn't exist */
		if (d != null) d.mkdirs();
		
		try {
			BufferedWriter bfwr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),charset));
			bfwr.write(data);
			/* closing the stream */
			bfwr.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find "+file);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();System.out.println("IO error, corrupted or in use file");
			e.printStackTrace();
		}
	}
	
	
}

