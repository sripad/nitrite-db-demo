/**
 * 
 */
package com.example.demo.controller;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dizitart.no2.index.IndexType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repo.NitriteDocumentCollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Sripad
 *
 */
@RequestMapping("/api")
@RestController
public class DataController {

	@Autowired
	private NitriteDocumentCollectionRepository repository;

	@Autowired
	public DataController() {
		super();
	}

	private String[] getResource(HttpServletRequest request) {
		String modelPath = request.getRequestURI().split("/api/data/")[1];
		String model = new File(modelPath).getName();
		if (modelPath.lastIndexOf("/") == -1) {
			return new String[] { "", model };
		}
		String resoucePath = modelPath.substring(0, modelPath.lastIndexOf("/"));
		return new String[] { resoucePath, model };
	}

	// generate data for performance testing
	@GetMapping("/generate")
	BodyBuilder generate() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		for (long i = 0; i < 100000L; i++) {
			ObjectNode node = (ObjectNode) mapper.createObjectNode();
			node.put("id", i);
			node.put("name", "Sripad" + i);
			node.put("last", "Rao");
			repository.insert("nodes", "logs", node);
			repository.createIndex("nodes", "logs", "id", IndexType.Unique, false);
			repository.createIndex("nodes", "logs", "name", IndexType.NonUnique, false);
			System.out.println(i);
		}
		return ResponseEntity.ok();
	}

	@GetMapping("/data/**")
	ArrayNode all(@RequestParam Map<String, String> parameters, HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.findAll(resource[0], resource[1], parameters);
	}

	@PostMapping("/data/**")
	JsonNode insert(@RequestBody JsonNode newRecord, HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.insert(resource[0], resource[1], newRecord);
	}

	@PutMapping("/data/**")
	JsonNode update(@RequestParam Map<String, String> parameters, @RequestBody JsonNode modifiedRecord,
			HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.update(resource[0], resource[1], parameters, modifiedRecord);
	}

	@DeleteMapping("/data/**")
	boolean delete(@RequestParam Map<String, String> parameters, @RequestBody JsonNode record,
			HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.delete(resource[0], resource[1], parameters, record);
	}
}
