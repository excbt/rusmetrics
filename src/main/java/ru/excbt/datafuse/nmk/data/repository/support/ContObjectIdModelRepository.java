/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository.support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
@NoRepositoryBean
public interface ContObjectIdModelRepository<T> extends JpaRepository<T, Long> {

	List<T> findByContObjectId(Long contObjectId);

    Optional<T> findOneByContObjectId(Long contObjectId);

	/**
	 *
	 * @param contObjectIds
	 * @return
	 */
	@Query("SELECT f FROM #{#entityName} f WHERE f.contObjectId in (:contObjectIds)")
	List<T> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);
}
