package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;

public interface ContObjectDaDataRepository extends CrudRepository<ContObjectDaData, Long> {

	public List<ContObjectDaData> findByContObjectId(Long contObjectId);
}
