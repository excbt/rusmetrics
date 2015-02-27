package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;


@Service
@Transactional
public class SubscrUserService {

	private final static int TEST_USER_ID = 729;
	
	@Autowired
	private SubscrUserRepository subscrUserRepository;
	
	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private ContObjectRepository contEventObjectRepository;
	
	@Transactional(readOnly = true)
	public List<ContObject> selectSubscrContObjects() {
		List<ContObject> result = contEventObjectRepository.selectSubscrContObjects(TEST_USER_ID); 
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository.findByContObjectId(contObjectId);
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ContEvent> findContEvents(long contObjectId) {
		List<ContEvent> result = contEventRepository.findByContObjectId(contObjectId);
		return result;
	}
	
}
