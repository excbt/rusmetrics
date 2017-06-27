package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectRI;

/**
 * Repository для ContObjectDaData
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.01.2015
 *
 */
public interface ContObjectDaDataRepository extends JpaRepository<ContObjectDaData, Long>, ContObjectRI<ContObjectDaData> {

}
