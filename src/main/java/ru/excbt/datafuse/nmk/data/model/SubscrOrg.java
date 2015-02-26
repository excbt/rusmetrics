package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="subscr_org")
public class SubscrOrg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "subscr_obj", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscr_obj")
	@Column
	private Long id;	
	
	@Column (name="subscr_org_role")
	private String role;

	@Column (name="subscr_org_info")
	private String info;

	@Column (name="subscr_org_comment")
	private String comment;
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;	
	
	@OneToOne 
	@JoinColumn(name = "organization_id")
    private Organization organization;

	
	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="subscr_org_cont",
    joinColumns=@JoinColumn(name="subscr_org_id"),
    inverseJoinColumns=@JoinColumn(name="cont_object_id"))
	private Collection<ContObject> contObjects;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Collection<ContObject> getContObjects() {
		return contObjects;
	}

	public void setContObjects(Collection<ContObject> contObjects) {
		this.contObjects = contObjects;
	}

	
	
}
