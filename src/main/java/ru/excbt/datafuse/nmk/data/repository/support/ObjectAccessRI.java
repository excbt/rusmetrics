package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by kovtonyk on 10.07.2017.
 */
public interface ObjectAccessRI<T> {

    @Query("SELECT a FROM #{#entityName} a WHERE a.accessTtlTz <= :ttl")
    List<T> findAllAccessTtlTZ(@Param("ttl") ZonedDateTime ttl);

    @Query("SELECT a FROM #{#entityName} a WHERE a.revokeTz <= :ttl")
    List<T> findAllRevokeTZ(@Param("ttl") ZonedDateTime ttl);


}
