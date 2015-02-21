ade-xjc
=======

ade-xjc is a command line tool and wrapper for the XML Schema binding compiler `xjc` shipped with [JAXB](https://jaxb.java.net/). ade-xjc compiles arbitrary CityGML Application Domain Extension (ADE) Schemas to a set of corresponding JAXB classes to be used with citygml4j. 

License
-------
ade-xjc is free software under the [GNU LGPL Version 3.0](http://www.gnu.org/licenses/lgpl.html). See the `LICENSE.txt` file for more details.

Contributing
------------
* To file bugs found in the software create a GitHub issue.
* To contribute code for fixing filed issues create a pull request with the issue id.
* To propose a new feature create a GitHub issue and open a discussion.

Building
--------
ade-xjc depends on JRE 7 or higher. The project uses [Apache Ant](http://ant.apache.org/) as the build tool. To build the program, run the `build.xml` file from the root of the repository with `dist` as target. 

    % ant dist

This will create a folder `ade-xjc-<version>` with the following subfolders:
* `license` -- license information
* `sample` -- a sample CityGML ADE that can be compiled with ade-xjc
* `schemas` -- the official CityGML, GML and xAL schemas

Compiling a CityGML ADE Schema
------------------------------
ade-xjc is a command line tool. In order to launch it, open a shell environment and navigate to the folder where you built the program. The command below runs ade-xjc and compiles the ADE Schema `CityGMLADE.xsd` to JAXB classes.

    % java -jar ade-xjc.jar /home/user/ade/CityGMLADE.xsd

To control the compilation process, ade-xjc offers the following program arguments.

|Argument | Description
|------|----------
|`binding <fileName>` | An optional JAXB binding file to control the schema compilation
|`clean` | Cleans the output folder before compilation
|`help`, `h` | Prints a help message to the console
|`non-strict` | Per default, ade-xjc terminates with an error if the contents of the `schemas` subfolder has been changed in order to ensure deterministic compilation results; use this argument to force ade-xjc to continue
|`output <folderName>` | Target folder where to create the JAXB classes (default: `src-gen`)
|`package <packageName>` | Name of the Java package to be used for the JAXB classes (default: `ade`)
|`version` | Prints version information to the console

Note that the ade-xjc binding compiler requires that your ADE schema points to the official CityGML and GML schemas provided in the subfolder `schemas`. So please adapt all `<xs:import>` statements in your ADE schema accordingly before launching ade-xjc.

Using the compiled JAXB classes
-------------------------------
