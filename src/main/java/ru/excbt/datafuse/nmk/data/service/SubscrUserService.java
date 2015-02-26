package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;


@Service
@Transactional
public class SubscrUserService {

	private final static int TEST_USER_ID = 729;
	
	@Autowired
	private SubscrUserRepository subscrUserRepository;
	
	@Transactional(readOnly = true)
	public List<ContObject> findContObjects() {
		return subscrUserRepository.findContObjects(TEST_USER_ID);
	}
}
