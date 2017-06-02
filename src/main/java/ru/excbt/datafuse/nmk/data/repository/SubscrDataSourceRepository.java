package ru.excbt.datafuse.nmk.data.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;

/**
 * Repository для SubscrDataSource
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
public interface SubscrDataSourceRepository extends CrudRepository<SubscrDataSource, Long> {

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	List<SubscrDataSource> findBySubscriberId(Long subscriberId);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT ds.id FROM SubscrDataSource ds WHERE ds.subscriberId = :subscriberId AND deleted = 0 ")
	List<Long> selectIdsBySubscriberId(@Param("subscriberId") Long subscriberId);

    /**
     *
     * @param ids
     * @return
     */
	@Query("SELECT ds FROM SubscrDataSource ds WHERE ds.id IN (:ids) AND deleted = 0 ")
	List<SubscrDataSource> selectBySubscrDataSourceIds(@Param("ids") Collection<Long> ids);


	Optional<SubscrDataSource> findByKeyname(String keyname);

}
