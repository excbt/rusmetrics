package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.StPlanChartTemplate;

@Repository
public interface StPlanTemplateItemRepository extends JpaRepository<StPlanChartTemplate, StPlanChartTemplate.PK> {

}
