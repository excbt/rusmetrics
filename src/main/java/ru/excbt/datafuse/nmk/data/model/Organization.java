package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;


@Entity
@Table(name="organization")
@EntityListeners({AuditingEntityListener.class})
public class Organization extends AbstractAuditableEntity<SystemUser, Long>{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2192600082628553203L;

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
}
