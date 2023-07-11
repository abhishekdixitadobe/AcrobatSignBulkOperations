# AcrobatSignBulkOperations
## Acrobat Sign Bulk Operation 
The Acrobat Sign Bulk Operations Tool is a comprehensive application designed to facilitate the efficient handling of bulk operations for Account/Group admins. With this tool, users can effortlessly perform tasks such as deleting agreements, downloading agreements and form fields, and hiding all agreements. The tool is specifically tailored for Account/Group admins, offering streamlined processes for managing agreements at scale.

## Key Features:
<ul>
  1. Bulk Operations:
<li>
  Delete: Seamlessly remove documents associated with agreements in bulk.
</li>
  <li>
    Download: Easily retrieve agreements and their associated form fields in bulk.
  </li>
  <li>
    Hide: Quickly conceal all agreements for enhanced organization and management.
  </li>
  2. Advanced Fetch Functionality:
  <li>
    Date Range Filtering: Fetch agreements based on specific date ranges, allowing for targeted retrieval of desired records.
  </li>
  <li>
    Agreement Status Filtering: Filter agreements based on their status, enabling quick access to agreements in specific states (e.g., completed, pending).
  </li>
  <li>
    Role-Based Fetching: Fetch agreements based on assigned roles, simplifying the process of retrieving agreements associated with specific users or groups.
  </li>
  3. Workflow Agreement Fetch:
  <li>
    Streamlined Retrieval: Effortlessly fetch agreements associated with workflows directly from the application, ensuring easy access to relevant records.
  </li>
</ul>


### Delete Operation
The delete operation is available to delete the documents associated with agreements. To enable the delete operation, please raise the support ticket and sign the retention policy with enable "agreement_retention" flag.
<br>

![image](https://github.com/abhishekdixitadobe/AcrobatSignBulkOperations/assets/93244386/b0cf89cd-0b3f-43c5-ab65-51f81badf6c3)



# Customer problem to be solved

Following bulk operations are available with the application:
  <ul>
  <li>
    Delete the documents associated with agreements.
  </li>
    <li>
     Download all the agreements. 
  </li>
  <li> Download Form fields.
  </li>
   <li> Hide agreements.
  </li>
   <li> Cancel reminders.
  </li>
    <li> Bulk agreement cancellation.</li>
  <li> Get agreements associated with Workflows.</li>
  </ul>

## Current vs Proposed solution

### Current process
 There is no OOTB solution available for bulk operations and this application is a platform for all the bulk operations.

### Proposed solution

<ul>
  <li>
    This application provides users to perform different bulk operations based on role. This will be a single platform to perform different bulk operations.
  </li>
  </ul>

## Technology stack
  <ul>
     <li>Java 1.8 </li>
     <li>Spring Boot 2.6.7 </li>
  </ul>

# Instructions to run the application
 <ul>
     <li>Please ensure that JDK 1.8 or newer version of Java is installed on the machine.</li>
     <li>Download the users list to run the tool for All users in the account. From Acrobat Sign Account -> Users -> Export all users. Remove users except for active users and remove columns except for email addresses.</li>
     <li>Create integration key - https://helpx.adobe.com/sign/kb/how-to-create-an-integration-key.html </li>
     <li>Update application.yml file with:
       <ul>
        <li>correct integration-key</li>
        <li>Update baseUrl</li>
        <li>Update agreement_status to include/exclude agreements based on status</li>
       </ul>
      </li>
     <li>Run application.bat  OR Run the below Command from the Command prompt::  java -jar -Dspring.config.location=<path-to-application.yml file> target/acrobatsignbulkoperationtool-0.0.1-SNAPSHOT.jar</li>
        <li> 
     The application will run as http://localhost:8090/
  </li>
  </ul>

# Instructions on how to run the code (For developers)
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
    Please add JAVA_HOME as an environment variable and set the path.
  </li>
  <li>
    Open the run.bat file from the project
  </li>
  <li>
    update the JAVA_HOME and MAVEN_HOME paths based on your local directory. Save and Close
  </li>
  <li> 
    double click on the run.bat file
  </li>
  <li> 
     The application will run as http://localhost:8090/
  </li>
</ul>

## Documentation for API Endpoints
 Swagger documentation is available. please deploy this spring boot application and use the below URL for swagger.
http://localhost:8090/swagger-ui.html#/

# Future automation opportunities
<ul>
   <li>
      OAuth 2.0 setup
   </li>
  </ul>
