package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectIdModelRepository;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectModelRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для ContObjectFias
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public interface ContObjectFiasRepository extends ContObjectModelRepository<ContObjectFias> {


}
