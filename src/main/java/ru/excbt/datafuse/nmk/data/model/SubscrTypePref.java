package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_type_pref")
public class SubscrTypePref extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9149973385103873472L;

	@Column(name = "subscr_type")
	private String subscrType;

	@Column(name = "subscr_pref")
	private String subscrPref;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getSubscrType() {
		return subscrType;
	}

	public void setSubscrType(String subscrType) {
		this.subscrType = subscrType;
	}

	public String getSubscrPref() {
		return subscrPref;
	}

	public void setSubscrPref(String subscrPref) {
		this.subscrPref = subscrPref;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}