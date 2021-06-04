## nitrite-db-demo

**NO**sql **O**bject (**NO<sub>2</sub>** a.k.a Nitrite) database is an open source nosql embedded
document store written in Java. It has MongoDB like API. It supports both
in-memory and file based persistent store.

Nitrite is an embedded database ideal for desktop, mobile or small web applications.

**Features**:

-   Schemaless document collection and object repository
-   In-memory / file-based store
-   Pluggable storage engines - mvstore, mapdb, rocksdb
-   ACID transaction
-   Schema migration
-   Indexing
-   Full text search
-   Rx-Java support
-   Both way replication via Nitrite DataGate server
-   Very fast, lightweight and fluent API 
-   Android compatibility (API Level 19)

### Evaluate nitrite-db-demo


<table border="1">
  <tbody>
    <tr>
      <th>Feature</th>
      <th align="center">Details</th>
    </tr>
    <tr>
      <td>Embedded</td>
      <td> 
	      <ul>
				<li>List item</li>
				<li>Light weight</li>
				<li>Easily configurable </li>
				<li>Embedded database in pod/mobile app </li>
			</ul>
      </td>
    </tr>
    <tr>
      <td>Data </td>
      <td> 
      <ul>
				<li>JSON</li>
				<li>Text, Doc, PDF, Images, Videos as bytes in JSON </li>
	  </ul>
      </td>
    </tr>
    <tr>
      <td>Security</td>
      <td>
           <ul>
				<li>Does not use Encryption for storage by default
				<br/><a href="https://github.com/nitrite/nitrite-java/issues/209">Database file encryption</a>
				<br/><a href="https://github.com/nitrite/nitrite-java/issues/373">Data accessible on File Database</a>
				Need to explore if Pluggable storage engines (mvstore, mapdb, rocksdb) encrypts data
				</li>
				<li>Support user level access </li>
				<li>Support compression </li>
	   </ul>
      </td>
    </tr>
    <tr>
      <td>Scalable</td>
      <td> 
      	<a href="https://github.com/nitrite/nitrite-jmh/tree/master/nitrite-v3">Benchmark Reference</a>
      </td>
    </tr>
    <tr>
      <td>Replication</td>
      <td> 
Both way replication via Nitrite DataGate server 
      
Automatic Replication


	NitriteCollection collection = db.getCollection("products");
	
	Replica replica = Replica.builder()
	    .of(collection)
	    // replication via websocket (ws/wss)
	    .remote("ws://127.0.0.1:9090/datagate/john/products")
	    // user authentication via JWT token
	    .jwtAuth("john", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
	    .create();
	
	replica.connect();
	
Schema Migration

	Migration migration1 = new Migration(Constants.INITIAL_SCHEMA_VERSION, 2) {
	    @Override
	    public void migrate(Instruction instruction) {
	        instruction.forDatabase()
	            // make a non-secure db to secure db
	            .addPassword("test-user", "test-password");
	
	        // create instruction for existing repository
	        instruction.forRepository(OldClass.class, null)
	
	            // rename the repository (in case of entity name changes)
	            .renameRepository("migrated", null)
	
	            // change datatype of field empId from String to Long and convert the values
	            .changeDataType("empId", (TypeConverter<String, Long>) Long::parseLong)
	
	            // change id field from uuid to empId
	            .changeIdField("uuid", "empId")
	
	            // delete uuid field
	            .deleteField("uuid")
	    
	            // rename field from lastName to familyName
	            .renameField("lastName", "familyName")
	
	            // add new field fullName and add default value as - firstName + " " + lastName
	            .addField("fullName", document -> document.get("firstName", String.class) + " "
	                + document.get("familyName", String.class))
	
	            // drop index on firstName
	            .dropIndex("firstName")
	
	            // drop index on embedded field literature.text
	            .dropIndex("literature.text")
	
	            // change data type of embedded field from float to integer and convert the values 
	            .changeDataType("literature.ratings", (TypeConverter<Float, Integer>) Math::round);
	    }
	};
	
	Migration migration2 = new Migration(2, 3) {
	    @Override
	    public void migrate(Instruction instruction) {
	        instruction.forCollection("test")
	            .addField("fullName", "Dummy Name");
	    }
	};
	
	MVStoreModule storeModule = MVStoreModule.withConfig()
	    .filePath("/temp/employee.db")
	    .compressHigh(true)
	    .build();
	
	db = Nitrite.builder()
	    .loadModule(storeModule)
	    
	    // schema versioning is must for migration
	    .schemaVersion(2)
	
	    // add defined migration paths
	    .addMigrations(migration1, migration2)
	    .openOrCreate();

`      
    	</td>
    </tr>
    <tr>
      <td>Recovery</td>
      <td>	      		
			
Export data to a file

	Exporter exporter = Exporter.of(db);
	exporter.exportTo(schemaFile);

Import data from the file

	Importer importer = Importer.of(db);
	importer.importFrom(schemaFile);
`
 	   </td>
    </tr>
  </tbody>
</table>


### Example REST APIs

Simple model

```
POST http://localhost:8080/api/medicalRecords
{
	"id": 1,
	"name": "Sripad",
	"createdDate": "2008-01-29"
}
```

```
GET http://localhost:8080/api/medicalRecords
[
	{
		"id": 1,
		"name": "Sripad",
		"createdDate": "2008-01-29"
	}
]
```

Documents 

Upload

```
POST http://localhost:8080/api/medicalDocuments

Form Data: 
file: <file>
id: 1
```

Download

```
GET http://localhost:8080/api/medicalDocuments/1
```

All Documents

```
GET GET http://localhost:8080/api/medicalDocuments
```