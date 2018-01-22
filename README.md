ade-xjc
=======

ade-xjc is a command line tool and wrapper for the XML Schema binding compiler `xjc` shipped with [JAXB](https://jaxb.java.net/). ade-xjc compiles arbitrary CityGML Application Domain Extension (ADE) schemas to a set of corresponding JAXB classes to be used with [citygml4j](https://github.com/citygml4j/citygml4j). 

License
-------
ade-xjc is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). See the `LICENSE` file for more details.

Note that releases of the software before version 2.3.0 continue to be licensed under GNU LGPL 3.0. To request a previous release of ade-xjc under Apache License 2.0 create a GitHub issue.

Latest release
--------------
The latest stable release of ade-xjc is 2.6.0.

Download the ade-xjc 2.6.0 release binariers [here](https://github.com/citygml4j/ade-xjc/releases/download/v2.6.0/ade-xjc-2.6.0.zip). Previous releases are available from the [releases section](https://github.com/citygml4j/ade-xjc/releases).

Contributing
------------
* To file bugs found in the software create a GitHub issue.
* To contribute code for fixing filed issues create a pull request with the issue id.
* To propose a new feature create a GitHub issue and open a discussion.

Building
--------
ade-xjc requires Java 8 or higher. The project uses [Gradle](https://gradle.org/) as build system. To build the program from source, run the following command from the root of the repository. 

    % gradlew installDist

This will create a folder `build/install/ade-xjc` with the following subfolders:
* `bin` -- start scripts for running ade-xjc
* `jaxb-plugins` -- optional JAXB plugins that can be used in the schema compilation
* `lib` -- the ade-xjc JAR file and its mandatory dependencies
* `license` -- license information
* `sample` -- a sample CityGML ADE that you may use for testing
* `schemas` -- the official CityGML, GML and xAL schemas

Compiling a CityGML ADE Schema
------------------------------
ade-xjc is a command line tool. In order to launch it, open a shell environment and navigate to the `bin` folder. This folder contains the two start scripts `ade-xjc.bat` and `ade-xjc` targeted at Microsoft Windows environments and UNIX-like environments (e.g. Linux, macOS). The command below exemplifies how to run ade-xjc to compile the ADE Schema `CityGMLADE.xsd` to JAXB classes.

    % ade-xjc /home/user/ade/CityGMLADE.xsd

To control the compilation process, ade-xjc offers the following program arguments.

|Argument | Description
|------|----------
|`-binding <fileName>` | An optional global JAXB binding file
|`-output <folderName>` | Target folder where to create the JAXB classes (default: `src-gen`)
|`-package <packageName>` | Name of the Java package to be used for the JAXB classes (default: `ade`)
|`-non-strict` | Per default, ade-xjc terminates with an error if the content of the `schemas` subfolder has been changed in order to ensure deterministic compilation results; use this argument to force ade-xjc to continue
|`-clean` | Cleans the output folder before compilation
|`-version, -v` | Prints version information to the console
|`-help`, `-h` | Prints a help message to the console

Note that the ade-xjc binding compiler requires that your ADE schema points to the official CityGML and GML schemas provided in the subfolder `schemas`. So please adapt all `<xs:import>` statements in your ADE schema accordingly before launching ade-xjc.

Using JAXB plugins
------------------
Starting from version 2.4.3, ade-xjc allows you to use JAXB plugins in the ADE Schema compilation. Simply put the JAXB plugin you want to use into the `jaxb-plugins` subfolder and provide the plugin-specific command line arguments when executing ade-xjc. Please refer to the documentation of the JAXB plugin to learn which command line arguments are available and how to use them.

The following JAXB plugins are already included and shipped with ade-xjc:
- [JAXB2 Basics Plugins](https://github.com/highsource/jaxb2-basics)
- [jaxb2-namespace-prefix](https://github.com/Siggen/jaxb2-namespace-prefix)

For example, if you want to augment your generated JAXB classes with setters for collections then you can use the `Setters Plugin` of the JAXB2 Basics Plugins project. This plugin is activated by the `-Xsetters` command line option and is documented [here](https://github.com/highsource/jaxb2-basics/wiki/JAXB2-Setters-Plugin). The following example shows how use this plugin with ade-xjc.

    % ade-xjc -Xsetters -Xsetters-mode=direct /home/user/ade/CityGMLADE.xsd


Sample ADE Schema compilation
-----------------------------
ade-xjc provides the sample CityGML ADE Schema `CityGML-SubsurfaceADE-0_9_0.xsd` to illustrate the schema binding process. Once you have built the library from source with [Gradle](https://gradle.org/) as described above, find the example in the `build/install/ade-xjc/sample` folder.

From within the `sample` folder, launch ade-xjc with the following command.

    % ../bin/ade-xjc -clean -binding binding.xjb -output src-gen -package ade.sub.jaxb CityGML-SubsurfaceADE-0_9_0.xsd

This will compile the `CityGML-SubsurfaceADE-0_9_0.xsd` schema to JAXB classes that are generated in the folder `sample/src-gen`. For convience, two `run-sample` start scripts are provided in the `sample` folder to run ade-xjc with the above settings under Windows or UNIX-like environments. 

Using the generated JAXB classes
--------------------------------
ade-xjc is designed to be used with citygml4j. During compilation of the ADE Schema, ade-xjc ensures that the resulting JAXB classes perfectly combine with the JAXB classes that are shipped with citygml4j and that map the official CityGML, GML and xAL schemas. As a consequence, the citygml4j library needs to be on your classpath in order to use the generated JAXB classes.

The intended scenario is to use citygml4j to parse or write a CityGML instance document with ADE content. citygml4j provides the class `ADEGenericElement` that will give you access to the ADE content through an `org.w3c.dom.Element` DOM object. You can either manually parse or create this DOM element or, more conveniently, use the JAXB classes generated with ade-xjc for this purpose. citygml4j comes with a sample program that illustrates this scenario. The sample program is available in the subfolder `citygml4j-<version>/samples/handling_ade/generic/unmarshalling_ade_using_jaxb` where you built the citygml4j library.

Since [citygml4j 2.5.0](https://github.com/citygml4j/citygml4j/releases/tag/v2.5.0), an alternative approach for processing ADE content using citygml4j is available. It allows for extending the citygml4j object model with user-defined ADE classes that seamlessly integrate with the predefined citygml4j object model. JAXB is used for parsing and writing the ADE classes to ADE-enriched CityGML datasets. The required JAXB classes can be easily generated from the ADE XML Schema using ade-xjc. Sample programs illustrating this ADE module approach are available in the subfolder `citygml4j-<version>/samples/handling_ade/ade_context` of the citygml4j library.

Note that you need to check which version of citygml4j is supposed by your version of ade-xjc. Use the following command to find out.

    % ade-xjc -version

The output will look similar to this:

    ade-xjc version 2.6.0
    XML Schema binding compiler for CityGML ADEs; use with citygml4j version 2.6.0
