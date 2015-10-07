package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;

public interface DataSourceTypeRepository extends JpaRepository<DataSourceType, String> {

}
