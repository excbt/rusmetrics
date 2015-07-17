package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;

public interface ReportTypeRepository extends JpaRepository<ReportType, String> {
	public List<ReportType> findByKeynameIgnoreCase (String keyname);
}
