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

import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;

/**
 * Полная информация о пользователе
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "full_user_info")
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

	@Column(name = "is_system", insertable = false, updatable = false)
	private boolean _system;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@OneToOne
	@JoinColumn(name = "subscriber_id", updatable = false, insertable = false)
	private Subscriber subscriber;

	@Column(name = "user_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID userUUID;

	@Column(name = "is_system")
	private Boolean isSystem;

	@Column(name = "is_admin")
	private Boolean isAdmin;

	@Column(name = "is_readonly")
	private Boolean isReadonly;

	@Column(name = "can_create_child")
	private Boolean canCreateChild;

	@Column(name = "is_child")
	private Boolean isChild;

	@Column(name = "subscr_type")
	private String subscrType;

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
			this.isSystem = src.isSystem;
			this.isReadonly = src.isReadonly;
			this.isAdmin = src.isAdmin;
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

	public Boolean getIsRma() {
		if (this._system) {
			return true;
		}
		if (this.subscriber == null) {
			return null;
		}
		return subscriber.getIsRma();
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public Boolean getIsReadonly() {
		return isReadonly;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public String getSubscrType() {
		return subscrType;
	}

	public Boolean getIsCabinet() {
		return SubscrTypeKey.CABINET.getKeyname().equals(subscrType) && Boolean.TRUE.equals(isChild);
	}

	public Boolean getCanCreateChild() {
		return canCreateChild;
	}

}
