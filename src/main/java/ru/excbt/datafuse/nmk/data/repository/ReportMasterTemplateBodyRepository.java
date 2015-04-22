package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;

public interface ReportMasterTemplateBodyRepository extends
		CrudRepository<ReportMasterTemplateBody, Long> {

	
	public List<ReportMasterTemplateBody> findByReportTypeKey(
			ReportTypeKey reportType);
	
}
