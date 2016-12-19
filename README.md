#jdbp

**_Currently Tested_**
1. mysql

### Build
There is no build required to use jdbp. Simply edit the jdbp.properties file based on the driver your project requires. Jdbp has 5 propertysets.

1. **_Driver Name [Required]_** 
..*Example: requestedDriverName=mysql
2. **_Url [Required]_**
..*Example: url=jdbc:mysql://localhost:3306/
3. **_Url Params_**
..*Example: urlParams=useSsl=true,anotherKey=anotherVal,otherKey=otherVal
4. **_Username_**
..*Example: username=root
5. **_Password_**
..*Example: password=poorlyObfuscatedPassword

### Getting Started
In your project, the only line required to start the dynamic lookup is:
```java
  Jdbp.initialize();
```

To get a connection from the connection pool:
```java
  String schemaName = "some_db_name";
  javax.sql.Connection connection = Jdbp.getConnection(schemaName);
```

...and to release the same Connection implemention:
```java
  Jdbp.releaseConnection(connection, schemaName);
```
More features will follow. Stay posted!
