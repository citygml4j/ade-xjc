ade-xjc
=======

ade-xjc is a command line tool and wrapper for the XML Schema binding compiler `xjc` shipped with [JAXB](https://jaxb.java.net/). ade-xjc compiles arbitrary CityGML Application Domain Extension (ADE) schemas to a set of corresponding JAXB classes to be used with citygml4j. 

License
-------
ade-xjc is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). See the `LICENSE` file for more details.

Note that releases of the software before version 2.3.0 continue to be licensed under GNU LGPL 3.0 license. To request a previous of ade-xjc under Apache 2.0 create a GitHub issue.

Latest release
--------------
The latest stable release of ade-xjc is 2.3.0.

Download the ade-xjc 2.3.0 release binariers [here](https://github.com/citygml4j/ade-xjc/releases/download/v2.3.0/ade-xjc-2.3.0.zip). Previous releases are available from the [releases section](https://github.com/citygml4j/ade-xjc/releases).

Note that ade-xjc is also bundled with citygml4j.

Contributing
------------
* To file bugs found in the software create a GitHub issue.
* To contribute code for fixing filed issues create a pull request with the issue id.
* To propose a new feature create a GitHub issue and open a discussion.

Building
--------
ade-xjc depends on JRE 7 or higher. The project uses [Apache Ant](http://ant.apache.org/) as build tool. To build the program, run the `build.xml` file from the root of the repository with `dist` as target. 

    % ant dist

This will create a folder `ade-xjc-<version>` containing the `ade-xjc.jar` executable and the following subfolders:
* `lib` -- mandatory dependencies of ade-xjc
* `license` -- license information
* `sample` -- a sample CityGML ADE that you may use for testing
* `schemas` -- the official CityGML, GML and xAL schemas

Compiling a CityGML ADE Schema
------------------------------
ade-xjc is a command line tool. In order to launch it, open a shell environment and navigate to the folder where you built the program. The command below exemplifies how to run ade-xjc to compile the ADE Schema `CityGMLADE.xsd` to JAXB classes.

    % java -jar ade-xjc.jar /home/user/ade/CityGMLADE.xsd

To control the compilation process, ade-xjc offers the following program arguments.

|Argument | Description
|------|----------
|`binding <fileName>` | An optional global JAXB binding file
|`output <folderName>` | Target folder where to create the JAXB classes (default: `src-gen`)
|`package <packageName>` | Name of the Java package to be used for the JAXB classes (default: `ade`)
|`non-strict` | Per default, ade-xjc terminates with an error if the content of the `schemas` subfolder has been changed in order to ensure deterministic compilation results; use this argument to force ade-xjc to continue
|`clean` | Cleans the output folder before compilation
|`version` | Prints version information to the console
|`help`, `h` | Prints a help message to the console

Note that the ade-xjc binding compiler requires that your ADE schema points to the official CityGML and GML schemas provided in the subfolder `schemas`. So please adapt all `<xs:import>` statements in your ADE schema accordingly before launching ade-xjc.

Sample ADE Schema compilation
-----------------------------
ade-xjc provides the sample CityGML ADE Schema `CityGML-SubsurfaceADE-0_9_0.xsd` to illustrate the schema binding process. Once you have built the library from source with [Apache Ant](http://ant.apache.org/) as described above, find the example in the `ade-xjc-<version>/sample` folder.

From within the `ade-xjc-<version>` folder, launch ade-xjc with the following command.

    % java -jar ade-xjc.jar -clean -binding sample/binding.xjb -output sample/src-gen 
    -package ade.sub.jaxb sample/CityGML-SubsurfaceADE-0_9_0.xsd

This will compile the `CityGML-SubsurfaceADE-0_9_0.xsd` schema to JAXB classes that are generated in the folder `sample/src-gen`. If you are running ade-xjc with Java 8, you will have to allow access to external XSD files by adding the following JVM argument to the above command.

    -Djavax.xml.accessExternalSchema=file

For convience, an Ant `build.xml` file is available in the `ade-xjc-<version>/sample` folder. Run this `build.xml` file with Ant using the default target to compile the schema with the same settings as shown above. 

Using the generated JAXB classes
--------------------------------
ade-xjc is designed to be used with citygml4j. During compilation of the ADE Schema, ade-xjc ensures that the resulting JAXB classes perfectly combine with the JAXB classes that are shipped with citygml4j and that map the official CityGML, GML and xAL schemas. As a consequence, the citygml4j library needs to be on your classpath in order to use the generated JAXB classes.

The intended scenario is to use citygml4j to parse or write a CityGML instance document with ADE content. citygml4j provides the class `ADEComponent` that will give you access to the ADE content through an `org.w3c.dom.Element` DOM object. You can either manually parse or create this DOM element or, more conveniently, use the JAXB classes generated with ade-xjc for this purpose. 

citygml4j comes with a sample program that illustrates this scenario. The sample program is available in the subfolder `citygml4j-<version>/samples/handling_ade/unmarshalling_ade_using_jaxb` where you built the citygml4j library.

Note that you need to check which version of citygml4j is supposed by your version of ade-xjc. Use the following command to find out.

    % java -jar ade-xjc.jar -version

The output will look similar to this:

    ade-xjc version 2.3.0+1
    XML Schema binding compiler for CityGML ADEs; use with citygml4j version 2.3.0
