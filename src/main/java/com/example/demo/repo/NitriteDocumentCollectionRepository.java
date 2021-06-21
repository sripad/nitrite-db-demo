package com.example.demo.repo;

import static org.dizitart.no2.filters.FluentFilter.where;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.index.IndexOptions;
import org.dizitart.no2.mapper.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.FileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class NitriteDocumentCollectionRepository {

	@Autowired
	NitriteDbConnection nitriteDbConnection;

	@Autowired
	public NitriteDocumentCollectionRepository(NitriteDbConnection nitriteDbConnection) {
		super();
		this.nitriteDbConnection = nitriteDbConnection;
	}

	public ArrayNode findAll(String resourcePath, String model, Map<String, String> parameters) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		Filter filter = getAndFilter(parameters);
		if (filter == null) {
			filter = Filter.ALL;
		}
		NitriteCollection collection = db.getCollection(model);
		List<Document> documents = collection.find(filter).toList();
		ArrayNode arrayNode = getArrayNode(documents);
		return arrayNode;
	}

	public Optional<JsonNode> findById(String resourcePath, String model, String id) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		System.out.println(id);
		Document document = collection.find(where("_id").eq(id)).firstOrNull();
		if (document != null) {
			JsonNode jsonNode = getJsonNode(document);
			return Optional.of(jsonNode);
		}
		return Optional.empty();
	}

	/**
	 * Find all records
	 * 
	 * @param model
	 * @return ArrayNode
	 */
	public ArrayNode findAll(String resourcePath, String model) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		List<Document> documents = collection.find(Filter.ALL).toList();
		ArrayNode arrayNode = getArrayNode(documents);
		return arrayNode;
	}

	/**
	 * Insert Document into nitrite db
	 * 
	 * @param model
	 * @param JsonNode
	 * @return JsonNode
	 */
	public JsonNode insert(String resourcePath, String model, JsonNode jsonNode) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		Document doc = getDocument(jsonNode);
		collection.insert(doc);
		return getJsonNode(doc);
	}

	public void createIndex(String resourcePath, String model, String field, String indexType, boolean async) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		IndexOptions indexOptions = IndexOptions.indexOptions(indexType, async);
		collection.createIndex(field, indexOptions);
	}

	/**
	 * Insert Document into nitrite db
	 * 
	 * @param model
	 * @param JsonNode
	 * @param MultipartFile
	 * @return JsonNode
	 * @throws IOException
	 */
	public JsonNode insert(String resourcePath, String model, String jsonString, MultipartFile file)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		if (jsonString == null) {
			jsonNode = objectMapper.createObjectNode();
		} else {
			jsonNode = objectMapper.readTree(jsonString);
		}
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);

		String fileName = FileService.getOriginalFileName(file);
		byte[] content = FileService.getByteArray(file);
		if (!jsonNode.has("fileName")) {
			((ObjectNode) jsonNode).put("fileName", fileName);
		}
		((ObjectNode) jsonNode).put("contentType", file.getContentType());
		((ObjectNode) jsonNode).put("content", content);
		((ObjectNode) jsonNode).put("fileSize", file.getSize());

		String modelPath = model;
		if (!resourcePath.isBlank() && !"/".equals(resourcePath)) {
			String rootPath = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
			new File(rootPath + "/db/" + resourcePath).mkdirs();
			modelPath = resourcePath + "/" + modelPath;
		}
		Document doc = getDocument(jsonNode);
		collection.insert(doc);
		((ObjectNode) jsonNode).put("uri", "/api/documents/download/" + modelPath + "?id=" + doc.getId());
		collection.update(doc);
		return getJsonNode(doc);
	}

	/**
	 * Update Document into nitrite db
	 * 
	 * @param model
	 * @param parameters
	 * @param modifiedRecord
	 * @return JsonNode
	 */
	public JsonNode update(String resourcePath, String model, Map<String, String> parameters, JsonNode modifiedRecord) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		Document doc = getDocument(modifiedRecord);
		Filter filter = getAndFilter(parameters);
		if (filter == null) {
			collection.insert(doc);
		} else {
			collection.update(filter, doc);
		}
		return getJsonNode(doc);
	}

	/**
	 * Delete Document into nitrite db
	 * 
	 * @param model
	 * @param parameters
	 * @param jsonNode
	 * @return boolean
	 */
	public boolean delete(String resourcePath, String model, Map<String, String> parameters, JsonNode jsonNode) {
		Nitrite db = nitriteDbConnection.getConnection(resourcePath, model);
		NitriteCollection collection = db.getCollection(model);
		Document doc = getDocument(jsonNode);
		Filter filter = getAndFilter(parameters);
		if (filter == null) {
			collection.remove(doc);
		} else {
			collection.remove(filter);
		}
		return true;
	}

	private ArrayNode getArrayNode(List<Document> documents) {
		List<JsonNode> jsonNodes = new ArrayList<JsonNode>();
		ArrayNode arrayNode = new ArrayNode(new JsonNodeFactory(false));
		for (Document document : documents) {
			JsonNode jsonNode = getJsonNode(document);
			arrayNode.add(jsonNode);
		}
		return arrayNode;
	}

//	private JsonNode getJsonNode(Document doc) {
//		Map<String, Object> map = new HashMap<>();
//		for (Pair<String, Object> pair : doc) {
//			map.put(pair.getFirst(), pair.getSecond());
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode jsonNode = mapper.valueToTree(map);
//		return jsonNode;
//	}

	private JsonNode getJsonNode(Document doc) {
		JacksonMapper mapper = new JacksonMapper();
		JsonNode jsonNode = mapper.convert(doc, JsonNode.class);
		return jsonNode;
	}

	private Document getDocument(JsonNode jsonNode) {
		JacksonMapper mapper = new JacksonMapper();
		Document doc = mapper.convert(jsonNode, Document.class);
		return doc;
	}

	private Filter getAndFilter(Map<String, String> parameters) {
		Filter filter = null;
		for (String field : parameters.keySet()) {
			if (parameters.get(field) != null && !parameters.get(field).isBlank()) {
				Filter eqFilter = where(field).eq(parameters.get(field));
				if (filter == null) {
					filter = eqFilter;
				} else {
					filter.and(eqFilter);
				}
			}
		}
		return filter;
	}

}
