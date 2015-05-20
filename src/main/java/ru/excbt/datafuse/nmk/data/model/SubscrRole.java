package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name="subscr_role")
public class SubscrRole extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Column (name="subscr_role_name")
	private String roleName;

	@Column (name="subscr_role_info")
	private String info;

	@Column (name="subscr_role_comment")
	private String comment;
	
	@Version
	private int version;
	

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	
	
}
