package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name="system_user")
public class AuditUser extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3252857396837049517L;
	
	@Column(name = "user_name")
	private String userName;
	
    @Version
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

}
