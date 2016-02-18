package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;

/**
 * Repository для ReportMetaParamSpecial
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.06.2015
 *
 */
public interface ReportMetaParamSpecialRepository extends CrudRepository<ReportMetaParamSpecial, Long> {

	public List<ReportMetaParamSpecial> findByReportTypeKeyname(String reportTypeKeyname);
}
