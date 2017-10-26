package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContEventNotificationDTO;

@Mapper(componentModel = "spring", uses = {ContEventTypeMapper.class})
public interface SubscrContEventNotificationMapper extends EntityMapper<SubscrContEventNotificationDTO, SubscrContEventNotification> {

}
