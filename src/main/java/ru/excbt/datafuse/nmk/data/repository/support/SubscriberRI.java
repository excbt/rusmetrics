package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;

import java.util.List;

/**
 * Created by kovtonyk on 27.06.2017.
 */
public interface SubscriberRI<T> {

    @Query("SELECT f FROM #{#entityName} f WHERE f.subscriber.id = :subscriberId")
    List<T> findBySubscriberId(@Param("subscriberId") Long subscriberId);
}
