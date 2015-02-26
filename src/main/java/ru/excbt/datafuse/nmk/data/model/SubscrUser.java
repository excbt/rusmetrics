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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="subscr_user")
public class SubscrUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "subscr_user", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscr_user")
	@Column
	private Long id;		
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "password")
	private String password;
	
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;

	
	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="subscr_org_user",
    joinColumns=@JoinColumn(name="subscr_user_id"),
    inverseJoinColumns=@JoinColumn(name="subscr_org_id"))
	private Collection<SubscrOrg> subscrOrgs;	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Collection<SubscrOrg> getSubscrOrgs() {
		return subscrOrgs;
	}

	public void setSubscrOrgs(Collection<SubscrOrg> subscrOrgs) {
		this.subscrOrgs = subscrOrgs;
	}		
	
	
}
