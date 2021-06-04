package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
    @Index(value = "name", type = IndexType.Unique),
    @Index(value = "createdDate", type = IndexType.NonUnique)
})
public class MedicalRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	Long id;
	String name;
	Date createdDate;
	
	public MedicalRecord() {}
	
	public MedicalRecord(Long id, String name, Date createdDate) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
