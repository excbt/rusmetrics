package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportPeriod;

public interface ReportPeriodRepository extends CrudRepository<ReportPeriod, String> {

	public List<ReportPeriod> findByKeynameIgnoreCase (String keyname);
}
