package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

/**
 * Сервис для работы с абонентами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
@Service
public class SecuritySubscriberService {

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	/**
	 * 
	 * @param userName
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrUser> findUserByUsername(String userName) {

		List<SubscrUser> userList = subscrUserRepository.findByUserNameIgnoreCase(userName);
		List<SubscrUser> result = userList.stream().filter(i -> i.getId() > 0).collect(Collectors.toList());
		result.forEach(i -> {
			i.getSubscriber();
		});

		return result;
	}

}
