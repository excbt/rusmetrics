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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "subscr_cont_event_type_action")
public class SubscrContEventTypeAction extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2322682172623184659L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", updatable = false, insertable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_type_id")
	@JsonIgnore
	private ContEventType contEventType;

	@Column(name = "cont_event_type_id", updatable = false, insertable = false)
	private Long contEventTypeId;

	@Column(name = "subscr_action_user_id")
	private Long subscrActionUserId;

	@Column(name = "is_email")
	private Boolean isEmail;

	@Column(name = "is_sms")
	private Boolean isSms;

	@Version
	@JsonIgnore
	private int version;

	@Column(name = "deleted")
	@JsonIgnore
	private int deleted;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ContEventType getContEventType() {
		return contEventType;
	}

	public void setContEventType(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

	public Long getSubscrActionUserId() {
		return subscrActionUserId;
	}

	public void setSubscrActionUserId(Long subscrActionUserId) {
		this.subscrActionUserId = subscrActionUserId;
	}

	public Boolean getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(Boolean isEmail) {
		this.isEmail = isEmail;
	}

	public Boolean getIsSms() {
		return isSms;
	}

	public void setIsSms(Boolean isSms) {
		this.isSms = isSms;
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

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Long getContEventTypeId() {
		return contEventTypeId;
	}

	public void setContEventTypeId(Long contEventTypeId) {
		this.contEventTypeId = contEventTypeId;
	}

}
