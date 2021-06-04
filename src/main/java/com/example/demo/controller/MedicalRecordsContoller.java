/**
 * 
 */
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.MedicalRecord;
import com.example.demo.repo.MedicalRecordRepository;

/**
 * @author Sripad
 *
 */
@RequestMapping("/api")
@RestController
public class MedicalRecordsContoller {

	@Autowired
	private MedicalRecordRepository repository;
	
	@Autowired
	public MedicalRecordsContoller() {
		super();
	}

	@GetMapping("/medicalRecords")
	List<MedicalRecord> all() {
		return repository.findAll();
	}

	@PostMapping("/medicalRecords")
	MedicalRecord newMedicalRecord(@RequestBody MedicalRecord newMedicalRecord) {
		return repository.insert(newMedicalRecord);
	}
}
