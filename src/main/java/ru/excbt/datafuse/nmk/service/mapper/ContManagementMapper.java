package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.dto.ContManagementDTO;

/**
 * Created by kovtonyk on 23.06.2017.
 */
@Mapper(componentModel = "spring")
public interface ContManagementMapper {

    ContManagementDTO contManagementToDto(ContManagement contManagement);

}
