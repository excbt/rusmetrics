package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.repository.AuditUserRepository;

@Service
public class AuditUserService {

	private final static long currentUserId = 19748714L;
	
	@Autowired
    private AuditUserRepository userRepository;
	
	
	public AuditUser getCurrentUser() {
		return userRepository.findOne(currentUserId);
	}
}
