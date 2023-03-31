# AcrobatSignBulkOperations
Acrobat Sign Bulk Operatio

### Use case : This application is developed for handling the bulk operations like delete, download agreements, download form fields, hide all agreements. The bulk operations are for Account/Group admins. The user can also use this tool to perform bulk operations on their account.

### Scenario:


# Customer problem to be solved

Following bulk operation are available with the application:
  <ul>
  <li>
    Delete of the documents associated with agreements.
  </li>
    <li>
     Download all the agreements. 
  </li>
  <li> Download Form fields.
  </li>
   <li> Hide agreements.
  </li>
  </ul>

## Current vs Proposed solution

### Current process
 There is no OOTB solution available for bulk operations and this application is a platform for all the bulk operations.

### Proposed solution

<ul>
  <li>
    This application provide users to perform different bulk operations based on role. This will be a single platform to perform differnt bulk operations.
  </li>
  </ul>

## Technology stack
  <ul>
     <li>Java 1.8 </li>
     <li>Spring Boot 2.6.7 </li>
  </ul>

# Instructions to run the application

### Run below Command from Command prompt.

java -jar -Dspring.config.location=<path-to-application.yml file> target/acrobatsignbulkoperationtool-0.0.1-SNAPSHOT.jar

# Instructions on how to run the code
## Prerequisites
For the building of this project, the client machine should have the following software installed:
<ul>
  <li>
    OS: Windows/Mac/Linux
  </li>
  <li>
    Java JDK: version 1.8
  </li>
  <li>
    Maven: 3.3.3 version or above. Download URL: https://maven.apache.org/download.cgi
  </li>
</ul>
  
## Installation
To install the application to your local repository:
<ul>
  <li>
    Please add JAVA_HOME as an environment variable and set path.
  </li>
  <li>
    Open the run.bat file from the docsigning project
  </li>
  <li>
    update the JAVA_HOME and MAVEN_HOME path based on your local directory. Save and Close
  </li>
  <li> 
    double click on the run.bat file
  </li>
  <li> 
     Application will run as http://localhost:8090/
  </li>
</ul>

## Documentation for API Endpoints
 Swagger documentation is available. please deploy this spring boot application and use below URL for swagger.
http://localhost:8090/swagger-ui.html#/

# Future automation opportunities
<ul>
    <li>
      Bulk Reminder cancellation
  </li>
  <li> 
    Email service
  </li>
  </ul>
