package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

@Entity
@Table(name="subscr_role")
@EntityListeners({AuditingEntityListener.class})
public class SubscrRole extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Column (name="subscr_role_name")
	private String name;

	@Column (name="subscr_role_info")
	private String info;

	@Column (name="subscr_role_comment")
	private String comment;
	
	@Version
	private int version;
	
	public String getName() {
		return name;
	}

	public void setName(String role) {
		this.name = role;
	}

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

	
	
}
