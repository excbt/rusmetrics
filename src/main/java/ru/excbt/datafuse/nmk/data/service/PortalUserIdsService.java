package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.security.SecurityUtils;

import java.util.Optional;

@Service
public class PortalUserIdsService {

    private final CurrentSubscriberService currentSubscriberService;

    private final SubscrUserRepository subscrUserRepository;

    @Autowired
    public PortalUserIdsService(CurrentSubscriberService currentSubscriberService, SubscrUserRepository subscrUserRepository) {
        this.currentSubscriberService = currentSubscriberService;
        this.subscrUserRepository = subscrUserRepository;
    }

    public PortalUserIds getCurrentIds() {
        return currentSubscriberService.getSubscriberParam();
    }

    public boolean isSystemUser() {
        return SecurityUtils.isSystemUser();
    }

    public Optional<SubscrUser> getSubscrUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(subscrUserRepository::findOneByUserName);
    }

}
