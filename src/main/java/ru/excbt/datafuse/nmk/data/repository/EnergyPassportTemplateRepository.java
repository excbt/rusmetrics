package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;

import java.util.Optional;

/**
 * Created by kovtonyk on 30.03.2017.
 */
public interface EnergyPassportTemplateRepository extends JpaRepository<EnergyPassportTemplate, Long> {

}
