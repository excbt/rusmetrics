package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;

import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 28.06.2017.
 */
public interface ContObjectAccessRepository extends JpaRepository<ContObjectAccess, ContObjectAccess.PK> {

    @Query("SELECT distinct a.subscriber.id FROM ContObjectAccess a WHERE a.accessTtl IS NULL")
    List<Long> findAllSubscriberIds();

    @Query("SELECT distinct a.contObject.id FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId AND a.accessTtl IS NULL")
    List<Long> findContObjectIdsBySubscriber (@Param("subscriberId") Long subscriberId);

    @Query("SELECT distinct a.contObject.id FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId")
    List<Long> findContObjectIdsBySubscriberTTL (@Param("subscriberId") Long subscriberId);

    @Query("SELECT distinct a.subscriberId FROM ContObjectAccess a WHERE a.contObjectId = :contObjectId AND a.accessTtl IS NULL")
    List<Long> findSubscriberByContObject(@Param("contObjectId") Long contObjectId);

    @Query("SELECT count(a) FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId AND a.contObjectId = :contObjectId AND a.accessTtl IS NULL")
    Optional<Long> findByPK(@Param("subscriberId") Long subscriberId, @Param("contObjectId") Long contObjectId);


    List<ContObjectAccess> findBySubscriberId (Long subscriberId);


    /// Queries for ContObject

    @Query("SELECT a.contObject FROM ContObjectAccess a WHERE a.subscriberId = :subscriberId")
    List<ContObject> findContObjectBySubscriberId (@Param("subscriberId") Long subscriberId);

}
