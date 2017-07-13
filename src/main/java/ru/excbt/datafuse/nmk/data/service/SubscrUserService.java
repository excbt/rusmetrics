package ru.excbt.datafuse.nmk.data.service;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;
import ru.excbt.datafuse.nmk.data.repository.UserPersistentTokenRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.ldap.service.LdapUserAccount;
import ru.excbt.datafuse.nmk.ldap.service.SubscrLdapException;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис для работы с пользователями абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Service
public class SubscrUserService extends AbstractService implements SecuredRoles {

	private static final Logger log = LoggerFactory.getLogger(SubscrUserService.class);

	public interface LdapAction {
		void doAction(LdapUserAccount user);
	}

	@Autowired
	private SubscrUserRepository subscrUserRepository;

    @Autowired
	private SystemUserRepository systemUserRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private LdapService ldapService;

	@Autowired
	private SubscrRoleService subscrRoleService;

    @Autowired
    private UserPersistentTokenRepository persistentTokenRepository;

	/**
	 *
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrRole> selectSubscrRoles(long subscrUserId) {
		List<SubscrRole> result = subscrUserRepository.selectSubscrRoles(subscrUserId);
		return ObjectFilters.disabledFilter(result);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrUser> selectBySubscriberId(Long subscriberId) {
		List<SubscrUser> resultList = subscrUserRepository.selectBySubscriberId(subscriberId);
		resultList.forEach(i -> {
			i.getSubscriber().getId();
		});
		return resultList;
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> findUserIdsBySubscriberOrRmaId(Long subscriberId) {
        List<Long> result = subscrUserRepository.findUserIdsBySubscriberOrRmaId(subscriberId);
        result.addAll(systemUserRepository.findUserIdsBySubscriberOrRmaId(subscriberId));
		return result;
	}

	/**
	 *
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser findOne(Long subscrUserId) {
        SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
        return subscrUser;
	}

	/**
	 *
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_SUBSCR_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser createSubscrUser(SubscrUser subscrUser, String password) {
		return createSubscrUser(subscrUser, password, false);
	}

	/**
	 *
	 * @param subscrUser
	 * @param password
	 * @param skipLdapAction
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser createSubscrUser(SubscrUser subscrUser, String password, boolean skipLdapAction) {
		checkNotNull(subscrUser);
		checkArgument(subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getSubscriberId());
		checkNotNull(subscrUser.getSubscrRoles());
		checkArgument(subscrUser.getDeleted() == 0);

		Subscriber subscriber = subscriberService.selectSubscriber(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		subscrUser.setUserName(subscrUser.getUserName().toLowerCase());

		SubscrUser result = subscrUserRepository.save(subscrUser);

		LdapAction action = (u) -> {
			ldapService.createUser(u);
			ldapService.changePassword(u, password);
		};

		if (!skipLdapAction) {
			processLdapAction(result, action);
		}

		return result;
	}

	/**
	 *
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser updateSubscrUser(SubscrUser subscrUser, String[] passwords) {
		checkNotNull(subscrUser);
		checkArgument(!subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getSubscriberId());
		checkNotNull(subscrUser.getSubscrRoles());

		SubscrUser currentUser = subscrUserRepository.findOne(subscrUser.getId());
		if (currentUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUser.getId()));
		}

		if (!currentUser.getUserName().equals(subscrUser.getUserName())) {
			throw new PersistenceException(
					String.format("Changing username is not allowed. SubscrUser (id=%d)", subscrUser.getId()));
		}

		if (currentUser.getDeleted() == 1) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is deleted", subscrUser.getId()));
		}

		Subscriber subscriber = subscriberService.selectSubscriber(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		String newPassword = null;

		if (passwords != null && passwords.length != 0) {
			if (passwords.length != 2) {
				throw new PersistenceException(
						String.format("Password for user(%s) is not set", subscrUser.getUserName()));
			}
			newPassword = passwords[1];
			subscrUser.setPassword(null);
		}

		SubscrUser result = subscrUserRepository.save(subscrUser);

		final String ldapPassword = newPassword;
		if (ldapPassword != null) {

			LdapAction action = (u) -> {
				//ldapService.changePassword(u, passwords[0], passwords[1]);
				ldapService.changePassword(u, ldapPassword);
			};
			processLdapAction(result, action);
		}

		if (Boolean.TRUE.equals(subscrUser.getIsBlocked())) {
			LdapAction action = (u) -> {
				ldapService.blockLdapUser(u);
			};
			processLdapAction(result, action);
		}

		if (Boolean.FALSE.equals(subscrUser.getIsBlocked())) {
			LdapAction action = (u) -> {
				ldapService.unblockLdapUser(u);
			};
			processLdapAction(result, action);
		}

		return result;
	}

	/**
	 *
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_SUBSCR_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteSubscrUser(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}
		subscrUserRepository.save(softDelete(subscrUser));

		// Delete from Ldap
		LdapAction action = (u) -> {
			ldapService.deleteUser(u);
		};

		try {
			processLdapAction(subscrUser, action);
		} catch (Exception e) {
			log.error("Error during processLdapAction for user {}: {}", subscrUser.getUserName(), e);
		}

	}

	/**
	 *
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteSubscrUserPermanent(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}
		subscrUserRepository.delete(subscrUser);
	}

	/**
	 *
	 * @param subscrUser
	 * @return
	 */
	public LdapUserAccount ldapAccountFactory(SubscrUser subscrUser, Long subscriberId) {

		checkNotNull(subscrUser.getSubscriber());

		String[] orgUnits = subscriberService.buildSubscriberLdapOu(subscrUser.getSubscriber());

		checkNotNull(orgUnits);

		String firstName = subscrUser.getUserName();
		String lastNameName = subscrUser.getUserName();

		String[] stringNames = new String[] { firstName, lastNameName };

		SubscrTypeKey subscrTypeKey = SubscrTypeKey.searchKeyname(subscrUser.getSubscriber().getSubscrType());

		String gidNumber = (subscrTypeKey != null && subscrTypeKey.isChild())
				&& (subscrUser.getSubscriber() != null && subscrUser.getSubscriber().getSubscrCabinetNr() != null)
						? subscrUser.getSubscriber().getSubscrCabinetNr() : null;

		LdapUserAccount user = new LdapUserAccount(subscrUser.getId(), subscrUser.getUserName(), stringNames, orgUnits,
				subscrUser.getUserEMail(), subscrUser.getUserDescription(), gidNumber,
				subscrTypeKey.isChild() ? subscrTypeKey.getKeyname() : null);
		return user;

	}

	/**
	 *
	 * @param action
	 */
	public void processLdapAction(SubscrUser subscrUser, LdapAction action) {
		LdapUserAccount user = ldapAccountFactory(subscrUser, subscrUser.getSubscriberId());
		try {
			action.doAction(user);
		} catch (Exception e) {
			log.error("LDAP Service Error Message: {}", e.getMessage());
			log.error("LDAP Service Exception: {}", e);
			log.error("LDAP Service Exception Stacktrace: {}", ExceptionUtils.getFullStackTrace(e));

			if (org.springframework.ldap.NamingException.class.isAssignableFrom(e.getClass())) {
				throw new SubscrLdapException(
						String.format("Can't process LDAP action for user: %s", user.getUserName()), e);
			} else {
				throw new SubscrLdapException(
						String.format("Can't process LDAP action for user: %s", user.getUserName()));
			}

		}
	}

	/**
	 *
	 * @param userName
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Optional<SubscrUser> findByUsername(String userName) {
		return subscrUserRepository.findOneByUserNameIgnoreCase(userName);
	}

	/**
	 *
	 * @param subscriberId
	 */
	@Secured({ ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> deleteSubscrUsers(Long subscriberId) {

		List<SubscrUser> subscrUsers = subscrUserRepository.selectBySubscriberId(subscriberId);

		// Delete from Ldap
		LdapAction action = (u) -> {
			ldapService.deleteUser(u);
		};

		for (SubscrUser subscrUser : subscrUsers) {
			try {
				processLdapAction(subscrUser, action);
			} catch (Exception e) {
				log.error("Error during processLdapAction for user {}: {}", subscrUser.getUserName(), e);
			}

		}

		List<Long> result = subscrUsers.stream().map(i -> i.getId()).collect(Collectors.toList());

		// Delete from table
		subscrUserRepository.delete(subscrUsers);

		return result;

	}

	/**
	 *
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_USER, ROLE_CABINET_USER })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void clearSubscrUserPassword(Long subscrUserId) {
		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}

		subscrUser.setPassword(null);
		subscrUserRepository.save(subscrUser);
	}

	/**
	 *
	 * @param subscriber
	 * @param isAdmin
	 * @param isReadonly
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrRole> processSubscrRoles(final Subscriber subscriber, final boolean isAdmin,
			final boolean isReadonly) {
		List<SubscrRole> subscrRoles = new ArrayList<>();

		if (Boolean.TRUE.equals(isReadonly)) {
			subscrRoles.addAll(subscrRoleService.subscrReadonlyRoles());
		} else {
			if (Boolean.TRUE.equals(isAdmin)) {
				subscrRoles.addAll(
						subscrRoleService.subscrAdminRoles(Boolean.TRUE.equals(subscriber.getCanCreateChild())));
				if (Boolean.TRUE.equals(subscriber.getIsRma())) {
					subscrRoles.addAll(
							subscrRoleService.subscrRmaAdminRoles(Boolean.TRUE.equals(subscriber.getCanCreateChild())));
				}
			} else {
				subscrRoles.addAll(subscrRoleService.subscrUserRoles());
			}
		}

		Map<Long, SubscrRole> subscrRolesMap = new HashMap<>();
		for (SubscrRole r : subscrRoles) {
			subscrRolesMap.put(r.getId(), r);
		}

		return new ArrayList<>(subscrRolesMap.values());
	}

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(value = TxConst.TX_DEFAULT)
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
            Long userId = token.getUserId();
            log.debug("Deleting token {} for userId: {}", token.getSeries(), userId);
            persistentTokenRepository.delete(token);
        });
    }

}
