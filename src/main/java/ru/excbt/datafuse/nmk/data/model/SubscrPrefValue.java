package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref_value")
public class SubscrPrefValue extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8915150007059190379L;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "pref_category")
	private String prefCategory;

	@Column(name = "subscr_pref")
	private String subscrPref;

	@Column(name = "pref_value")
	private String prefValue;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getPrefCategory() {
		return prefCategory;
	}

	public void setPrefCategory(String prefCategory) {
		this.prefCategory = prefCategory;
	}

	public String getSubscrPref() {
		return subscrPref;
	}

	public void setSubscrPref(String subscrPref) {
		this.subscrPref = subscrPref;
	}

	public String getPrefValue() {
		return prefValue;
	}

	public void setPrefValue(String prefValue) {
		this.prefValue = prefValue;
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