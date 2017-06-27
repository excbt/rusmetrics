/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
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
public interface ContObjectRI<T> {

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObject.id = :contObjectId")
    List<T> findByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObject.id = :contObjectId")
    Optional<T> findOneByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query("SELECT f FROM #{#entityName} f WHERE f.contObject.id in (:contObjectIds)")
    List<T> findByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

}
