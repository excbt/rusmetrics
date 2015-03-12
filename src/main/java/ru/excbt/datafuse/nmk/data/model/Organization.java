package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.IdEntity;
import ru.excbt.datafuse.nmk.data.domain.RowAudit;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="organization")
public class Organization extends IdEntity implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;


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
