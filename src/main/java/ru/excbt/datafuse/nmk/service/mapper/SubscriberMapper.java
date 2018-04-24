package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

/**
 * Created by kovtonyk on 12.07.2017.
 */
@Mapper(componentModel = "spring")
public interface SubscriberMapper extends EntityMapper<SubscriberDTO, Subscriber> {

    @Mapping(target = "info", source = "subscriberInfo")
    @Mapping(target = "comment", source = "subscriberComment")
    void updateSubscriber (SubscriberVM subscriberVM, @MappingTarget Subscriber subscriber);

    @Mapping(source = "info", target = "subscriberInfo")
    @Mapping(source = "comment", target = "subscriberComment")
    SubscriberVM toVM (Subscriber subscriber);
}
