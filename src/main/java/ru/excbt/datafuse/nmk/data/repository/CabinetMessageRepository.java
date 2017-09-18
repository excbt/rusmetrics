package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;

import java.util.List;


/**
 * Spring Data JPA repository for the CabinetMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CabinetMessageRepository extends JpaRepository<CabinetMessage,Long> {

    @Query("SELECT cm FROM CabinetMessage cm WHERE cm.fromPortalSubscriberId = :fromPortalSubscriberId " +
        " AND cm.messageType = :messageType ")
    public Page<CabinetMessage> findByFromPortalSubscriberId(@Param("fromPortalSubscriberId") Long fromSubscriberId,
                                                             @Param("messageType") String messageType,
                                                             Pageable pageable);

    @Query("SELECT cm FROM CabinetMessage cm WHERE cm.fromPortalSubscriberId = :fromPortalSubscriberId ")
    public Page<CabinetMessage> findByFromPortalSubscriberId(@Param("fromPortalSubscriberId") Long fromSubscriberId,
                                                             Pageable pageable);

    @Query("SELECT cm FROM CabinetMessage cm WHERE cm.toPortalSubscriberId IN (:toPortalSubscriberIds) " +
        " AND cm.messageType = :messageType")
    public Page<CabinetMessage> findByToSubscriberIds(@Param("toPortalSubscriberIds") List<Long> toSubscriberIds,
                                                      @Param("messageType") String messageType,
                                                      Pageable pageable);

    @Query("SELECT cm FROM CabinetMessage cm WHERE cm.masterId = :masterId  or  cm.id = :masterId " +
        " ORDER BY cm.creationDateTime")
    public List<CabinetMessage> findMessageChain(@Param("masterId") Long masterId);

}
