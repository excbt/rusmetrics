package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;

/**
 * Сервис для работы с системными пользователями
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 *
 */
@Service
public class SystemUserService {

	@Autowired
	private SystemUserRepository systemUserRepository;

	/**
	 *
	 * @param userName
	 * @return
	 */
	@Transactional( readOnly = true)
	public SystemUser findByUsername(final String userName) {
		List<SystemUser> resultList = systemUserRepository.findByUserName(userName);
		if (resultList.size() == 1) {
			return resultList.get(0);
		}
		return null;
	}
}
