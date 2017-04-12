package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
