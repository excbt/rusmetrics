package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;

import java.util.List;

/**
 * Created by kovtonyk on 31.03.2017.
 */
@Repository
public interface EnergyPassportSectionTemplateRepository extends JpaRepository<EnergyPassportSectionTemplate,Long> {

    List<EnergyPassportSectionTemplate> findByPassportTemplateId(Long passportTemplateId);
}
