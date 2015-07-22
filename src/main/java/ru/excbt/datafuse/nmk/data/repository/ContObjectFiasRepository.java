package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContObjectFias;

public interface ContObjectFiasRepository extends
		CrudRepository<ContObjectFias, Long> {

	public List<ContObjectFias> findByContObjectId(Long contObjectId);
}
