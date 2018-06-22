package ru.excbt.datafuse.nmk.data.service;


import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.energypassport.EPConstants.DocumentModes;
import ru.excbt.datafuse.nmk.data.energypassport.EPSectionValueUtil;
import ru.excbt.datafuse.nmk.data.energypassport.EPSectionValueUtil.JsonVars;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport_X;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionEntryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportShortDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Service
public class EnergyPassportService {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportService.class);

    private final EnergyPassportTemplateRepository passportTemplateRepository;
    private final EnergyPassportRepository passportRepository;
    private final EnergyPassportDataRepository passportDataRepository;
    private final EnergyPassportDataValueRepository energyPassportDataValueRepository;
    private final EnergyPassportSectionRepository passportSectionRepository;
    private final EnergyPassportSectionEntryRepository sectionEntryRepository;
    /**
     * Creates section from section templates
     */
    private BiFunction<EnergyPassport, EnergyPassportSectionTemplate, EnergyPassportSection> energyPassportSectionCreator = (passport, sectionTemplate) -> {
        log.debug("Adding new section to EnergyPassport(id={}), sectionKey: {}", passport.getId(), sectionTemplate.getSectionKey());
        EnergyPassportSection energyPassportSection = new EnergyPassportSection();
        energyPassportSection.setSectionKey(sectionTemplate.getSectionKey());
        energyPassportSection.setSectionJson(sectionTemplate.getSectionJson());
        energyPassportSection.setSectionOrder(sectionTemplate.getSectionOrder());
        energyPassportSection.setHasEntries(sectionTemplate.isHasEntries());
        passport.addSection(energyPassportSection);
        return energyPassportSection;
    };
    /**
     * Updates section from section templates
     */
    private BiFunction<EnergyPassportSection, EnergyPassportSectionTemplate, EnergyPassportSection> energyPassportSectionUpdater = (section, sectionTemplate) -> {
        log.debug("Updating passport section (id:{}, sectionKey:{}) from sectionTemplate (id:{})",
            section.getId(),
            section.getSectionKey(),
            sectionTemplate.getId());
        section.updateFromTemplate(sectionTemplate);
        return section;
    };


    public EnergyPassportService(EnergyPassportTemplateRepository energyPassportTemplateRepository,
                                 EnergyPassportRepository energyPassportRepository,
                                 EnergyPassportDataRepository energyPassportDataRepository,
                                 EnergyPassportDataValueRepository energyPassportDataValueRepository,
                                 EnergyPassportSectionRepository energyPassportSectionRepository,
                                 EnergyPassportSectionEntryRepository energyPassportSectionEntryRepository) {
        this.passportTemplateRepository = energyPassportTemplateRepository;
        this.passportRepository = energyPassportRepository;
        this.passportDataRepository = energyPassportDataRepository;
        this.energyPassportDataValueRepository = energyPassportDataValueRepository;
        this.passportSectionRepository = energyPassportSectionRepository;
        this.sectionEntryRepository = energyPassportSectionEntryRepository;
    }

    @Transactional
    public EnergyPassportDTO createPassport(String templateKeyname, Subscriber subscriber) {
        return createPassport(templateKeyname, null, subscriber);
    }

    @Transactional
    public EnergyPassportDTO createPassport(String templateKeyname, EnergyPassportVM energyPassportVM, Subscriber subscriber) {
        Optional<EnergyPassportTemplate> energyPassportTemplate = passportTemplateRepository.findByKeyname(templateKeyname);
        if (!energyPassportTemplate.isPresent()) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportTemplate.class, templateKeyname, true);
        }
        EnergyPassport energyPassport = new EnergyPassport();
        if (energyPassportTemplate.isPresent()) {
            energyPassport.setPassportTemplate(energyPassportTemplate.get());
        }
        energyPassport.setPassportDate(LocalDate.now());
        energyPassport.setSubscriber(subscriber);
        energyPassport.setDocumentMode(energyPassportTemplate.get().getDocumentMode());

        if (energyPassportVM != null) {
            energyPassport.setPassportName(energyPassportVM.getPassportName());
            energyPassport.setDescription(energyPassportVM.getDescription());
            if (energyPassportVM.getPassportDate() != null){
                energyPassport.setPassportDate(energyPassportVM.getPassportDate());
            }
            if (energyPassportVM.getOrganizationId() != null) {
                energyPassport.setOrganization(new Organization().id(energyPassportVM.getOrganizationId()));
            }
        }

        energyPassportTemplate.get().getSectionTemplates().forEach((s) -> energyPassportSectionCreator.apply(energyPassport, s));

        passportRepository.save(energyPassport);

        EnergyPassportDTO energyPassportDTO = energyPassport.getDTO();

        return setupPassportVars(energyPassportDTO);
    }

    /**
     *
     * @param energyPassportVM
     * @param subscriber
     * @return
     */
    @Transactional
    public EnergyPassportDTO updatePassport(EnergyPassportVM energyPassportVM, Subscriber subscriber) {
        EnergyPassport energyPassport = passportRepository.findOne(energyPassportVM.getId());
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, energyPassportVM.getId());
        }
        energyPassport.setPassportDate(energyPassportVM.getPassportDate());
        energyPassport.setPassportName(energyPassportVM.getPassportName());
        energyPassport.setDescription(energyPassportVM.getDescription());

        passportRepository.save(energyPassport);

        EnergyPassportDTO energyPassportDTO = energyPassport.getDTO();

        return setupPassportVars(energyPassportDTO);
    }

    /**
     *
     * @param energyPassportVM
     * @param subscriber
     * @return
     */
    @Transactional
    public EnergyPassportShortDTO updatePassportShort(EnergyPassportVM energyPassportVM, Subscriber subscriber) {
        EnergyPassport energyPassport = passportRepository.findOne(energyPassportVM.getId());
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, energyPassportVM.getId());
        }
        energyPassport.setPassportDate(energyPassportVM.getPassportDate());
        energyPassport.setPassportName(energyPassportVM.getPassportName());
        energyPassport.setDescription(energyPassportVM.getDescription());

        passportRepository.save(energyPassport);

        EnergyPassportShortDTO energyPassportDTO = energyPassport.getDTO_Short();

        return energyPassportDTO;
    }

        @Transactional
    public EnergyPassportDTO find(Long id) {
        EnergyPassport energyPassport = passportRepository.findOne(id);

        EnergyPassportDTO result = energyPassport != null ? energyPassport.getDTO() : null;
        if (result != null) {
            result.getSections().forEach((s) -> s.setEntries(findSectionEntries(s.getId())));
        }
        return setupPassportVars(result);
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportDTO> findBySubscriberId(Long subscriberId) {
        List<EnergyPassportDTO> resultList = passportRepository.findBySubscriberId(subscriberId, DocumentModes.DEFAULT).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
        resultList.forEach((p) -> {
            p.getSections().forEach((s) -> s.setEntries(findSectionEntries(s.getId())));
            setupPassportVars(p);
        });
        return resultList;
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportShortDTO> findShortBySubscriberId(Long subscriberId) {
        List<EnergyPassportShortDTO> resultList = passportRepository.findBySubscriberId(subscriberId, DocumentModes.DEFAULT).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO_Short()).collect(Collectors.toList());
        return resultList;
    }

    @Transactional
    public void delete(Long id, Subscriber subscriber) {
        EnergyPassport energyPassport = passportRepository.findOne(id);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, id);
        }

        if (!energyPassport.getSubscriber().equals(subscriber)) {
            throw DBExceptionUtil.newAccessDeniedException(Subscriber.class, subscriber.getId());
        }

        energyPassport.setDeleted(1);
        passportRepository.save(energyPassport);
    }

    @Transactional
    public EnergyPassportDataDTO savePassportData(EnergyPassportDataDTO energyPassportDataDTO) {
        EnergyPassport passport = passportRepository.findOne(energyPassportDataDTO.getPassportId());
        if (passport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, energyPassportDataDTO.getPassportId());
        }

        Optional<EnergyPassportSection> section = passport.searchSection(energyPassportDataDTO.getSectionKey());
        if (!section.isPresent()) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSection.class, energyPassportDataDTO.getSectionKey(), "sectionKey");
        }


        EnergyPassportData passportData;

        List<EnergyPassportData> dataList = passportDataRepository.findByPassportIdAndSectionEntry
            (energyPassportDataDTO.getPassportId(),
                energyPassportDataDTO.getSectionKey(),
                energyPassportDataDTO.getSectionEntryId());

        if (dataList.size() > 1) {
            throw new PersistenceException("Too many rows for EnergyPassportData");
        }

        if (dataList.size() == 1) {
            passportData = dataList.get(0);
        } else {
            passportData = new EnergyPassportData();
            passportData.setPassport(passport);
        }

        if (energyPassportDataDTO.getId() != null) {
            if (!energyPassportDataDTO.getId().equals(passportData.getId())) {
                throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportData.class, energyPassportDataDTO.getId());
            }
        }

        passportData.updateFromDTO(energyPassportDataDTO);
        if (section.isPresent()) {
            passportData.setSectionId(section.get().getId());
        }
        passportDataRepository.save(passportData);

        updateDataValue(energyPassportDataDTO);

        return passportData.getDTO();
    }

    /**
     * @param passportId
     * @return
     */
    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> findPassportData(Long passportId) {

        EnergyPassport energyPassport = passportRepository.findOne(passportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, passportId);
        }

        List<EnergyPassportData> passportDataList = passportDataRepository.findByPassportId(passportId);

        List<EnergyPassportDataDTO> passportDataDTOList = passportDataList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());

        if (passportDataDTOList.isEmpty()) {
            return extractEnergyPassportData(passportId);
        } else if (passportDataDTOList.size() != energyPassport.getSections().size()) {
            return enhanceEnergyPassportData(passportDataDTOList);
        }
        return passportDataDTOList;
    }

    /**
     * @param passportId
     * @param sectionId
     * @return
     */
    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> findPassportData(Long passportId, Long sectionId, Long sectionEntryId) {
        EnergyPassport energyPassport = passportRepository.findOne(passportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, passportId);
        }
        Optional<EnergyPassportSection> section = energyPassport.getSections().stream().filter(i -> i.getId().equals(sectionId)).findFirst();
        if (!section.isPresent()) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSection.class, sectionId);
        }
        List<EnergyPassportData> passportDataList = sectionEntryId == null ?
            passportDataRepository.findByPassportIdAndSectionEntry(passportId, section.get().getSectionKey(),0L) :
            passportDataRepository.findByPassportIdAndSectionEntry(passportId, section.get().getSectionKey(), sectionEntryId);

        if (passportDataList.isEmpty()) {
            List<EnergyPassportDataDTO> newDataDTO = Arrays.asList(extractEnergyPassportData(section.get()));
            if (sectionEntryId != null) {
                newDataDTO.forEach((i) -> i.setSectionEntryId(sectionEntryId));
            }
            return newDataDTO;
        }

        return passportDataList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
    }

    public boolean validatePassportData(EnergyPassportDataDTO dto) {
        if (dto == null)
            return false;

        return EPSectionValueUtil.validateJson(dto.getSectionDataJson());
    }

    /*

     */
    @Transactional(readOnly = true)
    public List<EnergyPassportDataDTO> extractEnergyPassportData(Long passportId) {
        EnergyPassport energyPassport = passportRepository.findOne(passportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, passportId);
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
     * @param src existing list of passport data
     * @return existing list of passport data + data of unsaved sections
     */
    private List<EnergyPassportDataDTO> enhanceEnergyPassportData(List<EnergyPassportDataDTO> src) {
        List<EnergyPassportDataDTO> result = new ArrayList<>(src);

        Long passportId = src.stream().mapToLong(EnergyPassportDataDTO::getPassportId).findFirst().getAsLong();

        EnergyPassport pass = passportRepository.findOne(passportId);

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
                i.getElements().forEach((element) -> {
                    EnergyPassportDataId id = EnergyPassportDataId.builder()
                        .passport(new EnergyPassport().id(energyPassportDataDTO.getPassportId()))
                        .sectionKey(energyPassportDataDTO.getSectionKey())
                        .sectionEntryId(energyPassportDataDTO.getSectionEntryId())
                        .complexIdx(element.get_complexIdx()).build();
                    EnergyPassportDataValue value = new EnergyPassportDataValue();
                    value.setEnergyPassportDataId(id);
                    value.setDataValue(element.valueAsString());
                    value.setDataType(element.dataType());
                    dataValues.add(value);
                })
            );

        energyPassportDataValueRepository.save(dataValues);
    }

    /**
     * @param passportId
     */
    protected void updateEnergyPassportFromTemplate(Long passportId) {
        EnergyPassport energyPassport = passportRepository.findOne(passportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, passportId);
        }
        EnergyPassportTemplate template = energyPassport.getPassportTemplate();
        if (template != null) {
            template.getSectionTemplates().forEach((t) ->
                energyPassport.searchSection(t.getSectionKey())
                    .map((s) -> energyPassportSectionUpdater.apply(s, t))
                    .orElseGet(() -> energyPassportSectionCreator.apply(energyPassport, t))
            );
        }
        passportRepository.save(energyPassport);
    }


    @Transactional
    public void updateExistingEnergyPassportsFromTemplate(Long passportTemplateId) {
        List<EnergyPassport> energyPassports = passportRepository.findByPassportTemplateId(passportTemplateId);
        energyPassports.forEach((p) -> {
            log.debug("Found EnergyPassport(id={})", p.getId());
            EnergyPassportTemplate template = p.getPassportTemplate();
            if (template != null) {
                template.getSectionTemplates().forEach((t) ->
                    p.searchSection(t.getSectionKey())
                        .map((s) -> energyPassportSectionUpdater.apply(s, t))
                        .orElseGet(() -> energyPassportSectionCreator.apply(p, t)));
            }
        });
        passportRepository.save(energyPassports);
    }


    private List<EnergyPassportSectionEntryDTO> saveSectionEntry(List<EnergyPassportSectionEntry> energyPassportSectionEntries) {

        energyPassportSectionEntries.forEach((i) -> {
        });

        return new ArrayList<>();
    }

    @Transactional
    public EnergyPassportSectionEntryDTO saveSectionEntry(EnergyPassportSectionEntryDTO energyPassportSectionEntryDTO) {

        EnergyPassportSectionEntry passportSectionEntry;
        if (energyPassportSectionEntryDTO.getId() == null) {
            passportSectionEntry = new EnergyPassportSectionEntry();
            EnergyPassportSection passportSection = passportSectionRepository.findOne(energyPassportSectionEntryDTO.getSectionId());
            if (passportSection == null) {
                throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSection.class, energyPassportSectionEntryDTO.getSectionId());
            }
            passportSectionEntry.setSection(passportSection);
        } else {
            passportSectionEntry = sectionEntryRepository.findOne(energyPassportSectionEntryDTO.getId());
            if (passportSectionEntry == null) {
                throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSectionEntry.class, energyPassportSectionEntryDTO.getId());
            }
        }

        passportSectionEntry.updateFromDTO(energyPassportSectionEntryDTO);

//        if (energyPassportSectionEntryDTO.getEntryOrder() == null) {
//            List<EnergyPassportSectionEntry> entries = sectionEntryRepository.findBySectionId(energyPassportSectionEntryDTO.getSectionId());
//            passportSectionEntry.setEntryOrder(entries.size() + 1);
//        }
        sectionEntryRepository.save(passportSectionEntry);
        Optional<EnergyPassportSectionEntryDTO> rearranged = sectionEntriesRearrange(energyPassportSectionEntryDTO.getSectionId()).stream().filter((i) -> passportSectionEntry.getId().equals(i.getId())).findFirst();
        return rearranged.orElse(null);
    }


    @Transactional
    public List<EnergyPassportSectionEntryDTO> sectionEntriesRearrange(Long sectionId) {
        List<EnergyPassportSectionEntry> entries = sectionEntryRepository.findBySectionId(sectionId);
        int idx = 1;
        for (EnergyPassportSectionEntry entry : entries) {
            if (!Integer.valueOf(idx).equals(entry.getEntryOrder())) {
                entry.setEntryOrder(idx);
            }
            idx++;
        }

        sectionEntryRepository.save(entries);

        return entries.stream().map((i) -> i.getDTO()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportSectionEntryDTO> findSectionEntries(Long sectionId) {
        return sectionEntryRepository.findBySectionId(sectionId).stream().map((i) -> i.getDTO()).collect(Collectors.toList());
    }


    @Transactional
    public void deleteSectionEntry(Long energyPassportId, Long sectionId, Long entryId, Subscriber subscriber) {
        EnergyPassport energyPassport = passportRepository.findOne(energyPassportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, energyPassportId);
        }

        if (!energyPassport.getSubscriber().equals(subscriber)) {
            throw DBExceptionUtil.newAccessDeniedException(Subscriber.class, subscriber.getId());
        }

        EnergyPassportSectionEntry entry = sectionEntryRepository.findOne(entryId);
        if (entry == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSectionEntry.class, entryId);
        }

        if (!entry.getSection().getId().equals(sectionId)) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassportSection.class, sectionId);
        }

        entry.setDeleted(1);
        sectionEntryRepository.save(entry);
    }


    private Properties fillEnergyPassportVars(EnergyPassportDTO energyPassport) {

        final String[] monthNames = new String[] {"января", "февраля", "марта",
            "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "февраля"};

        Properties vars = new Properties();
        if (energyPassport.getPassportDate() != null) {
            int yyyy = energyPassport.getPassportDate().getYear();
            int dd = energyPassport.getPassportDate().getDayOfMonth();
            int mm = energyPassport.getPassportDate().getMonth().getValue();
            vars.put(Pattern.quote(JsonVars.DD), Integer.toString(dd));
            vars.put(Pattern.quote(JsonVars.MONTH1), monthNames[mm - 1]);
            vars.put(Pattern.quote(JsonVars.YYYY), Integer.toString(yyyy));
            vars.put(Pattern.quote(JsonVars.YYYY_1), Integer.toString(yyyy-1));
            vars.put(Pattern.quote(JsonVars.YYYY_2), Integer.toString(yyyy-2));
            vars.put(Pattern.quote(JsonVars.YYYY_3), Integer.toString(yyyy-3));
            vars.put(Pattern.quote(JsonVars.YYYY_4), Integer.toString(yyyy-4));
            vars.put(Pattern.quote(JsonVars.YYYY_5), Integer.toString(yyyy-5));
        }
        return vars;
    }

    /**
     *
     * @param energyPassportDTO
     * @return
     */
    private EnergyPassportDTO setupPassportVars(EnergyPassportDTO energyPassportDTO) {
        Properties vars = fillEnergyPassportVars(energyPassportDTO);
        energyPassportDTO.getSections().forEach((i) -> {
            String inJson = i.getSectionJson();
            String outJson = EPSectionValueUtil.replaceJsonVars(inJson,vars);
            if (!inJson.equals(outJson)) {
                i.setSectionJson(outJson);
            }
        });
        return energyPassportDTO;
    }

    @Transactional
    public List<Long> linkEnergyPassportToContObjects (Long energyPassportId, List<Long> contObjectIds, Subscriber subscriber) {
        Preconditions.checkNotNull(contObjectIds);
        Preconditions.checkNotNull(subscriber);

        EnergyPassport energyPassport = passportRepository.findOne(energyPassportId);
        if (energyPassport == null) {
            throw DBExceptionUtil.newEntityNotFoundException(EnergyPassport.class, energyPassportId);
        }
        contObjectIds.forEach((i) -> energyPassport.getContObjects().add(new ContObject().id(i)));
        passportRepository.save(energyPassport);

        return energyPassport.getContObjects().stream().map(ContObject::getId).sorted().collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<Long> findEnergyPassportContObjectIds (Long energyPassportId) {
        return passportRepository.findEnergyPassportContObjectIds(energyPassportId).stream().sorted().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyPassportDTO> findContObjectEnergyPassport(Long contObjectId) {
        List<EnergyPassportDTO> resultList = passportRepository.findContObjectEnergyPassports(contObjectId,DocumentModes.CONT_OBJECT)
            .stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> i.getDTO()).collect(Collectors.toList());
        resultList.forEach((p) -> {
            p.getSections().forEach((s) -> s.setEntries(findSectionEntries(s.getId())));
            setupPassportVars(p);
        });
        return resultList;
    }

    @Transactional
    public EnergyPassportDTO createContObjectPassport(EnergyPassportVM energyPassportVM, List<Long> contObjectIds,
                                                      Subscriber subscriber) {
        EnergyPassportDTO result = createPassport(EnergyPassport_X.ENERGY_PASSPORT_X, energyPassportVM, subscriber);
        linkEnergyPassportToContObjects(result.getId(), contObjectIds, subscriber);
        return result;
    }

}
