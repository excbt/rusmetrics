package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;

@Mapper(componentModel = "spring")
public interface SubscrObjectTreeMapper {

    SubscrObjectTreeDTO toDto(SubscrObjectTree subscrObjectTree);

//    @Mapping(target = "vmMode", ignore = true)
//    SubscrObjectTreeVM toVM(SubscrObjectTree subscrObjectTree);
//
    SubscrObjectTreeVM toVMShort(SubscrObjectTree subscrObjectTree);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "subscriberId", ignore = true)
    @Mapping(target = "rmaSubscriberId", ignore = true)
    @Mapping(target = "isRma", ignore = true)
    @Mapping(target = "templateId", ignore = true)
    @Mapping(target = "templateItemId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateTreeFromVM (@MappingTarget SubscrObjectTree subscrObjectTree, SubscrObjectTreeVM vm);

    SubscrObjectTreeVM toVM(SubscrObjectTreeDTO subscrObjectTree);

    SubscrObjectTree toEntity(SubscrObjectTreeVM vm);
}
