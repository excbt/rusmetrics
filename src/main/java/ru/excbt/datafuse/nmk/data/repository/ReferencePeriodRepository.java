package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;

public interface ReferencePeriodRepository extends
		CrudRepository<ReferencePeriod, Long> {

	public List<ReferencePeriod> findBySubscriberIdAndContZPointId(long subscriberId, long contZPointId);
}
