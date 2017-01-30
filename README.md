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

### Defining your Data Access Objects
For each table in each Schema you plan to use in the database layer tier of your application, it is required that you define a Java object that extends ```jdbp.db.model.DBInfo```. This hierarchy achieves 2 important goals:
1. The ResultSet returned from standard JDBC operations can be converted into a useful Java object using the class definition of the DBInfo instance to determine field names and types.
2. With the addition of the ```java @SQLTable``` annotation (defined below) the conversion of a Java object to SQL Syntax is performed 'under the covers'
  
    @SQLTable(hasPrimaryKey = boolean, primaryKeyColumn = "primarykeyColumnName", isQueryable = boolean, isInsertable = true)
    
### Basic SQL Operations
With a reference to the ```JdbpSchema``` object you can invoke

Raw Query
```java
  List<DBInfo> resultSetTransposedToContainerClass = schema.executeQuery("SELECT * FROM SomeTable WHERE SomeKey = 'SomeVal'", SomeDBInfo.class);
```
Prepared Query (SELECT)
```java
  List<DBInfo> resultSetTransposedToContainerClass = schema.executeSelect("SomeTable", "id=12", SomeDBInfo.class);
```
Prepared Update (INSERT)
```java
  boolean isSuccess = schema.executeInsert("SomeTable", List<DBInfo> infoObjectsToInsert);
```
Callable Statement

***```JdbpSchema.executeStoredProcedure()``` is not fully operational, and should not yet be utilized***

As noted, ```Jdbp``` requires all Data Access Objects to extend from ```jdbp.db.model.DBInfo```. This guarantee allows a statement executed upon a ```JdbpSchema``` object to return a list of objects containing instances of ```jdbp.db.model.DBInfo``` the abstract Supertype.

Similary, for any update operation that requires some sequence of values, the guarantee of the DAO descending from the abstract supertype, allows Jdbp to work behind the scenes on converted all camelcase field names to their equivalency in SQL Syntax (ex: 'fieldName' to 'field_name') and retrieve the values contained in each object instance using reflection the conditions defined by the ```@SQLTable``` annotation.

More features will follow. Stay posted!
