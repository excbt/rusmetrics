package ru.excbt.datafuse.nmk.data.model.dto;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class SubscrContEventNotificationDTO {

    Long id;

    private Long subscriberId;

    private ContEvent contEvent;

    private Long contEventId;

    private Integer contEventLevel;

    private String contEventLevelColor;

    private LocalDateTime contEventTime;

    private Long contObjectId;

    private Long contEventTypeId;

    private ContEventTypeDTO contEventType;

    private Boolean isNew;

    private LocalDateTime notificationTime;

    private ZonedDateTime notificationTimeTZ;

    private LocalDateTime revisionTime;

    private ZonedDateTime revisionTimeTZ;

    private Long revisionSubscrUserId;

    private String contEventCategoryKeyname;

    private String contEventDeviationKeyname;

    private Short monVersion;

    public Date getContEventTimeDT() {
        return LocalDateUtils.asDate(contEventTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public ContEvent getContEvent() {
        return contEvent;
    }

    public void setContEvent(ContEvent contEvent) {
        this.contEvent = contEvent;
    }

    public Long getContEventId() {
        return contEventId;
    }

    public void setContEventId(Long contEventId) {
        this.contEventId = contEventId;
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

    public LocalDateTime getContEventTime() {
        return contEventTime;
    }

    public void setContEventTime(LocalDateTime contEventTime) {
        this.contEventTime = contEventTime;
    }

    public Long getContObjectId() {
        return contObjectId;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public Long getContEventTypeId() {
        return contEventTypeId;
    }

    public void setContEventTypeId(Long contEventTypeId) {
        this.contEventTypeId = contEventTypeId;
    }

    public ContEventTypeDTO getContEventType() {
        return contEventType;
    }

    public void setContEventType(ContEventTypeDTO contEventType) {
        this.contEventType = contEventType;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public ZonedDateTime getNotificationTimeTZ() {
        return notificationTimeTZ;
    }

    public void setNotificationTimeTZ(ZonedDateTime notificationTimeTZ) {
        this.notificationTimeTZ = notificationTimeTZ;
    }

    public LocalDateTime getRevisionTime() {
        return revisionTime;
    }

    public void setRevisionTime(LocalDateTime revisionTime) {
        this.revisionTime = revisionTime;
    }

    public ZonedDateTime getRevisionTimeTZ() {
        return revisionTimeTZ;
    }

    public void setRevisionTimeTZ(ZonedDateTime revisionTimeTZ) {
        this.revisionTimeTZ = revisionTimeTZ;
    }

    public Long getRevisionSubscrUserId() {
        return revisionSubscrUserId;
    }

    public void setRevisionSubscrUserId(Long revisionSubscrUserId) {
        this.revisionSubscrUserId = revisionSubscrUserId;
    }

    public String getContEventCategoryKeyname() {
        return contEventCategoryKeyname;
    }

    public void setContEventCategoryKeyname(String contEventCategoryKeyname) {
        this.contEventCategoryKeyname = contEventCategoryKeyname;
    }

    public String getContEventDeviationKeyname() {
        return contEventDeviationKeyname;
    }

    public void setContEventDeviationKeyname(String contEventDeviationKeyname) {
        this.contEventDeviationKeyname = contEventDeviationKeyname;
    }

    public Short getMonVersion() {
        return monVersion;
    }

    public void setMonVersion(Short monVersion) {
        this.monVersion = monVersion;
    }
}
