package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref_object_tree_type")
public class SubscrPrefObjectTreeType extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5446111068809742392L;

	@Column(name = "subscr_pref")
	private String subscrPref;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "dev_comment")
	private String devComment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getSubscrPref() {
		return subscrPref;
	}

	public void setSubscrPref(String subscrPref) {
		this.subscrPref = subscrPref;
	}

	public String getObjectTreeType() {
		return objectTreeType;
	}

	public void setObjectTreeType(String objectTreeType) {
		this.objectTreeType = objectTreeType;
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