package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportData;

import java.util.List;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Repository
public interface EnergyPassportDataRepository extends JpaRepository<EnergyPassportData, Long> {

    List<EnergyPassportData> findByPassportId(Long passportId);

    List<EnergyPassportData> findByPassportIdAndSectionKey(Long passportId, String sectionKey);

    @Query("SELECT d FROM EnergyPassportData d WHERE d.passport.id = :passportId AND d.sectionKey = :sectionKey " +
        " AND d.sectionEntryId = :sectionEntryId")
    List<EnergyPassportData> findByPassportIdAndSectionEntry(@Param("passportId") Long passportId,
                                                             @Param("sectionKey") String sectionKey,
                                                             @Param("sectionEntryId") Long sectionEntryId);

}
