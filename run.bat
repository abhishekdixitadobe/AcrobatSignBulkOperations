set JAVA_HOME = C:\Program Files\Java\jdk1.8.0_321
echo %JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

SET MAVEN_HOME= C:\Users\abhishekd\Downloads\apache-maven-3.8.5-bin\apache-maven-3.8.5
echo %MAVEN_HOME%
set PATH=%MAVEN_HOME%\bin;%PATH%

%MAVEN_HOME%\bin\mvn clean package spring-boot:repackage