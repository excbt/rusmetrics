package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public interface ReportParamsetRepository extends CrudRepository<ReportParamset, Long> {

	public List<ReportParamset> findByReportTemplateId(long reportTemplateId);
	
}
