package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Organization findOne(final long id) {
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

}
