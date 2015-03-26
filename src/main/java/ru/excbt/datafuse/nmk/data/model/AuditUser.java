package ru.excbt.datafuse.nmk.data.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name = "audit_user")
public class AuditUser extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3252857396837049517L;

	@Column(name = "user_name")
	private String userName;

	@Version
	private int version;

	@Column(name = "is_system")
	private boolean isSystem;

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
		if (srcObject.getId() != null) {
			this.setId(srcObject.getId().longValue());
		}
		this.isSystem = srcObject.isSystem;
	}

	public boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

}
