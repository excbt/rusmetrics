package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;


@Entity
@Table(name="organization")
@EntityListeners({AuditingEntityListener.class})
public class Organization extends AbstractAuditableEntity<AuditUser, Long>{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2192600082628553203L;

	@Column(name="organization_name")
	private String organizationName;
	
	@Column(name="organization_full_name")
	private String organizationFullName;
	
	@Column(name="organization_full_address")
	private String organizationFullAddress;
	
	@Column(name="ex_code")
	private String exCode;
	
	@Column(name="ex_system")
	private String exSystem;
	
	@Version
	private int version;



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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationFullName() {
		return organizationFullName;
	}

	public void setOrganizationFullName(String organizationFullName) {
		this.organizationFullName = organizationFullName;
	}

	public String getOrganizationFullAddress() {
		return organizationFullAddress;
	}

	public void setOrganizationFullAddress(String organizationFullAddress) {
		this.organizationFullAddress = organizationFullAddress;
	}
}
