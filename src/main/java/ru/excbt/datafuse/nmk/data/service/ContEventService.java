package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;

@Service
@Transactional
public class ContEventService {

	@Autowired
	private ContEventRepository contEventRepository;

	@Transactional(readOnly = true)
	public List<ContEvent> selectEventsBySubscriber(long subscriberId) {
		return contEventRepository.selectBySubscriberId(subscriberId);
	}

	@Transactional(readOnly = true)
	public List<ContEvent> findEventsByContObjectId(long contObjectId) {
		return contEventRepository.findByContObjectId(contObjectId);
	}
}
