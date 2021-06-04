package com.example.demo.repo;

import java.util.ArrayList;
import java.util.List;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.MedicalRecord;

@Component
public class MedicalRecordRepository {

	private Nitrite db;
	private ObjectRepository<MedicalRecord> repository;

	@Autowired
	public MedicalRecordRepository() {
		db = NitriteDbConnection.getConnection();
		repository = db.getRepository(MedicalRecord.class);
	}

	/**
	 * Find all MedicalRecords
	 * 
	 * @return List<MedicalRecord>
	 */
	public List<MedicalRecord> findAll() {
		List<MedicalRecord> medicalRecords = new ArrayList<MedicalRecord>();
		try {
			medicalRecords = repository.find().toList();
		} catch (Exception e) {
			System.out.println("Unable to find records");
			e.printStackTrace();
		}
		return medicalRecords;
	}

	/**
	 * Find MedicalRecord from nitrite db
	 * 
	 * @param offset
	 * @param size
	 * @return
	 */
	public List<MedicalRecord> find(int offset, int size) {
		List<MedicalRecord> medicalRecords = new ArrayList<MedicalRecord>();
		try {
			medicalRecords = repository.find(FindOptions.limit(offset, size)).toList();
		} catch (Exception e) {
			System.out.println("Unable to find transactions. ");
			e.printStackTrace();
		}
		return medicalRecords;
	}

	/**
	 * Insert MedicalRecord into nitrite db
	 * 
	 * @param MedicalRecord
	 * @return
	 */
	public MedicalRecord insert(MedicalRecord medicalRecord) {
		WriteResult wr = null;
		try {
			wr = repository.insert(medicalRecord);
			System.out.println("\n Affected Rows : " + wr.getAffectedCount());
			return medicalRecord;
		} catch (Exception e) {
			System.out.println("Unable to insert transaction. ");
			e.printStackTrace();
			throw e;
		}
	}
}
