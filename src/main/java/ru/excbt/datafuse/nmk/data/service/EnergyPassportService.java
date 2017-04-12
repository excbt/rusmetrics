package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.energypassport.EPSectionValueUtil;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;

import java.time.LocalDate;
import java.util.Arrays;
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
    private final EnergyPassportDataRepository energyPassportDataRepository;


    public EnergyPassportService(EnergyPassportTemplateRepository energyPassportTemplateRepository,
                                 EnergyPassportRepository energyPassportRepository,
                                 EnergyPassportDataRepository energyPassportDataRepository) {
        this.energyPassportTemplateRepository = energyPassportTemplateRepository;
        this.energyPassportRepository = energyPassportRepository;
        this.energyPassportDataRepository = energyPassportDataRepository;
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

    @Transactional
    public EnergyPassportDataDTO savePassportData(EnergyPassportDataDTO energyPassportDataDTO) {
        EnergyPassport passport = energyPassportRepository.findOne(energyPassportDataDTO.getPassportId());
        if (passport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class,energyPassportDataDTO.getPassportId());
        }

        Optional<EnergyPassportSection> section = passport.getSection(energyPassportDataDTO.getSectionKey());
        if (!section.isPresent()) {
            DBExceptionUtils.entityNotFoundException(EnergyPassportSection.class, energyPassportDataDTO.getSectionKey(), "sectionKey");
        }

        EnergyPassportData passportData;
        if (energyPassportDataDTO.getId() != null) {
            passportData = energyPassportDataRepository.findOne(energyPassportDataDTO.getId());
        } else {
            passportData = new EnergyPassportData();
            passportData.setPassport(passport);
        }

        passportData.updateFromDTO(energyPassportDataDTO);
        passportData.setSectionId(section.get().getId());
        energyPassportDataRepository.save(passportData);

        return passportData.getDTO();
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> findPassportData(Long passportId) {
        List<EnergyPassportData> passportDataList = energyPassportDataRepository.findByPassportId(passportId);

        if (passportDataList.isEmpty()) {
            return extractEnergyPassportData(passportId);
        }

        return passportDataList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> findPassportData(Long passportId, Long sectionId) {
        EnergyPassport energyPassport = energyPassportRepository.findOne(passportId);
        if (energyPassport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class, passportId);
        }
        Optional<EnergyPassportSection> section = energyPassport.getSections().stream().filter(i -> i.getId().equals(sectionId)).findFirst();
        if (!section.isPresent()) {
            DBExceptionUtils.entityNotFoundException(EnergyPassportSection.class, sectionId);
        }
        List<EnergyPassportData> passportDataList = energyPassportDataRepository.findByPassportIdAndSectionKey(passportId, section.get().getSectionKey());

        if (passportDataList.isEmpty()) {
            return Arrays.asList(extractEnergyPassportData(section.get()));
        }

        return passportDataList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
    }

    public boolean validatePassportData (EnergyPassportDataDTO dto) {
        if (dto == null)
            return false;

        return EPSectionValueUtil.validateJson(dto.getSectionDataJson());
    }

    /*

     */
    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> extractEnergyPassportData(Long passportId) {
        EnergyPassport energyPassport = energyPassportRepository.findOne(passportId);
        if (energyPassport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class, passportId);
        }
        return extractEnergyPassportData(energyPassport);
    }

    /*

     */
    private List<EnergyPassportDataDTO> extractEnergyPassportData(EnergyPassport energyPassport) {
        List<EnergyPassportDataDTO> result =
            energyPassport.getSections().stream().map((i) -> {
                EnergyPassportDataDTO dto = new EnergyPassportDataDTO();
                dto.setPassportId(energyPassport.getId());
                dto.setSectionKey(i.getSectionKey());
                EPSectionValueUtil.extractValues(i.getSectionJson()).ifPresent((json) -> dto.setSectionDataJson(json));
                return dto;
            }).collect(Collectors.toList());

        return result;
    }

    private EnergyPassportDataDTO extractEnergyPassportData(EnergyPassportSection energyPassportSection) {
        EnergyPassportDataDTO dto = new EnergyPassportDataDTO();
        dto.setPassportId(energyPassportSection.getPassport().getId());
        dto.setSectionKey(energyPassportSection.getSectionKey());
        EPSectionValueUtil.extractValues(energyPassportSection.getSectionJson()).ifPresent((json) -> dto.setSectionDataJson(json));
        return dto;
    }

}
