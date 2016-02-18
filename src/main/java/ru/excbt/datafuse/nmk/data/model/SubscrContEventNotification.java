package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Уведомления абонента по событиям
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
@Entity
@Table(name = "subscr_cont_event_notification")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@DynamicUpdate
public class SubscrContEventNotification extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 691445476392471888L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_id")
	private ContEvent contEvent;

	@Column(name = "cont_event_level")
	private Integer contEventLevel;

	@Column(name = "cont_event_level_color")
	private String contEventLevelColor;

	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date contEventTime;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Column(name = "cont_event_type_id")
	private Long contEventTypeId;

	@Column(name = "is_new")
	private Boolean isNew;

	@Column(name = "notification_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificationTime;

	@Column(name = "notification_time_tz")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificationTimeTZ;

	@Column(name = "revision_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date revisionTime;

	@Column(name = "revision_time_tz")
	@Temporal(TemporalType.TIMESTAMP)
	private Date revisionTimeTZ;

	@Column(name = "revision_subscr_user_id")
	private Long revisionSubscrUserId;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ContEvent getContEvent() {
		return contEvent;
	}

	public void setContEvent(ContEvent contEvent) {
		this.contEvent = contEvent;
	}

	public Integer getContEventLevel() {
		return contEventLevel;
	}

	public void setContEventLevel(Integer contEventLevel) {
		this.contEventLevel = contEventLevel;
	}

	public String getContEventLevelColor() {
		return contEventLevelColor;
	}

	public void setContEventLevelColor(String contEventLevelColor) {
		this.contEventLevelColor = contEventLevelColor;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Date getNotificationTime() {
		return notificationTime;
	}

	public void setNotificationTime(Date notificationTime) {
		this.notificationTime = notificationTime;
	}

	public Date getRevisionTime() {
		return revisionTime;
	}

	public void setRevisionTime(Date revisionTime) {
		this.revisionTime = revisionTime;
	}

	public Date getContEventTime() {
		return contEventTime;
	}

	public void setContEventTime(Date contEventTime) {
		this.contEventTime = contEventTime;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Long getRevisionSubscrUserId() {
		return revisionSubscrUserId;
	}

	public void setRevisionSubscrUserId(Long revisionSubscrUserId) {
		this.revisionSubscrUserId = revisionSubscrUserId;
	}

	public Long getContEventTypeId() {
		return contEventTypeId;
	}

	public void setContEventTypeId(Long contEventTypeId) {
		this.contEventTypeId = contEventTypeId;
	}

	public Date getNotificationTimeTZ() {
		return notificationTimeTZ;
	}

	public void setNotificationTimeTZ(Date notificationTimeTZ) {
		this.notificationTimeTZ = notificationTimeTZ;
	}

	public Date getRevisionTimeTZ() {
		return revisionTimeTZ;
	}

	public void setRevisionTimeTZ(Date revisionTimeTZ) {
		this.revisionTimeTZ = revisionTimeTZ;
	}

}
