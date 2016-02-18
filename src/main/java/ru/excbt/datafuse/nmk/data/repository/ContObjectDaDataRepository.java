package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;

/**
 * Repository для ContObjectDaData
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.01.2015
 *
 */
public interface ContObjectDaDataRepository extends CrudRepository<ContObjectDaData, Long> {

	public List<ContObjectDaData> findByContObjectId(Long contObjectId);
}
