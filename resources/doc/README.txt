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

The !app.name! is free software under the
GNU Lesser General Public License Version 3.0. See the LICENSE.txt file for more 
details. For a copy of the GNU Lesser General Public License see the files
COPYING.txt and COPYING.LESSER.txt or visit http://www.gnu.org/licenses/.


2. Copyright
------------

(C) !copyright.year!,
!copyright.owner! <!copyright.owner.email!>


3. About
--------

!app.name! is a command line tool and a wrapper for the XML Schema compiler
xjc shipped with JAXB. !app.name! compiles an arbitrary CityGML ADE Schema 
to a set of corresponding JAXB classes. The resulting classes are automatically
derived from the JAXB classes shipped with citygml4j. Thus, the classes can 
easily be used with the citygml4j library. However, this also requires the 
citygml4j library on your classpath in order to use the compiled classes in
your application.


4. System requirements
----------------------

* Java JRE or JDK >= 1.7
* citygml4j library version !app.version!


5. How to use it
----------------

!app.name! is a command line tool. In order to get a list of supported 
arguments, invoke the tool in a shell environment:

  > java -jar !app.jar! -help

The !app.name! compiler requires your ADE schema to point to the official
CityGML and GML schemas provided in the subfolder "schemas". So please 
adapt all <xs:import> statements in your ADE schema accordingly before 
invoking the !app.name! compiler!

Please find a sample ADE schema together with an ANT build file in the
subfolder "sample".

   
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