/*
 * ade-xjc - XML Schema binding compiler for CityGML ADEs
 * https://github.com/citygml4j/ade-xjc
 * 
 * ade-xjc is part of the citygml4j project
 * 
 * Copyright (C) 2013 - 2015,
 * Claus Nagel <claus.nagel@gmail.com>
 *
 * The ade-xjc program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package org.citygml4j.ade_xjc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

public class ADE_XJC {
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); 

	// some dirs we will need
	private File schema_dir = new File("schemas");

	// some files we will need
	private File baseProfileFile = new File(schema_dir.getAbsoluteFile() + "/CityGML/citygml4j_profile.xsd");
	private File bindingFile = new File(schema_dir.getAbsoluteFile() + "/CityGML/binding.xjb");
	private File adeSchemaFile = null;
	
	// args
	@Argument
	private List<String> adeSchemaFileList = new ArrayList<String>();

	@Option(name="-package", usage="package for the ADE JAXB classes (default: 'ade')", metaVar="<packageName>")
	private String packageName = "ade";

	@Option(name="-binding", usage="optional JAXB binding file for ADE XML Schema", metaVar="<fileName>")
	private File adeBindingFile = null;
	
	@Option(name="-output", usage="output folder (default: 'src-gen')", metaVar="<folderName>")
	private File outputFolder = new File("src-gen");

	@Option(name="-non-strict", usage="allow changes to contents of subfolder 'schemas'")
	private boolean nonStrict = false;
	
	@Option(name="-clean", usage="clean output folder")
	private boolean clean;
	
	@Option(name="-h", aliases={"-help"}, usage="print this help message and exit")
	private boolean help;
	
	@Option(name="-version", usage="print product version and exit")
	private boolean version;

	public static void main(String[] args) {
		new ADE_XJC().doMain(args);
	}

	private void doMain(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(80);

		List<String> tmp = new ArrayList<String>();
		for (String arg : args)
			tmp.addAll(Arrays.asList(arg.split(" +")));
		
		String[] newArgs = new String[tmp.size()];
		for (int i = 0; i < tmp.size(); i++)
			newArgs[i] = tmp.get(i);
		
		try {
			parser.parseArgument(newArgs);			
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printUsage(parser, System.err);
			System.exit(1);
		}

		if (help) {
			printUsage(parser, System.out);
			System.exit(0);
		}

		if (version) {
			System.out.println("ade-xjc version " + this.getClass().getPackage().getImplementationVersion());
			System.out.println(this.getClass().getPackage().getImplementationTitle());
			System.exit(0);
		}

		if (adeSchemaFileList.isEmpty()) {
			System.out.println("Provide the ADE XML Schema definition file to be compiled as argument");
			printUsage(parser, System.out);
			System.exit(1);
		}
		
		if (adeSchemaFileList.size() > 1) {
			System.out.println("Just provide one ADE XML Schema definition file at a time");
			printUsage(parser, System.out);
			System.exit(1);
		}
					
		log(LogLevel.INFO, "Starting ade-xjc compiler");
		adeSchemaFile = new File(adeSchemaFileList.get(0));
		int status = 0;
		
		try {
			log(LogLevel.INFO, "Setting up build environment");
			checkBuildEnvironment();
			
			if (clean) {
				log(LogLevel.INFO, "Cleaning output folder");
				Util.rmdir(outputFolder, true);
			}
				
			createBuildEnvironment();

			if (packageName.startsWith("org.citygml4j")) {
				log(LogLevel.ERROR, "The package " + packageName + " is not allowed for the ADE classes");
				log(LogLevel.ERROR, "Choose a package which is not a subpackage of org.citygml4j. Aborting.");
				System.exit(1);
			}
			
			log(LogLevel.INFO, "Using ADE schema " + adeSchemaFile.getCanonicalFile());
			if (adeBindingFile != null)
				log(LogLevel.INFO, "Using JAXB binding " + adeBindingFile.getCanonicalFile());
			
			log(LogLevel.INFO, "Using Java package " + packageName + " for JAXB classes");
			log(LogLevel.INFO, "Generating JAXB classes. This may take some time...");
			
			SchemaCompiler sc = XJC.createSchemaCompiler();
			sc.setDefaultPackageName(packageName);
			
			XJCErrorListener listener = new XJCErrorListener();
			sc.setErrorListener(listener);

			sc.parseSchema(new InputSource(baseProfileFile.toURI().toString()));
			sc.parseSchema(new InputSource(bindingFile.toURI().toString()));
			sc.parseSchema(new InputSource(adeSchemaFile.toURI().toString()));

			if (adeBindingFile != null)
				sc.parseSchema(new InputSource(adeBindingFile.toURI().toString()));
			
			S2JJAXBModel model = sc.bind();
			JCodeModel code = model.generateCode(null, listener);
			code.build(outputFolder, (PrintStream)null);

			File packageDir = new File(outputFolder.getAbsolutePath());
			log(LogLevel.INFO, "JAXB classes successfully written to " + packageDir.getCanonicalPath());	
		} catch (Exception e) {
			if (e.getMessage() != null)
				log(LogLevel.ERROR, e.getMessage());
			
			log(LogLevel.ERROR, "Unable to recover from previous error(s). Aborting.");
			status = 1;
			
			if (clean)
				Util.rmdir(outputFolder, true);
		} finally {
			cleanBuildEnvironment();
		}

		System.exit(status);
	}

	private void createBuildEnvironment() throws Exception {
		boolean success = true;

		if (success && !outputFolder.exists()) {
			success = outputFolder.mkdirs();
			if (!success)
				throw new Exception("Could not create directory " + outputFolder.getAbsolutePath());
		} else if (!outputFolder.isDirectory()) {
			success = outputFolder.delete();
			if (success)
				success = outputFolder.mkdirs();
			else
				throw new Exception("Could not create directory " + outputFolder.getAbsolutePath());
		}
	}

	private void checkBuildEnvironment() throws Exception, FileNotFoundException, NoSuchAlgorithmException {
		if (!schema_dir.exists())
			throw new FileNotFoundException("Could not open folder " + schema_dir.getAbsolutePath());

		if (!nonStrict) {
			log(LogLevel.INFO, "Running in strict mode. Checking sanity of subfolder 'schemas'");
			
			BigInteger md5 = new BigInteger("0");
			md5 = Util.dir2md5(schema_dir, md5);

			if (!md5.toString(16).equals("7c0d5a2c3b146528e929d321345239c"))
				throw new Exception("Contents of subfolder 'schemas' have been altered. Please restore its original state.");
		}
		
		if (!adeSchemaFile.exists() || !adeSchemaFile.isFile() || !adeSchemaFile.canRead())
			throw new Exception("Could not open ADE schema file " + adeSchemaFile.getAbsolutePath());
		
		if (adeBindingFile != null && (!adeBindingFile.exists() || !adeBindingFile.isFile() || !adeBindingFile.canRead()))
			throw new Exception("Could not open ADE binding file " + adeBindingFile.getAbsolutePath());
	}

	private void cleanBuildEnvironment() {
		Util.rmdir(new File(outputFolder.getAbsolutePath() + "/net/opengis/citygml"), true);
		Util.rmdir(new File(outputFolder.getAbsolutePath() + "/net/opengis/gml"), true);
		Util.rmdir(new File(outputFolder.getAbsolutePath() + "/net"), false);
		Util.rmdir(new File(outputFolder.getAbsolutePath() + "/oasis"), true);

		if (!packageName.startsWith("org"))
			Util.rmdir(new File(outputFolder.getAbsolutePath() + "/org"), true);
	}

	private void printUsage(CmdLineParser parser, PrintStream out) {
		out.println("Usage: java -jar ade-xjc.jar [-options] ade.xsd");
		out.println();
		out.println("where options include:");
		parser.printUsage(System.out);
		out.println();
	}
	
	private void log(LogLevel level, String msg) {
		StringBuilder builder = new StringBuilder();
		builder.append("[")
		.append(df.format(new Date()))
		.append(" ")
		.append(level.toString())
		.append("] ")
		.append(msg);
		
		System.out.println(builder.toString());
	}
	
	private enum LogLevel {
		INFO("info"),
		WARN("warn"),
		ERROR("error");
		
		private String value;
		
		LogLevel(String value) {
			this.value = value;
		}
		
		public String toString() {
			return value;
		}
	}
	
}
