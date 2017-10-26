package ru.excbt.datafuse.nmk.data.model;

import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.util.Date;

public interface ContEventMonitorX extends ContEventTypeModel {

    Long getContObjectId();

    default Long getContZPointId() {
        return null;
    }

    Long getContEventId();

    Long getContEventTypeId();

    ContEventType getContEventType();

    LocalDateTime getContEventTime();

    Integer getContEventLevel();

    String getContEventLevelColorKeyname();

    Long getLastContEventId();

    Date getLastContEventTime();

    Long getWorseContEventId();

    Date getWorseContEventTime();

    Boolean getIsScalar();

    default Date getContEventTimeDT() {
        return LocalDateUtils.asDate(getContEventTime());
    }

}
