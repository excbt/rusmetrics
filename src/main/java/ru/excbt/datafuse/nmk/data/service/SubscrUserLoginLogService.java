package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrUserLoginLog;

/**
 * Сервис для работы с протоколированием входа в систему
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.10.2015
 *
 */
@Service
public class SubscrUserLoginLogService {

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	/**
	 *
	 * @param subscriber
	 * @param subscrUser
	 */
	@Transactional
	public void registerLogin(Long subscrUserId, String userName) {
		checkNotNull(subscrUserId);
		checkNotNull(userName);
		SubscrUserLoginLog loginLog = new SubscrUserLoginLog();
		loginLog.setSubscrUserId(subscrUserId);
		loginLog.setLoginDateTime(new Date());
		loginLog.setUserName(userName);
		em.persist(loginLog);

	}

}
