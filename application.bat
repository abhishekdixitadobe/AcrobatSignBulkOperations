set JAVA_HOME = C:\Program Files\Java\jdk1.8.0_321
echo %JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

java -jar -Dspring.config.location="C:\Users\abhishekd\OneDrive - Adobe\Abhishek\Adobe_Oct_21\Adobe Sign\Projects\Test Application\AcrobatSignBulkOperations"\application.yml target/acrobatsignbulkoperationtool-0.0.1-SNAPSHOT.jar