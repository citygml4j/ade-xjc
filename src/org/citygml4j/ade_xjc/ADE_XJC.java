package org.citygml4j.ade_xjc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
	private static final Logger LOG = Logger.getInstance();

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
	
	@Option(name="-non-strict", usage="allow changes to contents of subfolder 'schema'")
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

		try {
			parser.parseArgument(args);			
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
			System.out.println("ade-xjc version \"0.2.0\"");
			System.out.println("ade-xjc is a JAXB xjc wrapper built for citygml4j version \"0.2.0\"");
			System.exit(0);
		}

		if (adeSchemaFileList.isEmpty()) {
			System.out.println("Provide the ADE XML Schema definition file to be compiled as argument");
			printUsage(parser, System.out);
			System.exit(1);
		}
		
		if (adeSchemaFileList.size() > 1) {
			System.out.println("Only provide one ADE XML Schema definition file at a time");
			printUsage(parser, System.out);
			System.exit(1);
		}
					
		LOG.info("Starting ade-xjc compiler");
		adeSchemaFile = new File(adeSchemaFileList.get(0));
		int status = 0;
		
		try {
			LOG.info("Setting up build environment");
			checkBuildEnvironment();
			
			if (clean) {
				LOG.info("Cleaning output folder");
				Util.rmdir(outputFolder);
			}
				
			createBuildEnvironment();

			if (packageName.startsWith("org.citygml4j")) {
				LOG.error("The package " + packageName + " is not allowed for the ADE classes");
				LOG.error("Choose a package which is not a subpackage of org.citygml4j. Aborting.");
				System.exit(1);
			}
			
			LOG.info("Using ADE schema " + adeSchemaFile.getCanonicalFile());
			if (adeBindingFile != null)
				LOG.info("Using JAXB binding " + adeBindingFile.getCanonicalFile());
			
			LOG.info("Using Java package " + packageName + " for JAXB classes");
			LOG.info("Generating JAXB classes. This may take some time...");
			
			SchemaCompiler sc = XJC.createSchemaCompiler();
			sc.setDefaultPackageName(packageName);

			XJCErrorListener listener = new XJCErrorListener();
			sc.setErrorListener(listener);

			InputSource schema = new InputSource(baseProfileFile.getAbsolutePath());
			schema.setSystemId(baseProfileFile.toURI().toString());
			sc.parseSchema(schema);

			InputSource binding = new InputSource(bindingFile.getAbsolutePath());
			binding.setSystemId(bindingFile.toURI().toString());
			sc.parseSchema(binding);

			InputSource adeSchema = new InputSource(adeSchemaFile.getAbsolutePath());
			adeSchema.setSystemId(adeSchemaFile.toURI().toString());
			sc.parseSchema(adeSchema);

			if (adeBindingFile != null) {
				InputSource adeBinding = new InputSource(adeBindingFile.getAbsolutePath());
				adeBinding.setSystemId(adeBindingFile.toURI().toString());
				sc.parseSchema(adeBinding);
			}
			
			S2JJAXBModel model = sc.bind();
			JCodeModel code = model.generateCode(null, listener);

			code.build(outputFolder, (PrintStream)null);

			File packageDir = new File(outputFolder.getAbsolutePath());
			LOG.info("JAXB classes successfully written to " + packageDir.getCanonicalPath());	
		} catch (Exception e) {
			if (e.getMessage() != null)
				LOG.error(e.getMessage());
			
			LOG.error("Unable to recover from previous error(s). Aborting.");
			status = 1;
			
			if (clean)
				Util.rmdir(outputFolder);
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
			LOG.info("Running in strict mode. Checking sanity of subfolder 'schemas'");
			
			BigInteger md5 = new BigInteger("0");
			File[] children = schema_dir.listFiles();
			for (File file : children)
				if (file.isDirectory())
					md5 = Util.dir2md5(file, md5);
				else 
					md5 = Util.file2md5(file.getAbsolutePath(), md5);
			
			if (!md5.toString(16).equals("b51bc6a36f36e2bac8c8674f1cb69042"))
				throw new Exception("Contents of subfolder 'schemas' have been altered. Please restore its original state.");
		}
		
		if (!adeSchemaFile.exists() || !adeSchemaFile.isFile() || !adeSchemaFile.canRead())
			throw new Exception("Could not open ADE schema file " + adeSchemaFile.getAbsolutePath());
		
		if (adeBindingFile != null && (!adeBindingFile.exists() || !adeBindingFile.isFile() || !adeBindingFile.canRead()))
			throw new Exception("Could not open ADE binding file " + adeBindingFile.getAbsolutePath());
				
	}

	private void cleanBuildEnvironment() {
		Util.rmdir(new File(outputFolder.getAbsolutePath() + "/org/citygml4j"));

		if (!packageName.startsWith("org"))
			Util.rmdir(new File(outputFolder.getAbsolutePath() + "/org"));
	}

	private void printUsage(CmdLineParser parser, PrintStream out) {
		out.println("Usage: java -jar ade-xjc.jar [-options] ade.xsd");
		out.println();
		out.println("where options include:");
		parser.printUsage(System.out);
		out.println();
	}
}
