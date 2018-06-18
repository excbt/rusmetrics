package ru.excbt.datafuse.nmk.web.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeValidationService;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrObjectTreeController extends SubscrObjectTreeController {

    public RmaSubscrObjectTreeController(SubscrObjectTreeService subscrObjectTreeService, SubscrObjectTreeValidationService subscrObjectTreeValidationService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, SubscrContEventNotificationService subscrContEventNotificationService, SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService, SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service, ContObjectService contObjectService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        super(subscrObjectTreeService, subscrObjectTreeValidationService, subscrObjectTreeContObjectService, subscrContEventNotificationService, subscrContEventNotifiicationStatusService, subscrContEventNotifiicationStatusV2Service, contObjectService, objectAccessService, portalUserIdsService);
    }
}
