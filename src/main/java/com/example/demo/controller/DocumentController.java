package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.repo.NitriteDocumentCollectionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RequestMapping("/api")
@RestController
public class DocumentController {

	@Autowired
	private NitriteDocumentCollectionRepository repository;

	@Autowired
	public DocumentController() {
		super();
	}

	private String[] getResource(HttpServletRequest request) {
		String modelPath = request.getRequestURI().split("/api/documents/")[1];
		String model = new File(modelPath).getName();
		if (modelPath.lastIndexOf("/") == -1) {
			return new String[] { "", model };
		}
		String resoucePath = modelPath.substring(0, modelPath.lastIndexOf("/"));
		return new String[] { resoucePath, model };
	}

	@GetMapping("/documents/**")
	ArrayNode all(@RequestParam Map<String, String> parameters, HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.findAll(resource[0], resource[1], parameters);
	}

	@GetMapping("/documents/download/**")
	ResponseEntity<Resource> download(@RequestParam("id") String id, HttpServletRequest request) throws IOException {
		String[] resourceType = getResource(request);
		Optional<JsonNode> jsonNode = repository.findById(resourceType[0], resourceType[1], id);
		if (jsonNode.isPresent()) {
			JsonNode json = jsonNode.get();
			MediaType contentType = MediaType.parseMediaType(json.get("contentType").asText());
			String fileName = json.get("fileName").asText();
			byte[] byteResource = json.get("content").binaryValue();
			ByteArrayResource resource = new ByteArrayResource(byteResource);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
					.contentLength(byteResource.length).contentType(contentType).body(resource);
		} else {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/documents/**")
	JsonNode insert(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
		String[] resource = getResource(request);
		return repository.insert(resource[0], resource[1], "{}", file);
	}

	@PutMapping("/documents/**")
	JsonNode update(@RequestParam Map<String, String> parameters, @RequestBody JsonNode modifiedRecord,
			HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.update(resource[0], resource[1], parameters, modifiedRecord);
	}

	@DeleteMapping("/documents/**")
	boolean delete(@RequestParam Map<String, String> parameters, @RequestBody JsonNode record,
			HttpServletRequest request) {
		String[] resource = getResource(request);
		return repository.delete(resource[0], resource[1], parameters, record);
	}
}
