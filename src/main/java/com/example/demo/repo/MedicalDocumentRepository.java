package com.example.demo.repo;

import java.util.ArrayList;
import java.util.List;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.MedicalDocument;

@Component
public class MedicalDocumentRepository {

	@Autowired
	NitriteDbConnection nitriteDbConnection;

	private Nitrite db;
	private ObjectRepository<MedicalDocument> repository;

	@Autowired
	public MedicalDocumentRepository(NitriteDbConnection nitriteDbConnection) {
		super();
		this.nitriteDbConnection = nitriteDbConnection;
		db = nitriteDbConnection.getConnection();
		repository = db.getRepository(MedicalDocument.class);
	}

	/**
	 * Find all MedicalDocument
	 * 
	 * @return List<MedicalDocument>
	 */
	public List<MedicalDocument> findAll() {
		List<MedicalDocument> medicalRecords = new ArrayList<MedicalDocument>();
		try {
			medicalRecords = repository.find().toList();
		} catch (Exception e) {
			System.out.println("Unable to find records");
			e.printStackTrace();
		}
		return medicalRecords;
	}

	/**
	 * Find MedicalDocument from nitrite db
	 * 
	 * @param offset
	 * @param size
	 * @return
	 */
	public MedicalDocument find(long medicalDocumentId) {
//		Cursor<MedicalDocument> cursor = repository.find(where("id").eq(medicalDocumentId));
		List<MedicalDocument> medicalRecords = findAll();
		for (MedicalDocument medicalDocument : medicalRecords) {
			if (medicalDocument.getMedicalDocumentId() == medicalDocumentId) {
				return medicalDocument;
			}
		}
		return null;
		// return
		// repository.getById(org.dizitart.no2.NitriteId.createId(medicalDocumentId));
	}

	/**
	 * Insert MedicalDocument into nitrite db
	 * 
	 * @param MedicalDocument
	 * @return
	 */
	public MedicalDocument insert(MedicalDocument medicalRecord) {
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
