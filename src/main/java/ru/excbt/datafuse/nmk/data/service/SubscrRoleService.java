package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.repository.SubscrRoleRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrRoleService {

	@Autowired
	private SubscrRoleRepository subscrRoleRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> findAll() {
		return subscrRoleRepository.findAll();
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrUserRoles() {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_SUBSCR_USER.equals(i.getRoleName()))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrAdminRoles() {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream()
				.filter(i -> SecuredRoles.ROLE_SUBSCR_USER.equals(i.getRoleName())
						|| SecuredRoles.ROLE_SUBSCR_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_CONT_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_ZPOINT_ADMIN.equals(i.getRoleName()))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrReadonlyRoles() {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_SUBSCR_READONLY.equals(i.getRoleName()))
				.collect(Collectors.toList());
	}

}
