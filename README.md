#jdbp
**Java Database Prototyper**
Utility for rapid database layer prototyping built for any Java application. Implements connection pooling with HikariCP project and targets JDBC Type 4 drivers. Goal of the project was to reduce the amount of code required to integrate JDBC with existing data stores

**Currently Tested JDBC Type 4 Drivers**
1. mysql

### Build
There is no build required to use jdbp. Simply edit the jdbp.properties file based on the driver your project requires. Jdbp has 5 propertysets.

1. **_Driver Name [Required]_** 
  * Example: requestedDriverName=mysql
2. **_Url [Required]_**
  * Example: url=jdbc:mysql://localhost:3306/
3. **_Url Params_**
  * Example: urlParams=useSsl=true,anotherKey=anotherVal,otherKey=otherVal
4. **_Username_**
  * Example: username=root
5. **_Password_**
  * Example: password=poorlyObfuscatedPassword

### Getting Started
In your project, the only line required to start the dynamic driver location and ResourceBundle parsing is:
```java
  Jdbp.initialize();
```

To get a reference to a JdbpSchema
```java
  String schemaName = "some_db_name";
  JdbpSchema schema = SchemaManager.getSchema(schemaName);
```

### Basic SQL Operations
With a reference to the JdbpSchema object you can invoke
```java
  List<DBInfo> resultSetTransposedToContainerClass = schema.executeRawQueryStatement(schemaName, "SELECT * FROM SomeTable WHERE SomeKey = 'SomeVal', SomeDBInfo.class);
```
If your application defines a Data Access Object that extends from ```java jdbp.db.model.DBInfo``` then the List of DBInfo will contain a mirror of each row returned in the initial resultSet from the raw query statement passed as a parameter.
More features will follow. Stay posted!
