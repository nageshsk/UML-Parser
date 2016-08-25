# UML-Parser

The following libraries have been used in this project:
* JavaParser - https://github.com/javaparser/javaparser
This parser generates an Abstract Syntax Tree which has the java source code structure, javadoc
and comments. This parser constructs a Compilation Unit and has visitor support and different
visitor methods are provided to traverse the compilation unit for constructors, fields, methods,
variables etc.
Dependency JAR name - javaparser-core-2.3.1-SNAPSHOT.jar

* PlantUML - http://plantuml.com/
PlantUML can be used to generate a UML diagram of a class according to the rules written. It
has a syntax which can be used to describe the structure. It uses GraphViz
(http://plantuml.com/graphvizdot.html) library to generate the UML diagrams.
Depedency JAR Name - plantuml.jar

The project consists of the files:
* ParseJavaToUML: This file has the code to traverse through all the java files on the path
provided and generate a PlantUML syntax string to generate the UML class diagram using
PlantUML. I have used the visitor methods provided in JavaParser library to parse the Java
source files and store the structure of the classes in a data structure. The data structure contains
all the information of the class regarding its fields, methods, variables and associations and
relationships. This information is used to generate the PlantUML syntax string.
* PlantUMLGenerator: This file uses the PlantUML syntax string to generate the UML diagram.
* MainParserController: This class contains the main file of the project. It takes the arguments as
the folder location of the java source files to parse and the image path to store the generated
UML diagram image.

##Reference Links
* https://github.com/javaparser/javaparser
* http://plantuml.com/graphvizdot.html
* http://plantuml.com/classes.html
* http://www.graphviz.org/

##Pre-requisites:
* Java 1.6(and above) JRE and JDK.
* Graphviz dot executable. Before the project is run, the Graphviz library needs to be installed. (http://www.graphviz.org/Download..php)
    By default, the dot executable will be looked for in:
    Windows:
      Firstly, in: c:\Program Files\Graphviz*\bin\dot.exe
      Then in: c:\Program Files (x86)\Graphviz*\bin\dot.exe
    The GraphViz library is provided. It should be unpacked to:
      ‘c:\Program Files\’ or ‘c:\Program Files (x86)\’ (on windows)

##Steps to import the project:

The UMLParserSource.zip is an eclipse IDE project. It contains the source code, javaParser and PlantUML
libraries. Extract the zip file and import the project in eclipse.
In Eclipse, File -> import -> Under ‘General’ -> Existing projects into Workspace.

###Compile and run in eclipse:
In Eclipse, Run -> Run Configurations -> Java Application -> New Launch Configuration ->
* Select the project as ‘UMLParser’,
* Main class as ‘com.UMLParser.PlantUMLGenerator’
* Name for the configuration. Example - UMLParserConf
* Under arguments - Give program arguments as (arguments will be separated by spaces):
    * First argument is the complete path of the folder containing the .java source files to
      parsed. Any folder name on the path should not contain spaces. The path should end
      with ‘\’ (on windows or ‘/’ on Linux) indicating it is a folder.
    * Second argument is the path of the image file along with the image name and extension
      as png to store the generated UML diagram. If file is not present the file is created.
      -> Apply -> Run.

###Create a runnable jar file to run on command line:
In Eclipse, File -> Export -> Java -> Runnable Jar File ->
* Select the launch configuration that is previously created. Example – ‘UMLParserConf –
UMLParser’
* Library Handling -> Extract required libraries into generated Jar
* Browse and give the export destination to save the jar file with filename as ‘umlparser’
-> Finish

##Running the jar file on command line:
The parser is executable on the command line with the following format:
* umlparser.jar <classpath> <output file name>.png
* <classpath> is a folder name where all the .java source files will be
* <output file name> is the name of the output image file the program will generate
Example: umlparser.jar C:\Users\nageshsk\Desktop\UML_Test_cases\uml-parser-test-1\ test1.png
