package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_sms_log")
public class SubscrSmsLog extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6284139169023335580L;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "sms_date")
	private Date smsDate;

	@Column(name = "sms_reciever_name")
	private String smsRecieverName;

	@Column(name = "sms_reciever_tel")
	private String smsRecieverTel;

	@Column(name = "sms_message")
	private String smsMessage;

	@Column(name = "sms_provider_url")
	private String smsProviderUrl;

	@Column(name = "sms_provider_response_code")
	private String smsProviderResponseCode;

	@Column(name = "sms_provider_response_body")
	private String smsProviderResponseBody;

	@Column(name = "sms_status")
	private String smsStatus;

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

	public Date getSmsDate() {
		return smsDate;
	}

	public void setSmsDate(Date smsDate) {
		this.smsDate = smsDate;
	}

	public String getSmsRecieverName() {
		return smsRecieverName;
	}

	public void setSmsRecieverName(String smsRecieverName) {
		this.smsRecieverName = smsRecieverName;
	}

	public String getSmsRecieverTel() {
		return smsRecieverTel;
	}

	public void setSmsRecieverTel(String smsRecieverTel) {
		this.smsRecieverTel = smsRecieverTel;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public String getSmsProviderUrl() {
		return smsProviderUrl;
	}

	public void setSmsProviderUrl(String smsProviderUrl) {
		this.smsProviderUrl = smsProviderUrl;
	}

	public String getSmsProviderResponseCode() {
		return smsProviderResponseCode;
	}

	public void setSmsProviderResponseCode(String smsProviderResponseCode) {
		this.smsProviderResponseCode = smsProviderResponseCode;
	}

	public String getSmsProviderResponseBody() {
		return smsProviderResponseBody;
	}

	public void setSmsProviderResponseBody(String smsProviderResponseBody) {
		this.smsProviderResponseBody = smsProviderResponseBody;
	}

	public String getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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