package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContGroup;

public interface ContGroupRepository extends CrudRepository<ContGroup, Long> {

	public List<ContGroup> findBySubscriberId(long subscriberId);

}