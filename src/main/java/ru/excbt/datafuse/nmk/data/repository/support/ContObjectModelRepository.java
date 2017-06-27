/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
@NoRepositoryBean
public interface ContObjectModelRepository<T> extends JpaRepository<T, Long> {

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObject.id = :contObjectId")
    List<T> findByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query ("SELECT f FROM #{#entityName} f WHERE f.contObject.id = :contObjectId")
    Optional<T> findOneByContObjectId(@Param("contObjectId") Long contObjectId);

    @Query("SELECT f FROM #{#entityName} f WHERE f.contObject.id in (:contObjectIds)")
    List<T> findByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

}
