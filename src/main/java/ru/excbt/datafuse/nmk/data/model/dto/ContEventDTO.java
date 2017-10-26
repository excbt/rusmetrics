package ru.excbt.datafuse.nmk.data.model.dto;

import java.util.Date;

public class ContEventDTO {

    private Long id;

    private Long contObjectId;

    private Long contZPointId;

    private ContEventTypeDTO contEventType;

    private Long contEventTypeId;

    private String contServiceType;

    private Date eventTime;

    private Date registrationTimeTZ;

    private String message;

    private String contEventDeviationKeyname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContObjectId() {
        return contObjectId;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public Long getContZPointId() {
        return contZPointId;
    }

    public void setContZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
    }

    public ContEventTypeDTO getContEventType() {
        return contEventType;
    }

    public void setContEventType(ContEventTypeDTO contEventType) {
        this.contEventType = contEventType;
    }

    public Long getContEventTypeId() {
        return contEventTypeId;
    }

    public void setContEventTypeId(Long contEventTypeId) {
        this.contEventTypeId = contEventTypeId;
    }

    public String getContServiceType() {
        return contServiceType;
    }

    public void setContServiceType(String contServiceType) {
        this.contServiceType = contServiceType;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getRegistrationTimeTZ() {
        return registrationTimeTZ;
    }

    public void setRegistrationTimeTZ(Date registrationTimeTZ) {
        this.registrationTimeTZ = registrationTimeTZ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContEventDeviationKeyname() {
        return contEventDeviationKeyname;
    }

    public void setContEventDeviationKeyname(String contEventDeviationKeyname) {
        this.contEventDeviationKeyname = contEventDeviationKeyname;
    }
}
