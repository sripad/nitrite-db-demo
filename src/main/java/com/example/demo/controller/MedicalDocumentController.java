package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.MedicalDocument;
import com.example.demo.repo.MedicalDocumentRepository;

@RequestMapping("/api")
@RestController
public class MedicalDocumentController {

	@Autowired
	private MedicalDocumentRepository repository;
	
	@Autowired
	public MedicalDocumentController() {
		super();
	}

	@PostMapping("/medicalDocuments")
	public MedicalDocument uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id)
			throws IOException {
		MedicalDocument medicalDocument = MedicalDocument.newMedicalDocument(file, id);
		repository.insert(medicalDocument);
		return medicalDocument;
	}

	@GetMapping("/medicalDocuments")
	public List<MedicalDocument> getAll() {
		return repository.findAll();
	}

	@GetMapping("/medicalDocuments/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest request) throws Exception {
		MedicalDocument medicalDocument = repository.find(id);
		if (medicalDocument == null) {
			throw new Exception("Document not found");
		}
		MediaType contentType = MediaType.parseMediaType(medicalDocument.getFileType());
	    byte[] byteResource = medicalDocument.getContent();
	    ByteArrayResource resource = new ByteArrayResource(byteResource);
		return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + medicalDocument.getFileName())
	            .contentLength(byteResource.length)
	            .contentType(contentType)
	            .body(resource);
	}
}
