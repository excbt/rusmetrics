package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;

@Service
@Transactional
public class ContZPointService {

	@Autowired
	private ContZPointRepository contZPointRepository;

	/**
	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ContZPoint findOne(long contZPointId) {
		return contZPointRepository.findOne(contZPointId);
	}
	
}
