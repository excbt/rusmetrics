package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.EmailNotification;
import ru.excbt.datafuse.nmk.data.repository.EmailNotificationRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class EmailNotificationService implements SecuredRoles {

	@Autowired
	private EmailNotificationRepository emailNotificationRepository;

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public EmailNotification saveEmailNotification(EmailNotification entity) {
		return emailNotificationRepository.save(entity);
	}

	/***
	 *
	 * @param fromSubscrUserId
	 */
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteEmailNotifications(Long subscrUserId) {
		List<EmailNotification> emailNotifications = emailNotificationRepository.findByFromSubscrUserId(subscrUserId);

		emailNotificationRepository.delete(emailNotifications);

		emailNotifications = emailNotificationRepository.findByToSubscrUserId(subscrUserId);

		emailNotificationRepository.delete(emailNotifications);
	}

	/**
	 *
	 * @param fromSubscrUserIds
	 */
	@Secured({ ROLE_SUBSCR_CREATE_CABINET, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteEmailNotifications(List<Long> fromSubscrUserIds) {
		checkNotNull(fromSubscrUserIds);
		for (Long i : fromSubscrUserIds) {
			deleteEmailNotifications(i);
		}
	}

}
