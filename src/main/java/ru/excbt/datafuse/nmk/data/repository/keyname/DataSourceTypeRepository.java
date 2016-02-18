package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;

/**
 * Repository для DataSourceType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
public interface DataSourceTypeRepository extends JpaRepository<DataSourceType, String> {

}
