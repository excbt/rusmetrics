package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="organization")
public class Organization {
	
	@Id
	@SequenceGenerator(name = "organization", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization")
	@Column
	private Long id;	
	
	
	@Column(name="organization_name")
	private String name;
	
	@Column(name="organization_full_name")
	private String fullName;
	
	@Column(name="organization_full_address")
	private String fullAddress;
	
	@Column(name="ex_code")
	private String exCode;
	
	@Column(name="ex_system")
	private String exSystem;
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;

	
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public String getExSystem() {
		return exSystem;
	}

	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}

	public RowAudit getRowAudit() {
		return rowAudit;
	}

	public void setRowAudit(RowAudit rowAudit) {
		this.rowAudit = rowAudit;
	}
	
}
