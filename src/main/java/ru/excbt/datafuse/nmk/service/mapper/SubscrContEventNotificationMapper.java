package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContEventNotificationDTO;

@Mapper(componentModel = "spring", uses = {})
public interface SubscrContEventNotificationMapper extends EntityMapper<SubscrContEventNotificationDTO, SubscrContEventNotification> {

}
