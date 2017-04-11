package ru.excbt.datafuse.nmk.data.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public EnergyPassportDTO createPassport(String templateKeyname, Subscriber subscriber) {
        return createPassport(templateKeyname, null, subscriber);
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

        if (energyPassportVM != null) {
            energyPassport.setPassportName(energyPassportVM.getPassportName());
            energyPassport.setDescription(energyPassportVM.getDescription());
            if (energyPassportVM.getOrganizationId() != null) {
                energyPassport.setOrganization(new Organization().id(energyPassportVM.getOrganizationId()));
            }
        }

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


    @Transactional(value = TxConst.TX_DEFAULT)
    public EnergyPassportDTO find(Long id) {
        EnergyPassport energyPassport = energyPassportRepository.findOne(id);
        return energyPassport != null ? energyPassport.getDTO() : null;
    }

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<EnergyPassportDTO> findBySubscriberId(Long subscriberId) {
        return energyPassportRepository.findBySubscriberId(subscriberId).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
    }

    @Transactional(value = TxConst.TX_DEFAULT)
    public void delete(Long id, Subscriber subscriber) {
        EnergyPassport energyPassport = energyPassportRepository.findOne(id);
        if (energyPassport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class, id);
        }

        if (!energyPassport.getSubscriber().equals(subscriber)) {
            DBExceptionUtils.accessDeniedException(Subscriber.class, subscriber.getId());
        }

        energyPassport.setDeleted(1);
        energyPassportRepository.save(energyPassport);
    }

}
