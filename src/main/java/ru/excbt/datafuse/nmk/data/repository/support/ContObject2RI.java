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
public interface ContObject2RI<T> {

    /**
     *
     * @param contObjectIds
     * @return
     */
    @Query("SELECT f FROM #{#entityName} f WHERE f.contObjectId in (:contObjectIds)")
    List<T> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObjectId = :contObjectId")
    List<T> findByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObjectId = :contObjectId")
    Optional<T> findOneByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query("SELECT f FROM #{#entityName} f WHERE f.contObjectId in (:contObjectIds)")
    List<T> findByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

}
