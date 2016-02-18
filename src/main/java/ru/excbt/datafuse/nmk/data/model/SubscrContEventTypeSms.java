package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

/**
 * Настройка СМС сообщения для абонента по типам событий
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@Entity
@Table(name = "subscr_cont_event_type_sms")
public class SubscrContEventTypeSms extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3626302771831986734L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_type_id")
	private ContEventType contEventType;

	@Column(name = "is_active")
	private Boolean isActive;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
