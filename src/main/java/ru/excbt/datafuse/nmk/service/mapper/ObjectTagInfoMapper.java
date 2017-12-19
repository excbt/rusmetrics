package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ObjectTagInfo;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagInfoDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ObjectTagInfoMapper extends EntityMapper<ObjectTagInfoDTO, ObjectTagInfo> {

    ObjectTagInfo.PK toPK (ObjectTagInfoDTO dto);

}
