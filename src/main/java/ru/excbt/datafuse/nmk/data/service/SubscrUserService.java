package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

@Service
@Transactional
public class SubscrUserService {

	@Autowired
	private SubscrUserRepository subscrUserRepository;
	
	@Transactional(readOnly = true)
	public List<SubscrRole> selectSubscrRoles (long subscrUserId) {
		return subscrUserRepository.selectSubscrRoles(subscrUserId);
	}
}
