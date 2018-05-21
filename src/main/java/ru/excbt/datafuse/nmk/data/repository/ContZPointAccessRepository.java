package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.repository.support.ContZPointRI;
import ru.excbt.datafuse.nmk.data.repository.support.ObjectAccessRI;
import ru.excbt.datafuse.nmk.data.repository.support.SubscriberRI;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by kovtonyk on 27.06.2017.
 */
public interface ContZPointAccessRepository extends JpaRepository<ContZPointAccess, ContZPointAccess.PK>,
    SubscriberRI<ContZPointAccess>, ContZPointRI<ContZPointAccess>, ObjectAccessRI<ContZPointAccess>,
    QueryDslPredicateExecutor<ContZPointAccess> {

    @Query("SELECT distinct a.subscriber.id FROM ContZPointAccess a")
    List<Long> findAllSubscriberIds();

    @Query("SELECT distinct a.contZPoint.id FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId")
    List<Long> findContZPointIds(@Param("subscriberId") Long subscriberId);

    /// Queries for ContZPoint
    // TODO Check AccessTtl

    @Query("SELECT a.contZPoint FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId " +
        " AND a.accessTtl IS NULL " +
        " ORDER BY a.contZPoint.contServiceTypeKeyname, a.contZPoint.id")
    List<ContZPoint> findAllContZPointsBySubscriberId(@Param("subscriberId") Long subscriberId);


    @Query("SELECT a.contZPoint FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId " +
        " AND a.accessTtl IS NULL " +
        " AND a.contZPoint.contObjectId = :contObjectId" +
        " ORDER BY a.contZPoint.contServiceTypeKeyname, a.contZPointId")
    List<ContZPoint> findAllContZPointsBySubscriberId(@Param("subscriberId") Long subscriberId, @Param("contObjectId") Long contObjectId);


    @Query("SELECT a.contZPointId FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId " +
        " AND a.accessTtl IS NULL " +
        " AND a.contZPoint.deleted = 0 " +
        " ORDER BY a.contZPoint.contServiceTypeKeyname, a.contZPoint.id")
    List<Long> findAllContZPointIds(@Param("subscriberId") Long subscriberId);


    @Query("SELECT zp.id, zp.contObjectId, zp.customServiceName, zp.contServiceTypeKeyname, st.caption " +
            " FROM ContZPoint zp INNER JOIN zp.contServiceType st WHERE zp.id IN " +
            " (SELECT a.contZPointId FROM ContZPointAccess a " +
            "  WHERE a.subscriberId = :subscriberId  " +
            "  AND a.accessTtl IS NULL" +
            " ) AND zp.deleted = 0")
    List<Object[]> findAllContZPointShortInfo(@Param("subscriberId") Long subscriberId);

    /**
     *
     * @param subscriberId
     * @return
     */
    @Query("SELECT DISTINCT zp.id, zp.contObjectId " +
            " FROM ContZPoint zp WHERE zp.id IN " +
            " (SELECT a.contZPointId FROM ContZPointAccess a " +
            "  WHERE a.subscriberId = :subscriberId " +
            "  AND a.accessTtl IS NULL" +
            " ) AND zp.deleted = 0")
    List<Object[]> findAllContZPointIdPairs(@Param("subscriberId") Long subscriberId);


//    @Query("SELECT zp.deviceObject FROM ContZPointAccess a LEFT JOIN a.contZPoint zp " +
//        " WHERE a.subscriberId = :subscriberId " +
//        " AND a.accessTtl IS NULL" +
//        " AND a.contZPoint.deleted = 0 ")
    @Query("SELECT dev FROM DeviceObject dev WHERE dev.deleted = 0 and dev.id IN ( " +
        " SELECT zp.deviceObject.id FROM ContZPointAccess a LEFT JOIN a.contZPoint zp " +
        " WHERE a.subscriberId = :subscriberId " +
        " AND a.accessTtl IS NULL" +
        " AND a.contZPoint.deleted = 0 )")
    List<DeviceObject> findAllDeviceObjects(@Param("subscriberId") Long subscriberId);


    // TODO check query. Issue with LAST Spring boot 1.5.13
    @Query(value = "SELECT DISTINCT a.subscriberId as subscriberId, zp.contObject.id as contObjectId, zp.id as contZPointId, zp.tsNumber as tsNumber, " +
        " zp.isManualLoading as isManualLoading, d.id as deviceObjectId, d.number as deviceObjectNumber, ds.subscrDataSource.id as subscrDataSourceId" +
        " FROM ContZPointAccess a, ContZPoint zp INNER JOIN zp.deviceObject d LEFT JOIN d.deviceObjectDataSource ds" +
        " WHERE a.subscriberId = :subscriberId AND zp.id = a.contZPointId AND d.number IN (:deviceObjectNumbers) " +
        " AND a.accessTtl IS NULL " +
        " AND zp.deleted = 0")
    List<Tuple> findAllDeviceObjectsEx(@Param("subscriberId") Long subscriberId,
                                       @Param("deviceObjectNumbers") List<String> deviceObjectNumbers);


    @Query("SELECT a.contZPoint.contObjectId as contObjectId, count(a.contZPointId) FROM ContZPointAccess a " +
        " WHERE a.subscriberId = :subscriberId AND a.contZPoint.contObjectId IN (:contObjectIds) "  +
        " AND a.accessTtl IS NULL " +
        " AND a.contZPoint.deleted = 0 " +
        " GROUP BY a.contZPoint.contObjectId" +
        " ORDER BY a.contZPoint.contObjectId")
    List<Object[]> findContObjectZPointStats(@Param("subscriberId") Long subscriberId,
                                             @Param("contObjectIds") List<Long> contObjectIds);


}
