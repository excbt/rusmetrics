package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;

@Mapper(componentModel = "spring")
public interface ObjectTagMapper extends EntityMapper<ObjectTagDTO, ObjectTag> {

    @Mapping(target = "subscriberId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    ObjectTag toEntity(ObjectTagDTO dto);

    @Mapping(target = "subscriberId", ignore = true)
    ObjectTag.PK toEntityPK(ObjectTagDTO dto);

}
