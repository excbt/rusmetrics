package ru.excbt.datafuse.nmk.data.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;


@Entity
@Table(name="subscr_user")
@EntityListeners({AuditingEntityListener.class})
public class SubscrUser extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7393532811974219624L;

	/**
	 * 
	 */

	
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

	
	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="subscr_org_user",
    joinColumns=@JoinColumn(name="subscr_user_id"),
    inverseJoinColumns=@JoinColumn(name="subscr_org_id"))
	private Collection<SubscrOrg> subscrOrgs;	
	
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


	public Collection<SubscrOrg> getSubscrOrgs() {
		return subscrOrgs;
	}

	public void setSubscrOrgs(Collection<SubscrOrg> subscrOrgs) {
		this.subscrOrgs = subscrOrgs;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}		
	
	
}
