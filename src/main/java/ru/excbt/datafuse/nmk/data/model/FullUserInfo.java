package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "full_user_info")
public class FullUserInfo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6844261142252428185L;

	@Id
	@Column(name="id")
	private Long id;	
	
	@Column(name = "user_name")
	private String userName;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Version
	private int version;

	@Column(name = "is_system")
	private boolean _system;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	public FullUserInfo() {
		
	}
	
	public FullUserInfo(FullUserInfo src) {
		if (src != null) {
			this.id = src.id;
			this.userName = src.userName;
			this.firstName = src.firstName;
			this.lastName = src.lastName;
			this.version = src.version;
			this._system = src._system;			
			this.subscriberId = src.subscriberId;
		}
	}	
	
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public boolean is_system() {
		return _system;
	}

	public void set_system(boolean _system) {
		this._system = _system;
	}



}
