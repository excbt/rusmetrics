package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;

public interface ReferencePeriodRepository extends
		PagingAndSortingRepository<ReferencePeriod, Long> {

	public List<ReferencePeriod> findBySubscriberIdAndContZPointId(
			long subscriberId, long contZPointId);

	@Query("SELECT rp FROM ReferencePeriod rp "
			+ " WHERE rp.contZPoint.id = :contZPointId AND rp.subscriber.id = :subscriberId "
			+ " ORDER BY rp.id desc")
	public List<ReferencePeriod> selectLastReferencePeriod(@Param("subscriberId") long subscriberId,
			@Param("contZPointId") long contZPointId, Pageable pageable);
}
