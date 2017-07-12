package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.repository.support.ObjectAccessRI;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 28.06.2017.
 */
public interface ContObjectAccessRepository extends JpaRepository<ContObjectAccess, ContObjectAccess.PK>, ObjectAccessRI<ContObjectAccess> {

    @Query("SELECT distinct a.subscriber.id FROM ContObjectAccess a WHERE a.accessTtl IS NULL")
    List<Long> findAllSubscriberIdsNoTtl();

    @Query("SELECT distinct a.contObject.id FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId AND a.accessTtl IS NULL")
    List<Long> findAllContObjectIdsNoTtl (@Param("subscriberId") Long subscriberId);

    @Query("SELECT distinct a.contObject.id FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId ")
    List<Long> findAllContObjectIds (@Param("subscriberId") Long subscriberId);

//    @Query("SELECT distinct a.contObject.id FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId")
//    List<Long> findContObjectIdsBySubscriberTTL (@Param("subscriberId") Long subscriberId);

    @Query("SELECT distinct a.subscriberId FROM ContObjectAccess a WHERE a.contObjectId = :contObjectId AND a.accessTtl IS NULL")
    List<Long> findSubscriberByContObjectNoTtl(@Param("contObjectId") Long contObjectId);

    @Query("SELECT count(a) FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId AND a.contObjectId = :contObjectId AND a.accessTtl IS NULL")
    Optional<Long> findByPK(@Param("subscriberId") Long subscriberId, @Param("contObjectId") Long contObjectId);


    List<ContObjectAccess> findBySubscriberId (Long subscriberId);


    /// Queries for ContObject

//    @Query("SELECT a.contObject FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId "
//        + " AND a.accessTtl IS NULL "
//        + " ORDER BY a.contObject.fullAddress, a.contObject.id")
//    List<ContObject> findContObjectsBySubscriberId(@Param("subscriberId") Long subscriberId);


    @Query("SELECT a.contObject FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId "
        + " ORDER BY a.contObject.fullAddress, a.contObject.id")
    List<ContObject> findAllContObjects(@Param("subscriberId") Long subscriberId);


    @Query("SELECT a.contObject FROM ContObjectAccess a "
        + " WHERE a.subscriberId = :subscriberId "
        + " AND a.contObjectId NOT IN (:idList)"
        + " AND a.accessTtl IS NULL "
        + " ORDER BY a.contObject.fullAddress, a.contObject.id")
    List<ContObject> findContObjectsExcludingIdsNoTtl(@Param("subscriberId") Long subscriberId,
                                                 @Param("idList") List<Long> idList);


    @Query("SELECT DISTINCT a.contObjectId FROM ContObjectAccess a WHERE a.subscriberId IN "
        + " (SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId AND s.deleted = 0) "
        + " AND a.accessTtl IS NULL "
    )
    List<Long> findRmaSubscribersContObjectIdsNoTtl(@Param("rmaSubscriberId") Long rmaSubscriberId);


    @Query("SELECT a.contObject FROM ContObjectAccess a "
        + " WHERE a.subscriberId = :subscriberId "
        + " AND a.contObjectId IN (:contObjectIds) AND a.contObject.deleted = 0"
        + " AND a.accessTtl IS NULL"
        + " ORDER BY a.contObject.fullAddress, a.contObject.id")
    List<ContObject> findContObjectsByIdsNoTtl(@Param("subscriberId") Long subscriberId,
                                          @Param("contObjectIds") List<Long> contObjectIds);


    @Query("SELECT a.subscriberId FROM ContObjectAccess a "
        + " WHERE a.subscriberId IN (SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId AND s.deleted = 0) "
        + " AND a.contObjectId = :contObjectId "
        + " AND a.accessTtl IS NULL "
    )
    List<Long> findRmaSubscriberIds(@Param("rmaSubscriberId") Long rmaSubscriberId,
                                    @Param("contObjectId") Long contObjectId);


    @Query("SELECT ra.contObject FROM ContObjectAccess ra WHERE ra.subscriberId = :rmaSubscriberId AND ra.contObjectId NOT IN "
        + " (SELECT sa.contObjectId FROM ContObjectAccess sa WHERE sa.subscriberId=:subscriberId) "
        + " ORDER BY ra.contObject.fullAddress, ra.contObject.id ")
    List<ContObject> findRmaAvailableContObjects(@Param("subscriberId") Long subscriberId,
                                                 @Param("rmaSubscriberId") Long rmaSubscriberId);



    @Query("SELECT a.contObjectId, COUNT(a.contObjectId) as cnt FROM ContObjectAccess a "
        + " WHERE a.subscriberId IN (SELECT s.id FROM Subscriber s WHERE s.parentSubscriberId = :parentSubscriberId AND s.isChild = true"
        + " AND s.deleted = 0 )"
        + " GROUP BY a.contObjectId")
    List<Object[]> findChildSubscrCabinetContObjectsStats(@Param("parentSubscriberId") Long parentSubscriberId);


    @Query("SELECT a.contObject FROM ContObjectAccess a WHERE a.contObjectId IN "
        + "( SELECT ci.contObject.id FROM SubscrContGroupItem ci INNER JOIN ci.contGroup cg "
        + " WHERE cg.id = :contGroupId ) AND" + " a.subscriberId = :subscriberId"
        + " ORDER BY a.contObject.name, a.contObject.id ")
    List<ContObject> findContObjectsBySubscriberGroup(@Param("subscriberId") long subscriberId,
                                                        @Param("contGroupId") long contGroupId);

    @Query("SELECT do FROM DeviceObject do "
        + " WHERE do.contObject.id IN (SELECT a.contObjectId FROM ContObjectAccess a "
        + " WHERE a.subscriberId = :subscriberId )"
        + " ORDER BY do.contObject.fullAddress, do.contObject.id ")
    List<DeviceObject> selectDeviceObjects(@Param("subscriberId") Long subscriberId);


//    @Query("SELECT a FROM #{#entityName} a WHERE a.accessTtlTz <= :ttl")
//    List<ContObjectAccess> findAllAccessTtlTZ(@Param("ttl") ZonedDateTime ttl);
//
//    @Query("SELECT a FROM #{#entityName} a WHERE a.revokeTz <= :ttl")
//    List<ContObjectAccess> findAllRevokeTZ(@Param("ttl") ZonedDateTime ttl);
//

}
