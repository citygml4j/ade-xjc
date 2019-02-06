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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileDeleter extends SimpleFileVisitor<Path> {
	private boolean recursive;
	private boolean onlyEmptyDirs;

	FileDeleter(boolean recursive) {
		this(recursive, false);
	}
	
	FileDeleter(boolean recursive, boolean onlyEmptyDirs) {
		this.recursive = recursive;
		this.onlyEmptyDirs = onlyEmptyDirs;
	}

	private void deleteFile(Path file) throws IOException {
		Files.deleteIfExists(file);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (!onlyEmptyDirs)
			deleteFile(file);

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
		if (e != null)
			throw e;

		boolean delete = true;
		if (onlyEmptyDirs) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
				delete = !stream.iterator().hasNext();
			}
		}

		if (delete)
			deleteFile(dir);

		return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		return FileVisitResult.CONTINUE;
	}

}
