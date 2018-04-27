package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

import java.util.List;

/**
 * Created by kovtonyk on 12.07.2017.
 */
@Mapper(componentModel = "spring")
public interface SubscriberMapper  {

    Subscriber toEntity(SubscriberDTO dto);

    @Mapping(source = "info", target = "subscriberInfo")
    @Mapping(source = "comment", target = "subscriberComment")
    SubscriberDTO toDto(Subscriber entity);

    List<Subscriber> toEntity(List<SubscriberDTO> dtoList);

    List <SubscriberDTO> toDto(List<Subscriber> entityList);

    @Mapping(target = "info", source = "subscriberInfo")
    @Mapping(target = "comment", source = "subscriberComment")
    void updateSubscriber (SubscriberVM subscriberVM, @MappingTarget Subscriber subscriber);

    @Mapping(source = "info", target = "subscriberInfo")
    @Mapping(source = "comment", target = "subscriberComment")
    SubscriberVM toVM (Subscriber subscriber);

    default Subscriber subscriberFromId(Long id) {
        if (id == null) {
            return null;
        }
        return new Subscriber().id(id);
    }

}
