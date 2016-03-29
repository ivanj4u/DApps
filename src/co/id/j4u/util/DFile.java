package co.id.j4u.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DFile {

	public static void doReadFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String s;
		while((s = reader.readLine()) != null) {
			System.out.println("Reading..." + s);
		}
		if (reader != null)
			reader.close();
		if (in != null)
			in.close();
		System.out.println("File Done...");
	}
	
	public static void doWriteFile(File file, String s) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(s);
		writer.flush();
		System.out.println("Writing...");
		if (writer != null)
			writer.close();
		if (out != null)
			out.close();
		System.out.println("File Done...");
	}
	
	public static void doWriteFile(File file, byte[] b) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(b);
		out.flush();
		System.out.println("Writing...");
		if (out != null)
			out.close();
		System.out.println("File Done...");
	}
	
	public static void deleteFolder(File file) throws IOException {
		file.delete();
	}
}
