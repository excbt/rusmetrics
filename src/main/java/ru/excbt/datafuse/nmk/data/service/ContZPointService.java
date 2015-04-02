package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

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
	 * /**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ContZPoint findOne(long contZPointId) {
		return contZPointRepository.findOne(contZPointId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository
				.findByContObjectId(contObjectId);
		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean checkContZPointOwnership(long contZPointId, long contObjectId) {
		List<?> checkIds = contZPointRepository.findByIdAndContObject(
				contZPointId, contObjectId);
		return checkIds.size() > 0;
	}

}
