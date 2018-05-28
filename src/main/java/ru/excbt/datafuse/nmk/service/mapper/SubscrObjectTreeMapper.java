package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;

@Mapper(componentModel = "spring")
public interface SubscrObjectTreeMapper {

    SubscrObjectTreeDTO toDto(SubscrObjectTree subscrObjectTree);

//    @Mapping(target = "vmMode", ignore = true)
//    SubscrObjectTreeVM toVM(SubscrObjectTree subscrObjectTree);
//
    @Mapping(target = "childObjectList", ignore = true)
    SubscrObjectTreeVM toVMShort(SubscrObjectTree subscrObjectTree);

}
