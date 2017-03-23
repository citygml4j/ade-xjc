/*
 * ade-xjc - XML Schema binding compiler for CityGML ADEs
 * https://github.com/citygml4j/ade-xjc
 * 
 * ade-xjc is part of the citygml4j project
 * 
 * Copyright 2013-2017 Claus Nagel <claus.nagel@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
}
