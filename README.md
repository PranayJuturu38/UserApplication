#Excel REST Service

###Description

This Rest service accepts only Excel files which contain the details of a user.
RestTemplate is used to make POST and GET requests to an external api.

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