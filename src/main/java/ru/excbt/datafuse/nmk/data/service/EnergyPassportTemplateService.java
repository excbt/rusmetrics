package ru.excbt.datafuse.nmk.data.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@Service
public class EnergyPassportTemplateService {

    private final EnergyPassportTemplateRepository energyPassportTemplateRepository;

    private final ModelMapper modelMapper;

    public EnergyPassportTemplateService(EnergyPassportTemplateRepository energyPassportTemplateRepository,
                                         ModelMapper modelMapper) {
        this.energyPassportTemplateRepository = energyPassportTemplateRepository;
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
        return energyPassportTemplate != null ?
            modelMapper.map(energyPassportTemplate, EnergyPassportTemplateDTO.class) : null;
    }

}
