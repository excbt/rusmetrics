package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с организациями
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Service
public class OrganizationService extends AbstractService implements SecuredRoles {

	@Autowired
	private OrganizationRepository organizationRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Organization selectOrganization(final long id) {
		return organizationRepository.findOne(id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Organization findOrganization(final long id) {
		return organizationRepository.findOne(id);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Organization> selectRsoOrganizations() {
		List<Organization> organizations = organizationRepository.selectRsoOrganizations();
		List<Organization> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE).collect(Collectors.toList());
		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Organization> selectCmOrganizations() {
		List<Organization> organizations = organizationRepository.selectCmOrganizations();
		List<Organization> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE).collect(Collectors.toList());
		return result;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Organization> selectOrganizations() {
		List<Organization> organizations = organizationRepository.selectOrganizations();
		List<Organization> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE).collect(Collectors.toList());
		return result;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public Organization selectByKeyname(String keyname) {
		List<Organization> organizations = organizationRepository.findByKeyname(keyname);
		return organizations.size() > 0 ? organizations.get(0) : null;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public Organization saveOrganization(Organization entity) {
		return organizationRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public Organization deleteOrganization(Organization entity) {
		return organizationRepository.save(softDelete(entity));
	}

}
