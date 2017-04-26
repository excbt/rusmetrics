package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionEntry;

import java.util.List;

/**
 * Created by kovtonyk on 17.04.2017.
 */
public interface EnergyPassportSectionEntryRepository extends JpaRepository<EnergyPassportSectionEntry,Long> {

    @Query(value = "SELECT e FROM EnergyPassportSectionEntry e WHERE e.section.id = :sectionId " +
        " AND e.deleted = 0 " +
        " ORDER BY e.entryOrder ASC NULLS LAST, e.lastModifiedDate DESC NULLS LAST")
    List<EnergyPassportSectionEntry> findBySectionId(@Param("sectionId") Long sectionId);

}
