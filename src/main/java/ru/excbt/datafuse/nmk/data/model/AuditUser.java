package ru.excbt.datafuse.nmk.data.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.model.security.AuditUserPrincipal;

@Entity
@Table(name = "audit_user")
public class AuditUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3252857396837049517L;

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "user_name")
	private String userName;

	@Version
	private int version;

	@Column(name = "is_system")
	private boolean _system;

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

	public AuditUser() {

	}

	public AuditUser(AuditUser srcObject) {
		checkNotNull(srcObject, "AuditUser: parameter srcObject is null");

		this.userName = srcObject.userName;
		this.version = srcObject.version;
		this.id = srcObject.id;
		this._system = srcObject._system;
	}

	public AuditUser(AuditUserPrincipal srcObject) {
		checkNotNull(srcObject, "AuditUser: parameter srcObject is null");

		this.userName = srcObject.getUserName();
		this.version = srcObject.getVersion();
		this.id = srcObject.getId();
		this._system = srcObject.is_system();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean is_system() {
		return _system;
	}

	public void set_system(boolean _system) {
		this._system = _system;
	}

}
