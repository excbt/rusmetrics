package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.repository.SubscrRoleRepository;
import ru.excbt.datafuse.nmk.security.AdminUtils;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
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
	@Transactional
	public List<SubscrRole> findAllRoles() {
		List<SubscrRole> result = subscrRoleRepository.findAll();
		return ObjectFilters.disabledFilter(result);
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<SubscrRole> subscrUserRoles() {
		List<SubscrRole> allRoles = findAllRoles();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_SUBSCR_USER.equals(i.getRoleName())
				|| SecuredRoles.ROLE_SUBSCR.equals(i.getRoleName())).collect(Collectors.toList());
	}

	/**
	 *
	 * @param canCreateCabinet
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrRole> subscrAdminRoles(boolean canCreateCabinet) {
		List<SubscrRole> allRoles = findAllRoles();

		List<String> adminRoles = new ArrayList<>(AuthoritiesConstants.subscrAdminNoChild());

		return allRoles.stream()
				.filter(i -> adminRoles.contains(i.getRoleName())
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CHILD.equals(i.getRoleName()))
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CABINET.equals(i.getRoleName())))
				.collect(Collectors.toList());
	}

	@Transactional
	public List<SubscrRole> subscrAdminRoles() {
		return subscrAdminRoles(false);
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<SubscrRole> subscrRmaAdminRoles() {
		return subscrRmaAdminRoles(false);
	}

	/**
	 *
	 * @param canCreateCabinet
	 * @return
	 */
	@Transactional
	public List<SubscrRole> subscrRmaAdminRoles(boolean canCreateCabinet) {
		List<SubscrRole> allRoles = findAllRoles();
		return allRoles.stream()
				.filter(i -> SecuredRoles.ROLE_RMA_CONT_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_DEVICE_OBJECT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_SUBSCRIBER_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA_ZPOINT_ADMIN.equals(i.getRoleName())
						|| SecuredRoles.ROLE_RMA.equals(i.getRoleName())
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CHILD.equals(i.getRoleName()))
						|| (canCreateCabinet && SecuredRoles.ROLE_SUBSCR_CREATE_CABINET.equals(i.getRoleName())))
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<SubscrRole> subscrReadonlyRoles() {
		List<SubscrRole> allRoles = findAllRoles();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_SUBSCR_READONLY.equals(i.getRoleName())
				|| SecuredRoles.ROLE_SUBSCR.equals(i.getRoleName())).collect(Collectors.toList());
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<SubscrRole> subscrCabinetRoles() {
		List<SubscrRole> allRoles = findAllRoles();
		return allRoles.stream().filter(i -> SecuredRoles.ROLE_CABINET_USER.equals(i.getRoleName())
				|| SecuredRoles.ROLE_CABINET.equals(i.getRoleName())).collect(Collectors.toList());
	}

}
