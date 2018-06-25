package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.validators.UsernameValidator;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

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

    private final SubscriberRepository subscriberRepository;

    private final LdapService ldapService;

    private final CacheManager cacheManager;

    public SubscrUserManageService(SubscrUserRepository subscrUserRepository, SubscrUserMapper subscrUserMapper, SubscrUserService subscrUserService, SubscriberRepository subscriberRepository, LdapService ldapService, CacheManager cacheManager) {
        this.subscrUserRepository = subscrUserRepository;
        this.subscrUserMapper = subscrUserMapper;
        this.subscrUserService = subscrUserService;
        this.subscriberRepository = subscriberRepository;
        this.ldapService = ldapService;
        this.cacheManager = cacheManager;
    }

    /**
     *
     * @param subscrUserDTO
     * @param password
     * @return
     */
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    @Transactional
    public Optional<SubscrUser> createSubscrUser(final SubscrUserDTO subscrUserDTO,
                                                 final String password) {
        Objects.requireNonNull(subscrUserDTO);

        if (subscrUserDTO.getSubscriberId() == null) {
            return Optional.empty();
        }

        if (subscrUserDTO.getUserName() != null) {
            subscrUserDTO.setUserName(subscrUserDTO.getUserName().toLowerCase());
        }

        if (!usernameValidator.validate(subscrUserDTO.getUserName())) {
            return Optional.empty();
        }

        Optional<SubscrUser> checkUser = subscrUserRepository.findOneByUserNameIgnoreCase(subscrUserDTO.getUserName());
        if (checkUser.isPresent()) {
            throw new PersistenceException("User with name " + subscrUserDTO.getUserName() + "already exists");
        }

        Subscriber subscriber = subscriberRepository.findById(subscrUserDTO.getSubscriberId())
            .orElseThrow(() -> new EntityNotFoundException(Subscriber.class, subscrUserDTO.getSubscriberId()));

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
     * @param subscrUserDTO
     * @param passwords
     * @return
     */
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    @Transactional
    public Optional<SubscrUser> updateSubscrUser(final SubscrUserDTO subscrUserDTO,
                                                 final String[] passwords) {

        boolean isAdmin = Boolean.TRUE.equals(subscrUserDTO.getIsAdmin());
        boolean isReadonly = Boolean.TRUE.equals(subscrUserDTO.getIsReadonly());

        SubscrUser subscrUser = subscrUserRepository.findById(subscrUserDTO.getId())
            .orElseThrow(() -> new EntityNotFoundException(SubscrUser.class, subscrUserDTO.getId()));

        if (!subscrUser.getUserName().equals(subscrUserDTO.getUserName())) {
            throw new PersistenceException(
                String.format("Changing username is not allowed. SubscrUser (id=%d)", subscrUser.getId()));
        }

        if (subscrUser.getDeleted() == 1) {
            throw new PersistenceException(String.format("SubscrUser (id=%d) is deleted", subscrUser.getId()));
        }

        String newPassword = null;
        if (passwords != null && passwords.length != 0) {
            if (passwords.length != 2) {
                throw new PersistenceException(
                    String.format("Password for user(%s) is not set", subscrUser.getUserName()));
            }
            newPassword = passwords[1];
            subscrUser.setPassword(null);
        }


        List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(subscrUser.getSubscriber(), isAdmin, isReadonly);
        subscrUser.getSubscrRoles().clear();
        subscrUser.getSubscrRoles().addAll(subscrRoles);

        subscrUserMapper.updateSubscrUser(subscrUser, subscrUserDTO);

        SubscrUser savedSubscrUser = subscrUserRepository.save(subscrUser);
        Optional.ofNullable(cacheManager).map(i -> i.getCache(subscrUserRepository.USERS_BY_LOGIN_CACHE))
            .ifPresent(cm -> cm.evict(savedSubscrUser.getUserName()));
//        cacheManager.getCache(subscrUserRepository.USERS_BY_LOGIN_CACHE).evict(savedSubscrUser.getUserName());

        final String ldapPassword = newPassword;
        if (ldapPassword != null) {

            SubscrUserService.LdapAction action = (u) -> {
                //ldapService.changePassword(u, passwords[0], passwords[1]);
                ldapService.changePassword(u, ldapPassword);
            };
            subscrUserService.processLdapAction(savedSubscrUser, action);
        }

        if (Boolean.TRUE.equals(subscrUser.getIsBlocked())) {
            SubscrUserService.LdapAction action = (u) -> {
                ldapService.blockLdapUser(u);
            };
            subscrUserService.processLdapAction(savedSubscrUser, action);
        }

        if (Boolean.FALSE.equals(subscrUser.getIsBlocked())) {
            SubscrUserService.LdapAction action = (u) -> {
                ldapService.unblockLdapUser(u);
            };
            subscrUserService.processLdapAction(savedSubscrUser, action);
        }
        return Optional.of(savedSubscrUser);
    }
    /**
     *
     * @param subscriber
     */
    @Secured({ AuthoritiesConstants.RMA_SUBSCRIBER_ADMIN, AuthoritiesConstants.ADMIN })
    @Transactional
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
