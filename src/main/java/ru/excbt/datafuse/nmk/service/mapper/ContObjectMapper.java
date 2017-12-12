package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;

/**
 * Created by kovtonyk on 14.06.2017.
 */
@Mapper(componentModel = "spring", uses = {ContManagementMapper.class})
public interface ContObjectMapper extends EntityMapper<ContObjectDTO, ContObject> {

    @Mapping(target = "_activeContManagement", source = "_activeContManagement")
    ContObjectDTO toDto (ContObject contObject);

    @Mapping(target = "_activeContManagement", source = "_activeContManagement")
    ContObjectMonitorDTO contObjectToMonitorDto (ContObject contObject);

    default ContObject contObjectFromId(Long id) {
        return id == null ? null : new ContObject().id(id);
    }

}
