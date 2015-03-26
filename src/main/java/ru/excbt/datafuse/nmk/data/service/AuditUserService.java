package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.repository.AuditUserRepository;

@Service
@Transactional(readOnly = true)
public class AuditUserService {

	private static final Logger logger = LoggerFactory
			.getLogger(AuditUserService.class);

	@Autowired
	private AuditUserRepository auditUserRepository;

	public AuditUser findByUserName(String userName) {
		List<AuditUser> auditUsers = auditUserRepository
				.findByUserName(userName);

		if (auditUsers.size() == 1) {
			return auditUsers.get(0);
		} else if (auditUsers.size() > 0) {
			logger.error(
					"There is more than 1 AuditUser in system (user_name={})",
					userName);
		}
		return null;
	}

	public AuditUser findOne(long id) {
		return auditUserRepository.findOne(id);
	}

}
