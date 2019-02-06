/*
 * ade-xjc - XML Schema binding compiler for CityGML ADEs
 * https://github.com/citygml4j/ade-xjc
 *
 * ade-xjc is part of the citygml4j project
 *
 * Copyright 2013-2019 Claus Nagel <claus.nagel@gmail.com>
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;

public class Util {

	public static BigInteger dir2md5(Path dir, BigInteger md5) throws IOException, NoSuchAlgorithmException {
		// find schema files and make sure that iteration order is deterministic
		Set<Path> files = new TreeSet<>((p1, p2) -> p1.getFileName().toString().compareTo(p2.getFileName().toString()));
		Files.walk(dir)
				.filter(p -> Files.isDirectory(p)
						|| p.getFileName().toString().endsWith(".xsd")
						|| p.getFileName().toString().endsWith(".xjb"))
				.forEach(files::add);

		for (Path file : files) {
			md5 = string2md5(file.getFileName().toString(), md5);
			if (Files.isRegularFile(file))
				md5 = file2md5(file, md5);
		}

		return md5;
	}

	private static BigInteger file2md5(Path file, BigInteger md5) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file)))) {
			String line;
			while ((line = reader.readLine()) != null)
				digest.update(line.getBytes());
		}

		byte[] md5sum = digest.digest();
		return md5.xor(new BigInteger(1, md5sum));
	}

	private static BigInteger string2md5(String input, BigInteger md5) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] md5sum = digest.digest(input.getBytes());
		return md5.xor(new BigInteger(1, md5sum));
	}

	public static class URLClassLoader extends java.net.URLClassLoader {
		URLClassLoader() {
			super(new URL[]{}, ADESchemaCompiler.class.getClassLoader());
		}

		@Override
		protected void addURL(URL url) {
			super.addURL(url);
		}

		void addPath(Path path) {
			try {
				super.addURL(path.toUri().toURL());
			} catch (MalformedURLException e) {
				//
			}
		}
	}

}
