package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;

@Mapper(componentModel = "spring", uses = {SubscriberMapper.class})
public interface SubscrUserMapper {

    @Mapping(source = "subscriberId", target = "subscriber")
    SubscrUser toEntity(SubscrUserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "subscrRoles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateSubscrUser (@MappingTarget SubscrUser subscrUser, SubscrUserDTO subscriberVM);

}
