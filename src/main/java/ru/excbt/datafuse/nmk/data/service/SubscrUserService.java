package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.ldap.service.LdapUserAccount;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrUserService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrUserService.class);

	private interface LdapAction {
		void doAction(LdapUserAccount user);
	}

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private LdapService ldapService;

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrRole> selectSubscrRoles(long subscrUserId) {
		return subscrUserRepository.selectSubscrRoles(subscrUserId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrUser> findBySubscriberId(Long subscriberId) {
		List<SubscrUser> resultList = subscrUserRepository.findBySubscriberId(subscriberId);
		resultList.forEach(i -> {
			i.getSubscriber().getId();
		});
		return resultList;
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser findOne(Long subscrUserId) {
		return subscrUserRepository.findOne(subscrUserId);
	}

	/**
	 * 
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser createOne(SubscrUser subscrUser, String password) {
		checkNotNull(subscrUser);
		checkArgument(subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getFirstName());
		checkNotNull(subscrUser.getLastName());
		checkNotNull(subscrUser.getSubscriberId());
		checkNotNull(subscrUser.getSubscrRoles());
		checkArgument(subscrUser.getDeleted() == 0);

		Subscriber subscriber = subscriberService.findOne(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		subscrUser.setUserName(subscrUser.getUserName().toLowerCase());

		SubscrUser result = subscrUserRepository.save(subscrUser);

		LdapAction action = (u) -> {
			ldapService.createUser(u);
			ldapService.changePassword(u, password);
		};

		processLdapAction(result, action);

		return result;
	}

	/**
	 * 
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser updateOne(SubscrUser subscrUser, String[] passwords) {
		checkNotNull(subscrUser);
		checkArgument(!subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getFirstName());
		checkNotNull(subscrUser.getLastName());
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

		Subscriber subscriber = subscriberService.findOne(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		SubscrUser result = subscrUserRepository.save(subscrUser);

		if (passwords != null && passwords.length != 0) {

			if (passwords.length != 2) {
				throw new PersistenceException(
						String.format("Password for user(%s) is not set", subscrUser.getUserName()));
			}

			LdapAction action = (u) -> {
				ldapService.changePassword(u, passwords[0], passwords[1]);
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
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOne(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}
		subscrUserRepository.save(softDelete(subscrUser));
	}

	/**
	 * 
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOnePermanent(Long subscrUserId) {
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
	private LdapUserAccount ldapAccount(SubscrUser subscrUser, Long subscriberId) {

		String ou = subscriberService.getRmaLdapOu(subscriberId);

		LdapUserAccount user = new LdapUserAccount(subscrUser.getId(), subscrUser.getUserName(),
				new String[] { subscrUser.getFirstName(), subscrUser.getLastName() }, ou, subscrUser.getUserEMail());
		return user;

	}

	/**
	 * 
	 * @param action
	 */
	private void processLdapAction(SubscrUser subscrUser, LdapAction action) {
		LdapUserAccount user = ldapAccount(subscrUser, subscrUser.getSubscriberId());
		try {
			action.doAction(user);
		} catch (Exception e) {
			logger.error("LDAP Service Error Message: {}", e.getMessage());
			logger.error("LDAP Service Exception: {}", e);
			logger.error("LDAP Service Exception Stacktrace: {}", ExceptionUtils.getFullStackTrace(e));
			throw new PersistenceException(String.format("Can't process LDAP action for user: %s", user.getUserName()));
		}
	}

}
