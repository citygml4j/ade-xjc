!app.name! - !app.title!

  This program is free software and is distributed in the hope
  that it will be useful, but WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE. See the LICENSE.txt file for more details.


0. Index
--------

1. License
2. Copyright
3. About
4. System requirements
5. How to use it
6. Developers
7. Contact
8. Websites


1. License
----------

!app.name! is free software under the GNU Lesser General Public License
Version 3.0. See the LICENSE.txt file for more details. For a copy of the GNU
Lesser General Public License see the files COPYING.txt and COPYING.LESSER.txt
or visit http://www.gnu.org/licenses/.


2. Copyright
------------

(C) !copyright.year!,
!copyright.owner! <!copyright.owner.email!>


3. About
--------

!app.name! is a command line tool and wrapper for the XML Schema binding compiler
xjc shipped with JAXB. !app.name! compiles an arbitrary CityGML ADE Schema 
to a set of corresponding JAXB classes to be used with citygml4j. 

!app.name! ensures that the resulting JAXB classes are correctly derived from
and related to the CityGML, GML and xAL JAXB classes shipped with citygml4j,
and thus that they can be directly used with the citygml4j library. The citygml4j
library needs to be on your classpath in order to use the compiled classes
in your application.

!app.name! is part of the citygml4j project.


4. System requirements
----------------------

* Java JRE or JDK >= 1.7 to launch the !app.name! binding compiler
* citygml4j v!app.version! to use the compiled JAXB classes


5. How to use it
----------------

!app.name! is a command line tool. In order to get a list of supported 
arguments, invoke the tool in a shell environment:

  % java -jar !app.jar! -help

The !app.name! compiler requires that your ADE schema points to the official
CityGML and GML schemas provided in the subfolder "schemas". So please 
adapt all <xs:import> statements in your ADE schema accordingly before 
invoking the !app.name! compiler.

A sample ADE schema together with an ANT build.xml file is provided in the
subfolder "sample". Simply run the build.xml with Ant using the default
target to compile the sample ADE to JAXB classes.

   
6. Developers
-------------

!developer.main.name! <!developer.main.email!>


7. Contact
----------

!developer.main.email!


8. Websites
-----------

Official !app.name! website: 
http://www.citygml4j.org/
https://github.com/citygml4j/

Related websites:
http://www.citygml.org/
http://www.citygmlwiki.org/
http://www.opengeospatial.org/standards/citygml