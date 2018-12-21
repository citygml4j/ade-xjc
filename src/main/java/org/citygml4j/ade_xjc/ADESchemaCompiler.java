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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.PrologCodeWriter;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl;
import org.citygml4j.ade_xjc.Util.URLClassLoader;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ADESchemaCompiler {
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	private Path appHome;
	private Path workDir;

	@Argument
	private List<String> adeSchemaFileList = new ArrayList<>();

	@Option(name="-package", usage="package for the ADE JAXB classes", metaVar="<packageName>")
	private String packageName = "ade";

	@Option(name="-binding", usage="optional JAXB binding file for ADE XML Schema", metaVar="<fileName>")
	private Path adeBindingFile;

	@Option(name="-output", usage="output folder", metaVar="<folderName>")
	private Path outputFolder = Paths.get("src-gen");

	@Option(name="-non-strict", usage="allow changes to contents of subfolder 'schemas'")
	private boolean nonStrict = false;

	@Option(name="-clean", usage="clean output folder")
	private boolean clean;

	@Option(name="-help", aliases={"-h"}, usage="print this help message and exit")
	private boolean help;

	@Option(name="-version", aliases={"-v"}, usage="print product version and exit")
	private boolean version;

	@Option(name="-X{arg}", usage="one or more arguments for JAXB plugins")
	private Boolean dummy;

	public static void main(String[] args) {
		new ADESchemaCompiler().doMain(args);
	}

	private void doMain(String[] _args) {
		CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withUsageWidth(80));
		Map<Boolean, List<String>> args = Arrays.stream(_args)
				.flatMap(arg -> Arrays.stream(arg.split("\\s+")))
				.collect(Collectors.partitioningBy(arg -> !arg.startsWith("-X")));

		try {
			parser.parseArgument(args.get(Boolean.TRUE));			
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
			System.out.println(getClass().getPackage().getImplementationTitle() + " version " + getClass().getPackage().getImplementationVersion());
			System.out.println("XML Schema binding compiler for CityGML ADEs; use with citygml4j version " + getClass().getPackage().getImplementationVersion());
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
		int status = 0;
		initialize();

		try {
			log(LogLevel.INFO, "Setting up build environment");
			Path schemaDir = appHome.resolve("schemas");
			Path pluginsDir = appHome.resolve("jaxb-plugins");

			Path adeSchemaFile = Paths.get(adeSchemaFileList.get(0));
			if (!adeSchemaFile.isAbsolute())
				adeSchemaFile = workDir.resolve(adeSchemaFile);

			if (adeBindingFile != null && !adeBindingFile.isAbsolute())
				adeBindingFile = workDir.resolve(adeBindingFile);

			if (!outputFolder.isAbsolute())
				outputFolder = workDir.resolve(outputFolder);

			checkBuildEnvironment(schemaDir, adeSchemaFile);

			if (clean && Files.exists(outputFolder)) {
				log(LogLevel.INFO, "Cleaning output folder");
				Files.walkFileTree(outputFolder, new FileDeleter(true));
			}

			createBuildEnvironment();

			if (packageName.startsWith("org.citygml4j")) {
				log(LogLevel.ERROR, "The package " + packageName + " is not allowed for the ADE classes");
				log(LogLevel.ERROR, "Choose a package which is not a subpackage of org.citygml4j. Aborting.");
				System.exit(1);
			}

			log(LogLevel.INFO, "Using ADE schema " + adeSchemaFile.toAbsolutePath().toString());
			if (adeBindingFile != null)
				log(LogLevel.INFO, "Using JAXB binding " + adeBindingFile.toAbsolutePath());

			log(LogLevel.INFO, "Using Java package " + packageName + " for JAXB classes");

			SchemaCompiler sc = XJC.createSchemaCompiler();
			sc.setDefaultPackageName(packageName);

			if (!args.get(Boolean.FALSE).isEmpty() && Files.exists(pluginsDir)) {
				log(LogLevel.INFO, "Loading and configuring JAXB plugins");
				String[] pluginArgs = args.get(Boolean.FALSE).toArray(new String[0]);
				Options options = ((SchemaCompilerImpl)sc).getOptions();

				URLClassLoader loader = new URLClassLoader();
				try (Stream<Path> stream = Files.walk(pluginsDir).filter(path -> path.getFileName().toString().toLowerCase().endsWith(".jar"))) {
					stream.forEach(loader::addPath);
				}

				ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class, loader);				
				for (Plugin plugin : plugins) {
					for (int i = 0; i < pluginArgs.length; i++) {
						if (('-' + plugin.getOptionName()).equals(pluginArgs[i])) {
							log(LogLevel.INFO, "Found JAXB plugin for option '-" + plugin.getOptionName() + "'");
							options.activePlugins.add(plugin);
							options.pluginURIs.addAll(plugin.getCustomizationURIs());
							options.getAllPlugins().add(plugin);
							plugin.onActivated(options);
						}

						plugin.parseArgument(options, pluginArgs, i);
					}
				}
			}

			XJCErrorListener listener = new XJCErrorListener();
			sc.setErrorListener(listener);

			log(LogLevel.INFO, "Generating JAXB classes. This may take some time...");
			sc.parseSchema(new InputSource(schemaDir.resolve("citygml4j.xsd").toUri().toString()));
			sc.parseSchema(new InputSource(schemaDir.resolve("binding.xjb").toUri().toString()));
			sc.parseSchema(new InputSource(adeSchemaFile.toUri().toString()));

			if (adeBindingFile != null)
				sc.parseSchema(new InputSource(adeBindingFile.toUri().toString()));

			S2JJAXBModel model = sc.bind();
			JCodeModel code = model.generateCode(null, listener);

			// write classes using a header comment
			PrologCodeWriter writer = new PrologCodeWriter(new FileCodeWriter(outputFolder.toFile(), "UTF-8"),
					"Generated with ade-xjc - XML Schema binding compiler for CityGML ADEs, version " + this.getClass().getPackage().getImplementationVersion() + "\n"
							+ "ade-xjc is part of the citygml4j project, see https://github.com/citygml4j\n"
							+ "Any modifications to this file will be lost upon recompilation of the source\n"
							+ "Generated: " + new Date().toString() + "\n");
			code.build(writer);

			log(LogLevel.INFO, "JAXB classes successfully written to " + outputFolder.toAbsolutePath());
		} catch (Exception e) {
			if (e.getMessage() != null)
				log(LogLevel.ERROR, e.getMessage());
			else
				log(LogLevel.ERROR, "An unexpected " + e + " occurred.");

			log(LogLevel.ERROR, "Unable to recover from previous error(s). Aborting.");
			status = 1;
		} finally {
			try {
				cleanBuildEnvironment(status == 1);
			} catch (IOException e) {
				log(LogLevel.ERROR, "Failed to clean build environment.");
				status = 1;
			}
		}

		System.exit(status);
	}

	private void initialize() {
		String appHomeName = System.getenv("APP_HOME");
		String workDirName = System.getenv("WORKING_DIR");

		if (appHomeName == null) {
			log(LogLevel.WARN, "Environment variable APP_HOME not set. Using current working directory.");
			appHomeName = ".";
		}

		if (workDirName == null) {
			log(LogLevel.WARN, "Environment variable WORKING_DIR not set. Using current working directory.");
			workDirName = ".";
		}

		appHome = Paths.get(appHomeName).normalize().toAbsolutePath();
		if (!Files.exists(appHome)) {
			log(LogLevel.ERROR, "Invalid working directory '" + appHomeName + "'.");
			System.exit(1);
		}

		workDir = Paths.get(workDirName).normalize().toAbsolutePath();
		if (!Files.exists(workDir)) {
			log(LogLevel.ERROR, "Invalid working directory '" + workDirName + "'.");
			System.exit(1);
		}
	}

	private void createBuildEnvironment() throws Exception {
		if (!Files.exists(outputFolder))
			Files.createDirectories(outputFolder);
		else if (!Files.isDirectory(outputFolder)) {
			Files.delete(outputFolder);
			Files.createDirectories(outputFolder);
		}
	}

	private void checkBuildEnvironment(Path schemaDir, Path adeSchemaFile) throws Exception {
		if (!Files.exists(schemaDir))
			throw new FileNotFoundException("Failed to open folder " + schemaDir.toAbsolutePath().toString());

		if (!nonStrict) {
			log(LogLevel.INFO, "Running in strict mode. Checking sanity of subfolder 'schemas'");

			BigInteger md5 = new BigInteger("0");
			md5 = Util.dir2md5(schemaDir, md5);

			if (!md5.toString(16).equals("e63ab0fde528fbafc380af37b792e914"))
				throw new Exception("Contents of subfolder 'schemas' have been altered. Please restore its original state.");
		}

		if (!Files.exists(adeSchemaFile) || !Files.isRegularFile(adeSchemaFile) || !Files.isReadable(adeSchemaFile))
			throw new Exception("Could not open ADE schema file " + adeSchemaFile.toAbsolutePath().toString());

		if (adeBindingFile != null && (!Files.exists(adeBindingFile)
				|| !Files.isRegularFile(adeBindingFile)
				|| !Files.isReadable(adeBindingFile)))
			throw new Exception("Could not open ADE binding file " + adeBindingFile.toAbsolutePath());
	}

	private void cleanBuildEnvironment(boolean cleanAll) throws IOException {
		if (!cleanAll) {
			Files.walkFileTree(outputFolder.resolve("net/opengis/citygml"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("net/opengis/gml"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("oasis/names/tc/ciq/xsdschema/xal/_2"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("org/citygml4j"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("org/w3/_1999/xlink"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("org/w3/_2001/smil20"), new FileDeleter(true));
			Files.walkFileTree(outputFolder.resolve("net"), new FileDeleter(true, true));
			Files.walkFileTree(outputFolder.resolve("org"), new FileDeleter(true, true));
			Files.walkFileTree(outputFolder.resolve("oasis"), new FileDeleter(true, true));
		} else
			Files.walkFileTree(outputFolder, new FileDeleter(true));
	}

	private void printUsage(CmdLineParser parser, PrintStream out) {
		out.println("Usage: " + getClass().getPackage().getImplementationTitle() + " [-options] ade.xsd");
		out.println();
		out.println("where options include:");
		parser.printUsage(System.out);
		out.println();
	}

	private void log(LogLevel level, String msg) {
		System.out.println("[" + df.format(new Date()) + " " + level.toString() + "] " + msg);
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
