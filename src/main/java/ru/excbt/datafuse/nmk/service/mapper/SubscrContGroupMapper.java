package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.service.dto.SubscrContGroupDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ContGroupTypeMapper.class, SubscriberMapper.class})
public interface SubscrContGroupMapper {

    @Mapping(source = "subscriber.id", target = "subscriberId")
    SubscrContGroupDTO toDto(SubscrContGroup subscrContGroup);
    List<SubscrContGroupDTO> toDto(List<SubscrContGroup> subscrContGroupList);

    @Mapping(source = "subscriberId", target = "subscriber")
    SubscrContGroup toEntity(SubscrContGroupDTO dto);
}
