package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.excbt.datafuse.nmk.data.model.HeatRadiatorType;

import java.util.List;

/**
 * Created by kovtonyk on 26.05.2017.
 */
public interface HeatRadiatorTypeRepository extends JpaRepository<HeatRadiatorType, Long> {

    @Query("select t from HeatRadiatorType t order by t.orderIdx")
    List<HeatRadiatorType> findAllSorted();

}
