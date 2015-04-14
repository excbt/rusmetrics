package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

public interface ReportTypeRepository extends CrudRepository<ReportType, String> {
	public List<ReportType> findByKeynameIgnoreCase (String keyname);
}
