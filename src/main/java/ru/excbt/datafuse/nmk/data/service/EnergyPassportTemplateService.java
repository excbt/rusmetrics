package ru.excbt.datafuse.nmk.data.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportSectionTemplateRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.energypassport.EnergyPassport401_2014;
import ru.excbt.datafuse.nmk.data.service.energypassport.EnergyPassport401_2014_Add;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@Service
public class EnergyPassportTemplateService {

    private final EnergyPassportTemplateRepository energyPassportTemplateRepository;

    private final EnergyPassportSectionTemplateRepository energyPassportSectionTemplateRepository;

    private final ModelMapper modelMapper;

    private final EnergyPassport401_2014 energyPassport401_2014;
    private final EnergyPassport401_2014_Add energyPassport401_2014_add;

    public EnergyPassportTemplateService(EnergyPassportTemplateRepository energyPassportTemplateRepository,
                                         EnergyPassportSectionTemplateRepository energyPassportSectionTemplateRepository,
                                         ModelMapper modelMapper,
                                         EnergyPassport401_2014 energyPassport401_2014,
                                         EnergyPassport401_2014_Add energyPassport401_2014_add
                                         ) {
        this.energyPassportTemplateRepository = energyPassportTemplateRepository;
        this.energyPassportSectionTemplateRepository = energyPassportSectionTemplateRepository;
        this.energyPassport401_2014 = energyPassport401_2014;
        this.energyPassport401_2014_add = energyPassport401_2014_add;
        this.modelMapper = modelMapper;

    }

    @Transactional(readOnly = true)
    public List<EnergyPassportTemplateDTO> findAllTemplates() {
        return energyPassportTemplateRepository.findAll().stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(i -> modelMapper.map(i, EnergyPassportTemplateDTO.class)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnergyPassportTemplateDTO findOneTemplate(Long id) {
        EnergyPassportTemplate energyPassportTemplate = energyPassportTemplateRepository.findOne(id);
        EnergyPassportTemplateDTO result = energyPassportTemplate != null ?
            modelMapper.map(energyPassportTemplate, EnergyPassportTemplateDTO.class) : null;
        if (energyPassportTemplate.getSectionTemplates() != null)
            energyPassportTemplate.getSectionTemplates()
                .forEach(section ->
                    result.addSection(modelMapper.map(section, EnergyPassportSectionTemplateDTO.class)));

        return result;
    }

    public EnergyPassportTemplateDTO createNew() {
        EnergyPassportTemplateDTO templateDTO = new EnergyPassportTemplateDTO();
        templateDTO.addSection(createSection(energyPassport401_2014.sectionMainFactory()));
        templateDTO.addSection(createSection(energyPassport401_2014_add.section_2_3()));
        templateDTO.addSection(createSection(energyPassport401_2014_add.section_2_10()));
        templateDTO.setDocumentDate(LocalDate.of(2014,6,30));
        templateDTO.setDocumentName("ПРИКАЗ 401");
        templateDTO.setDescription("ОБ УТВЕРЖДЕНИИ ПОРЯДКА ПРЕДСТАВЛЕНИЯ ИНФОРМАЦИИ ОБ ЭНЕРГОСБЕРЕЖЕНИИ И О ПОВЫШЕНИИ ЭНЕРГЕТИЧЕСКОЙ ЭФФЕКТИВНОСТИ");
        return templateDTO;
    }

    private EnergyPassportSectionTemplateDTO createSection(EnergyPassportSectionTemplateFactory factory){
        EnergyPassportSectionTemplateDTO sectionTemplateDTO = new EnergyPassportSectionTemplateDTO();
        sectionTemplateDTO.setSectionKey(factory.getSectionKey());
        sectionTemplateDTO.setSectionJson(factory.createSectionTemplateJson());
        return sectionTemplateDTO;
    }


    public List<EnergyPassportSectionDataDTO> createNewData() {
        List<EnergyPassportSectionDataDTO> result = new ArrayList<>();
        result.add(createSectionData(energyPassport401_2014.sectionMainFactory()));
        result.add(createSectionData(energyPassport401_2014_add.section_2_3()));
        result.add(createSectionData(energyPassport401_2014_add.section_2_10()));
        return result;
    }

    private EnergyPassportSectionDataDTO createSectionData(EnergyPassportSectionTemplateFactory factory) {
        EnergyPassportSectionDataDTO dataDTO = new EnergyPassportSectionDataDTO();
        dataDTO.setSectionKey(factory.getSectionKey());
        dataDTO.setSectionDataJson(factory.createSectionValuesJson());
        return dataDTO;
    }

}
