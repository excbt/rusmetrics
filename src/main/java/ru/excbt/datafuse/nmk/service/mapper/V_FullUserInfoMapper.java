package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.service.dto.V_FullUserInfoDTO;

@Mapper(componentModel = "spring")
public interface V_FullUserInfoMapper {
    V_FullUserInfoDTO toDto(V_FullUserInfo fullUserInfo);
}
