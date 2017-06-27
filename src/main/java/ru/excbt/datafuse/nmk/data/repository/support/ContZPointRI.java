/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
public interface ContZPointRI<T> {

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contZPoint.id = :contZPointId")
    List<T> findByContZPointId(@Param("contZPointId") Long contZPointId);

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contZPoint.id = :contZPointId")
    Optional<T> findOneByContZPointId(@Param("contZPointId") Long contZPointId);

    @Query("SELECT f FROM #{#entityName} f WHERE f.contZPoint.id in (:contZPointIds)")
    List<T> findByContZPointIds(@Param("contZPointIds") List<Long> contZPointIds);

}
