package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportDataId;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportDataValue;

import java.util.List;

/**
 * Created by kovtonyk on 12.04.2017.
 */
public interface EnergyPassportDataValueRepository extends JpaRepository<EnergyPassportDataValue,EnergyPassportDataId> {

    @Modifying
    @Query("DELETE FROM EnergyPassportDataValue v WHERE v.energyPassportDataId.passport.id = :passportId")
    void deleteByPassportId(@Param("passportId") Long passportId);

    @Query("SELECT v FROM EnergyPassportDataValue v WHERE v.energyPassportDataId.passport.id = :passportId")
    List<EnergyPassportDataValue> findByPassportId(@Param("passportId") Long passportId);

    @Query("SELECT v FROM EnergyPassportDataValue v WHERE v.energyPassportDataId.passport.id = :passportId " +
        " AND v.energyPassportDataId.sectionKey = :sectionKey")
    List<EnergyPassportDataValue> findByPassportIdAndSectionKey(@Param("passportId") Long passportId,
                                                                @Param("sectionKey") String sectionKey);

    @Query("SELECT v FROM EnergyPassportDataValue v WHERE v.energyPassportDataId.passport.id = :passportId " +
        " AND v.energyPassportDataId.sectionKey = :sectionKey " +
        " AND v.energyPassportDataId.sectionEntryId = :sectionEntryId")
    List<EnergyPassportDataValue> findByPassportIdAndSectionKey(@Param("passportId") Long passportId,
                                                                @Param("sectionKey") String sectionKey,
                                                                @Param("sectionEntryId") Long sectionEntryId);

}
