#Excel REST Service
###Built With
*Java 8     
*SpringBoot v2.5.5        
*Maven      
*Apache POI  v3.15


###Description

This Rest service accepts only Excel files which contain the details of a user.
RestTemplate is used to make POST and GET requests to an external api.

##Getting Started
***
These are the steps to be followed to run the application locally

1.Clone the repository
````
git clone https://github.com/PranayJuturu38/UserApplication/
git clone https://github.com/yogesh2699/DBUser
````
2. Unzip and open the folder in an IDE which supports springboot applications
````
Few IDEs that can be used are 
1.Eclipse 
2.Intellij
3.Visual Studio Code
````

###Further Steps
***
####Uploading File
The following steps will help in running the program and upload a file.

1.Check the port number in the applications.properties file available in resources folder
````
server.port=4564 for File Application
server.port= 8080 for Database Application
````
2.Run both the applications.The applications will be running on server ports 4564 and 8080 respectively.

````
http://localhost:4564
http://localhost:8080
````
3.PostMan is used to test the endpoints.    
4.To upload an Excel file `/upload` endpoint is used.
````
http://localhost:4564/upload

Set the request type to POST and select Body->Form-data
Set the name of key as "file" and fo rvalue select an excel file

````
5.If the Excel file is empty an error message will be shown
````
message=File empty.
````
6.If there is no error the message will be either
````
message= Database updated
````
or
````
message= File contains same values
````
7.If the Database Application is not running the message will be
````
message=Service is down
````
***
####Retrieving password
The following steps will allow you to retrieve the password of a user using userName

1.Once the database it updated with the values,hit the endpoint `/users/{userName}`
````
Example:
http://localhost:4564/users/pr123456778
````
2.If the user is present 
````
Password for pr123456778 is *********
````
3.If user is not present in the database
````
message=User does not exist
````

####Apis 
````
1. /upload
2. /users/{username}
````

######1.Upload Api
The upload api accepts only Excel files.

Firstly, the format of the file is checked when it is uploaded. If the file is not of Excel format, an exception is thrown stating that the "file type is not supported".

Further, the contents of the file are verified if the file is empty an exception is thrown stating "File is empty".
An uniqueID is created for every user. The uniqueID consists of first two characters of firstName and the whole contactNumber.
````
Example 
FirstName : Pranay
ContactNumber: 1234567789
Unique ID : pr1234567789 

````
This modified file is sent to an external api which stores the user details in the database.

######2.Users/{userName} Api
This api takes userName as parameter and returns the password for that user.

The userName is verified if it exists in the database or not, if it is present in the database password will be returned.
else an exception is thrown stating that the user does not exist in the database.