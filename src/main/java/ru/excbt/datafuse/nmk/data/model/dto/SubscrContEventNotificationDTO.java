package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscrContEventNotificationDTO {

    private Long id;

    private Long subscriberId;

    private ContEventDTO contEvent;

    private Long contEventId;

    private Integer contEventLevel;

    private String contEventLevelColor;

    private Date contEventTime;

    private Long contObjectId;

    private Long contEventTypeId;

    private ContEventTypeDTO contEventType;

    private Boolean isNew;

    private Date notificationTime;

    private Date notificationTimeTZ;

    private Date revisionTime;

    private Date revisionTimeTZ;

    private Long revisionSubscrUserId;

    private String contEventCategoryKeyname;

    private String contEventDeviationKeyname;

    private Short monVersion;

    public Date getContEventTimeDT() {
        return contEventTime;
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

    public ContEventDTO getContEvent() {
        return contEvent;
    }

    public void setContEvent(ContEventDTO contEvent) {
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

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Date getNotificationTimeTZ() {
        return notificationTimeTZ;
    }

    public void setNotificationTimeTZ(Date notificationTimeTZ) {
        this.notificationTimeTZ = notificationTimeTZ;
    }

    public Date getRevisionTime() {
        return revisionTime;
    }

    public void setRevisionTime(Date revisionTime) {
        this.revisionTime = revisionTime;
    }

    public Date getRevisionTimeTZ() {
        return revisionTimeTZ;
    }

    public void setRevisionTimeTZ(Date revisionTimeTZ) {
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
