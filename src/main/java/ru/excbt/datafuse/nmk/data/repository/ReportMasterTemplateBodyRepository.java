package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public interface ReportMasterTemplateBodyRepository extends
		CrudRepository<ReportMasterTemplateBody, Long> {

	
	public List<ReportMasterTemplateBody> findByReportTypeKey(
			ReportTypeKey reportTypeKey);
	
}
