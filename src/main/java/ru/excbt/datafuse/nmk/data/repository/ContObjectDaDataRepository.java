package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectModelRepository;

/**
 * Repository для ContObjectDaData
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.01.2015
 *
 */
public interface ContObjectDaDataRepository extends ContObjectModelRepository<ContObjectDaData> {

}
