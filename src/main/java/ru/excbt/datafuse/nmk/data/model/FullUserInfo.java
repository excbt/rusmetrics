package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
	@Column(name = "id")
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

	@OneToOne
	@JoinColumn(name = "subscriber_id", updatable = false, insertable = false)
	private Subscriber subscriber;

	@Column(name = "user_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID userUUID;

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
			this.subscriber = src.subscriber;
			this.userUUID = src.userUUID;
		}
	}

	public int getVersion() {
		return version;
	}

	public String getUserName() {
		return userName;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public boolean is_system() {
		return _system;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public UUID getUserUUID() {
		return userUUID;
	}

}
