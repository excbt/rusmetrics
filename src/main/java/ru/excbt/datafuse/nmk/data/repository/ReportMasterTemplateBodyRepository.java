package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;

/**
 * Repository для ReportMasterTemplateBody
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
public interface ReportMasterTemplateBodyRepository extends CrudRepository<ReportMasterTemplateBody, Long> {

	public List<ReportMasterTemplateBody> findByReportTypeKeyname(String reportTypeKeyname);

}
