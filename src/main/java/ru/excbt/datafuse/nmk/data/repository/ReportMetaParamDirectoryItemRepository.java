package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamDirectoryItem;

public interface ReportMetaParamDirectoryItemRepository extends JpaRepository<ReportMetaParamDirectoryItem, Long> {

	@Query("SELECT i FROM ReportMetaParamDirectoryItem i WHERE i.paramDirectoryKeyname = UPPER(:paramDirectoryKeyname) ORDER BY i.itemOrder NULLS LAST ")
	public List<ReportMetaParamDirectoryItem> selectDirectoryItems(
			@Param("paramDirectoryKeyname") String paramDirectoryKeyname);

}
