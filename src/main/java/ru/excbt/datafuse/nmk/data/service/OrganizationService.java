package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.Organization;
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
		return organizationRepository.selectRsoOrganizations();
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
