package ru.excbt.datafuse.nmk.data.model.dto;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitorX;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.util.Date;

public class ContEventMonitorXDTO {

    private Long id;

    private Long contObjectId;

    private Long contZPointId;

    private Long contEventId;

    private Long contEventTypeId;

    private ContEventTypeDTO contEventType;

    private LocalDateTime contEventTime;

    private Integer contEventLevel;

    private String contEventLevelColorKeyname;

    private Long lastContEventId;

    private LocalDateTime lastContEventTime;

    private Long worseContEventId;

    private LocalDateTime worseContEventTime;

    private Boolean isScalar;

    private String contServiceType;

    public ContEventMonitorXDTO() {

    }

    public ContEventMonitorXDTO(ContEventMonitorX x) {
        this.id = x.getId();
        this.contObjectId = x.getContObjectId();
        this.contZPointId = x.getContZPointId();
        this.contEventId = x.getContEventId();
        this.contEventTypeId = x.getContEventTypeId();
        this.contEventType = ContEventTypeDTO.MAPPER.toDto(x.getContEventType());
        this.contEventTime = x.getContEventTime();
        this.contEventLevel = x.getContEventLevel();
        this.contEventLevelColorKeyname = x.getContEventLevelColorKeyname();
        this.lastContEventId = x.getLastContEventId();
        this.lastContEventTime = x.getLastContEventTime();
        this.worseContEventId = x.getWorseContEventId();
        this.worseContEventTime = x.getWorseContEventTime();
        this.isScalar = x.getIsScalar();
    }

    public Date getContEventTimeDT() {
        return LocalDateUtils.asDate(contEventTime);
    }

    ////////

    public Long getId() {
        return id;
    }

    public Long getContObjectId() {
        return contObjectId;
    }

    public Long getContZPointId() {
        return contZPointId;
    }

    public Long getContEventId() {
        return contEventId;
    }

    public Long getContEventTypeId() {
        return contEventTypeId;
    }

    public ContEventTypeDTO getContEventType() {
        return contEventType;
    }

    public LocalDateTime getContEventTime() {
        return contEventTime;
    }

    public Integer getContEventLevel() {
        return contEventLevel;
    }

    public String getContEventLevelColorKeyname() {
        return contEventLevelColorKeyname;
    }

    public Long getLastContEventId() {
        return lastContEventId;
    }

    public LocalDateTime getLastContEventTime() {
        return lastContEventTime;
    }

    public Long getWorseContEventId() {
        return worseContEventId;
    }

    public LocalDateTime getWorseContEventTime() {
        return worseContEventTime;
    }

    public Boolean getScalar() {
        return isScalar;
    }

    public String getContServiceType() {
        return contServiceType;
    }

    public void setContServiceType(String contServiceType) {
        this.contServiceType = contServiceType;
    }

    ContEventMonitorXDTO contServiceType(String contServiceType) {
        this.contServiceType = contServiceType;
        return this;
    }
}
