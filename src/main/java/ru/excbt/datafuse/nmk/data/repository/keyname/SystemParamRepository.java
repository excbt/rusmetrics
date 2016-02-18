package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.SystemParam;

/**
 * Repository для SystemParam
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.04.2015
 *
 */
public interface SystemParamRepository extends CrudRepository<SystemParam, String> {

}
