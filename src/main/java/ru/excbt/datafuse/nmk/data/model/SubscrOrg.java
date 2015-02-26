package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="subscr_org")
public class SubscrOrg extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Column (name="subscr_org_role")
	private String role;

	@Column (name="subscr_org_info")
	private String info;

	@Column (name="subscr_org_comment")
	private String comment;
	
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
