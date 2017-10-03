package ru.excbt.datafuse.nmk.data.service.support;

import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;

@Service
public class PortalUserIdsService {

    private final CurrentSubscriberService currentSubscriberService;


    public PortalUserIdsService(CurrentSubscriberService currentSubscriberService) {
        this.currentSubscriberService = currentSubscriberService;
    }

    public PortalUserIds getCurrentIds() {
        return currentSubscriberService.getSubscriberParam().asPortalUserIds();
    }

}
