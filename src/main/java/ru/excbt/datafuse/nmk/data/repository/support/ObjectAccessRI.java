package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by kovtonyk on 10.07.2017.
 */
public interface ObjectAccessRI<T> {

    @Query("SELECT a FROM #{#entityName} a WHERE a.subscriber.id = :subscriberId AND " +
        " (a.accessTtlTz IS NULL OR a.accessTtlTz >= :accessTtlTz )")
    List<T> findAllAccess(@Param("subscriberId") Long subscriberId, @Param("accessTtlTz") ZonedDateTime zonedDateTime);


}
