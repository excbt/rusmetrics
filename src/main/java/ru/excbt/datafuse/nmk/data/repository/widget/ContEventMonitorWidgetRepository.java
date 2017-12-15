package ru.excbt.datafuse.nmk.data.repository.widget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV3;

import java.util.List;

public interface ContEventMonitorWidgetRepository extends JpaRepository<ContEventMonitorV3, Long> {

    @Query("SELECT m.contObjectId, m.contEventTypeId, " +
        " max(m.lastContEventTime) as lastContEventTime, count(m.contEventTypeId) as typeCount " +
        "FROM ContEventMonitorV3 m GROUP BY m.contObjectId, m.contEventTypeId")
    List<Object[]> findContObjectStats();

    @Query("SELECT m.contZPointId, m.contEventTypeId, " +
        " max(m.lastContEventTime) as lastContEventTime, count(m.contEventTypeId) as typeCount " +
        "FROM ContEventMonitorV3 m " +
        "WHERE m.contObjectId = :contObjectId " +
        "GROUP BY m.contZPointId, m.contEventTypeId")
    List<Object[]> findContZPointStats(@Param("contObjectId") Long contObjectId);
}
