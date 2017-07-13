package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;

/**
 * Created by kovtonyk on 12.07.2017.
 */
@Mapper(componentModel = "spring")
public interface SubscriberMapper {

    SubscriberDTO subscriberToDTO (Subscriber subscriber);

    Subscriber DTOToSubscriber (SubscriberDTO subscriberDTO);

}
