package ru.excbt.datafuse.nmk.data.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

/**
 * Пользователь для аудита
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
@Entity
@Table(name = "audit_user")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class V_AuditUser implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3252857396837049517L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "user_name")
	private String userName;

	@Version
	private int version;

	@Column(name = "is_system")
	private Boolean isSystem;

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public V_AuditUser() {

	}

	public V_AuditUser(V_AuditUser srcObject) {
		checkNotNull(srcObject, "AuditUser: parameter srcObject is null");

		this.userName = srcObject.userName;
		this.version = srcObject.version;
		this.id = srcObject.id;
		this.isSystem = srcObject.isSystem;
	}

	public V_AuditUser(SubscriberUserDetails srcObject) {
		checkNotNull(srcObject, "AuditUser: parameter srcObject is null");

		this.userName = srcObject.getUsername();
		this.version = srcObject.getVersion();
		this.id = srcObject.getId();
		this.isSystem = srcObject.getIsSystem();
	}

	public V_AuditUser id(Long id) {
	    this.setId(id);
	    return this;
    }

	public Boolean getIsSystem() {
		return isSystem;
	}

}
