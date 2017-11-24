package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlanChart;

@Repository
public interface SubscrStPlanChartRepository extends JpaRepository<SubscrStPlanChart, Long> {
}
