package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;

@Service
@Transactional
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Transactional(readOnly = true)
	public Organization findOne (final long id)  {
		return organizationRepository.findOne(id);
	}
	
}
