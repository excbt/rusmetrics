package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.V_LastDataDateAggr;

import java.util.List;

@Repository
public interface V_LastDataDateAggrRepository extends JpaRepository<V_LastDataDateAggr, Long> {
    List<V_LastDataDateAggr> findByContZPointId (Long contZPointId);
}
