package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.security.SecurityUtils;

@Service
public class PortalUserIdsService {

    private final CurrentSubscriberService currentSubscriberService;

    @Autowired
    public PortalUserIdsService(CurrentSubscriberService currentSubscriberService) {
        this.currentSubscriberService = currentSubscriberService;
    }

    public PortalUserIds getCurrentIds() {
        return currentSubscriberService.getSubscriberParam();
    }


    public boolean isSystemUser() {
        return SecurityUtils.isSystemUser();
    }
}
