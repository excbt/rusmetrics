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

/**
 * Сервис для работы с ролями абонентов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
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
	 * @param canCreateCabinet
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrAdminRoles(boolean canCreateCabinet) {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream()
				.filter(i -> SecuredRoles.ROLE_SUBSCR_USER.equals(i.getRoleName())
						|| SecuredRoles.ROLE_SUBSCR_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_CONT_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_ZPOINT_ADMIN.equals(i.getRoleName())
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CHILD.equals(i.getRoleName()))
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CABINET.equals(i.getRoleName())))
				.collect(Collectors.toList());
	}

	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrAdminRoles() {
		return subscrAdminRoles(false);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrRmaAdminRoles() {
		return subscrRmaAdminRoles(false);
	}

	/**
	 * 
	 * @param canCreateCabinet
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrRmaAdminRoles(boolean canCreateCabinet) {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream()
				.filter(i -> SecuredRoles.ROLE_RMA_CONT_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_DEVICE_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_SUBSCRIBER_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_ZPOINT_ADMIN.equals(i.getRoleName())
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CHILD.equals(i.getRoleName()))
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CABINET.equals(i.getRoleName())))
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

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrRole> subscrCabinetRoles() {
		List<SubscrRole> allRoles = findAll();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_SUBSCR_USER.equals(i.getRoleName())
				|| SecuredRoles.ROLE_CABINET_USER.equals(i.getRoleName())).collect(Collectors.toList());
	}

}
