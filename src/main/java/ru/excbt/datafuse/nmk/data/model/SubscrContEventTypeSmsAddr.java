package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(name = "subscr_cont_event_type_sms_addr")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class SubscrContEventTypeSmsAddr extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557265399290800488L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_cont_event_type_sms_id")
	private SubscrContEventTypeSms subscrContEventTypeSms;

	@Column(name = "subscr_cont_event_type_sms_id", insertable = false, updatable = false)
	private Long subscrContEventTypeSmsId;

	@Column(name = "address_name")
	private String addressName;

	@Column(name = "address_type")
	private String addressType = "sms";

	@Column(name = "address_sms")
	private String addressSms;

	@Column(name = "address_email")
	private String addressEmail;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public SubscrContEventTypeSms getSubscrContEventTypeSms() {
		return subscrContEventTypeSms;
	}

	public void setSubscrContEventTypeSms(SubscrContEventTypeSms subscrContEventTypeSms) {
		this.subscrContEventTypeSms = subscrContEventTypeSms;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAddressSms() {
		return addressSms;
	}

	public void setAddressSms(String addressSms) {
		this.addressSms = addressSms;
	}

	public String getAddressEmail() {
		return addressEmail;
	}

	public void setAddressEmail(String addressEmail) {
		this.addressEmail = addressEmail;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getSubscrContEventTypeSmsId() {
		return subscrContEventTypeSmsId;
	}

	public void setSubscrContEventTypeSmsId(Long subscrContEventTypeSmsId) {
		this.subscrContEventTypeSmsId = subscrContEventTypeSmsId;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}
