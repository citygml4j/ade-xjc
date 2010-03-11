package org.citygml4j.ade_xjc;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	
	public static BigInteger dir2md5(File file, BigInteger md5) throws NoSuchAlgorithmException, FileNotFoundException {
		md5 = string2md5(file.getName(), md5);
		
		if (file.isDirectory()) {
			File[] children = file.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory())
						return true;
					
					if (pathname.getName().endsWith("xsd") || pathname.getName().endsWith("xjb"))
						return true;
					
					return false;
				}
			});
			
			for (File next : children)
				md5 = dir2md5(next, md5);
			
		} else
			md5 = file2md5(file.getAbsolutePath(), md5);
		
		return md5;
	}

	public static BigInteger file2md5(String fileName, BigInteger md5) throws NoSuchAlgorithmException, FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(new File(fileName));				
		byte[] buffer = new byte[8192];
		int read = 0;

		try {
			while( (read = is.read(buffer)) > 0)
				digest.update(buffer, 0, read);

			byte[] md5sum = digest.digest();
			
			return md5.xor(new BigInteger(1, md5sum));
		} catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}

	private static BigInteger string2md5(String input, BigInteger md5) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");	
		byte[] md5sum = digest.digest(input.getBytes());
		
		return md5.xor(new BigInteger(1, md5sum));
	}
	
	public static void rmdir(File folder) {
		if (!folder.exists())
			return;
		
		File[] children = folder.listFiles();
		
		for (File file : children) {
			if (file.isDirectory())
				rmdir(file);
			
			file.delete();
		}
		
		folder.delete();
	}
}
