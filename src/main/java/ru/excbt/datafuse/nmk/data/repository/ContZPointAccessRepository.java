package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

}
