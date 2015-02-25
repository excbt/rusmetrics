package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name="cont_object")
@SQLDelete(sql="UPDATE cont_object SET deleted = 1 WHERE id = ? and version = ?")
@Where(clause="deleted <> 1")
public class ContObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "cont_object", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cont_object")
	@Column
	private Long id;	
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;
	
	@Column(name="cont_object_name")
	private String name;

	@Column(name="cont_object_full_name")
	private String fullName;
	
	@Column(name="cont_object_full_address")
	private String fullAddress;

	@Column(name="cont_object_number")
	private String number;

	@Column(name="cont_object_owner")
	private String owner;

	@Column(name="owner_contacts")
	private String ownerContacts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public RowAudit getRowAudit() {
		return rowAudit;
	}

	public void setRowAudit(RowAudit rowAudit) {
		this.rowAudit = rowAudit;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerContacts() {
		return ownerContacts;
	}

	public void setOwnerContacts(String ownerContacts) {
		this.ownerContacts = ownerContacts;
	}

	
}
