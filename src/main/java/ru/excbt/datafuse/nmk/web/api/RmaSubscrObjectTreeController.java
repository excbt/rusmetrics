package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.service.*;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrObjectTreeController extends SubscrObjectTreeController {

    public RmaSubscrObjectTreeController(SubscrObjectTreeService subscrObjectTreeService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService, SubscrContEventNotificationService subscrContEventNotificationService, SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService, SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service, ContObjectService contObjectService, ObjectAccessService objectAccessService) {
        super(subscrObjectTreeService, subscrObjectTreeContObjectService, subscrContEventNotificationService, subscrContEventNotifiicationStatusService, subscrContEventNotifiicationStatusV2Service, contObjectService, objectAccessService);
    }
}
