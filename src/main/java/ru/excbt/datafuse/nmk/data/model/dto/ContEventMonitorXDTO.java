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

    private Date lastContEventTime;

    private Long worseContEventId;

    private Date worseContEventTime;

    private Boolean isScalar;

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

    public Date getLastContEventTime() {
        return lastContEventTime;
    }

    public Long getWorseContEventId() {
        return worseContEventId;
    }

    public Date getWorseContEventTime() {
        return worseContEventTime;
    }

    public Boolean getScalar() {
        return isScalar;
    }


    public Date getContEventTimeDT() {
        return LocalDateUtils.asDate(getContEventTime());
    }

}
