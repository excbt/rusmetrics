package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.EmailNotification;
import ru.excbt.datafuse.nmk.data.repository.EmailNotificationRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class EmailNotificationService extends AbstractService implements SecuredRoles {

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

}
