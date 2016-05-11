package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

/**
 * Сервис для работы с работы с пользователями абонентов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
@Service
public class SecuritySubscrUserService {

	@Autowired
	private SubscrUserRepository subscrUserRepository;

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

}
