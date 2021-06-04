package com.example.demo.repo;

import org.dizitart.no2.Nitrite;

public class NitriteDbConnection {

	private static Nitrite db;

	public static Nitrite getConnection() {
		if (db == null) {
			db = getDefaultConnection("./test.db", "user", "password");
		}
		return db;
	}
	
	/**
	 * create a default Nitrite storage module
	 * 
	 * @param dbFilePath
	 * @param user
	 * @param password
	 * @return
	 */
	public static Nitrite getDefaultConnection(String dbFilePath, String user, String password) {
		return Nitrite.builder()
					.compressed()
					.filePath(dbFilePath)
					.openOrCreate(user, password);
		
	}
	
	/**
	 * create a mvstore backed storage module
	 * 
	 * @param dbFilePath
	 * @param user
	 * @param password
	 * @return
	 */
//	public static Nitrite getMvStoreConnection(String dbFilePath, String user, String password) {
//		MVStoreModule storeModule = MVStoreModule.withConfig()
//		    .filePath(dbFilePath)  // for android - .dbFilePath(getFilesDir().getPath() + "/test.db")
//		    .compress(true)
//		    .build();
//	
//	
//		// initialization using builder
//		return Nitrite.builder()
//		        .loadModule(storeModule)
//		        // .loadModule(new JacksonMapperModule())  // optional
//		        .openOrCreate(user, password);
//	}
	
	
	/**
	 * create a rocksdb based storage module
	 * 
	 * @param dbFilePath
	 * @param user
	 * @param password
	 * @return
	 */
//	public static Nitrite getRocksDBConnection(String dbFilePath, String user, String password) {
//		RocksDBModule storeModule = RocksDBModule.withConfig()
//		    .filePath(dbFilePath)
//		    .build();
//	
//		return Nitrite.builder()
//		        .loadModule(storeModule)
//		        // .loadModule(new JacksonMapperModule())  // optional
//		        .openOrCreate(user, password);
//	}
}
