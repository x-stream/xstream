Building with Maven 2

Requires maven 2.0.4

Before building:

mvn install:install-file -Dfile=xstream/lib/xml-writer-0.2.jar -DgroupId=xml-writer -DartifactId=xml-writer -Dversion=0.2 -Dpackaging=jar

To build:

mvn clean install

Before deploying:

copy settings-template.xml to ~/.m2/settings.xml adding your Codehaus DAV username and passwords.

To deploy (optionally adding sources and javadoc jars):
mvn deploy [-DperformRelease=true]


