package com.example.demo.repo;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.mapper.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.sync.Replica;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class NitriteDbConnection implements ApplicationContextAware {

	@Value("${nitrite.db.user}")
	private String user;

	@Value("${nitrite.db.password}")
	private String password;

	@Value("${nitrite.db.encryptionKey}")
	private String encryptionKey;

	@Value("${nitrite.db.dbName}")
	private String dbName;

	@Value("${nitrite.db.isSingleDb}")
	private boolean isSingleDb;
	
    private ApplicationContext applicationContext;

	private Map<String, Nitrite> map = new HashMap<String, Nitrite>();

	public Nitrite getConnection(String resourcePath, String model) {
		if (isSingleDb) {
			return getConnection();
		}
		String modelPath = model;
		if (!resourcePath.isBlank() && !"/".equals(resourcePath)) {
			String rootPath = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
			new File(rootPath + "/db/" + resourcePath).mkdirs();
			modelPath = resourcePath + "/" + modelPath;
		}
		if (map.get(modelPath) == null) {
			Nitrite db = getMvStoreConnection("./db/" + modelPath + ".db");
			map.put(modelPath, db);
			return db;
		}
		return map.get(modelPath);
	}

	public Nitrite getConnection() {
		if (map.get(dbName) == null) {
			Nitrite db = getMvStoreConnection("./db/" + dbName + ".db");
			map.put(dbName, db);
			return db;
		}
		return map.get(dbName);
	}

	public void autoReplicaNitriteCollection(NitriteCollection collection, String model) {
		Replica replica = Replica.builder().of(collection)
				// replication via websocket (ws/wss)
				.remote("ws://127.0.0.1:9090/datagate/sripad/" + model)
				// user authentication via JWT token
//				.jwtAuth("john", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
				.create();

		replica.connect();
	}

	public void autoReplicaObjectRepository(ObjectRepository objectRepo, String model) {
		Replica replica = Replica.builder().of(objectRepo)
				// replication via websocket (ws/wss)
				.remote("ws://127.0.0.1:9090/datagate/sripad/" + model)
				// user authentication via JWT token
//		    .jwtAuth("john", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
				.create();

		replica.connect();
	}

	/**
	 * create a mvstore backed storage module
	 * 
	 * @param dbFilePath
	 * @param user
	 * @param password
	 * @return
	 */
	public Nitrite getMvStoreConnection(String dbFilePath) {
		MVStoreModule storeModule = MVStoreModule
				.withConfig()
				.filePath(dbFilePath)
				.compressHigh(true)
				.encryptionKey(encryptionKey.toCharArray())
				.build();

		return Nitrite.builder()
				.loadModule(storeModule)
				.loadModule(new JacksonMapperModule())
				.openOrCreate(user, password);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

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
//		         .loadModule(new JacksonMapperModule())   optional
//		        .openOrCreate(user, password);
//	}

	/*
	 * public static void main(String [ ] args) { DataGateClient dataGateClient =
	 * new DataGateClient("http://localhost:9090") .withAuth("userId", "password");
	 * DataGateSyncTemplate syncTemplate = new DataGateSyncTemplate(dataGateClient,
	 * "remote-collection@userId");
	 * 
	 * // create sync handle SyncHandle syncHandle = Replicator.of(db)
	 * .forLocal(collection) // a DataGate sync template implementation
	 * .withSyncTemplate(syncTemplate) // replication attempt delay of 1 sec
	 * .delay(timeSpan(1, TimeUnit.SECONDS)) // both-way replication
	 * .ofType(ReplicationType.BOTH_WAY) // sync event listener .withListener(new
	 * SyncEventListener() {
	 * 
	 * @Override public void onSyncEvent(SyncEventData eventInfo) {
	 * 
	 * } }) .configure();
	 * 
	 * // start sync in the background using handle syncHandle.startSync();
	 * 
	 * }
	 */
}
