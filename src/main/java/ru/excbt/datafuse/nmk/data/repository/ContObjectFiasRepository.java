package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContObjectFias;

/**
 * Repository для ContObjectFias
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public interface ContObjectFiasRepository extends CrudRepository<ContObjectFias, Long> {

	public List<ContObjectFias> findByContObjectId(Long contObjectId);
}
