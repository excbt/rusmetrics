package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportTypeContServiceType;

public interface ReportTypeContServiceTypeRepository extends JpaRepository<ReportTypeContServiceType, Long> {

	/**
	 * 
	 * @param reportTypeKeyname
	 * @return
	 */
	@Query("SELECT st FROM ReportTypeContServiceType st WHERE st.reportTypeKeyname = :reportTypeKeyname")
	public List<ReportTypeContServiceType> selectContServiceTypes(@Param("reportTypeKeyname") String reportTypeKeyname);

}
