package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;

@Service
public class CurrentUserService {

	private final static long currentUserId = 19748714L;
	
	@Autowired
    private SystemUserRepository userRepository;
	
	
	public SystemUser getCurrentUser() {
		return userRepository.findOne(currentUserId);
	}
}
