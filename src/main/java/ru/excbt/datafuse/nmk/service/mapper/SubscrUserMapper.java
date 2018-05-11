package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;

@Mapper(componentModel = "spring", uses = {SubscriberMapper.class})
public interface SubscrUserMapper {

    @Mapping(source = "subscriberId", target = "subscriber")
    SubscrUser toEntity(SubscrUserDTO dto);

//    @Mapping(source = "subscriber.id", target = "subscriberId")
//    SubscrUserDTO toDto(SubscrUser entity);

    //default S
}
