package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;

@Mapper(componentModel = "spring")
public interface SubscrObjectTreeMapper {

    SubscrObjectTreeDTO toDto(SubscrObjectTree subscrObjectTree);

}
