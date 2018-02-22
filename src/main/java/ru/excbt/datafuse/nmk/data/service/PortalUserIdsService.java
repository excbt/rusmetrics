package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;
import ru.excbt.datafuse.nmk.security.SecurityUtils;

import java.util.Optional;

@Service
public class PortalUserIdsService {

    private final CurrentSubscriberService currentSubscriberService;

    private final SubscrUserRepository subscrUserRepository;

    private final SystemUserRepository systemUserRepository;

    @Autowired
    public PortalUserIdsService(CurrentSubscriberService currentSubscriberService, SubscrUserRepository subscrUserRepository, SystemUserRepository systemUserRepository) {
        this.currentSubscriberService = currentSubscriberService;
        this.subscrUserRepository = subscrUserRepository;
        this.systemUserRepository = systemUserRepository;
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

    public Optional<SystemUser> getSystemUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(systemUserRepository::findOneByUserName);
    }


}
