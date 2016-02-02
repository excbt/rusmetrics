package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

/**
 * Repository для ReportType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface ReportTypeRepository extends JpaRepository<ReportType, String> {
	public List<ReportType> findByKeynameIgnoreCase(String keyname);
}
