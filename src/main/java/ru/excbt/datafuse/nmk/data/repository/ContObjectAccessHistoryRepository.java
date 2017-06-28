package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccessHistory;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccessHistory;

import java.util.List;

/**
 * Created by kovtonyk on 28.06.2017.
 */
public interface ContObjectAccessHistoryRepository extends JpaRepository<ContObjectAccessHistory, Long> {

    List<ContObjectAccessHistory> findBySubscriberIdAndContObjectId(Long subscriberId, Long contZPointId);

}
