package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectRI;

/**
 * Repository для ContObjectFias
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public interface ContObjectFiasRepository extends JpaRepository<ContObjectFias, Long>, ContObjectRI<ContObjectFias> {


}
