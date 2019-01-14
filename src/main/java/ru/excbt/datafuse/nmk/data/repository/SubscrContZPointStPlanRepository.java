package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.SubscrContZPointStPlan;

import java.util.List;

@Repository
public interface SubscrContZPointStPlanRepository extends JpaRepository<SubscrContZPointStPlan, Long> {

    @Query("SELECT s FROM SubscrContZPointStPlan s WHERE s.subscriberId = :subscriberId AND s.contZPoint.id = :contZPointId")
    List<SubscrContZPointStPlan> findBySubscriberAndContZPoint(@Param("contZPointId")  Long contZPointId, @Param("subscriberId") Long subscriberId);

}
