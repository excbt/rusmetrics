package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;

public interface ReportMetaParamSpecialRepository extends CrudRepository<ReportMetaParamSpecial, Long> {

	public List<ReportMetaParamSpecial> findByReportTypeKeyname(String reportTypeKeyname);
}
