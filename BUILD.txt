Building with Maven 2

Requires maven 2.0.4

Before building:

mvn install:install-file -Dfile=xstream-website/lib/sitemesh-20051115.jar
-DgroupId=opensymphony -DartifactId=sitemesh -Dversion=20051115
-Dpackaging=jar -DgeneratePom=true

To build:

mvn clean install

Before deploying:

copy settings-template.xml to ~/.m2/settings.xml adding your Codehaus DAV username and passwords.

To deploy (optionally adding sources and javadoc jars):
mvn deploy [-DperformRelease=true]


