package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;

import java.util.List;

@Repository
public interface SubscrStPlanRepository extends JpaRepository<SubscrStPlan, Long> {

    List<SubscrStPlan> findBySubscriberId(Long subscriberId);

}
