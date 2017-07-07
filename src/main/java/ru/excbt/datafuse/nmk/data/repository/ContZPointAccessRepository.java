package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.data.repository.support.ContZPointRI;
import ru.excbt.datafuse.nmk.data.repository.support.SubscriberRI;

import java.util.List;

/**
 * Created by kovtonyk on 27.06.2017.
 */
public interface ContZPointAccessRepository extends JpaRepository<ContZPointAccess, ContZPointAccess.PK>,
    SubscriberRI<ContZPointAccess>, ContZPointRI<ContZPointAccess> {

    @Query("SELECT distinct a.subscriber.id FROM ContZPointAccess a")
    List<Long> findAllSubscriberIds();

    @Query("SELECT distinct a.contZPoint.id FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId")
    List<Long> findContZPointIds (@Param("subscriberId") Long subscriberId);

    /// Queries for ContZPoint


    @Query("SELECT a.contZPoint FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId AND a.accessTtl IS NULL"
        + " AND a.accessTtl IS NULL "
        + " ORDER BY a.contZPoint.contServiceTypeKeyname, a.contZPoint.id")
    List<ContZPoint> findAllContZPointsBySubscriberId(@Param("subscriberId") Long subscriberId);

    @Query("SELECT a.contZPointId FROM ContZPointAccess a WHERE a.subscriberId = :subscriberId AND a.accessTtl IS NULL"
        + " AND a.accessTtl IS NULL AND a.contZPoint.deleted = 0 "
        + " ORDER BY a.contZPoint.contServiceTypeKeyname, a.contZPoint.id")
    List<Long> findAllContZPointIds(@Param("subscriberId") Long subscriberId);

    @Query("SELECT zp.id, zp.contObjectId, zp.customServiceName, zp.contServiceTypeKeyname, st.caption "
        + " FROM ContZPoint zp INNER JOIN zp.contServiceType st WHERE zp.id IN "
        + " (SELECT a.contZPointId FROM ContZPointAccess a "
        + " WHERE a.subscriberId = :subscriberId  AND a.accessTtl IS NULL) AND zp.deleted = 0")
    List<Object[]> findAllContZPointShortInfo(@Param("subscriberId") Long subscriberId);



}
