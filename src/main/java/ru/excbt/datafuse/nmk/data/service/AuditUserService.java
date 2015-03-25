package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.repository.AuditUserRepository;

@Service
@Transactional
public class AuditUserService {

	@Autowired
	private AuditUserRepository auditUserRepository;

	@Transactional(readOnly = true)
	public AuditUser findByUsername(String userName) {
		List<AuditUser> auditUsers = auditUserRepository
				.findByUserName(userName);
		if (auditUsers.size() == 1) {
			return auditUsers.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public AuditUser findOne(long id) {
		return auditUserRepository.findOne(id);
	}

}
