package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.dto.HeatRadiatorTypeDto;
import ru.excbt.datafuse.nmk.data.repository.HeatRadiatorTypeRepository;
import ru.excbt.datafuse.nmk.service.mapper.HeatRadiatorTypeMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 26.05.2017.
 */
@Service
@Transactional(readOnly = true)
public class HeatRadiatorTypeService {


    private final HeatRadiatorTypeRepository heatRadiatorTypeRepository;

    private final HeatRadiatorTypeMapper heatRadiatorTypeMapper;


    public HeatRadiatorTypeService(HeatRadiatorTypeRepository heatRadiatorTypeRepository,
                                   HeatRadiatorTypeMapper heatRadiatorTypeMapper) {
        this.heatRadiatorTypeRepository = heatRadiatorTypeRepository;
        this.heatRadiatorTypeMapper = heatRadiatorTypeMapper;
    }


    public List<HeatRadiatorTypeDto> findAllHeatRadiators() {
        return heatRadiatorTypeRepository.findAllSorted().stream()
            .map((i) -> heatRadiatorTypeMapper.heatRadiatorTypeToDto(i)).collect(Collectors.toList());
    }


}
