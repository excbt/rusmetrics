package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.service.validators.UsernameValidator;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
public class SubscrUserManageService {

    private static final Logger log = LoggerFactory.getLogger(SubscrUserManageService.class);

    private final static UsernameValidator usernameValidator = new UsernameValidator();

    private final SubscrUserRepository subscrUserRepository;

    private final SubscrUserMapper subscrUserMapper;

    private final SubscrUserService subscrUserService;

    public SubscrUserManageService(SubscrUserRepository subscrUserRepository, SubscrUserMapper subscrUserMapper, SubscrUserService subscrUserService) {
        this.subscrUserRepository = subscrUserRepository;
        this.subscrUserMapper = subscrUserMapper;
        this.subscrUserService = subscrUserService;
    }

    /**
     *
     * @param subscriber
     * @param subscrUserDTO
     * @param password
     * @return
     */
    public Optional<SubscrUser> createSubscrUser(final Subscriber subscriber,
                                                 final SubscrUserDTO subscrUserDTO,
                                                 final String password) {
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(subscriber.getId());
        Objects.requireNonNull(subscrUserDTO);

        if (subscrUserDTO.getUserName() != null) {
            subscrUserDTO.setUserName(subscrUserDTO.getUserName().toLowerCase());
        }

        if (!usernameValidator.validate(subscrUserDTO.getUserName())) {
            Optional.empty();
        }

        Optional<SubscrUser> checkUser = subscrUserRepository.findOneByUserNameIgnoreCase(subscrUserDTO.getUserName());
        if (checkUser.isPresent()) {
            new PersistenceException("User with name " + subscrUserDTO.getUserName() + "already exists");
        }


        SubscrUser subscrUser = subscrUserMapper.toEntity(subscrUserDTO);

        List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(subscriber,
            subscrUserDTO.isAdmin() && !subscrUserDTO.isReadonly(), subscrUserDTO.isReadonly());

        subscrUser.getSubscrRoles().clear();
        subscrUser.getSubscrRoles().addAll(subscrRoles);
        subscrUser.setSubscriber(subscriber);

        SubscrUser result = subscrUserService.createSubscrUser(subscrUser, password);

        return Optional.of(result);
    }

    /**
     *
     * @param subscriber
     */
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    @Transactional(value = TxConst.TX_DEFAULT)
    public void setupSubscriberAdminUserRoles(Subscriber subscriber) {

        checkNotNull(subscriber);
        checkArgument(!subscriber.isNew());

        List<SubscrUser> subscrUsers = subscrUserRepository.selectBySubscriberId(subscriber.getId());
        List<SubscrUser> adminUsers = ObjectFilters.filterToList(subscrUsers, i -> Boolean.TRUE.equals(i.getIsAdmin())
            && !Boolean.TRUE.equals(i.getIsReadonly()) && !Boolean.TRUE.equals(i.getIsBlocked()));

        adminUsers.forEach(i -> {
            log.debug("Update roles for {}. isAdmin: {}, isReadonly: {}", i.getUserName(), i.getIsAdmin(),
                i.getIsReadonly());

            log.debug("Exisiting roles:");
            for (SubscrRole subscrRole : i.getSubscrRoles()) {
                log.debug("Role: {}", subscrRole.getRoleName());
            }

            i.getSubscrRoles().clear();
            List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(subscriber,
                Boolean.TRUE.equals(i.getIsAdmin()), Boolean.TRUE.equals(i.getIsReadonly()));
            i.getSubscrRoles().addAll(subscrRoles);

            log.debug("New roles:");
            for (SubscrRole subscrRole : i.getSubscrRoles()) {
                log.debug("Role: {}", subscrRole.getRoleName());
            }

        });

    }
}
