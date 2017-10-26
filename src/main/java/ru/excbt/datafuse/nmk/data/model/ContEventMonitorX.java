package ru.excbt.datafuse.nmk.data.model;

import ru.excbt.datafuse.nmk.data.model.dto.ContEventMonitorXDTO;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.util.Date;

public interface ContEventMonitorX extends ContEventTypeModel {

    Long getId();

    Long getContObjectId();

    default Long getContZPointId() {
        return null;
    }

    Long getContEventId();

    Long getContEventTypeId();

    LocalDateTime getContEventTime();

    Integer getContEventLevel();

    String getContEventLevelColorKeyname();

    Long getLastContEventId();

    LocalDateTime getLastContEventTime();

    Long getWorseContEventId();

    LocalDateTime getWorseContEventTime();

    Boolean getIsScalar();

}
