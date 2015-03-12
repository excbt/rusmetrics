package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

@Entity
@Table(name="system_user")
@EntityListeners({AuditingEntityListener.class})
public class SystemUser extends AbstractAuditableEntity<SystemUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8167917557955085535L;
	
	@Basic
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "password")
	private String password;	
	

    @Version
    @Column
    private int version;


	public int getVersion() {
		return version;
	}
	
	public void setVersion(final int version) {
		this.version = version;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}
	


}
