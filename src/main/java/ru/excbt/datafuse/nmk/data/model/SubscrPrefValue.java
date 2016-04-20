package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_pref_value")
public class SubscrPrefValue extends JsonAbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8915150007059190379L;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "subscr_pref_category")
	private String subscrPrefCategory;

	@Column(name = "subscr_pref")
	private String subscrPrefKeyname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_pref", insertable = false, updatable = false)
	private SubscrPref subscrPref;

	@Column(name = "value")
	private String value;

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

	public String getSubscrPrefCategory() {
		return subscrPrefCategory;
	}

	public void setSubscrPrefCategory(String subscrPrefCategory) {
		this.subscrPrefCategory = subscrPrefCategory;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSubscrPrefKeyname() {
		return subscrPrefKeyname;
	}

	public void setSubscrPrefKeyname(String subscrPrefKeyname) {
		this.subscrPrefKeyname = subscrPrefKeyname;
	}

	public SubscrPref getSubscrPref() {
		return subscrPref;
	}

	public void setSubscrPref(SubscrPref subscrPref) {
		this.subscrPref = subscrPref;
	}

}