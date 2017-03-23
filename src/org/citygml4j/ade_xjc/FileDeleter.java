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

	public FileDeleter(boolean recursive) {
		this(recursive, false);
	}
	
	public FileDeleter(boolean recursive, boolean onlyEmptyDirs) {
		this.recursive = recursive;
		this.onlyEmptyDirs = onlyEmptyDirs;
	}

	boolean deleteFile(Path file) throws IOException {
		return Files.deleteIfExists(file);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (!onlyEmptyDirs)
			deleteFile(file);

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {		
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
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
