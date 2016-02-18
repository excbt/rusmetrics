package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContGroup;

/**
 * Repository для ContGroup
 * 
 * @author S.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */
public interface ContGroupRepository extends CrudRepository<ContGroup, Long> {

	public List<ContGroup> findBySubscriberId(long subscriberId);

}