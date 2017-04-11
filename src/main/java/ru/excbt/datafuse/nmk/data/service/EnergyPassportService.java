package ru.excbt.datafuse.nmk.data.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Service
public class EnergyPassportService {

    private final EnergyPassportTemplateRepository energyPassportTemplateRepository;
    private final EnergyPassportRepository energyPassportRepository;
    private final ModelMapper modelMapper;


    public EnergyPassportService(ModelMapper modelMapper,
                                 EnergyPassportRepository energyPassportRepository,
                                 EnergyPassportTemplateRepository energyPassportTemplateRepository) {
        this.modelMapper = modelMapper;
        this.energyPassportRepository = energyPassportRepository;
        this.energyPassportTemplateRepository = energyPassportTemplateRepository;
    }


    @Transactional(value = TxConst.TX_DEFAULT)
    public EnergyPassportDTO createPassport(String templateKeyname, EnergyPassportVM energyPassportVM, Subscriber subscriber) {
        Optional<EnergyPassportTemplate> energyPassportTemplate = energyPassportTemplateRepository.findByKeyname(templateKeyname);
        if (!energyPassportTemplate.isPresent()) {
            DBExceptionUtils.entityNotFoundException(EnergyPassportTemplate.class, templateKeyname, true);
        }
        EnergyPassport energyPassport = new EnergyPassport();
        energyPassport.setPassportTemplate(energyPassportTemplate.get());
        energyPassport.setPassportDate(LocalDate.now());
        energyPassport.setSubscriber(subscriber);

        for (EnergyPassportSectionTemplate sectionTemplate : energyPassportTemplate.get().getSectionTemplates()) {
            EnergyPassportSection energyPassportSection = new EnergyPassportSection();
            energyPassportSection.setSectionKey(sectionTemplate.getSectionKey());
            energyPassportSection.setSectionJson(sectionTemplate.getSectionJson());
            energyPassport.addSection(energyPassportSection);
        }
        energyPassportRepository.save(energyPassport);

        EnergyPassportDTO energyPassportDTO = energyPassport.getDTO();

        return energyPassportDTO;
    }

}
