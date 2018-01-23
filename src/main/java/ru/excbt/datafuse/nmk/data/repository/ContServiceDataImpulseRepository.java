/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.repository.support.ConsuptionRepositoryRI;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
public interface ContServiceDataImpulseRepository extends JpaRepository<ContServiceDataImpulse, Long>,
    ConsuptionRepositoryRI<ContServiceDataImpulse> {

	/**
	 *
	 * @param contZPointId
	 * @param timeDetailType
	 * @param beginDate
	 * @param endDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataImpulse d "
			+ " WHERE d.contZPointId = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType AND d.deleted = 0 ")
	public Page<ContServiceDataImpulse> selectByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String timeDetailType, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate, Pageable pageable);

}
