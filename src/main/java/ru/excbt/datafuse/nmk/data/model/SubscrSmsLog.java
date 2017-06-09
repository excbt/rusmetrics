package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_sms_log")
@Getter
@Setter
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

}
