package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV3;
import ru.excbt.datafuse.nmk.data.repository.support.ContEventMonitorXRI;

import java.util.List;

public interface ContEventMonitorV3Repository extends JpaRepository<ContEventMonitorV3, Long>, ContEventMonitorXRI<ContEventMonitorV3> {


    @Query(value = "SELECT CAST(f.city_fias_uuid as TEXT) city_fias_uuid, COUNT(m.cont_event_id) cont_event_count " +
        "FROM portal.cont_event_monitor_v3 m, cont_object_fias f, portal.cont_zpoint_access ca " +
        "WHERE m.cont_object_id = f.cont_object_id AND m.cont_zpoint_id = ca.cont_zpoint_id " +
        " AND (m.is_scalar IS NULL OR m.is_scalar = FALSE) " + "AND ca.subscriber_id = :subscriberId " +
        " AND ca.access_ttl_tz IS NULL " +
        " GROUP BY city_fias_uuid ", nativeQuery = true)
    List<Object[]> selectCityContObjectMonitorEventCount(@Param("subscriberId") Long subscriberId);

}
