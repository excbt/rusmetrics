package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataValueRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.energypassport.EPSectionValueUtil;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Service
public class EnergyPassportService {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportService.class);

    private final EnergyPassportTemplateRepository energyPassportTemplateRepository;
    private final EnergyPassportRepository energyPassportRepository;
    private final EnergyPassportDataRepository energyPassportDataRepository;
    private final EnergyPassportDataValueRepository energyPassportDataValueRepository;


    public EnergyPassportService(EnergyPassportTemplateRepository energyPassportTemplateRepository,
                                 EnergyPassportRepository energyPassportRepository,
                                 EnergyPassportDataRepository energyPassportDataRepository,
                                 EnergyPassportDataValueRepository energyPassportDataValueRepository) {
        this.energyPassportTemplateRepository = energyPassportTemplateRepository;
        this.energyPassportRepository = energyPassportRepository;
        this.energyPassportDataRepository = energyPassportDataRepository;
        this.energyPassportDataValueRepository = energyPassportDataValueRepository;
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

        energyPassportTemplate.get().getSectionTemplates().forEach((s) -> energyPassportSectionCreator.apply(energyPassport,s));

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

        Optional<EnergyPassportSection> section = passport.searchSection(energyPassportDataDTO.getSectionKey());
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

        updateDataValue(energyPassportDataDTO);

        return passportData.getDTO();
    }

    /**
     *
     * @param passportId
     * @return
     */
    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> findPassportData(Long passportId) {

        EnergyPassport energyPassport = energyPassportRepository.findOne(passportId);
        if (energyPassport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class, passportId);
        }

        List<EnergyPassportData> passportDataList = energyPassportDataRepository.findByPassportId(passportId);

        List<EnergyPassportDataDTO> passportDataDTOList = passportDataList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());

        if (passportDataDTOList.isEmpty()) {
            return extractEnergyPassportData(passportId);
        } else if (passportDataDTOList.size() != energyPassport.getSections().size()) {
            return enhanceEnergyPassportData(passportDataDTOList);
        }
        return passportDataDTOList;
    }

    /**
     *
     * @param passportId
     * @param sectionId
     * @return
     */
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

    /**
     *
     * @param src existing list of passport data
     * @return existing list of passport data + data of unsaved sections
     */
    private List<EnergyPassportDataDTO> enhanceEnergyPassportData(List<EnergyPassportDataDTO> src){
        List<EnergyPassportDataDTO> result = new ArrayList<>(src);

        Long passportId = src.stream().mapToLong(EnergyPassportDataDTO::getPassportId).findFirst().getAsLong();

        EnergyPassport pass = energyPassportRepository.findOne(passportId);

        pass.getSections().forEach((i) -> {
            EnergyPassportDataDTO templateData = extractEnergyPassportData(i);
            boolean exists = src.stream().filter((j) -> j.getSectionKey() != null && j.getSectionKey().equals(i.getSectionKey()))
                .findFirst().isPresent();
            if (!exists) {
                result.add(templateData);
            }
        });


        return result;
    }

    private void updateDataValue(EnergyPassportDataDTO energyPassportDataDTO) {
        energyPassportDataValueRepository.deleteByPassportId(energyPassportDataDTO.getPassportId());
        List<EnergyPassportDataValue> dataValues = new ArrayList<>();
        EPSectionValueUtil.jsonToValueCellsDTO(energyPassportDataDTO.getSectionDataJson())
            .ifPresent((i) ->
                i.getElements().forEach((e) -> {
                    EnergyPassportDataId id = EnergyPassportDataId.builder()
                        .passport(new EnergyPassport().id(energyPassportDataDTO.getPassportId()))
                        .sectionKey(energyPassportDataDTO.getSectionKey())
                        .complexIdx(e.get_complexIdx()).build();
                    EnergyPassportDataValue value = new EnergyPassportDataValue();
                    value.setEnergyPassportDataId(id);
                    value.setDataValue(e.valueAsString());
                    value.setDataType(e.dataType());
                    dataValues.add(value);
                })
            );

        energyPassportDataValueRepository.save(dataValues);
    }

    /**
     *
     * @param passportId
     * @return
     */
    private EnergyPassport findPassportChecked(Long passportId) {
        EnergyPassport energyPassport = energyPassportRepository.findOne(passportId);
        if (energyPassport == null) {
            DBExceptionUtils.entityNotFoundException(EnergyPassport.class, passportId);
        }
        return energyPassport;
    }

    /**
     * Creates section from section templates
     */
    private BiFunction<EnergyPassport, EnergyPassportSectionTemplate, EnergyPassportSection> energyPassportSectionCreator = (passport, sectionTemplate) -> {
        log.debug("Adding new section to EnergyPassport(id={}), sectionKey: {}", passport.getId(), sectionTemplate.getSectionKey());
        EnergyPassportSection energyPassportSection = new EnergyPassportSection();
        energyPassportSection.setSectionKey(sectionTemplate.getSectionKey());
        energyPassportSection.setSectionJson(sectionTemplate.getSectionJson());
        energyPassportSection.setSectionOrder(sectionTemplate.getSectionOrder());
        passport.addSection(energyPassportSection);
        return energyPassportSection;
    };

    /**
     * Updates section from section templates
     */
    private BiFunction<EnergyPassportSection, EnergyPassportSectionTemplate, EnergyPassportSection> energyPassportSectionUpdater = (section, sectionTemplate) -> {
        section.updateFromTemplate(sectionTemplate);
        return section;
    };

    /**
     *
     * @param passportId
     */
    @Transactional
    protected void updateEnergyPassportFromTemplate(Long passportId) {
        EnergyPassport energyPassport = findPassportChecked(passportId);
        EnergyPassportTemplate template = energyPassport.getPassportTemplate();
        template.getSectionTemplates().forEach((t) ->
            energyPassport.searchSection(t.getSectionKey())
                .map((s) -> energyPassportSectionUpdater.apply(s, t))
                .orElseGet(() -> energyPassportSectionCreator.apply(energyPassport, t))
        );
        energyPassportRepository.save(energyPassport);
    }


    @Transactional
    public void updateExistingEnergyPassportsFromTemplate(Long passportTemplateId) {
        List<EnergyPassport> energyPassports = energyPassportRepository.findByPassportTemplateId(passportTemplateId);
        energyPassports.forEach((p) -> {
            log.debug("Found EnergyPassport(id={})", p.getId());
            EnergyPassportTemplate template = p.getPassportTemplate();
            template.getSectionTemplates().forEach((t) ->
                p.searchSection(t.getSectionKey())
                    .map((s) -> energyPassportSectionUpdater.apply(s, t))
                    .orElseGet(() -> energyPassportSectionCreator.apply(p,t)));
        });
        ;
        energyPassportRepository.save(energyPassports);
    }

}
