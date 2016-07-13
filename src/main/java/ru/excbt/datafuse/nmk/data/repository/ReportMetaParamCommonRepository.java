package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;

public interface ReportMetaParamCommonRepository extends JpaRepository<ReportMetaParamCommon, String> {

	@Query("SELECT pc FROM ReportMetaParamCommon pc WHERE pc.reportTypeKeyname = :keyname")
	public List<ReportMetaParamCommon> selectByReportTypeKeyname(@Param("keyname") String keyname);
}
