package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicUpdate;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
@DynamicUpdate
public class SubscrContEventNotification extends AbstractAuditableModel implements ContEventTypeModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 691445476392471888L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id", updatable = false)
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@Transient
	private ContEvent contEvent;

	@Column(name = "cont_event_id", insertable = false, updatable = false)
	private Long contEventId;

	@Column(name = "cont_event_level", insertable = false, updatable = false)
	private Integer contEventLevel;

	@Column(name = "cont_event_level_color", insertable = false, updatable = false)
	private String contEventLevelColor;

	@Column(name = "cont_event_time", insertable = false, updatable = false)
	private LocalDateTime contEventTime;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@Column(name = "cont_event_type_id", insertable = false, updatable = false)
	private Long contEventTypeId;

	@Transient
	private ContEventType contEventType;

	@Column(name = "is_new")
	private Boolean isNew;

	@Column(name = "notification_time", insertable = false, updatable = false)
	private LocalDateTime notificationTime;

	@Column(name = "notification_time_tz", insertable = false, updatable = false)
	private ZonedDateTime notificationTimeTZ;

	@Column(name = "revision_time")
	private LocalDateTime revisionTime;

	@Column(name = "revision_time_tz")
	private ZonedDateTime revisionTimeTZ;

	@Column(name = "revision_subscr_user_id")
	private Long revisionSubscrUserId;

	@Column(name = "cont_event_category", insertable = false, updatable = false)
	private String contEventCategoryKeyname;

	@Column(name = "cont_event_deviation", insertable = false, updatable = false)
	private String contEventDeviationKeyname;

	@Column(name = "mon_version")
	@NotNull
	private Short monVersion = 1;


    public Subscriber getSubscriber() {
        return this.subscriber;
    }

    public Long getSubscriberId() {
        return this.subscriberId;
    }

    public ContEvent getContEvent() {
        return this.contEvent;
    }

    public Long getContEventId() {
        return this.contEventId;
    }

    public Integer getContEventLevel() {
        return this.contEventLevel;
    }

    public String getContEventLevelColor() {
        return this.contEventLevelColor;
    }

    public LocalDateTime getContEventTime() {
        return this.contEventTime;
    }

    public Long getContObjectId() {
        return this.contObjectId;
    }

    public Long getContEventTypeId() {
        return this.contEventTypeId;
    }

    public ContEventType getContEventType() {
        return this.contEventType;
    }

    public Boolean getIsNew() {
        return this.isNew;
    }

    public LocalDateTime getNotificationTime() {
        return this.notificationTime;
    }

    public ZonedDateTime getNotificationTimeTZ() {
        return this.notificationTimeTZ;
    }

    public LocalDateTime getRevisionTime() {
        return this.revisionTime;
    }

    public ZonedDateTime getRevisionTimeTZ() {
        return this.revisionTimeTZ;
    }

    public Long getRevisionSubscrUserId() {
        return this.revisionSubscrUserId;
    }

    public String getContEventCategoryKeyname() {
        return this.contEventCategoryKeyname;
    }

    public String getContEventDeviationKeyname() {
        return this.contEventDeviationKeyname;
    }

    public Short getMonVersion() {
        return this.monVersion;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public void setContEvent(ContEvent contEvent) {
        this.contEvent = contEvent;
    }

    public void setContEventId(Long contEventId) {
        this.contEventId = contEventId;
    }

    public void setContEventLevel(Integer contEventLevel) {
        this.contEventLevel = contEventLevel;
    }

    public void setContEventLevelColor(String contEventLevelColor) {
        this.contEventLevelColor = contEventLevelColor;
    }

    public void setContEventTime(LocalDateTime contEventTime) {
        this.contEventTime = contEventTime;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public void setContEventTypeId(Long contEventTypeId) {
        this.contEventTypeId = contEventTypeId;
    }

    public void setContEventType(ContEventType contEventType) {
        this.contEventType = contEventType;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public void setNotificationTimeTZ(ZonedDateTime notificationTimeTZ) {
        this.notificationTimeTZ = notificationTimeTZ;
    }

    public void setRevisionTime(LocalDateTime revisionTime) {
        this.revisionTime = revisionTime;
    }

    public void setRevisionTimeTZ(ZonedDateTime revisionTimeTZ) {
        this.revisionTimeTZ = revisionTimeTZ;
    }

    public void setRevisionSubscrUserId(Long revisionSubscrUserId) {
        this.revisionSubscrUserId = revisionSubscrUserId;
    }

    public void setContEventCategoryKeyname(String contEventCategoryKeyname) {
        this.contEventCategoryKeyname = contEventCategoryKeyname;
    }

    public void setContEventDeviationKeyname(String contEventDeviationKeyname) {
        this.contEventDeviationKeyname = contEventDeviationKeyname;
    }

    public void setMonVersion(Short monVersion) {
        this.monVersion = monVersion;
    }
}
