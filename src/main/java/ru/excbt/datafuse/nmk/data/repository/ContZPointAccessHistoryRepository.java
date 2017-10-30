package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccessHistory;
import ru.excbt.datafuse.nmk.data.repository.support.ContZPointRI;

import java.util.List;

/**
 * Created by kovtonyk on 27.06.2017.
 */
public interface ContZPointAccessHistoryRepository extends JpaRepository<ContZPointAccessHistory, Long>, ContZPointRI<ContZPointAccessHistory> {

    List<ContZPointAccessHistory> findBySubscriberIdAndContZPointId(Long subscriberId, Long contZPointId);

}
