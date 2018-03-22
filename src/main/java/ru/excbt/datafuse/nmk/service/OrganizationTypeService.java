package ru.excbt.datafuse.nmk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.domain.OrganizationType;
import ru.excbt.datafuse.nmk.repository.OrganizationTypeRepository;
import ru.excbt.datafuse.nmk.service.dto.OrganizationTypeDTO;
import ru.excbt.datafuse.nmk.service.mapper.OrganizationTypeMapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrganizationTypeService {

    private final OrganizationTypeRepository organizationTypeRepository;

    private final OrganizationTypeMapper organizationTypeMapper;

    public OrganizationTypeService(OrganizationTypeRepository organizationTypeRepository, OrganizationTypeMapper organizationTypeMapper) {
        this.organizationTypeRepository = organizationTypeRepository;
        this.organizationTypeMapper = organizationTypeMapper;
    }

    public List<OrganizationTypeDTO> findAll() {
        return organizationTypeRepository.findAll()
            .stream()
            .sorted(Comparator.comparingLong(OrganizationType::getId))
            .map(organizationTypeMapper::toDto)
            .collect(Collectors.toList());
    }

}
